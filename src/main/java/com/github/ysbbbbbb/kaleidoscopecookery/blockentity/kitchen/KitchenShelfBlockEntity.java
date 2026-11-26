package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.KitchenShelfBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.KitchenShelfItemHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.KitchenShelfItemStorage;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.KitchenShelfItemHandler.CAPACITY;

public class KitchenShelfBlockEntity extends BaseBlockEntity {
    private static final int RESTOCK_INTERVAL = 83;
    private static final String INVENTORY_KEY = "Inventory";
    private static final String WAS_POWERED_KEY = "WasPowered";

    private final KitchenShelfItemHandler inventory = new KitchenShelfItemHandler(this);
    private final KitchenShelfItemStorage itemStorage = new KitchenShelfItemStorage(inventory, this::refresh);
    private boolean wasPowered;

    public KitchenShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.KITCHEN_SHELF_BE, pos, state);
    }

    public ItemStack getStoredStack() {
        return inventory.getStackInSlot(0);
    }

    public Storage<ItemVariant> getItemStorage() {
        return itemStorage;
    }

    public boolean interact(LivingEntity living, boolean all) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ItemStack held = living.getMainHandItem();
        ItemStack stored = getStoredStack();

        if (held.isEmpty()) {
            if (stored.isEmpty()) {
                return false;
            }
            ItemStack taken;
            if (all) {
                taken = stored.copy();
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            } else {
                taken = inventory.extractItem(0, 1, false);
            }
            dropToFront(serverLevel, taken, false);
            return true;
        }

        if (!stored.isEmpty() && !ItemStack.isSameItemSameComponents(stored, held)) {
            return false;
        }
        int amount = all ? Math.min(held.getCount(), CAPACITY - stored.getCount()) : 1;
        if (amount <= 0) {
            return false;
        }
        ItemStack inserted = held.copyWithCount(amount);
        int remainder = inventory.insertItem(0, inserted, false).getCount();
        if (!(living instanceof Player player) || !player.isCreative()) {
            held.shrink(amount - remainder);
        }
        return remainder < amount;
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, KitchenShelfBlockEntity shelf) {
        long offsetTime = level.getGameTime() + pos.hashCode();
        if (Math.floorMod(offsetTime, RESTOCK_INTERVAL) == 0) {
            shelf.restock(state);
        }
    }

    public boolean updatePower(boolean powered) {
        if (powered == wasPowered) {
            return false;
        }
        wasPowered = powered;
        setChanged();
        return powered;
    }

    private void restock(BlockState state) {
        if (level == null || getStoredStack().getCount() >= CAPACITY) {
            return;
        }
        Direction front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        for (Direction direction : Direction.values()) {
            if (direction == front) {
                continue;
            }
            BlockPos sourcePos = worldPosition.relative(direction);
            BlockEntity source = level.getBlockEntity(sourcePos);
            if (source == null || source instanceof KitchenShelfBlockEntity) {
                continue;
            }
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, sourcePos, direction.getOpposite());
            if (storage != null) {
                restockFromStorage(storage);
            }
            if (getStoredStack().getCount() >= CAPACITY) {
                break;
            }
        }
    }

    private void restockFromStorage(Storage<ItemVariant> storage) {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
                ItemVariant candidate = view.getResource();
                ItemStack stored = getStoredStack();
                if (candidate.isBlank() || (!stored.isEmpty() && !candidate.matches(stored))) {
                    continue;
                }
                int needed = Math.min(16, CAPACITY - stored.getCount());
                try (Transaction transfer = transaction.openNested()) {
                    long extracted = view.extract(candidate, needed, transfer);
                    if (extracted > 0 && itemStorage.insert(candidate, extracted, transfer) == extracted) {
                        transfer.commit();
                    }
                }
                if (getStoredStack().getCount() >= CAPACITY) {
                    break;
                }
            }
            transaction.commit();
        }
    }

    public void dispense() {
        if (level instanceof ServerLevel serverLevel) {
            dispense(serverLevel, 1);
        }
    }

    public void dispenseMatching(List<ItemStack> ingredients, boolean useRecipeCounts) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        var remaining = new Reference2IntOpenHashMap<Item>(ingredients.size());
        for (ItemStack ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                Item item = ingredient.getItem();
                if (useRecipeCounts) {
                    remaining.addTo(item, ingredient.getCount());
                } else {
                    remaining.put(item, 1);
                }
            }
        }
        if (remaining.isEmpty()) {
            return;
        }

        for (BlockPos pos : KitchenShelfBlock.getConnectedShelves(serverLevel, worldPosition)) {
            if (!(serverLevel.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf)) {
                continue;
            }
            ItemStack stored = shelf.getStoredStack();
            if (stored.isEmpty()) {
                continue;
            }
            Item item = stored.getItem();
            int requested = remaining.getInt(item);
            if (requested == 0) {
                continue;
            }
            int missing = requested - shelf.dispense(serverLevel, requested);
            if (missing > 0) {
                remaining.put(item, missing);
            } else {
                remaining.removeInt(item);
                if (remaining.isEmpty()) {
                    break;
                }
            }
        }
    }

    private int dispense(ServerLevel level, int requested) {
        ItemStack remaining = getStoredStack().copy();
        ItemStack taken = remaining.split(requested);
        int count = taken.getCount();
        if (count > 0) {
            inventory.setStackInSlot(0, remaining.isEmpty() ? ItemStack.EMPTY : remaining);
            dropToFront(level, taken, true);
        }
        return count;
    }

    private void dropToFront(ServerLevel level, ItemStack stack, boolean dispenseEffects) {
        if (stack.isEmpty()) {
            return;
        }

        Direction front = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        double x = worldPosition.getX() + 0.5 + front.getStepX() * 0.65;
        double y = worldPosition.getY() + 0.45;
        double z = worldPosition.getZ() + 0.5 + front.getStepZ() * 0.65;

        while (!stack.isEmpty()) {
            ItemEntity entity = new ItemEntity(level, x, y, z, stack.split(stack.getMaxStackSize()));
            entity.setDeltaMovement(front.getStepX() * 0.2, 0.15, front.getStepZ() * 0.2);
            level.addFreshEntity(entity);
        }
        if (dispenseEffects) {
            level.playSound(null, worldPosition, ModSounds.BLOCK_KITCHEN_SHELF, SoundSource.BLOCKS, 1, 1);
            level.sendParticles(ModParticles.COOKING, x, y, z, 6, 0.12, 0.08, 0.12, 0.01);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        inventory.save(valueOutput.child(INVENTORY_KEY));
        valueOutput.putBoolean(WAS_POWERED_KEY, wasPowered);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        inventory.load(valueInput.childOrEmpty(INVENTORY_KEY));
        wasPowered = valueInput.getBooleanOr(WAS_POWERED_KEY, false);
    }
}
