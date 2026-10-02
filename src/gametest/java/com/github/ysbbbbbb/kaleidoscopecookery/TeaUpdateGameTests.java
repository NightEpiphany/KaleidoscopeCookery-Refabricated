package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.TeaTreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.EmptyCupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TeaUpdateGameTests implements FabricGameTest {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    @GameTest(template = EMPTY_STRUCTURE)
    public void recipesAndMilkAreLoaded(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        for (String path : new String[]{"bamboo_tray", "tea_banner", "red_lantern", "long_bench",
                "eight_immortals_table", "butter_tea_bag", "teapot/butter_tea_1",
                "stockpot/clay_pot_milk_tea_count_1", "stockpot/tea_egg_count_4"}) {
            helper.assertTrue(manager.byKey(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path)).isPresent(),
                    "Missing recipe: " + path);
        }
        helper.assertTrue(manager.getAllRecipesFor(ModRecipes.BAMBOO_TRAY_RECIPE).size() >= 27, "Missing tray recipes");
        helper.assertTrue(ModItems.TEA_SEED.getDefaultInstance().is(TagMod.COOKERY_MOD_SEEDS), "Tea seed tag missing");
        helper.assertTrue(SoupBaseManager.getSoupBase(ModSoupBases.MILK).isSoupBase(Items.MILK_BUCKET.getDefaultInstance()),
                "Milk soup base missing");
        helper.assertTrue(TeaFluidHelper.getFilledContainer(ModSoupBases.MILK).is(Items.MILK_BUCKET), "Milk display missing");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 2800)
    public void teaAndInvalidIngredientsBrew(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        TeapotBlockEntity valid = placeTeapot(helper, POS);
        TeapotBlockEntity invalid = placeTeapot(helper, POS.east(2));
        helper.assertTrue(valid.receiveDripstoneFluid(Fluids.WATER), "Could not fill water");
        helper.assertTrue(invalid.receiveDripstoneFluid(Fluids.WATER), "Could not fill second teapot");
        ItemStack tea = new ItemStack(ModItems.BARLEY_TEA_BAG, 2);
        ItemStack stone = new ItemStack(Items.COBBLESTONE, 2);
        helper.assertTrue(valid.addIngredient(helper.getLevel(), player, tea), "Tea was rejected");
        helper.assertTrue(invalid.addIngredient(helper.getLevel(), player, stone), "Invalid ingredient should make mystery tea");
        helper.assertTrue(tea.getCount() == 1 && stone.getCount() == 1, "Incorrect ingredient consumption");
        helper.succeedWhen(() -> {
            helper.assertTrue(valid.getStatus() == ITeapot.FINISHED && invalid.getStatus() == ITeapot.FINISHED, "Still brewing");
            helper.assertTrue(valid.getResult().is(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA)), "Wrong tea");
            helper.assertTrue(invalid.getResult().is(TeacupRegistry.getItem(TeacupRegistry.MYSTERY_TEA)), "Wrong failure result");
            helper.assertTrue(valid.getResult().getCount() == 12 && invalid.getResult().getCount() == 4,
                    "Expected twelve cups of tea or four cups of mystery tea");
        });
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void teapotTransferRollsBackAndPersists(GameTestHelper helper) {
        TeapotBlockEntity teapot = placeTeapot(helper, POS);
        teapot.receiveDripstoneFluid(Fluids.WATER);
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(POS), Direction.UP);
        helper.assertTrue(storage != null, "No teapot storage");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(storage.insert(ItemVariant.of(ModItems.OOLONG_TEA_BAG), 64, tx) == 1, "Wrong capacity");
        }
        helper.assertTrue(teapot.getInput().isEmpty() && teapot.getCurrentTick() == -1, "Simulation changed teapot");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(storage.insert(ItemVariant.of(ModItems.OOLONG_TEA_BAG), 64, tx) == 1, "Insertion failed");
            tx.commit();
        }
        var restored = new TeapotBlockEntity(helper.absolutePos(POS), ModBlocks.TEAPOT.defaultBlockState());
        restored.loadAdditional(teapot.saveWithoutMetadata(helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertTrue(restored.getInput().is(ModItems.OOLONG_TEA_BAG)
                && restored.getCurrentTick() == TeapotBlockEntity.INGREDIENT_TIME, "Input/timer was not saved");
        restored.loadAdditional(new CompoundTag(), helper.getLevel().registryAccess());
        helper.assertTrue(restored.canReceiveDripstoneFluid(), "Fresh/dispenser teapot NBT is invalid");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, skyAccess = true, timeoutTicks = 60)
    public void bambooTrayProgressUsesRecipeDurationAndSyncs(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.BAMBOO_TRAY);
        BambooTrayBlockEntity tray = helper.getBlockEntity(POS);
        tray.setItem(0, ModItems.FRESH_TEA_LEAVES.getDefaultInstance());
        int duration = helper.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.BAMBOO_TRAY_RECIPE).stream()
                .map(holder -> holder.value())
                .filter(recipe -> recipe.getSubtype() == BambooTrayRecipe.Subtype.DRYING
                        && recipe.getIngredient().test(tray.getItem(0)))
                .findFirst().orElseThrow().getDuration();
        CompoundTag saved = tray.saveWithoutMetadata(helper.getLevel().registryAccess());
        saved.putIntArray("ProcessingProgress", new int[]{duration / 2, 0, 0, 0});
        saved.remove("ProcessingDurations");
        tray.loadAdditional(saved, helper.getLevel().registryAccess());
        helper.succeedWhen(() -> {
            var registries = helper.getLevel().registryAccess();
            CompoundTag update = tray.getUpdateTag(registries);
            helper.assertTrue(update.getIntArray("ProcessingDurations")[0] == duration, "Recipe duration was not recorded");
            int expected = (int) Math.clamp(update.getIntArray("ProcessingProgress")[0] * 100L / duration, 0L, 100L);
            helper.assertTrue(expected >= 50 && expected < 100 && tray.getProgressPercent(0) == expected,
                    "Wrong recipe conversion percentage");
            var clientCopy = new BambooTrayBlockEntity(helper.absolutePos(POS), ModBlocks.BAMBOO_TRAY.defaultBlockState());
            clientCopy.loadAdditional(update, registries);
            helper.assertTrue(clientCopy.getProgressPercent(0) == expected, "Progress was not synchronized");
            helper.setBlock(POS.above(), Blocks.STONE);
            BambooTrayBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(POS), tray.getBlockState(), tray);
            helper.assertTrue(tray.getProgressPercent(0) == expected, "Paused processing lost progress");
            tray.setItem(0, Items.STONE.getDefaultInstance());
            helper.assertTrue(tray.getProgressPercent(0) == 0, "Replacement kept stale progress");
            tray.clearContent();
            helper.assertTrue(tray.getProgressPercent(0) == 0, "Empty tray has progress");
        });
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void bambooTrayProgressHandlesBoundsAndMissingDuration(GameTestHelper helper) {
        var tray = new BambooTrayBlockEntity(helper.absolutePos(POS), ModBlocks.BAMBOO_TRAY.defaultBlockState());
        for (int slot = 0; slot < 4; slot++) {
            tray.setItem(slot, ModItems.FRESH_TEA_LEAVES.getDefaultInstance());
        }
        var registries = helper.getLevel().registryAccess();
        CompoundTag saved = tray.saveWithoutMetadata(registries);
        saved.putIntArray("ProcessingProgress", new int[]{Integer.MAX_VALUE, -19, 50, 50});
        saved.putIntArray("ProcessingDurations", new int[]{100, 100, 0, -1});
        tray.loadAdditional(saved, registries);
        helper.assertTrue(tray.getProgressPercent(0) == 100, "Progress overflowed");
        helper.assertTrue(tray.getProgressPercent(1) == 0 && tray.getProgressPercent(2) == 0
                && tray.getProgressPercent(3) == 0, "Invalid progress or duration was not handled");
        helper.assertTrue(tray.getProgressPercent(-1) == 0 && tray.getProgressPercent(4) == 0, "Invalid slot was not handled");
        saved.remove("ProcessingDurations");
        tray.loadAdditional(saved, registries);
        helper.assertTrue(tray.getProgressPercent(0) == 0, "Old save retained stale duration");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, skyAccess = true, timeoutTicks = 60)
    public void bambooTrayDriesAndTransfersWithoutLosingCompletion(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.BAMBOO_TRAY);
        BambooTrayBlockEntity tray = helper.getBlockEntity(POS);
        tray.setItem(0, new ItemStack(ModItems.FRESH_TEA_LEAVES, 4));
        primeTray(helper, tray, BambooTrayRecipe.Subtype.DRYING);
        helper.succeedWhen(() -> {
            helper.assertTrue(tray.getItem(0).is(ModItems.DRIED_TEA_LEAVES), "Leaves did not dry");
            helper.assertTrue(tray.getProgressPercent(0) == 100, "Finished recipe should report 100 percent");
            Storage<ItemVariant> bottom = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(POS), Direction.DOWN);
            ItemVariant dried = ItemVariant.of(ModItems.DRIED_TEA_LEAVES);
            try (Transaction tx = Transaction.openOuter()) {
                helper.assertTrue(bottom.extract(dried, 4, tx) == 4, "Cannot extract completed leaves");
            }
            helper.assertTrue(tray.getItem(0).getCount() == 4 && tray.canTakeItemThroughFace(0, tray.getItem(0), Direction.DOWN),
                    "Rollback lost completion or items");
            try (Transaction tx = Transaction.openOuter()) {
                helper.assertTrue(bottom.extract(dried, 1, tx) == 1, "Partial extraction failed");
                tx.commit();
            }
            helper.assertTrue(tray.getItem(0).getCount() == 3 && tray.canTakeItemThroughFace(0, tray.getItem(0), Direction.DOWN),
                    "Partial extraction lost completion");
            var saved = tray.saveWithoutMetadata(helper.getLevel().registryAccess());
            var restored = new BambooTrayBlockEntity(helper.absolutePos(POS), ModBlocks.BAMBOO_TRAY.defaultBlockState());
            restored.loadAdditional(saved, helper.getLevel().registryAccess());
            helper.assertTrue(restored.getProgressPercent(0) == 100, "Finished percentage was not saved");
            helper.assertTrue(restored.canTakeItemThroughFace(0, restored.getItem(0), Direction.DOWN), "Completion not saved");
        });
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 60)
    public void bambooTrayWetsUnderDripstone(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.BAMBOO_TRAY);
        helper.setBlock(POS.above(3), Blocks.STONE);
        helper.setBlock(POS.above(4), Blocks.WATER);
        helper.setBlock(POS.above(2), Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP));
        BambooTrayBlockEntity tray = helper.getBlockEntity(POS);
        tray.setItem(0, new ItemStack(ModItems.DRIED_TEA_LEAVES, 4));
        primeTray(helper, tray, BambooTrayRecipe.Subtype.WETTING);
        helper.succeedWhen(() -> helper.assertTrue(tray.getItem(0).is(ModItems.FRESH_TEA_LEAVES), "Dripstone did not wet leaves"));
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void teaTreeHarvestKeepsThePlant(GameTestHelper helper) {
        helper.setBlock(POS.below(), Blocks.DIRT);
        helper.setBlock(POS, ModBlocks.TEA_TREE.defaultBlockState().setValue(TeaTreeBlock.AGE, 5));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ((TeaTreeBlock) ModBlocks.TEA_TREE).useItemOn(ItemStack.EMPTY, helper.getBlockState(POS), helper.getLevel(),
                helper.absolutePos(POS), player, InteractionHand.MAIN_HAND, hit(helper, POS));
        helper.assertBlockProperty(POS, TeaTreeBlock.AGE, 3);
        helper.assertItemEntityCountIs(ModItems.FRESH_TEA_LEAVES, POS, 2, 1);
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void lastTeaCupLeavesEmptyCups(GameTestHelper helper) {
        TeacupBlock tea = (TeacupBlock) TeacupRegistry.getBlock(TeacupRegistry.BARLEY_TEA);
        helper.setBlock(POS, tea.defaultBlockState().setValue(tea.getCupCountProperty(), 3));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        tea.useItemOn(player.getMainHandItem(), helper.getBlockState(POS), helper.getLevel(),
                helper.absolutePos(POS), player, InteractionHand.MAIN_HAND, hit(helper, POS));
        helper.assertBlockPresent(ModBlocks.EMPTY_CUP, POS);
        helper.assertBlockProperty(POS, EmptyCupBlock.CUP_COUNT, 2);
        helper.assertItemEntityCountIs(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA), POS, 2, 1);
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void teaBannerKeepsDyeAndPattern(GameTestHelper helper) {
        helper.setBlock(POS.below(), Blocks.STONE);
        helper.setBlock(POS, ModBlocks.TEA_BANNER);
        TeaBannerBlockEntity banner = helper.getBlockEntity(POS);
        banner.setColor(DyeColor.CYAN);
        banner.setPatternItem(Items.FLOW_BANNER_PATTERN.getDefaultInstance());
        ItemStack item = banner.createItemStack(helper.getLevel());
        var restored = new TeaBannerBlockEntity(helper.absolutePos(POS), ModBlocks.TEA_BANNER.defaultBlockState());
        restored.loadAdditional(item.get(DataComponents.BLOCK_ENTITY_DATA).copyTag(), helper.getLevel().registryAccess());
        helper.assertTrue(restored.getColor() == DyeColor.CYAN && restored.getPatternItem().is(Items.FLOW_BANNER_PATTERN),
                "Banner components did not survive item conversion");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void milkTeaRemovesOnlyHarmfulEffects(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200));
        ItemStack result = ModItems.CLAY_POT_MILK_TEA.finishUsingItem(
                ModItems.CLAY_POT_MILK_TEA.getDefaultInstance(), helper.getLevel(), player);
        helper.assertFalse(player.hasEffect(MobEffects.POISON), "Poison was not removed");
        helper.assertTrue(player.hasEffect(MobEffects.MOVEMENT_SPEED), "Beneficial effect was removed");
        helper.assertTrue(result.is(Items.FLOWER_POT), "Missing returned flower pot");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 650)
    public void milkBrewsButterTeaAndClayPotMilkTea(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        TeapotBlockEntity teapot = placeTeapot(helper, POS);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.MILK_BUCKET.getDefaultInstance());
        helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), player, player.getMainHandItem())
                        && player.getMainHandItem().is(Items.BUCKET),
                "Milk bucket was not replaced with an empty bucket");
        helper.assertTrue(teapot.getTeaFluidId().equals(ModSoupBases.MILK), "Incorrect milk fluid ID");
        helper.assertTrue(teapot.addIngredient(helper.getLevel(), player, ModItems.BUTTER_TEA_BAG.getDefaultInstance()),
                "Butter tea bag was rejected");

        BlockPos stockpotPos = POS.east(2);
        helper.setBlock(stockpotPos.below(), Blocks.MAGMA_BLOCK);
        helper.setBlock(stockpotPos, ModBlocks.STOCKPOT);
        StockpotBlockEntity stockpot = helper.getBlockEntity(stockpotPos);
        helper.assertTrue(stockpot.addSoupBase(helper.getLevel(), player, Items.MILK_BUCKET.getDefaultInstance()),
                "Milk soup base was rejected");
        helper.assertTrue(stockpot.addIngredient(helper.getLevel(), player, ModItems.BILUOCHUN_TEA_BAG.getDefaultInstance()),
                "Milk tea ingredient was rejected");
        helper.assertTrue(stockpot.onLidClick(helper.getLevel(), player, ModItems.STOCKPOT_LID.getDefaultInstance()),
                "Could not close stockpot");
        helper.succeedWhen(() -> {
            helper.assertTrue(teapot.getResult().is(TeacupRegistry.getItem(TeacupRegistry.BUTTER_TEA)), "Wrong butter tea");
            helper.assertTrue(stockpot.getResult().is(ModItems.CLAY_POT_MILK_TEA), "Wrong clay pot milk tea");
        });
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 30)
    public void dispensersPlaceTeaEquipment(GameTestHelper helper) {
        BlockPos second = POS.east(3);
        helper.setBlock(POS, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.SOUTH));
        helper.setBlock(second, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.SOUTH));
        DispenserBlockEntity firstDispenser = helper.getBlockEntity(POS);
        DispenserBlockEntity secondDispenser = helper.getBlockEntity(second);
        firstDispenser.setItem(0, ModItems.TEAPOT.getDefaultInstance());
        secondDispenser.setItem(0, new ItemStack(ModItems.BAMBOO_TRAY, 2));
        helper.setBlock(POS.above(), Blocks.REDSTONE_BLOCK);
        helper.setBlock(second.above(), Blocks.REDSTONE_BLOCK);
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(ModBlocks.TEAPOT, POS.south());
            helper.assertBlockPresent(ModBlocks.BAMBOO_TRAY, second.south());
            helper.assertTrue(firstDispenser.getItem(0).isEmpty() && secondDispenser.getItem(0).getCount() == 1,
                    "Dispenser consumed the wrong count");
            TeapotBlockEntity teapot = helper.getBlockEntity(POS.south());
            helper.assertTrue(teapot.canReceiveDripstoneFluid(), "Dispensed teapot is not usable");
        });
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void millstoneAcceptsIngredientsOnlyFromAbove(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.MILLSTONE);
        MillstoneBlockEntity millstone = helper.getBlockEntity(POS);
        Storage<ItemVariant> top = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(POS), Direction.UP);
        Storage<ItemVariant> bottom = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(POS), Direction.DOWN);
        helper.assertTrue(top != null && bottom == null, "Incorrect millstone sided storage");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(top.insert(ItemVariant.of(Items.WHEAT), 64, tx) == 8, "Incorrect millstone capacity");
        }
        helper.assertTrue(millstone.getInput().isEmpty(), "Aborted insertion changed millstone");
        try (Transaction tx = Transaction.openOuter()) {
            helper.assertTrue(top.insert(ItemVariant.of(Items.WHEAT), 64, tx) == 8, "Millstone insertion failed");
            tx.commit();
        }
        helper.assertTrue(millstone.getInput().is(Items.WHEAT) && millstone.getInput().getCount() == 8,
                "Millstone input was not committed");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void eightImmortalsTableBreaksAsOne(GameTestHelper helper) {
        var stack = ModItems.EIGHT_IMMORTALS_TABLE.getDefaultInstance();
        var context = new DirectionalPlaceContext(helper.getLevel(), helper.absolutePos(POS), Direction.NORTH, stack, Direction.UP);
        helper.assertTrue(((net.minecraft.world.item.BlockItem) ModItems.EIGHT_IMMORTALS_TABLE).place(context).consumesAction(),
                "Table placement failed");
        long parts = BlockPos.betweenClosedStream(helper.absolutePos(new BlockPos(0, 2, 0)),
                helper.absolutePos(new BlockPos(5, 2, 5)))
                .filter(pos -> helper.getLevel().getBlockState(pos).is(ModBlocks.EIGHT_IMMORTALS_TABLE)).count();
        helper.assertTrue(parts == 4, "Table should occupy four blocks");
        helper.getLevel().destroyBlock(helper.absolutePos(POS), true);
        helper.assertTrue(BlockPos.betweenClosedStream(helper.absolutePos(new BlockPos(0, 2, 0)),
                        helper.absolutePos(new BlockPos(5, 2, 5)))
                .noneMatch(pos -> helper.getLevel().getBlockState(pos).is(ModBlocks.EIGHT_IMMORTALS_TABLE)), "Orphaned table part");
        helper.assertItemEntityCountIs(ModItems.EIGHT_IMMORTALS_TABLE, POS, 3, 1);
        helper.succeed();
    }

    private static TeapotBlockEntity placeTeapot(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.MAGMA_BLOCK);
        helper.setBlock(pos, ModBlocks.TEAPOT);
        return helper.getBlockEntity(pos);
    }

    private static void primeTray(GameTestHelper helper, BambooTrayBlockEntity tray, BambooTrayRecipe.Subtype subtype) {
        int duration = helper.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.BAMBOO_TRAY_RECIPE).stream()
                .map(holder -> holder.value()).filter(recipe -> recipe.getSubtype() == subtype
                        && recipe.getIngredient().test(tray.getItem(0))).findFirst().orElseThrow().getDuration();
        CompoundTag tag = tray.saveWithoutMetadata(helper.getLevel().registryAccess());
        tag.putIntArray("ProcessingProgress", new int[]{duration - 19, 0, 0, 0});
        tray.loadAdditional(tag, helper.getLevel().registryAccess());
    }

    private static BlockHitResult hit(GameTestHelper helper, BlockPos pos) {
        BlockPos absolute = helper.absolutePos(pos);
        return new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
    }
}
