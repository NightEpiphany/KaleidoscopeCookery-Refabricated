package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ServerThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IBambooTray;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.BambooTraySlotStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags.GLASS_BLOCKS_COLORLESS;
import static net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags.GLASS_PANES_COLORLESS;

public class BambooTrayBlockEntity extends BaseBlockEntity implements WorldlyContainer, IBambooTray {
    private static final int[] SLOTS = {0, 1, 2, 3};
    private static final String PROGRESS_TAG = "ProcessingProgress";
    private static final String DURATIONS_TAG = "ProcessingDurations";
    private static final String COMPLETION_STATES_TAG = "CompletionStates";

    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] processingProgress = new int[4];
    private final int[] processingDurations = new int[4];
    private final CompletionState[] completionStates = new CompletionState[4];
    private final Storage<ItemVariant> storage = new CombinedStorage<>(List.of(
            new BambooTraySlotStorage(this, 0), new BambooTraySlotStorage(this, 1),
            new BambooTraySlotStorage(this, 2), new BambooTraySlotStorage(this, 3)));
    private final Storage<ItemVariant> inputStorage = FilteringStorage.insertOnlyOf(storage);
    private final Storage<ItemVariant> outputStorage = FilteringStorage.extractOnlyOf(storage);

    public BambooTrayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BAMBOO_TRAY_BE, pos, state);
        Arrays.fill(this.completionStates, CompletionState.NONE);
    }

    @SuppressWarnings("unused")
    public static void serverTick(Level level, BlockPos pos, BlockState state, BambooTrayBlockEntity tray) {
        if (level.isClientSide()) {
            return;
        }

        long offset = pos.getX() + pos.getY() + pos.getZ();
        if ((level.getGameTime() + offset) % 19 != 0) {
            return;
        }

        boolean wetting = level.isRainingAt(pos.above()) || hasWaterDripstone(level, pos);
        boolean drying = !level.isRaining() && hasDryingExposure(level, pos);
        if (!wetting && !drying) {
            return;
        }
        BambooTrayRecipe.Subtype subtype = wetting
                ? BambooTrayRecipe.Subtype.WETTING
                : BambooTrayRecipe.Subtype.DRYING;

        boolean changed = false;
        List<RecipeHolder<BambooTrayRecipe>> recipes = level.recipeAccess() instanceof net.minecraft.world.item.crafting.RecipeManager recipeManager
                ? getRecipes(recipeManager)
                : List.of();
        for (int slot = 0; slot < tray.items.size(); slot++) {
            ItemStack input = tray.items.get(slot);
            CompletionState completionState = tray.completionStates[slot];
            if (input.isEmpty() || completionState.matches(subtype)) {
                continue;
            }

            BambooTrayRecipe recipe = recipes.stream()
                    .map(RecipeHolder::value)
                    .filter(candidate -> candidate.getSubtype() == subtype)
                    .filter(candidate -> candidate.getIngredient().test(input))
                    .findFirst()
                    .orElse(null);
            if (recipe == null) {
                if (tray.processingProgress[slot] != 0 || tray.processingDurations[slot] != 0) {
                    tray.processingProgress[slot] = 0;
                    tray.processingDurations[slot] = 0;
                    changed = true;
                }
                continue;
            }

            if (completionState.isCompleted()) {
                tray.processingProgress[slot] = 0;
                tray.completionStates[slot] = CompletionState.NONE;
            }
            tray.processingDurations[slot] = recipe.getDuration();
            tray.processingProgress[slot] += 19;
            changed = true;

            if (tray.processingProgress[slot] >= recipe.getDuration()) {
                ItemStack result = recipe.getResult().create();
                int count = Math.min(result.getMaxStackSize(), result.getCount() * input.getCount());
                result.setCount(count);

                tray.items.set(slot, result);
                tray.processingProgress[slot] = recipe.getDuration();
                tray.completionStates[slot] = CompletionState.fromSubtype(subtype);
            }
        }

        if (changed) {
            tray.refresh();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<RecipeHolder<BambooTrayRecipe>> getRecipes(
            net.minecraft.world.item.crafting.RecipeManager recipeManager) {
        return recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == ModRecipes.BAMBOO_TRAY_RECIPE)
                .map(holder -> (RecipeHolder<BambooTrayRecipe>) (RecipeHolder<?>) holder)
                .toList();
    }

    private static boolean hasDryingExposure(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        if (level.canSeeSky(above)) {
            return true;
        }
        for (int y = above.getY(); y < level.getMaxY(); y++) {
            BlockState state = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
            if (state.isAir() || state.is(GLASS_BLOCKS_COLORLESS) || state.is(GLASS_PANES_COLORLESS) || state.is(ModBlocks.BAMBOO_TRAY)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private static boolean hasWaterDripstone(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        BlockPos tipPos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(level, pos);
        return tipPos != null && PointedDripstoneBlock.getCauldronFillFluidType(serverLevel, tipPos) == Fluids.WATER;
    }

    @Override
    public boolean onPutItem(Level level, LivingEntity user, ItemStack held, int slot) {
        if (slot < 0 || slot >= this.items.size() || held.isEmpty()) {
            return false;
        }

        int moved = insertIntoSlot(slot, held);
        if (moved <= 0) {
            return false;
        }
        held.consume(moved, user);
        user.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        this.refresh();
        return true;
    }

    @Override
    public boolean onTakeOut(Level level, LivingEntity user, int slot, boolean takeAll) {
        if (slot < 0 || slot >= this.items.size()) {
            return false;
        }

        ItemStack stored = this.items.get(slot);
        if (stored.isEmpty()) {
            return false;
        }

        int amount = takeAll ? stored.getCount() : 1;
        ItemStack extracted = stored.split(amount);
        if (stored.isEmpty()) {
            this.items.set(slot, ItemStack.EMPTY);
            resetProcessing(slot);
        }
        ItemUtils.getItemToLivingEntity(user, extracted);
        user.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
        this.refresh();
        return true;
    }

    private int insertIntoSlot(int slot, ItemStack stack) {
        ItemStack stored = this.items.get(slot);
        if (stored.isEmpty()) {
            int moved = Math.min(stack.getCount(), stack.getMaxStackSize());
            ItemStack inserted = stack.copy();
            inserted.setCount(moved);
            this.items.set(slot, inserted);
            resetProcessing(slot);
            return moved;
        }
        if (this.completionStates[slot].isCompleted() || !ItemStack.isSameItemSameComponents(stored, stack)) {
            return 0;
        }
        int moved = Math.min(stack.getCount(), stored.getMaxStackSize() - stored.getCount());
        stored.grow(moved);
        return moved;
    }

    private void resetProcessing(int slot) {
        this.processingProgress[slot] = 0;
        this.processingDurations[slot] = 0;
        this.completionStates[slot] = CompletionState.NONE;
    }

    public int getProgressPercent(int slot) {
        if (this.items.get(slot).isEmpty()) {
            return 0;
        }
        if (this.completionStates[slot].isCompleted()) {
            return 100;
        }
        int duration = this.processingDurations[slot];
        return duration <= 0 ? 0 : (int) Math.clamp(this.processingProgress[slot] * 100L / duration, 0L, 100L);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public Storage<ItemVariant> getStorage(@Nullable Direction side) {
        return side == Direction.DOWN ? outputStorage : inputStorage;
    }

    public void setTransferStack(int slot, ItemStack stack) {
        this.items.set(slot, stack);
    }

    public void onTransferCommitted(int slot) {
        if (this.items.get(slot).isEmpty()) {
            resetProcessing(slot);
        }
        this.refresh();
    }

    @ServerThreadSafe
    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ValueOutput.TypedOutputList<ItemStackWithSlot> itemList = valueOutput.list("Items", ItemStackWithSlot.CODEC);
        for (int slot = 0; slot < this.items.size(); slot++) {
            ItemStack itemStack = this.items.get(slot);
            if (!itemStack.isEmpty()) {
                itemList.add(new ItemStackWithSlot(slot, itemStack));
            }
        }
        valueOutput.putIntArray(PROGRESS_TAG, this.processingProgress);
        valueOutput.putIntArray(DURATIONS_TAG, this.processingDurations);

        int[] completionValues = new int[this.completionStates.length];
        for (int i = 0; i < this.completionStates.length; i++) {
            completionValues[i] = this.completionStates[i].getSerializedValue();
        }
        valueOutput.putIntArray(COMPLETION_STATES_TAG, completionValues);
    }

    @ServerThreadSafe
    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.items.clear();

        Arrays.fill(this.processingProgress, 0);
        Arrays.fill(this.processingDurations, 0);
        Arrays.fill(this.completionStates, CompletionState.NONE);
        for (ItemStackWithSlot itemStackWithSlot : valueInput.listOrEmpty("Items", ItemStackWithSlot.CODEC)) {
            if (itemStackWithSlot.isValidInContainer(this.items.size())) {
                this.items.set(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }

        int[] progress = valueInput.getIntArray(PROGRESS_TAG).orElse(new int[0]);
        System.arraycopy(progress, 0, this.processingProgress, 0, Math.min(progress.length, this.processingProgress.length));
        int[] durations = valueInput.getIntArray(DURATIONS_TAG).orElse(new int[0]);
        System.arraycopy(durations, 0, this.processingDurations, 0, Math.min(durations.length, this.processingDurations.length));
        int[] completionValues = valueInput.getIntArray(COMPLETION_STATES_TAG).orElse(new int[0]);
        for (int i = 0; i < Math.min(completionValues.length, this.completionStates.length); i++) {
            this.completionStates[i] = CompletionState.fromSerializedValue((byte) completionValues[i]);
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NonNull Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NonNull ItemStack stack, @Nullable Direction side) {
        if (side == Direction.DOWN || this.completionStates[slot].isCompleted()) {
            return false;
        }
        ItemStack stored = this.items.get(slot);
        return stored.isEmpty() || ItemStack.isSameItemSameComponents(stored, stack) && stored.getCount() < stored.getMaxStackSize();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NonNull ItemStack stack, @NonNull Direction side) {
        return side == Direction.DOWN && this.completionStates[slot].isCompleted();
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            if (this.items.get(slot).isEmpty()) {
                resetProcessing(slot);
            }
            this.refresh();
        }
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(this.items, slot);
        if (!result.isEmpty()) {
            resetProcessing(slot);
        }
        return result;
    }

    @Override
    public void setItem(int slot, @NonNull ItemStack stack) {
        ItemStack previous = this.items.get(slot);
        this.items.set(slot, stack);
        if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(previous, stack)
                || this.completionStates[slot].isCompleted() && stack.getCount() > previous.getCount()) {
            resetProcessing(slot);
        }
        this.refresh();
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        Arrays.fill(this.processingProgress, 0);
        Arrays.fill(this.processingDurations, 0);
        Arrays.fill(this.completionStates, CompletionState.NONE);
        this.refresh();
    }

    private enum CompletionState {
        NONE((byte) 0),
        DRYING_COMPLETED((byte) 1),
        WETTING_COMPLETED((byte) 2);

        private final byte serializedValue;

        CompletionState(byte serializedValue) {
            this.serializedValue = serializedValue;
        }

        private boolean isCompleted() {
            return this != NONE;
        }

        private boolean matches(BambooTrayRecipe.Subtype subtype) {
            return (this == DRYING_COMPLETED && subtype == BambooTrayRecipe.Subtype.DRYING)
                   || (this == WETTING_COMPLETED && subtype == BambooTrayRecipe.Subtype.WETTING);
        }

        private byte getSerializedValue() {
            return this.serializedValue;
        }

        private static CompletionState fromSubtype(BambooTrayRecipe.Subtype subtype) {
            return subtype == BambooTrayRecipe.Subtype.DRYING ? DRYING_COMPLETED : WETTING_COMPLETED;
        }

        private static CompletionState fromSerializedValue(byte value) {
            for (CompletionState state : values()) {
                if (state.serializedValue == value) {
                    return state;
                }
            }
            return NONE;
        }
    }
}
