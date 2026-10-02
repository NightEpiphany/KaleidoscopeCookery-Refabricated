package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.FluidSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.FluidUtils;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluids;

public final class MilkGameTests implements FabricGameTest {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    @GameTest(template = EMPTY_STRUCTURE)
    public void milkBucketsSupportTransactions(GameTestHelper helper) {
        var milk = BuiltInRegistries.FLUID.get(ModFluids.MILK_ID);
        helper.assertTrue(ModFluids.MILK_ID.getNamespace().equals(KaleidoscopeCookery.MOD_ID),
                "Milk fluid must use the mod namespace");
        helper.assertTrue(milk != Fluids.EMPTY && milk.getBucket() == Items.MILK_BUCKET, "Milk fluid is not registered");
        helper.assertTrue(milk.is(TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", "milk"))),
                "Milk fluid tag is missing");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.BUCKET.getDefaultInstance());
        var context = ContainerItemContext.ofPlayerHand(player, InteractionHand.MAIN_HAND);
        var emptyBucket = context.find(FluidStorage.ITEM);
        helper.assertTrue(emptyBucket != null, "Missing empty bucket storage");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(emptyBucket.insert(FluidVariant.of(milk), FluidConstants.BUCKET, tx) == FluidConstants.BUCKET,
                    "Milk must fill exactly one bucket");
        }
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "Aborted filling changed bucket");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(emptyBucket.insert(FluidVariant.of(milk), FluidConstants.BUCKET, tx) == FluidConstants.BUCKET,
                    "Milk insertion failed");
            tx.commit();
        }
        helper.assertTrue(player.getMainHandItem().is(Items.MILK_BUCKET), "Filling did not produce milk bucket");
        var fullBucket = context.find(FluidStorage.ITEM);
        helper.assertTrue(fullBucket != null && FluidUtils.findFirstAmount(fullBucket) == FluidConstants.BUCKET,
                "Milk bucket storage has wrong capacity");
        var storedMilk = FluidUtils.findFirstResource(fullBucket);
        helper.assertTrue(ModFluids.matchesTeaFluid(ModFluids.MILK_ID, BuiltInRegistries.FLUID.getKey(storedMilk.getFluid())),
                "Milk bucket provider has wrong fluid");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(fullBucket.extract(storedMilk, FluidConstants.BUCKET, tx) == FluidConstants.BUCKET,
                    "Milk extraction failed");
        }
        helper.assertTrue(player.getMainHandItem().is(Items.MILK_BUCKET), "Aborted extraction changed bucket");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(fullBucket.extract(storedMilk, FluidConstants.BUCKET, tx) == FluidConstants.BUCKET,
                    "Milk extraction failed");
            tx.commit();
        }
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "Draining did not return empty bucket");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void milkSurvivesTeapotSaveAndDrain(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.TEAPOT);
        TeapotBlockEntity teapot = helper.getBlockEntity(POS);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.MILK_BUCKET.getDefaultInstance());
        helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), player, player.getMainHandItem()), "Cannot fill teapot");
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "Empty bucket not returned");
        var actualFluid = teapot.getTeaFluidId();
        helper.assertTrue(ModFluids.matchesTeaFluid(ModFluids.MILK_ID, actualFluid), "Wrong stored milk fluid");
        teapot.loadAdditional(teapot.saveWithoutMetadata(helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertTrue(actualFluid.equals(teapot.getTeaFluidId()), "Save changed milk provider ID");
        helper.assertTrue(teapot.removeTeaFluid(helper.getLevel(), player, player.getMainHandItem()), "Cannot drain milk");
        helper.assertTrue(player.getMainHandItem().is(Items.MILK_BUCKET), "Milk bucket not returned");
        helper.assertTrue(teapot.getTeaFluidId().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID), "Teapot did not empty");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void milkSoupUsesFluidWithoutBurningPlayer(GameTestHelper helper) {
        var milk = BuiltInRegistries.FLUID.get(ModFluids.MILK_ID);
        var soup = SoupBaseManager.getSoupBase(ModSoupBases.MILK);
        helper.assertTrue(soup instanceof FluidSoupBase fluidSoup && fluidSoup.getFluid() == milk,
                "Milk soup must use the registered fluid");
        helper.setBlock(POS, ModBlocks.STOCKPOT);
        StockpotBlockEntity stockpot = helper.getBlockEntity(POS);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(stockpot.addSoupBase(helper.getLevel(), player, Items.MILK_BUCKET.getDefaultInstance()), "Milk rejected");
        helper.assertTrue(stockpot.addIngredient(helper.getLevel(), player, Items.APPLE.getDefaultInstance()), "Ingredient rejected");
        float health = player.getHealth();
        helper.assertTrue(stockpot.removeIngredient(helper.getLevel(), player), "Cannot retrieve ingredient");
        helper.assertTrue(player.getHealth() == health, "Milk burned player");
        helper.assertTrue(stockpot.removeSoupBase(helper.getLevel(), player, Items.BUCKET.getDefaultInstance()),
                "Cannot recover milk soup");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void butterTeaMatchesMilkProvidersOnly(GameTestHelper helper) {
        var recipe = (TeapotRecipe) helper.getLevel().getRecipeManager().byKey(
                ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot/butter_tea_1")).orElseThrow().value();
        for (var fluid : BuiltInRegistries.FLUID) {
            if (fluid.getBucket() == Items.MILK_BUCKET) {
                helper.assertTrue(recipe.matches(new TeapotInput(ModItems.BUTTER_TEA_BAG.getDefaultInstance(),
                        BuiltInRegistries.FLUID.getKey(fluid)), helper.getLevel()), "Milk provider was rejected");
            }
        }
        helper.assertFalse(recipe.matches(new TeapotInput(ModItems.BUTTER_TEA_BAG.getDefaultInstance(),
                ResourceLocation.withDefaultNamespace("water")), helper.getLevel()), "Water matched milk recipe");
        helper.assertFalse(recipe.matches(new TeapotInput(ItemStack.EMPTY, ModFluids.MILK_ID), helper.getLevel()),
                "Empty input matched milk recipe");
        helper.assertFalse(ModFluids.matchesTeaFluid(ModFluids.MILK_ID,
                ResourceLocation.fromNamespaceAndPath("test", "unknown_milk")), "Unknown fluid matched milk");
        helper.succeed();
    }
}
