package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TransmutationLunchBagItem extends WithTooltipsItem {
    public static final int NO_ITEMS = 0;
    public static final int HAS_ITEMS = 1;

    private static final int MAX_SIZE = 24;

    public TransmutationLunchBagItem(Properties p) {
        super(p.stacksTo(1).rarity(Rarity.UNCOMMON)
                .food(
                        new FoodProperties(0, 0, true),
                        Consumables.DEFAULT_FOOD
                ),
                "transmutation_lunch_bag");
    }

    @SuppressWarnings("unused")
    @Deprecated
    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (!hasItems(stack)) {
            return NO_ITEMS;
        }
        return HAS_ITEMS;
    }

    public static boolean hasItems(ItemStack bag) {
        return bag.has(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
    }

    public static ItemStackHandler getItems(ItemStack bag) {
        ItemContainer container = bag.get(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
        if (container != null) {
            return ItemContainer.of(container.items()).items();
        }
        return new ItemStackHandler(MAX_SIZE);
    }

    public static void setItems(ItemStack bag, ItemStackHandler items) {
        // 先判断是否全空
        boolean allEmpty = true;
        for (int i = 0; i < items.getSlots(); i++) {
            if (!items.getStackInSlot(i).isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        if (allEmpty) {
            bag.remove(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
        } else {
            bag.set(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS, ItemContainer.of(items));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof FruitBasketBlockEntity fruitBasket)) {
            return super.useOn(context);
        }
        Player player = context.getPlayer();
        if (player == null) {
            return super.useOn(context);
        }
        ItemStack bag = context.getItemInHand();
        ItemStackHandler bagItems = TransmutationLunchBagItem.getItems(bag);
        ItemStackHandler fruitBasketItems = new ItemStackHandler(fruitBasket.getItems());

        // 先检查果篮是否为空
        boolean basketEmpty = true;
        for (int i = 0; i < fruitBasketItems.getSlots(); i++) {
            if (!fruitBasketItems.getStackInSlot(i).isEmpty()) {
                basketEmpty = false;
                break;
            }
        }

        // 果篮空了，那么尝试放入物品
        if (hasItems(bag) && basketEmpty) {
            for (int i = 0; i < bagItems.getSlots(); i++) {
                ItemStack stack = bagItems.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
                    ItemStack remaining = ItemUtils.insertItemStacked(fruitBasketItems, stack, false);
                    bagItems.extractItem(i, stack.getCount() - remaining.getCount(), false);
                }
            }
            TransmutationLunchBagItem.setItems(bag, bagItems);
            fruitBasket.refresh();
            playRemoveOneSound(player);
            return context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        // 果篮不为空，尝试取出物品
        for (int i = 0; i < fruitBasketItems.getSlots(); i++) {
            ItemStack stack = fruitBasketItems.getStackInSlot(i);
            if (!stack.isEmpty() && canAdd(stack)) {
                ItemStack remaining = ItemUtils.insertItemStacked(bagItems, stack, false);
                fruitBasketItems.extractItem(i, stack.getCount() - remaining.getCount(), false);
            }
        }
        TransmutationLunchBagItem.setItems(bag, bagItems);
        fruitBasket.refresh();
        playDropContentsSound(player);
        return context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        // 里面有物品
        if (hasItems(itemInHand)) {
            ItemStackHandler items = getItems(itemInHand);
            for (int i = 0; i < items.getSlots(); i++) {
                if (canConsume(items.getStackInSlot(i))) {
                    player.startUsingItem(hand);
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NonNull ItemStack bag, Level level, @NonNull LivingEntity entity) {
        if (level.isClientSide() || !hasItems(bag)) {
            return bag;
        }
        ItemStackHandler items = getItems(bag);
        boolean consumedAny = false;
        boolean consumedFood = false;
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (stackInSlot.isEmpty()) {
                continue;
            }

            FoodProperties foodProperties = stackInSlot.get(DataComponents.FOOD);
            if (foodProperties != null && stackInSlot.has(DataComponents.CONSUMABLE)) {
                while (!items.getStackInSlot(i).isEmpty() && entity instanceof Player player
                        && (!consumedFood || player.getFoodData().getFoodLevel() < 20)) {
                    consumeOne(items.extractItem(i, 1, false), level, entity);
                    consumedAny = true;
                    consumedFood = true;
                }
            } else if (stackInSlot.is(Items.POTION)) {
                while (!items.getStackInSlot(i).isEmpty()) {
                    consumeOne(items.extractItem(i, 1, false), level, entity);
                    consumedAny = true;
                }
            }
        }

        if (consumedAny && entity instanceof ServerPlayer player) {
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_TRANSMUTATION_LUNCH_BAG);
        }
        setItems(bag, items);
        return bag;
    }

    private static boolean canConsume(ItemStack stack) {
        return !stack.isEmpty() && ((stack.has(DataComponents.FOOD) && stack.has(DataComponents.CONSUMABLE)) || stack.is(Items.POTION));
    }

    private static void consumeOne(ItemStack stack, Level level, LivingEntity entity) {
        Item containerItem = ItemUtils.getContainerItem(stack);
        ItemStack returnStack = stack.finishUsingItem(level, entity);
        if (returnStack.isEmpty() && containerItem != Items.AIR) {
            returnStack = containerItem.getDefaultInstance();
        }
        if (!returnStack.isEmpty() && (!(entity instanceof Player player) || !player.getAbilities().instabuild)) {
            ItemUtils.getItemToLivingEntity(entity, returnStack);
        }
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack itemStack) {
        ItemContainer container = itemStack.get(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
        if (container != null) {
            ItemStackHandler items = container.items();
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack stored = items.getStackInSlot(i);
                if (stored.has(DataComponents.FOOD) && stored.has(DataComponents.CONSUMABLE)) {
                    return ItemUseAnimation.EAT;
                }
                if (stored.is(Items.POTION)) {
                    return ItemUseAnimation.DRINK;
                }
            }
        }
        return ItemUseAnimation.NONE;
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity entity) {
        return 32;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack bag, @NonNull Slot slot, @NonNull ClickAction action, @NonNull Player player) {
        if (bag.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack clickItem = slot.getItem();
        if (clickItem.isEmpty()) {
            // 当点击的地方为空，那么取出物品
            this.playRemoveOneSound(player);
            removeOne(bag).ifPresent(stack -> add(bag, slot.safeInsert(stack)));
        } else if (clickItem.getItem().canFitInsideContainerItems() && canAdd(clickItem)) {
            // 否则，放入食物
            int addCount = add(bag, clickItem, true);
            if (addCount > 0) {
                ItemStack takeout = slot.safeTake(clickItem.getCount(), addCount, player);
                if (!takeout.isEmpty()) {
                    add(bag, takeout);
                }
                this.playInsertSound(player);
            }
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bag, @NonNull ItemStack other, @NonNull Slot slot, @NonNull ClickAction action, @NonNull Player
            player, @NonNull SlotAccess access) {
        if (bag.getCount() != 1) {
            return false;
        }
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false;
        }
        if (other.isEmpty()) {
            removeOne(bag).ifPresent(stack -> {
                this.playRemoveOneSound(player);
                access.set(stack);
            });
        } else {
            int added = add(bag, other);
            if (added > 0) {
                this.playInsertSound(player);
                other.shrink(added);
            }
        }
        return true;
    }

    public static boolean canAdd(ItemStack food) {
        if (food.isEmpty()) {
            return false;
        }
        if (!food.getItem().canFitInsideContainerItems()) {
            return false;
        }
        return (food.has(DataComponents.FOOD) && food.has(DataComponents.CONSUMABLE)) || food.has(DataComponents.POTION_CONTENTS);
    }

    private static Optional<ItemStack> removeOne(ItemStack bag) {
        if (!hasItems(bag)) {
            return Optional.empty();
        }
        ItemStackHandler items = getItems(bag);
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack extractItem = items.extractItem(i, items.getSlotLimit(i), false);
            if (!extractItem.isEmpty()) {
                setItems(bag, items);
                return Optional.of(extractItem);
            }
        }
        return Optional.empty();
    }

    private static int add(ItemStack bag, ItemStack food) {
        return add(bag, food, false);
    }

    private static int add(ItemStack bag, ItemStack food, boolean simulate) {
        if (food.isEmpty() || !food.getItem().canFitInsideContainerItems() || !canAdd(food)) {
            return 0;
        }
        int totalCount = food.getCount();

        ItemStackHandler items = getItems(bag);
        ItemStack remaining = insertInOrder(items, food, simulate);

        int addCount = totalCount - (remaining.isEmpty() ? 0 : remaining.getCount());
        if (!simulate && addCount > 0) {
            setItems(bag, items);
        }
        return addCount;
    }

    private static ItemStack insertInOrder(ItemStackHandler items, ItemStack input, boolean simulate) {
        ItemStack remainder = input.copy();
        int lastOccupied = -1;
        for (int i = 0; i < items.getSlots(); i++) {
            if (!items.getStackInSlot(i).isEmpty()) lastOccupied = i;
        }
        if (lastOccupied >= 0) {
            ItemStack last = items.getStackInSlot(lastOccupied);
            if (ItemStack.isSameItemSameComponents(last, remainder)) {
                int moved = Math.min(remainder.getCount(), last.getMaxStackSize() - last.getCount());
                if (moved > 0) {
                    if (!simulate) last.grow(moved);
                    remainder.shrink(moved);
                }
            }
        }
        for (int i = 0; i < items.getSlots() && !remainder.isEmpty(); i++) {
            if (items.getStackInSlot(i).isEmpty()) {
                int moved = Math.min(remainder.getCount(), remainder.getMaxStackSize());
                if (!simulate) items.setStackInSlot(i, remainder.copyWithCount(moved));
                remainder.shrink(moved);
            }
        }
        return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
    }

    public static boolean dropContents(ItemStack bag, Player player) {
        if (!hasItems(bag)) {
            return false;
        }
        boolean result = false;
        ItemStackHandler items = getItems(bag);
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemUtils.giveItemToPlayer(player, stack);
            result = true;
        }
        if (result) {
            items = new ItemStackHandler(MAX_SIZE);
            setItems(bag, items);
        }
        return result;
    }

    private void playRemoveOneSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    public void playDropContentsSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NonNull ItemStack stack) {
        ItemContainer container = stack.get(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
        if (container == null) {
            return Optional.empty();
        }
        return Optional.of(new ItemContainerTooltip(container.items()));
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context,
                               @NonNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        for (int line = 1; line <= 3; line++) {
            tooltip.accept(Component.translatable(key + ".line" + line).withStyle(ChatFormatting.GRAY));
        }
    }

    public record ItemContainer(ItemStackHandler items) {
        public static ItemContainer of(ItemStackHandler items) {
            ItemStackHandler copy = new ItemStackHandler(MAX_SIZE);
            for (int i = 0; i < Math.min(items.getSlots(), copy.getSlots()); i++) {
                copy.setStackInSlot(i, items.getStackInSlot(i).copy());
            }
            return new ItemContainer(copy);
        }

        public static final Codec<ItemContainer> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(
                list -> {
                    ItemStackHandler handler = new ItemStackHandler(MAX_SIZE);
                    for (int i = 0; i < Math.min(list.size(), handler.getSlots()); i++) {
                        handler.setStackInSlot(i, list.get(i));
                    }
                    return ItemContainer.of(handler);
                },
                container -> {
                    ItemStackHandler handler = container.items();
                    List<ItemStack> output = new ArrayList<>(handler.getSlots());
                    for (int i = 0; i < handler.getSlots(); i++) {
                        output.add(handler.getStackInSlot(i));
                    }
                    return output;
                }
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TransmutationLunchBagItem.ItemContainer> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public TransmutationLunchBagItem.@NotNull ItemContainer decode(RegistryFriendlyByteBuf buffer) {
                CompoundTag compoundTag = buffer.readNbt();
                ItemStackHandler handler = new ItemStackHandler(MAX_SIZE);
                if (compoundTag != null) {
                    handler.deserializeNBT(TagValueInput.create(ProblemReporter.DISCARDING, buffer.registryAccess(), compoundTag));
                }
                return ItemContainer.of(handler);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, TransmutationLunchBagItem.ItemContainer value) {
                CompoundTag compoundTag = value.items().serializeNBT(TagValueOutput.createWithContext(ProblemReporter.DISCARDING, buffer.registryAccess()));
                buffer.writeNbt(compoundTag);
            }
        };
    }
}
