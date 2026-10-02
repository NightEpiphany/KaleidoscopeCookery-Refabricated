package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.EightImmortalsTableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.TeapotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.EmptyCupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeapotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.forge.ItemStackHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.FluidSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.CustomFluidTank;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CookeryGameTests implements FabricGameTest {
    private static final BlockPos POT = new BlockPos(1, 1, 1);

    @GameTest(template = EMPTY_STRUCTURE)
    public void customEnchantmentsRestrictSurvivalAnvilTargets(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        player.getAbilities().instabuild = false;
        player.experienceLevel = 100;
        for (Item knife : new Item[]{ModItems.IRON_KITCHEN_KNIFE, ModItems.GOLD_KITCHEN_KNIFE,
                ModItems.DIAMOND_KITCHEN_KNIFE, ModItems.NETHERITE_KITCHEN_KNIFE}) {
            assertAnvilEnchantment(helper, player, knife, ModEnchantments.QUICK_KNIFE, true);
            assertAnvilEnchantment(helper, player, knife, ModEnchantments.SWEEP, false);
        }
        assertAnvilEnchantment(helper, player, ModItems.SICKLE, ModEnchantments.QUICK_KNIFE, false);
        assertAnvilEnchantment(helper, player, ModItems.SICKLE, ModEnchantments.SWEEP, true);
        assertAnvilEnchantment(helper, player, Items.IRON_SWORD, ModEnchantments.QUICK_KNIFE, false);
        assertAnvilEnchantment(helper, player, Items.IRON_SWORD, ModEnchantments.SWEEP, false);
        assertAnvilEnchantment(helper, player, Items.ENCHANTED_BOOK, ModEnchantments.QUICK_KNIFE, true);
        assertAnvilEnchantment(helper, player, Items.ENCHANTED_BOOK, ModEnchantments.SWEEP, true);
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void creativeAnvilBypassesCustomEnchantmentTargetChecks(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        player.getAbilities().instabuild = true;
        helper.assertTrue(!ModEnchantments.QUICK_KNIFE.canEnchant(new ItemStack(ModItems.SICKLE)),
                "Quick Knife's target predicate must reject sickles");
        helper.assertTrue(!ModEnchantments.SWEEP.canEnchant(new ItemStack(ModItems.IRON_KITCHEN_KNIFE)),
                "Sweep's target predicate must reject kitchen knives");
        assertAnvilEnchantment(helper, player, ModItems.SICKLE, ModEnchantments.QUICK_KNIFE, true);
        assertAnvilEnchantment(helper, player, ModItems.IRON_KITCHEN_KNIFE, ModEnchantments.SWEEP, true);
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void customEnchantmentsRestrictEnchantingTableCandidates(GameTestHelper helper) {
        for (Item item : new Item[]{ModItems.IRON_KITCHEN_KNIFE, ModItems.GOLD_KITCHEN_KNIFE,
                ModItems.DIAMOND_KITCHEN_KNIFE, ModItems.NETHERITE_KITCHEN_KNIFE,
                ModItems.SICKLE, Items.IRON_SWORD, Items.IRON_PICKAXE, Items.BOOK}) {
            ItemStack stack = new ItemStack(item);
            for (Enchantment enchantment : new Enchantment[]{ModEnchantments.QUICK_KNIFE, ModEnchantments.SWEEP}) {
                for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
                    boolean offered = EnchantmentHelper.getAvailableEnchantmentResults(enchantment.getMinCost(level), stack, false)
                            .stream().anyMatch(candidate -> candidate.enchantment == enchantment);
                    helper.assertTrue(offered == (stack.is(Items.BOOK) || enchantment.canEnchant(stack)),
                            "Enchanting table must honor custom targets: " + enchantment.getDescriptionId() + " on " + item);
                }
            }
        }
        helper.succeed();
    }

    private static void assertAnvilEnchantment(GameTestHelper helper, Player player, Item target,
                                               Enchantment enchantment, boolean allowed) {
        AnvilMenu menu = new AnvilMenu(0, player.getInventory());
        menu.getSlot(AnvilMenu.INPUT_SLOT).set(new ItemStack(target));
        menu.getSlot(AnvilMenu.ADDITIONAL_SLOT).set(
                EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
        menu.createResult();
        ItemStack output = menu.getSlot(AnvilMenu.RESULT_SLOT).getItem();
        helper.assertTrue(output.isEmpty() != allowed,
                "Unexpected anvil result: " + enchantment.getDescriptionId() + " on " + target);
        if (allowed) {
            helper.assertTrue(EnchantmentHelper.getEnchantments(output).getOrDefault(enchantment, 0) == enchantment.getMaxLevel(),
                    "Anvil output must contain the requested enchantment");
            helper.assertTrue(menu.getSlot(AnvilMenu.RESULT_SLOT).mayPickup(player), "Player must be able to take the anvil output");
        }
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void lunchBagSupportsFullHungerAndCannotContainItself(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(0);
        ItemStack bag = new ItemStack(ModItems.TRANSMUTATION_LUNCH_BAG);
        ItemStackHandler items = new ItemStackHandler(16);
        items.setStackInSlot(0, new ItemStack(Items.APPLE, 2));
        TransmutationLunchBagItem.setItems(bag, items);
        player.setItemInHand(InteractionHand.MAIN_HAND, bag);
        helper.assertTrue(bag.isEdible() && player.canEat(bag.getItem().getFoodProperties().canAlwaysEat()),
                "AppleSkin's food gate must allow the lunch bag at full hunger");
        helper.assertTrue(bag.getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult().consumesAction(),
                "Lunch bag must remain usable at full hunger");
        bag.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(TransmutationLunchBagItem.getItems(bag).getStackInSlot(0).getCount() == 1,
                "Using the bag must consume one contained apple");
        helper.assertTrue(player.getFoodData().getFoodLevel() == 20 && player.getFoodData().getSaturationLevel() > 0,
                "Eating at full hunger must restore saturation without exceeding full hunger");
        helper.assertTrue(!TransmutationLunchBagItem.canAdd(bag), "Food metadata must not enable nesting lunch bags");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void lunchBagGrantsFoodEffectsEvenAtFullSaturation(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20);
        ItemStack bag = new ItemStack(ModItems.TRANSMUTATION_LUNCH_BAG);
        ItemStackHandler items = TransmutationLunchBagItem.getItems(bag);
        items.setStackInSlot(0, new ItemStack(Items.GOLDEN_APPLE, 2));
        items.setStackInSlot(1, new ItemStack(Items.APPLE, 3));
        TransmutationLunchBagItem.setItems(bag, items);
        player.setItemInHand(InteractionHand.MAIN_HAND, bag);

        for (int use = 0; use < 2; use++) {
            player.removeAllEffects();
            helper.assertTrue(bag.getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult().consumesAction(),
                    "Full hunger and saturation must not prevent repeated use");
            helper.assertTrue(bag.finishUsingItem(helper.getLevel(), player) == bag, "Eating must retain the lunch bag");
            player.stopUsingItem();
            items = TransmutationLunchBagItem.getItems(bag);
            helper.assertTrue(items.getStackInSlot(0).getCount() == 1 - use, "Each use must consume exactly one food");
            helper.assertTrue(items.getStackInSlot(1).getCount() == 3, "Full hunger must not consume food from later slots");
            helper.assertTrue(player.hasEffect(MobEffects.REGENERATION) && player.hasEffect(MobEffects.ABSORPTION),
                    "Contained food must grant its effects even at full hunger and saturation");
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void lunchBagStillFillsHungerAndStopsWhenFull(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        player.getFoodData().setFoodLevel(10);
        player.getFoodData().setSaturation(0);
        ItemStack bag = new ItemStack(ModItems.TRANSMUTATION_LUNCH_BAG);
        ItemStackHandler items = TransmutationLunchBagItem.getItems(bag);
        items.setStackInSlot(0, new ItemStack(Items.APPLE));
        items.setStackInSlot(1, new ItemStack(Items.APPLE, 4));
        TransmutationLunchBagItem.setItems(bag, items);

        bag.finishUsingItem(helper.getLevel(), player);
        items = TransmutationLunchBagItem.getItems(bag);
        helper.assertTrue(player.getFoodData().getFoodLevel() == 20, "The bag must keep eating until hunger is full");
        helper.assertTrue(items.getStackInSlot(0).isEmpty() && items.getStackInSlot(1).getCount() == 2,
                "Filling ten hunger points must consume three apples across slots and preserve the rest");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void lunchBagPotionBeforeFoodDoesNotPreventEatingAtFullHunger(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(0);
        ItemStack bag = new ItemStack(ModItems.TRANSMUTATION_LUNCH_BAG);
        ItemStackHandler items = TransmutationLunchBagItem.getItems(bag);
        items.setStackInSlot(0, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.SWIFTNESS));
        items.setStackInSlot(1, new ItemStack(Items.APPLE));
        TransmutationLunchBagItem.setItems(bag, items);
        player.setItemInHand(InteractionHand.MAIN_HAND, bag);

        bag.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(player.hasEffect(MobEffects.MOVEMENT_SPEED), "Potion effects must still apply");
        helper.assertTrue(player.getInventory().contains(new ItemStack(Items.GLASS_BOTTLE)), "Drinking must return the bottle");
        helper.assertTrue(player.getFoodData().getSaturationLevel() > 0, "A preceding potion must not skip the first food");
        helper.assertTrue(!TransmutationLunchBagItem.hasItems(bag), "Consuming the last potion and food must empty the bag");
        helper.assertTrue(!bag.getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult().consumesAction(),
                "An empty bag must not start another use");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void creativeWaterBucketFillsTeapotWithoutConsumption(GameTestHelper helper) {
        testCreativeBucket(helper, Items.WATER_BUCKET, InteractionHand.MAIN_HAND, 1);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void creativeOffhandWaterBucketsDoNotReturnEmptyBuckets(GameTestHelper helper) {
        testCreativeBucket(helper, Items.WATER_BUCKET, InteractionHand.OFF_HAND, 2);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void creativeMilkBucketFillsTeapotWithoutConsumption(GameTestHelper helper) {
        testCreativeBucket(helper, Items.MILK_BUCKET, InteractionHand.MAIN_HAND, 1);
    }

    private static void testCreativeBucket(GameTestHelper helper, Item item,
                                           InteractionHand hand, int count) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        Player player = helper.makeMockPlayer();
        player.getAbilities().instabuild = true;
        ItemStack bucket = new ItemStack(item, count);
        bucket.getOrCreateTag().putString("CreativeBucket", "preserved");
        ItemStack original = bucket.copy();
        player.setItemInHand(hand, bucket);
        ItemStack[] inventoryBefore = copyInventory(player);
        helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), player, player.getItemInHand(hand)),
                "Creative bucket must fill the teapot");
        helper.assertTrue(FluidUtils.findFirstAmount(teapot.getTeaTank()) == FluidConstants.BUCKET,
                "Creative filling must transfer exactly one bucket");
        helper.assertTrue(ItemStack.matches(original, player.getItemInHand(hand)),
                "Creative filling must preserve the held item, count and NBT");
        assertInventoryUnchanged(helper, player, inventoryBefore);
        assertNoDroppedBuckets(helper);
        helper.assertTrue(!teapot.addTeaFluid(helper.getLevel(), player, player.getItemInHand(hand)),
                "A full teapot must reject another bucket");
        helper.assertTrue(ItemStack.matches(original, player.getItemInHand(hand)),
                "Rejected filling must preserve the creative bucket");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void creativeTeapotInteractionDoesNotReturnOrDropBuckets(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        player.getAbilities().instabuild = true;
        BlockPos absolute = helper.absolutePos(POT);
        player.setPos(absolute.getX(), absolute.getY(), absolute.getZ());
        for (boolean fullInventory : new boolean[]{false, true}) {
            player.getInventory().clearContent();
            if (fullInventory) {
                for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
                    player.getInventory().setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
                }
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET, 2));
            ItemStack[] inventoryBefore = copyInventory(player);
            for (int attempt = 0; attempt < 2; attempt++) {
                helper.setBlock(POT, Blocks.AIR);
                TeapotBlockEntity teapot = placeTeapot(helper);
                helper.assertTrue(ModBlocks.TEAPOT.use(teapot.getBlockState(), helper.getLevel(), absolute,
                                player, InteractionHand.MAIN_HAND,
                                new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false)).consumesAction(),
                        "Creative bucket interaction must be handled");
                helper.assertTrue(FluidUtils.findFirstAmount(teapot.getTeaTank()) == FluidConstants.BUCKET,
                        "Creative interaction must fill the teapot");
                assertInventoryUnchanged(helper, player, inventoryBefore);
                assertNoDroppedBuckets(helper);
            }
        }
        helper.succeed();
    }

    private static ItemStack[] copyInventory(Player player) {
        ItemStack[] stacks = new ItemStack[player.getInventory().getContainerSize()];
        for (int slot = 0; slot < stacks.length; slot++) {
            stacks[slot] = player.getInventory().getItem(slot).copy();
        }
        return stacks;
    }

    private static void assertInventoryUnchanged(GameTestHelper helper, Player player, ItemStack[] before) {
        for (int slot = 0; slot < before.length; slot++) {
            helper.assertTrue(ItemStack.matches(before[slot], player.getInventory().getItem(slot)),
                    "Creative filling must preserve every inventory slot, including item NBT: " + slot);
        }
    }

    private static void assertNoDroppedBuckets(GameTestHelper helper) {
        var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(POT)).inflate(8));
        helper.assertTrue(drops.stream().noneMatch(entity -> entity.getItem().is(Items.BUCKET)),
                "Creative filling must not drop empty buckets into the world");
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void survivalOffhandWaterBucketIsConsumed(GameTestHelper helper) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        Player player = helper.makeMockSurvivalPlayer();
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.WATER_BUCKET));
        helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), player, player.getOffhandItem()),
                "Survival offhand bucket must fill the teapot");
        helper.assertTrue(player.getOffhandItem().is(Items.BUCKET) && player.getOffhandItem().getCount() == 1,
                "Survival filling must replace the water bucket with one empty bucket");
        helper.assertTrue(FluidUtils.findFirstAmount(teapot.getTeaTank()) == FluidConstants.BUCKET,
                "Survival filling must transfer exactly one bucket");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void failedBucketExtractionRollsBackTankInsertion(GameTestHelper helper) {
        for (boolean creative : new boolean[]{false, true}) {
            Player player = creative ? helper.makeMockPlayer() : helper.makeMockSurvivalPlayer();
            player.getAbilities().instabuild = creative;
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
            CustomFluidTank tank = new CustomFluidTank(FluidConstants.BUCKET / 2, null);
            helper.assertTrue(!FluidUtils.emptyItem(player, player.getMainHandItem(), tank, CustomFluidTank.MB_PER_BUCKET),
                    "A bucket must reject partial extraction even in creative mode");
            helper.assertTrue(tank.getAmount() == 0 && tank.isResourceBlank(),
                    "Failed extraction must roll back the tank insertion");
            helper.assertTrue(player.getMainHandItem().is(Items.WATER_BUCKET) && player.getMainHandItem().getCount() == 1,
                    "Failed extraction must preserve the water bucket");
            helper.assertTrue(!player.getInventory().contains(new ItemStack(Items.BUCKET)),
                    "Failed extraction must not produce an empty bucket");
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void milkBucketFillsAndDrainsTeapot(GameTestHelper helper) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        Player player = helper.makeMockSurvivalPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MILK_BUCKET));
        helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), player, player.getMainHandItem()), "Milk bucket must fill teapot");
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "Filling must return an empty bucket");
        helper.assertTrue(FluidUtils.findFirstAmount(teapot.getTeaTank()) == FluidConstants.BUCKET, "Must transfer exactly one bucket");
        helper.assertTrue(ModFluids.matchesTeaFluid(ModFluids.MILK_ID, teapot.getTeaFluidId()), "Stored fluid must match milk recipes");
        ResourceLocation storedMilk = teapot.getTeaFluidId();
        teapot.load(teapot.saveWithoutMetadata());
        helper.assertTrue(teapot.getTeaFluidId().equals(storedMilk), "Saving must preserve the actual milk fluid ID");
        helper.assertTrue(teapot.removeTeaFluid(helper.getLevel(), player, player.getMainHandItem()), "Milk must be recoverable");
        helper.assertTrue(player.getMainHandItem().is(Items.MILK_BUCKET), "Draining must return a milk bucket");
        helper.assertTrue(FluidUtils.findFirstAmount(teapot.getTeaTank()) == 0, "Draining must empty the teapot");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void registeredMilkSupportsTransactionsAndSoupBase(GameTestHelper helper) {
        Fluid milk = BuiltInRegistries.FLUID.get(ModFluids.MILK_ID);
        helper.assertTrue(milk != Fluids.EMPTY && milk.getBucket() == Items.MILK_BUCKET, "minecraft:milk must be registered");
        TeapotBlockEntity teapot = placeTeapot(helper);
        Player player = helper.makeMockSurvivalPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertTrue(teapot.getTeaTank().insert(FluidVariant.of(milk), FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET,
                    "Registered milk must enter the tank");
            transaction.commit();
        }
        var bucket = ContainerItemContext.ofPlayerHand(player, InteractionHand.MAIN_HAND).find(FluidStorage.ITEM);
        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertTrue(bucket.insert(FluidVariant.of(milk), FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET,
                    "Empty buckets must accept registered milk");
        }
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET), "Aborted filling must preserve empty bucket");
        helper.assertTrue(teapot.removeTeaFluid(helper.getLevel(), player, player.getMainHandItem()), "Registered milk must drain into a bucket");
        helper.assertTrue(player.getMainHandItem().is(Items.MILK_BUCKET), "Must produce a vanilla milk bucket");
        var soup = SoupBaseManager.getSoupBase(ModSoupBases.MILK);
        helper.assertTrue(soup instanceof FluidSoupBase && ((FluidSoupBase) soup).getFluid() == milk, "Milk soup base must use registered fluid");
        helper.assertTrue(soup.getReturnContainer(helper.getLevel(), player, player.getMainHandItem()).is(Items.BUCKET), "Soup must return an empty bucket");
        helper.assertTrue(soup.getReturnSoupBase(helper.getLevel(), player, new ItemStack(Items.BUCKET)).is(Items.MILK_BUCKET), "Soup must return milk");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void butterTeaRecipeAcceptsMilkProvidersOnly(GameTestHelper helper) {
        var recipe = (TeapotRecipe) helper.getLevel().getRecipeManager()
                .byKey(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "teapot/butter_tea_1")).orElseThrow();
        for (Fluid fluid : BuiltInRegistries.FLUID) {
            if (fluid.getBucket() == Items.MILK_BUCKET) {
                helper.assertTrue(recipe.matches(new TeapotContainer(new ItemStack(ModItems.BUTTER_TEA_BAG),
                        BuiltInRegistries.FLUID.getKey(fluid)), helper.getLevel()), "Milk provider must match butter tea: " + BuiltInRegistries.FLUID.getKey(fluid));
            }
        }
        helper.assertTrue(!recipe.matches(new TeapotContainer(new ItemStack(ModItems.BUTTER_TEA_BAG), new ResourceLocation("water")), helper.getLevel()),
                "Water must not brew butter tea");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void milkSoupAllowsIngredientRetrievalWithoutDamage(GameTestHelper helper) {
        helper.setBlock(POT.below(), Blocks.STONE);
        helper.setBlock(POT, ModBlocks.STOCKPOT);
        StockpotBlockEntity stockpot = (StockpotBlockEntity) helper.getBlockEntity(POT);
        Player player = helper.makeMockSurvivalPlayer();
        helper.assertTrue(stockpot.addSoupBase(helper.getLevel(), player, new ItemStack(Items.MILK_BUCKET)), "Milk soup base must be accepted");
        helper.assertTrue(stockpot.addIngredient(helper.getLevel(), player, new ItemStack(Items.APPLE)), "Ingredient must enter milk soup");
        float health = player.getHealth();
        helper.assertTrue(stockpot.removeIngredient(helper.getLevel(), player), "Ingredient must be recoverable from milk soup");
        helper.assertTrue(player.getHealth() == health, "Milk soup must not burn the player");
        helper.assertTrue(stockpot.removeSoupBase(helper.getLevel(), player, new ItemStack(Items.BUCKET)), "Milk soup must remain recoverable");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 600)
    public void milkBucketBrewsTwelveButterTeas(GameTestHelper helper) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        helper.setBlock(POT.below(), Blocks.MAGMA_BLOCK);
        Player player = helper.makeMockSurvivalPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MILK_BUCKET));
        helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), player, player.getMainHandItem()), "Milk must fill the teapot");
        ItemStack ingredient = new ItemStack(ModItems.BUTTER_TEA_BAG);
        helper.assertTrue(teapot.addIngredient(helper.getLevel(), player, ingredient), "Butter tea bag must be accepted");
        helper.assertTrue(ingredient.isEmpty(), "Exactly one tea bag must be consumed");
        helper.assertTrue(!teapot.removeTeaFluid(helper.getLevel(), player, player.getMainHandItem()), "Milk cannot be removed after adding ingredients");
        helper.succeedWhen(() -> {
            helper.assertTrue(teapot.getResult().is(TeacupRegistry.getItem(TeacupRegistry.BUTTER_TEA))
                    && teapot.getResult().getCount() == 12, "Milk and one butter tea bag must brew twelve butter teas");
        });
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 100)
    public void dripstoneFillsWater(GameTestHelper helper) {
        testDripstone(helper, Fluids.WATER);
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 100)
    public void dripstoneFillsLava(GameTestHelper helper) {
        testDripstone(helper, Fluids.LAVA);
    }

    private static void testDripstone(GameTestHelper helper, Fluid fluid) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        helper.setBlock(POT.above(2), Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP));
        helper.setBlock(POT.above(3), Blocks.DRIPSTONE_BLOCK);
        helper.setBlock(POT.above(4), fluid.defaultFluidState().createLegacyBlock());
        BlockPos absolute = helper.absolutePos(POT);
        ModBlocks.TEAPOT.randomTick(teapot.getBlockState(), helper.getLevel(), absolute, helper.getLevel().random);
        helper.assertTrue(teapot.canReceiveDripstoneFluid(), "Drip must be delayed");
        helper.runAfterDelay(60, () -> {
            helper.assertTrue(teapot.getTeaTank().iterator().next().getAmount() == FluidConstants.BUCKET, "Drip must fill one bucket");
            helper.assertTrue(teapot.getTeaFluidId().equals(new ResourceLocation(fluid == Fluids.WATER ? "water" : "lava")), "Wrong fluid");
            helper.assertTrue(!teapot.receiveDripstoneFluid(fluid == Fluids.WATER ? Fluids.LAVA : Fluids.WATER), "Must not overwrite a filled teapot");
            helper.succeed();
        });
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 100)
    public void obstructedDripstoneDoesNotFill(GameTestHelper helper) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        helper.setBlock(POT.above(2), Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP));
        helper.setBlock(POT.above(3), Blocks.DRIPSTONE_BLOCK);
        helper.setBlock(POT.above(4), Blocks.WATER);
        helper.setBlock(POT.above(), Blocks.STONE);
        ModBlocks.TEAPOT.randomTick(teapot.getBlockState(), helper.getLevel(), helper.absolutePos(POT), helper.getLevel().random);
        helper.runAfterDelay(60, () -> {
            helper.assertTrue(teapot.canReceiveDripstoneFluid(), "Solid blocks must stop drips");
            helper.succeed();
        });
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 60)
    public void hopperInsertsExactlyOneIngredient(GameTestHelper helper) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        teapot.receiveDripstoneFluid(Fluids.WATER);
        helper.setBlock(POT.above(), Blocks.HOPPER);
        HopperBlockEntity hopper = (HopperBlockEntity) helper.getBlockEntity(POT.above());
        hopper.setItem(0, new ItemStack(ModItems.BARLEY_TEA_BAG, 64));
        helper.runAfterDelay(20, () -> {
            helper.assertTrue(teapot.getInput().is(ModItems.BARLEY_TEA_BAG) && teapot.getInput().getCount() == 1, "Hopper must insert one tea bag");
            helper.assertTrue(hopper.getItem(0).getCount() == 63, "Hopper must retain the other 63 bags");
            helper.assertTrue(teapot.getCurrentTick() == TeapotBlockEntity.INGREDIENT_TIME, "Cold teapot must retain ingredient grace period");
            helper.succeed();
        });
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 100)
    public void teaBagBrewsTwelveCups(GameTestHelper helper) {
        brew(helper, ModItems.BARLEY_TEA_BAG.getDefaultInstance(), TeacupRegistry.BARLEY_TEA, 12);
    }

    @GameTest(template = EMPTY_STRUCTURE, timeoutTicks = 100)
    public void invalidIngredientBrewsFourMysteryCups(GameTestHelper helper) {
        brew(helper, new ItemStack(Items.COBBLESTONE), TeacupRegistry.MYSTERY_TEA, 4);
    }

    private static void brew(GameTestHelper helper, ItemStack input, ResourceLocation result, int count) {
        TeapotBlockEntity teapot = placeTeapot(helper);
        helper.setBlock(POT.below(), Blocks.MAGMA_BLOCK);
        teapot.receiveDripstoneFluid(Fluids.WATER);
        teapot.insertIngredient(input);
        CompoundTag tag = teapot.saveWithoutMetadata();
        tag.putInt("CurrentTick", 0);
        teapot.load(tag);
        helper.succeedWhen(() -> {
            if (teapot.getStatus() == ITeapot.PROCESSING) {
                CompoundTag processing = teapot.saveWithoutMetadata();
                processing.putInt("CurrentTick", 0);
                teapot.load(processing);
                try (Transaction transaction = Transaction.openOuter()) {
                    var storage = FluidStorage.SIDED.find(helper.getLevel(), helper.absolutePos(POT), Direction.DOWN);
                    helper.assertTrue(storage.extract(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, transaction) == 0, "Processing fluid must not be extracted");
                }
            }
            helper.assertTrue(teapot.getStatus() == ITeapot.FINISHED, "Tea has not finished");
            helper.assertTrue(teapot.getResult().is(TeacupRegistry.getItem(result)) && teapot.getResult().getCount() == count, "Incorrect tea result");
            ItemStack dropped = teapot.getDrops().get(0);
            helper.assertTrue(TeapotItem.getPourOut(dropped).getCount() == count, "Picked-up teapot lost its result");
            for (int i = 0; i < count; i++) TeapotItem.pourOut(dropped);
            helper.assertTrue(BlockItem.getBlockEntityData(dropped) == null, "Empty teapot must reset its data");
        });
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dispenserPlacesFilledTeapot(GameTestHelper helper) {
        helper.setBlock(POT.below(), Blocks.STONE);
        helper.setBlock(POT.west(), Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.EAST));
        ItemStack stack = ModItems.TEAPOT.getDefaultInstance();
        TeapotItem.fillFluid(stack, Fluids.WATER, helper.makeMockPlayer());
        new TeapotDispenseBehavior().dispense(new BlockSourceImpl(helper.getLevel(), helper.absolutePos(POT.west())), stack);
        helper.assertTrue(stack.isEmpty(), "Dispenser must consume the placed teapot");
        TeapotBlockEntity placed = (TeapotBlockEntity) helper.getBlockEntity(POT);
        helper.assertTrue(placed.getTeaFluidId().equals(new ResourceLocation("water")), "Placement must preserve water NBT");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void lastTeaLeavesUsableEmptyCups(GameTestHelper helper) {
        TeacupBlock tea = (TeacupBlock) TeacupRegistry.getBlock(TeacupRegistry.BARLEY_TEA);
        BlockState state = tea.defaultBlockState().setValue(tea.getCupCountProperty(), 3)
                .setValue(tea.getTeaCountProperty(), 1).setValue(TeacupBlock.WATERLOGGED, true);
        helper.setBlock(POT, state);
        Player player = helper.makeMockSurvivalPlayer();
        BlockPos absolute = helper.absolutePos(POT);
        tea.use(state, helper.getLevel(), absolute, player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false));
        BlockState remaining = helper.getBlockState(POT);
        helper.assertTrue(remaining.is(ModBlocks.EMPTY_CUP) && remaining.getValue(EmptyCupBlock.CUP_COUNT) == 2, "Last tea must leave two empty cups");
        helper.assertTrue(remaining.getValue(EmptyCupBlock.WATERLOGGED), "Cup conversion must preserve waterlogging");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void clayPotTeaRemovesOnlyHarmfulEffects(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200));
        ItemStack result = ModItems.CLAY_POT_MILK_TEA.finishUsingItem(new ItemStack(ModItems.CLAY_POT_MILK_TEA), helper.getLevel(), player);
        helper.assertTrue(!player.hasEffect(MobEffects.POISON), "Milk tea must remove harmful effects");
        helper.assertTrue(player.hasEffect(MobEffects.MOVEMENT_SPEED), "Milk tea must retain beneficial effects");
        helper.assertTrue(result.is(Items.FLOWER_POT), "Milk tea must return a flower pot");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void tablePlacementAndBreakingEveryPart(GameTestHelper helper) {
        BlockPos anchor = helper.absolutePos(new BlockPos(3, 1, 3));
        for (Block table : new Block[]{ModBlocks.EIGHT_IMMORTALS_TABLE}) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                for (EightImmortalsTableBlock.Part broken : EightImmortalsTableBlock.Part.values()) {
                    ItemStack item = new ItemStack(table);
                    DirectionalPlaceContext context = new DirectionalPlaceContext(helper.getLevel(), anchor, facing, item, Direction.UP);
                    helper.assertTrue(((BlockItem) item.getItem()).place(context).consumesAction(), "Table placement failed");
                    Direction actualFacing = helper.getLevel().getBlockState(anchor).getValue(EightImmortalsTableBlock.FACING);
                    BlockPos breakPos = anchor;
                    for (EightImmortalsTableBlock.Part part : EightImmortalsTableBlock.Part.values()) {
                        BlockPos pos = tablePart(anchor, actualFacing, part);
                        BlockState state = helper.getLevel().getBlockState(pos);
                        helper.assertTrue(state.is(table) && state.getValue(EightImmortalsTableBlock.PART) == part, "Missing table part");
                        if (part == broken) breakPos = pos;
                    }
                    helper.getLevel().destroyBlock(breakPos, true);
                    for (EightImmortalsTableBlock.Part part : EightImmortalsTableBlock.Part.values()) {
                        helper.assertTrue(helper.getLevel().getBlockState(tablePart(anchor, actualFacing, part)).isAir(), "Breaking one part must remove the whole table");
                    }
                    var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(anchor).inflate(3));
                    int count = drops.stream().filter(entity -> entity.getItem().is(table.asItem())).mapToInt(entity -> entity.getItem().getCount()).sum();
                    helper.assertTrue(count == 1, "Each table must drop exactly one item");
                    drops.forEach(ItemEntity::discard);
                }
            }
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void obstructedTablePlacementDoesNotConsumeItem(GameTestHelper helper) {
        BlockPos anchor = helper.absolutePos(POT);
        for (Direction side : Direction.Plane.HORIZONTAL) {
            helper.getLevel().setBlockAndUpdate(anchor.relative(side), Blocks.STONE.defaultBlockState());
        }
        ItemStack item = new ItemStack(ModItems.EIGHT_IMMORTALS_TABLE);
        var context = new DirectionalPlaceContext(helper.getLevel(), anchor, Direction.NORTH, item, Direction.UP);
        helper.assertTrue(!((BlockItem) item.getItem()).place(context).consumesAction(), "Obstructed table must not place");
        helper.assertTrue(item.getCount() == 1 && helper.getLevel().getBlockState(anchor).isAir(), "Failed placement must leave no partial table or consume items");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void creativeMilkTeaKeepsTheItem(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        ItemStack input = new ItemStack(ModItems.CLAY_POT_MILK_TEA);
        ItemStack result = ModItems.CLAY_POT_MILK_TEA.finishUsingItem(input, helper.getLevel(), player);
        helper.assertTrue(!player.hasEffect(MobEffects.POISON), "Creative players must also lose harmful effects");
        helper.assertTrue(result == input && result.getCount() == 1, "Creative drinking must not consume the item");
        helper.assertTrue(!player.getInventory().contains(new ItemStack(Items.FLOWER_POT)), "Creative drinking must not create containers");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void newRecipesAndEffectsAreRegistered(GameTestHelper helper) {
        for (String tea : new String[]{"barley_tea", "biluochun", "butter_tea_1", "flower_tea", "oolong", "sakura_fubuki", "tieguanyin"}) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "teapot/" + tea)).isPresent(), "Missing tea recipe: " + tea);
        }
        for (int i = 1; i <= 9; i++) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "stockpot/clay_pot_milk_tea_count_" + i)).isPresent(), "Missing clay pot milk tea recipe");
        }
        for (int i = 1; i <= 4; i++) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "stockpot/tea_egg_count_" + i)).isPresent(), "Missing tea egg recipe");
        }
        Player player = helper.makeMockSurvivalPlayer();
        ModItems.TEA_EGG.finishUsingItem(new ItemStack(ModItems.TEA_EGG), helper.getLevel(), player);
        helper.assertTrue(player.hasEffect(ModEffects.SULFUR.get()), "Tea eggs must grant Sulfur");
        helper.assertTrue(player.getEffect(ModEffects.SULFUR.get()).getDuration() == 1200, "Tea egg effect must last one minute");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void milkFluidUsesModIdAndCommonTag(GameTestHelper helper) {
        Fluid milk = BuiltInRegistries.FLUID.get(ModFluids.MILK_ID);
        helper.assertTrue(milk != Fluids.EMPTY, "Mod milk fluid is not registered");
        helper.assertTrue(ModFluids.MILK_ID.getNamespace().equals(KaleidoscopeCookery.MOD_ID),
                "Milk fluid must use the mod namespace");
        helper.assertTrue(milk.is(TagKey.create(Registries.FLUID, new ResourceLocation("c", "milk"))),
                "Milk fluid must be in the common milk tag");
        helper.succeed();
    }

    private static BlockPos tablePart(BlockPos anchor, Direction facing, EightImmortalsTableBlock.Part part) {
        return switch (part) {
            case RIGHT_BOTTOM -> anchor;
            case LEFT_BOTTOM -> anchor.relative(facing.getCounterClockWise());
            case RIGHT_TOP -> anchor.relative(facing);
            case LEFT_TOP -> anchor.relative(facing).relative(facing.getCounterClockWise());
        };
    }

    private static TeapotBlockEntity placeTeapot(GameTestHelper helper) {
        helper.setBlock(POT.below(), Blocks.STONE);
        helper.setBlock(POT, ModBlocks.TEAPOT);
        return (TeapotBlockEntity) helper.getBlockEntity(POT);
    }
}
