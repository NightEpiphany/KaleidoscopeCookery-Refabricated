package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

public class ItemUtils {
    public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack) {
        getItemToLivingEntity(entity, stack, -1);
    }

    public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack, int preferredSlot) {
        if (stack.isEmpty()) {
            return;
        }
        if (entity.getMainHandItem().isEmpty()) {
            RandomSource random = entity.level().random;
            entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
            entity.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else if (entity instanceof Player player) {
            player.getInventory().placeItemBackInInventory(stack, true);
//            player.getInventory().placeItemBackInInventory(stack, preferredSlot);
        } else {
            // 否则直接在实体所处位置生成物品
            ItemEntity dropItem = entity.spawnAtLocation(stack);
            if (dropItem != null) {
                dropItem.setPickUpDelay(0);
            }
        }
    }

    public static Pair<Integer, ItemStack> getLastStack(NonNullList<ItemStack> itemList) {
        for (int i = itemList.size(); i > 0; i--) {
            int index = i - 1;
            ItemStack stack = itemList.get(index);
            if (!stack.isEmpty()) {
                return Pair.of(index, stack);
            }
        }
        return Pair.of(0, ItemStack.EMPTY);
    }

    public static Item getContainerItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return Items.AIR;
        }
        Item item = stack.getItem();
        Item remainingItem = item.getCraftingRemainingItem();
        if (remainingItem != null && remainingItem != Items.AIR) {
            return remainingItem;
        }
        if (item instanceof IHasContainer hasContainer) {
            return hasContainer.getContainerItem();
        } else if (item instanceof BowlFoodItem) {
            return Items.BOWL;
        } else if (item instanceof SuspiciousStewItem) {
            return Items.BOWL;
        } else if (stack.is(TagMod.BOWL_CONTAINER)) {
            return Items.BOWL;
        } else if (stack.is(TagMod.GLASS_BOTTLE_CONTAINER)) {
            return Items.GLASS_BOTTLE;
        } else if (stack.is(TagMod.BUCKET_CONTAINER)) {
            return Items.BUCKET;
        } else if (stack.is(Items.POTION)) {
            return Items.GLASS_BOTTLE;
        }
        return Items.AIR;
    }

    public static boolean returnIngredientContainers(LivingEntity user, ItemStack ingredient) {
        if (user.level().isClientSide) {
            return false;
        }
        ItemStack containers = getIngredientContainerStack(ingredient);
        if (containers.isEmpty()) {
            return false;
        }
        while (!containers.isEmpty()) {
            getItemToLivingEntity(user, containers.split(containers.getMaxStackSize()));
        }
        return true;
    }

    public static boolean consumeIngredientContainers(LivingEntity user, ItemStack ingredient) {
        ItemStack containers = getIngredientContainerStack(ingredient);
        if (containers.isEmpty()) {
            return true;
        }
        ItemStack held = user.getMainHandItem();
        if (!ItemStack.isSameItemSameTags(held, containers) || held.getCount() < containers.getCount()) {
            if (user instanceof ServerPlayer player) {
                player.displayClientMessage(Component.translatable(
                        "tip.kaleidoscope_cookery.kitchen.remove_ingredient.need_container", containers.getHoverName()), true);
            }
            return false;
        }
        if (!user.level().isClientSide) {
            held.shrink(containers.getCount());
        }
        return true;
    }

    private static ItemStack getIngredientContainerStack(ItemStack ingredient) {
        if (ingredient.isEmpty() || !ingredient.is(TagMod.SPECIAL)) {
            return ItemStack.EMPTY;
        }
        Item remainder = ingredient.getItem().getCraftingRemainingItem();
        if (remainder == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(remainder, ingredient.getCount());
    }
}
