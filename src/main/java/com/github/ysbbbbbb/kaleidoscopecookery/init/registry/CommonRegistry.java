package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.LivingChangeTargetEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.RecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.BambooTrayDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.ChoppingBoardDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.OilPotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.TeapotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.create.contraption.init.CreateCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight.FarmersDelightCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.harvest.HarvestCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.init.TrinketsCompatServer;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable.GiftLootTables;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.resources.MillstoneBindableDataReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.event.*;
import com.github.ysbbbbbb.kaleidoscopecookery.event.effect.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModVillager;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class CommonRegistry {
    public static void init() {
        registerDataListeners();
        registerFluidStorage();
        ItemStorage.SIDED.registerForBlockEntity((teapot, side) -> teapot.getInputStorage(), ModBlocks.TEAPOT_BE);
        modCompat();
        addComposter();
        registerPlateBlocks();
        registerTeacupBlocks();
        registerFoodBiteBlocks();
        registerServerEvents();
        addVillagerGift();
        addDispenserBehavior();
        fuelRegister();
    }

    public static void registerDataListeners() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new MillstoneBindableDataReloadListener());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void registerFluidStorage() {
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getTeaTank(), ModBlocks.TEAPOT_BE);
    }

    public static void registerServerEvents() {
        SatiatedShieldEvent.register();
        PreservationEvent.register();
        VitalityEvent.register();
        HinderEvent.register();
        ProjectileDodgeEvent.register();
        EntityJoinWorldEvent.register();
        HoeUseEvent.register();
        RightClickEvent.register();
        LeftClickEvent.register();
        MillstoneEntityEvent.register();
        ExtraLootTableDrop.register();
        ArmorEffectEvent.register();
        RecipeItemEvent.register();
        SickleHarvestEvent.register();
        LivingChangeTargetEvent.register();
        AddVillageStructuresEvent.addVillageStructures();
    }

    public static void fuelRegister() {
        FuelRegistry.INSTANCE.add(ModItems.OIL, 1600);
        FuelRegistry.INSTANCE.add(ModItems.OIL_BLOCK, 14400);
    }

    private static void registerPlateBlocks() {
        PlateRegistry.init();

        PlateRegistry.PLATE_DATA_MAP.forEach((resourceLocation, data) -> {
            PlateBlock plateBlock = new PlateBlock(data.getMaxCount(), data.getServingItems());
            VoxelShape aabb = data.getAABB();
            if (aabb != null) {
                plateBlock.setAABB(aabb);
            }
            Registry.register(BuiltInRegistries.BLOCK, resourceLocation, plateBlock);

            Block block = BuiltInRegistries.BLOCK.get(resourceLocation);
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, new PlateBlockItem(block, resourceLocation.getPath()));
        });
    }

    private static void registerTeacupBlocks() {
        TeacupRegistry.init();

        TeacupRegistry.TEACUP_DATA_MAP.forEach((resourceLocation, data) -> {
            TeacupBlock teacupBlock = new TeacupBlock(data.getMaxCount(), data.getAnimateTick());
            VoxelShape aabb = data.getAABB();
            if (aabb != null) {
                teacupBlock.setAABB(aabb);
            }
            Registry.register(BuiltInRegistries.BLOCK, resourceLocation, teacupBlock);

            Block block = BuiltInRegistries.BLOCK.get(resourceLocation);
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, new TeacupItem(block, data.getEffects()));
        });
    }

    private static void registerFoodBiteBlocks() {
        FoodBiteRegistry.init();

        FoodBiteRegistry.FOOD_DATA_MAP.forEach((resourceLocation, data) -> {
            FoodBiteBlock biteBlock = getFoodBiteBlock(data);
            Registry.register(BuiltInRegistries.BLOCK, resourceLocation, biteBlock);

            Block block = BuiltInRegistries.BLOCK.get(resourceLocation);
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, new BowlFoodBlockItem(block, data.itemFood()));
        });
    }

    private static @NotNull FoodBiteBlock getFoodBiteBlock(FoodBiteRegistry.FoodData data) {
        FoodBiteBlock biteBlock;
        if (data.blockType() == FoodBiteRegistry.BlockType.ONE_BY_TWO) {
            biteBlock = new FoodBiteOneByTwoBlock(data.blockFood(), data.maxBites(), data.animateTick());
        } else {
            biteBlock = new FoodBiteBlock(data.blockFood(), data.maxBites(), data.animateTick());
        }

        VoxelShape aabb = data.getAABB();
        if (aabb != null) {
            biteBlock.setAABB(aabb);
        }
        return biteBlock;
    }

    private static void modCompat() {
        FarmersDelightCompat.init();
        HarvestCompat.init();
        CreateCompat.init();
        TrinketsCompatServer.init();
    }

    private static void addVillagerGift() {
        GiveGiftToHero.GIFTS.put(ModVillager.CHEF, GiftLootTables.CHEF_GIFT);
    }

    private static void addComposter() {
        CompostingChanceRegistry.INSTANCE.add(ModItems.FRESH_TEA_LEAVES, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.TEA_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.TOMATO_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.CHILI_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.LETTUCE_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.WILD_RICE_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RICE_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.TOMATO, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RED_CHILI, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.GREEN_CHILI, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.LETTUCE, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RICE_PANICLE, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.CATERPILLAR, 1.0F);
    }

    private static void addDispenserBehavior() {
        DispenserBlock.registerBehavior(ModItems.OIL_POT, new OilPotDispenseBehavior());
        DispenserBlock.registerBehavior(ModItems.BAMBOO_TRAY, new BambooTrayDispenseBehavior());
        DispenserBlock.registerBehavior(ModItems.TEAPOT, new TeapotDispenseBehavior());
        ChoppingBoardDispenseBehavior.register();
    }
}
