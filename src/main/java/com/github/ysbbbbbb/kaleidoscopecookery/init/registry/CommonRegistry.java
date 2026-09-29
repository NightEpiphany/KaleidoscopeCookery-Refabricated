package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.BambooTrayDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.ChoppingBoardDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.OilPotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.TeapotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.create.automation.init.AutomationCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight.FarmersDelightCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.init.TrinketsCompatServer;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.resources.MillstoneBindableDataReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.event.*;
import com.github.ysbbbbbb.kaleidoscopecookery.event.effect.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModVillager;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.PlateBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public final class CommonRegistry {

    public static void init() {
        registerDataListeners();
        modCompat();
        addComposter();
        registerPlateBlocks();
        registerTeacupBlocks();
        registerFoodBiteBlocks();
        registerServerEvents();
        addVillagerGift();
        addDispenserBehavior();
        fuelRegister();
        storageRegister();
    }

    public static void storageRegister() {
        ItemStorage.SIDED.registerForBlockEntity(BambooTrayBlockEntity::getStorage, ModBlocks.BAMBOO_TRAY_BE);
        ItemStorage.SIDED.registerForBlockEntity((teapot, _) -> teapot.getIngredientStorage(), ModBlocks.TEAPOT_BE);
    }

    public static void registerDataListeners() {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone_bindable_data"), new MillstoneBindableDataReloadListener());
    }

    private static void addVillagerGift() {
        GiveGiftToHero.GIFTS.put(ModVillager.CHEF, ModVillager.CHEF_GIFT_LOOT_KEY);
    }

    public static void fuelRegister() {
        FuelValueEvents.BUILD.register((registry, i) -> {
            registry.add(ModItems.OIL, i.baseSmeltTime() * 8);
            registry.add(ModItems.OIL_BLOCK, i.baseSmeltTime() * 72);
        });
    }

    public static void registerServerEvents() {
        MillstoneEntityEvent.register();
        PlaceIngredientEvent.register();
        SatiatedShieldEvent.register();
        FlatulenceServerEvent.register();
        PreservationEvent.register();
        HinderEvent.register();
        ProjectileDodgeEvent.register();
        VitalityEvent.register();
        ArmorEffectHandler.register();
        ChangeTargetEvent.register();
        AddVillageStructuresEvent.register();
        ScarecrowFarmlandTrampleEvent.register();
        EntityJoinWorldEvent.register();
        SickleHarvestNetherWartEvent.register();
        HoeUseEvent.register();
        RightClickEvent.register();
        LeftClickEvent.register();
        ExtraLootTableDrop.register();
        PlayerSitEvent.register();
    }

    private static void registerPlateBlocks() {
        PlateRegistry.init();

        PlateRegistry.PLATE_DATA_MAP.forEach((resourceLocation, data) -> {
            PlateBlock plateBlock = new PlateBlock(data.getMaxCount(), data.getServingItems(),
                    BlockBehaviour.Properties
                            .of()
                            .setId(PortHelper.createBlockId(resourceLocation.getPath())));
            VoxelShape aabb = data.getAABB();
            if (aabb != null) {
                plateBlock.setAABB(aabb);
            }
            Registry.register(BuiltInRegistries.BLOCK, resourceLocation, plateBlock);

            Block block = BuiltInRegistries.BLOCK.getValue(resourceLocation);
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, new PlateBlockItem(block, resourceLocation.getPath()));
        });
    }

    private static void registerTeacupBlocks() {
        TeacupRegistry.init();

        TeacupRegistry.TEACUP_DATA_MAP.forEach((resourceLocation, data) -> {
            TeacupBlock teacupBlock = new TeacupBlock(
                    BlockBehaviour.Properties
                            .of()
                            .setId(PortHelper.createBlockId(resourceLocation.getPath())),
                    data.getMaxCount(),
                    data.getAnimateTick());
            VoxelShape aabb = data.getAABB();
            if (aabb != null) {
                teacupBlock.setAABB(aabb);
            }
            Registry.register(BuiltInRegistries.BLOCK, resourceLocation, teacupBlock);

            Block block = BuiltInRegistries.BLOCK.getValue(resourceLocation);
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, new TeacupItem(block, data.getEffects(), new Item.Properties().setId(PortHelper.createItemId(resourceLocation.getPath()))));
        });
    }

    private static void registerFoodBiteBlocks() {
        FoodBiteRegistry.init();

        FoodBiteRegistry.FOOD_DATA_MAP.forEach((resourceLocation, data) -> {
                FoodBiteBlock biteBlock = getFoodBiteBlock(data, resourceLocation.getPath());
                Registry.register(BuiltInRegistries.BLOCK, resourceLocation, biteBlock);

                Block block = BuiltInRegistries.BLOCK.getValue(resourceLocation);
                // 选取第一个掉落物作为 usingConvertsTo
                ItemLike first = data.getLootItems().getFirst();
            BowlFoodBlockItem register = Registry.register(BuiltInRegistries.ITEM, resourceLocation, new BowlFoodBlockItem(block, data.itemFood(), data.itemConsumable(), first, resourceLocation.getPath()));
            FoodBiteRegistry.FOOD_ITEM_MAP.put(resourceLocation, register);
        });
    }

    private static @NotNull FoodBiteBlock getFoodBiteBlock(FoodBiteRegistry.FoodData data, String name) {
        FoodBiteBlock biteBlock;
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion();

        if (data.blockType() == FoodBiteRegistry.BlockType.ONE_BY_TWO) {
            biteBlock = new FoodBiteOneByTwoBlock(properties.setId(PortHelper.createBlockId(name)), data.blockFood(), data.blockConsumable(), data.maxBites(), data.animateTick());
        } else {
            biteBlock = new FoodBiteBlock(properties.setId(PortHelper.createBlockId(name)), data.blockFood(), data.blockConsumable(), data.maxBites(), data.animateTick());
        }

        VoxelShape aabb = data.getAABB();
        if (aabb != null) {
            biteBlock.setAABB(aabb);
        }
        return biteBlock;
    }

    private static void addComposter() {
        CompostableRegistry.INSTANCE.add(ModItems.TEA_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.FRESH_TEA_LEAVES, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.DRIED_TEA_LEAVES, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.TOMATO_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.CHILI_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.LETTUCE_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.WILD_RICE_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.RICE_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.TOMATO, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.RED_CHILI, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.GREEN_CHILI, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.LETTUCE, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.RICE_PANICLE, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.CATERPILLAR, 1.0F);
    }

    private static void modCompat() {
        TrinketsCompatServer.init();
        FarmersDelightCompat.init();
        AutomationCompat.init();
    }

    private static void addDispenserBehavior() {
        DispenserBlock.registerBehavior(ModItems.OIL_POT, new OilPotDispenseBehavior());
        DispenserBlock.registerBehavior(ModItems.BAMBOO_TRAY, new BambooTrayDispenseBehavior());
        DispenserBlock.registerBehavior(ModItems.TEAPOT, new TeapotDispenseBehavior());
        ChoppingBoardDispenseBehavior.register();
    }
}
