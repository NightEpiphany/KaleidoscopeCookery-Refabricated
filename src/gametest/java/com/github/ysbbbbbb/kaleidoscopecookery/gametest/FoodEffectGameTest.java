package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

@SuppressWarnings("all")
public class FoodEffectGameTest {
    @GameTest
    public void creativeInventoryCanHashEveryModItem(GameTestHelper helper) {
        var stacks = ItemStackLinkedSet.createTypeAndComponentsSet();
        for (var item : BuiltInRegistries.ITEM) {
            if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(KaleidoscopeCookery.MOD_ID)) {
                ItemStack stack = item.getDefaultInstance();
                stacks.add(stack);
                helper.assertTrue(stacks.contains(stack.copy()), Component.literal("Cannot look up " + item));
            }
        }
        helper.succeed();
    }

    @GameTest
    public void donkeyBurgerStillAppliesSatiatedShield(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        ModItems.DONKEY_BURGER.getDefaultInstance().finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(player.hasEffect(ModEffects.SATIATED_SHIELD),
                Component.literal("Donkey burger lost its satiated shield effect"));
        helper.succeed();
    }

    @GameTest
    public void lunchBagConsumesFoodInOrderUntilFull(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.getFoodData().setFoodLevel(0);

        ItemStackHandler contents = new ItemStackHandler(16);
        contents.setStackInSlot(0, new ItemStack(net.minecraft.world.item.Items.APPLE, 2));
        contents.setStackInSlot(1, new ItemStack(net.minecraft.world.item.Items.BREAD, 4));
        ItemStack bag = ModItems.TRANSMUTATION_LUNCH_BAG.getDefaultInstance();
        TransmutationLunchBagItem.setItems(bag, contents);

        bag.finishUsingItem(helper.getLevel(), player);

        helper.assertTrue(player.getFoodData().getFoodLevel() == 20,
                Component.literal("Lunch bag must consume food until full"));
        ItemStackHandler remaining = TransmutationLunchBagItem.getItems(bag);
        helper.assertTrue(remaining.getSlots() == 24 && remaining.getStackInSlot(0).isEmpty(),
                Component.literal("Lunch bag must expand old contents and consume apples first"));
        helper.assertTrue(remaining.getStackInSlot(1).getCount() == 1,
                Component.literal("Lunch bag must stop consuming bread once full"));
        helper.succeed();
    }
}
