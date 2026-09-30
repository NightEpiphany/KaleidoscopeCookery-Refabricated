package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEnchantments;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

@SuppressWarnings("all")
public class Update16GameTest {
    @GameTest
    public void dispenserCutsChickenIntoAllOutputs(GameTestHelper helper) {
        BlockPos boardPos = new BlockPos(1, 1, 1);
        BlockPos dispenserPos = new BlockPos(1, 1, 2);
        helper.setBlock(boardPos.below(), Blocks.STONE);
        helper.setBlock(boardPos, ModBlocks.CHOPPING_BOARD);
        var state = Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.NORTH);
        helper.setBlock(dispenserPos, state);
        var source = new BlockSource(helper.getLevel(), helper.absolutePos(dispenserPos), state,
                helper.getBlockEntity(dispenserPos, DispenserBlockEntity.class));
        ItemStack chicken = new ItemStack(Items.CHICKEN, 2);
        DispenserBlock.DISPENSER_REGISTRY.get(Items.CHICKEN).dispense(source, chicken);
        helper.assertTrue(chicken.getCount() == 1, Component.literal("Dispenser must insert exactly one chicken"));

        ItemStack knife = new ItemStack(ModItems.IRON_KITCHEN_KNIFE);
        knife.enchant(helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ModEnchantments.QUICK_KNIFE), 2);
        var behavior = DispenserBlock.DISPENSER_REGISTRY.get(knife.getItem());
        behavior.dispense(source, knife);
        var board = helper.getBlockEntity(boardPos, ChoppingBoardBlockEntity.class);
        helper.assertTrue(board.getCurrentCutCount() == 4,
                Component.literal("Quick Knife II must complete four cuts in one operation"));
        behavior.dispense(source, knife);
        helper.assertTrue(board.getCurrentCutStack().isEmpty(), Component.literal("Finished board must reset"));
        helper.assertItemEntityCountIs(ModItems.RAW_CUT_SMALL_MEATS, boardPos, 1, 3);
        helper.assertItemEntityCountIs(Items.BONE, boardPos, 1, 1);
        helper.succeed();
    }

    @GameTest
    public void millstoneReleasesBoundEntityWhenDamaged(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.MILLSTONE);
        var millstone = helper.getBlockEntity(pos, MillstoneBlockEntity.class);
        var cow = helper.spawnWithNoFreeWill(EntityTypes.COW, new BlockPos(1, 2, 1));
        helper.assertTrue(millstone.canBindEntity(cow), Component.literal("Adult cow should be bindable"));
        millstone.bindEntity(cow);
        helper.assertTrue(millstone.isBoundTo(cow), Component.literal("Millstone did not bind the cow"));
        cow.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 1);
        helper.assertTrue(!millstone.isBoundTo(cow), Component.literal("Damage must release the bound cow"));
        helper.assertTrue(!millstone.canBindEntity(helper.makeMockServerPlayerInLevel()),
                Component.literal("Players must remain blacklisted"));
        helper.succeed();
    }

    @GameTest
    public void fullLunchBagStillConsumesPotionsAndReturnsBottle(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(20);
        ItemStackHandler contents = new ItemStackHandler(24);
        contents.setStackInSlot(0, new ItemStack(Items.APPLE, 2));
        ItemStack potion = new ItemStack(Items.POTION);
        potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SWIFTNESS));
        contents.setStackInSlot(23, potion);
        ItemStack bag = ModItems.TRANSMUTATION_LUNCH_BAG.getDefaultInstance();
        TransmutationLunchBagItem.setItems(bag, contents);
        bag.finishUsingItem(helper.getLevel(), player);
        var remaining = TransmutationLunchBagItem.getItems(bag);
        helper.assertTrue(remaining.getStackInSlot(0).getCount() == 1,
                Component.literal("A full player must consume exactly the first food"));
        helper.assertTrue(remaining.getStackInSlot(23).isEmpty() && player.hasEffect(MobEffects.SPEED),
                Component.literal("Potion in the last slot must be consumed even when full"));
        helper.assertTrue(player.getInventory().contains(new ItemStack(Items.GLASS_BOTTLE)),
                Component.literal("Drinking a potion must return its bottle"));
        helper.succeed();
    }
}
