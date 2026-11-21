package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.ChiliCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.LettuceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.TeaTreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.ClayPotMilkTeaBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.EmptyCupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.*;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.*;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.*;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.Shapes;

public final class ModBlocks {
    // Kitchen blocks
    public static final Block STOVE = new StoveBlock();
    public static final Block POT = new PotBlock();
    public static final Block STOCKPOT = new StockpotBlock();
    public static final Block FRUIT_BASKET = new FruitBasketBlock();
    public static final Block CHOPPING_BOARD = new ChoppingBoardBlock();
    public static final Block OIL_BLOCK = new OilBlock();
    public static final Block ENAMEL_BASIN = new EnamelBasinBlock();
    public static final Block KITCHENWARE_RACKS = new KitchenwareRacksBlock();
    public static final Block KITCHEN_SHELF = new KitchenShelfBlock();
    public static final Block CHILI_RISTRA = new ChiliRistraBlock();
    public static final Block STRUNG_MUSHROOMS  = new StrungMushroomsBlock();
    public static final Block STRAW_BLOCK = new StrawBlocks();
    public static final Block SHAWARMA_SPIT = new ShawarmaSpitBlock();
    public static final Block MILLSTONE = new MillstoneBlock();
    public static final Block STEAMER = new SteamerBlock();
    public static final Block RECIPE_BLOCK = new RecipeBlock();
    public static final Block OIL_POT = new OilPotBlock();

    // Tea
    public static final Block TEAPOT = new TeapotBlock();
    public static final Block EMPTY_CUP = new EmptyCupBlock();
    public static final Block CLAY_POT_MILK_TEA = new ClayPotMilkTeaBlock();
    public static final Block BAMBOO_TRAY = new BambooTrayBlock();
    public static final Block TEA_BANNER = new TeaBannerBlock();
    public static final Block LONG_BENCH = new LongBenchBlock();
    public static final Block RED_LANTERN = new RedLanternBlock();
    public static final Block EIGHT_IMMORTALS_TABLE = new EightImmortalsTableBlock();
    public static final Block TEA_TREE = new TeaTreeBlock();


    public static final Block TRASH_CAN = new TrashCanBlock();

    // Crop blocks
    public static final Block TOMATO_CROP = new BaseCropBlock(() -> ModItems.TOMATO, () -> ModItems.TOMATO_SEED);
    public static final Block CHILI_CROP = new ChiliCropBlock();
    public static final Block LETTUCE_CROP = new LettuceCropBlock();
    public static final Block RICE_CROP = new RiceCropBlock();

    // Cook stools
    public static final Block COOK_STOOL_OAK = new CookStoolBlock(SoundType.WOOD);
    public static final Block COOK_STOOL_SPRUCE = new CookStoolBlock(SoundType.WOOD);
    public static final Block COOK_STOOL_ACACIA = new CookStoolBlock(SoundType.WOOD);
    public static final Block COOK_STOOL_BAMBOO = new CookStoolBlock(SoundType.BAMBOO);
    public static final Block COOK_STOOL_BIRCH = new CookStoolBlock(SoundType.WOOD);
    public static final Block COOK_STOOL_CHERRY = new CookStoolBlock(SoundType.CHERRY_WOOD);
    public static final Block COOK_STOOL_CRIMSON = new CookStoolBlock(SoundType.NETHER_WOOD);
    public static final Block COOK_STOOL_DARK_OAK = new CookStoolBlock(SoundType.WOOD);
    public static final Block COOK_STOOL_JUNGLE = new CookStoolBlock(SoundType.WOOD);
    public static final Block COOK_STOOL_MANGROVE = new CookStoolBlock(SoundType.WOOD);
    public static final Block COOK_STOOL_WARPED = new CookStoolBlock(SoundType.NETHER_WOOD);

    // Chairs
    public static final Block CHAIR_OAK = new ChairBlock(SoundType.WOOD);
    public static final Block CHAIR_SPRUCE = new ChairBlock(SoundType.WOOD);
    public static final Block CHAIR_ACACIA = new ChairBlock(SoundType.WOOD);
    public static final Block CHAIR_BAMBOO = new ChairBlock(SoundType.BAMBOO);
    public static final Block CHAIR_BIRCH = new ChairBlock(SoundType.WOOD);
    public static final Block CHAIR_CHERRY = new ChairBlock(SoundType.CHERRY_WOOD);
    public static final Block CHAIR_CRIMSON = new ChairBlock(SoundType.NETHER_WOOD);
    public static final Block CHAIR_DARK_OAK = new ChairBlock(SoundType.WOOD);
    public static final Block CHAIR_JUNGLE = new ChairBlock(SoundType.WOOD);
    public static final Block CHAIR_MANGROVE = new ChairBlock(SoundType.WOOD);
    public static final Block CHAIR_WARPED = new ChairBlock(SoundType.NETHER_WOOD);

    // Tables
    public static final Block TABLE_OAK = new TableBlock(SoundType.WOOD);
    public static final Block TABLE_SPRUCE = new TableBlock(SoundType.WOOD);
    public static final Block TABLE_ACACIA = new TableBlock(SoundType.WOOD);
    public static final Block TABLE_BAMBOO = new TableBlock(SoundType.BAMBOO);
    public static final Block TABLE_BIRCH = new TableBlock(SoundType.WOOD);
    public static final Block TABLE_CHERRY = new TableBlock(SoundType.CHERRY_WOOD);
    public static final Block TABLE_CRIMSON = new TableBlock(SoundType.NETHER_WOOD);
    public static final Block TABLE_DARK_OAK = new TableBlock(SoundType.WOOD);
    public static final Block TABLE_JUNGLE = new TableBlock(SoundType.WOOD);
    public static final Block TABLE_MANGROVE = new TableBlock(SoundType.WOOD);
    public static final Block TABLE_WARPED = new TableBlock(SoundType.NETHER_WOOD);

    //Feast
    public static final Block COLD_CUT_HAM_SLICES = new FoodBiteThreeByThreeBlock(ModFoods.COLD_CUT_HAM_SLICES_BLOCK, 8, null);

    public static final Block BAMBOO_TUBE_RICE = StackableFoodBlock.create()
            .maxCount(4)
            .item(() -> ModItems.BAMBOO_TUBE_RICE)
            .shapes(
                    Block.box(4, 0, 4, 12, 10, 12),
                    Shapes.or(
                            Block.box(7, 0, 1, 15, 10, 9),
                            Block.box(1, 0, 7, 9, 10, 15)
                    ),
                    Shapes.or(
                            Block.box(0, 0, 6, 16, 10, 15),
                            Block.box(4, 0, 0, 12, 10, 15)
                    ),
                    Block.box(0, 0, 0, 16, 10, 16)
            ).build();

    // Candied skewers
    public static final Block CANDIED_GLOW_BERRY_SKEWER_UPRIGHT = new CandiedSkewerBlock(12, () -> ModItems.CANDIED_GLOW_BERRY_SKEWER, 8, true);
    public static final Block CANDIED_GLOW_BERRY_SKEWER_SIDE = new CandiedSkewerBlock(5, () -> ModItems.CANDIED_GLOW_BERRY_SKEWER, 8, false);
    public static final Block CANDIED_SWEET_BERRY_SKEWER_UPRIGHT = new CandiedSkewerBlock(12, () -> ModItems.CANDIED_SWEET_BERRY_SKEWER, 0, true);
    public static final Block CANDIED_SWEET_BERRY_SKEWER_SIDE = new CandiedSkewerBlock(5, () -> ModItems.CANDIED_SWEET_BERRY_SKEWER, 0, false);
    public static final Block CANDIED_CHORUS_FRUIT_SKEWER_UPRIGHT = new CandiedSkewerBlock(5, () -> ModItems.CANDIED_CHORUS_FRUIT_SKEWER, 0, true);
    public static final Block CANDIED_CHORUS_FRUIT_SKEWER_SIDE = new CandiedSkewerBlock(4, () -> ModItems.CANDIED_CHORUS_FRUIT_SKEWER, 0, false);
    public static final Block CANDIED_APPLE_SKEWER_UPRIGHT = new CandiedSkewerBlock(5, () -> ModItems.CANDIED_APPLE_SKEWER, 0, true);
    public static final Block CANDIED_APPLE_SKEWER_SIDE = new CandiedSkewerBlock(4, () -> ModItems.CANDIED_APPLE_SKEWER, 0, false);
    public static final Block CANDIED_CATERPILLAR_SKEWER_UPRIGHT = new CandiedSkewerBlock(12, () -> ModItems.CANDIED_CATERPILLAR_SKEWER, 0, true);
    public static final Block CANDIED_CATERPILLAR_SKEWER_SIDE = new CandiedSkewerBlock(5, () -> ModItems.CANDIED_CATERPILLAR_SKEWER, 0, false);

    // Block entities
    public static final BlockEntityType<BambooTrayBlockEntity> BAMBOO_TRAY_BE = BlockEntityType.Builder.of(BambooTrayBlockEntity::new, BAMBOO_TRAY).build(null);
    public static final BlockEntityType<TeaBannerBlockEntity> TEA_BANNER_BE = BlockEntityType.Builder.of(TeaBannerBlockEntity::new, TEA_BANNER).build(null);
    public static final BlockEntityType<PotBlockEntity> POT_BE = BlockEntityType.Builder.of(PotBlockEntity::new, POT).build(null);
    public static final BlockEntityType<StockpotBlockEntity> STOCKPOT_BE = BlockEntityType.Builder.of(StockpotBlockEntity::new, STOCKPOT).build(null);
    public static final BlockEntityType<FruitBasketBlockEntity> FRUIT_BASKET_BE = BlockEntityType.Builder.of(FruitBasketBlockEntity::new, FRUIT_BASKET).build(null);
    public static final BlockEntityType<ChoppingBoardBlockEntity> CHOPPING_BOARD_BE = BlockEntityType.Builder.of(ChoppingBoardBlockEntity::new, CHOPPING_BOARD).build(null);
    public static final BlockEntityType<KitchenwareRacksBlockEntity> KITCHENWARE_RACKS_BE = BlockEntityType.Builder.of(KitchenwareRacksBlockEntity::new, KITCHENWARE_RACKS).build(null);
    public static final BlockEntityType<KitchenShelfBlockEntity> KITCHEN_SHELF_BE = BlockEntityType.Builder.of(KitchenShelfBlockEntity::new, KITCHEN_SHELF).build(null);
    public static final BlockEntityType<ShawarmaSpitBlockEntity> SHAWARMA_SPIT_BE = BlockEntityType.Builder.of(ShawarmaSpitBlockEntity::new, SHAWARMA_SPIT).build(null);
    public static final BlockEntityType<SteamerBlockEntity> STEAMER_BE = BlockEntityType.Builder.of(SteamerBlockEntity::new, STEAMER).build(null);
    public static final BlockEntityType<MillstoneBlockEntity> MILLSTONE_BE = BlockEntityType.Builder.of(MillstoneBlockEntity::new, MILLSTONE).build(null);
    public static final BlockEntityType<RecipeBlockEntity> RECIPE_BLOCK_BE = BlockEntityType.Builder.of(RecipeBlockEntity::new, RECIPE_BLOCK).build(null);
    public static final BlockEntityType<OilPotBlockEntity> OIL_POT_BE = BlockEntityType.Builder.of(OilPotBlockEntity::new, OIL_POT).build(null);
    public static final BlockEntityType<TeapotBlockEntity> TEAPOT_BE = BlockEntityType.Builder.of(TeapotBlockEntity::new, TEAPOT).build(null);
    public static final BlockEntityType<TrashCanBlockEntity> TRASH_CAN_BE = BlockEntityType.Builder.of(TrashCanBlockEntity::new, TRASH_CAN).build(null);
    public static final BlockEntityType<FoodBiteThreeByThreeBlockEntity> FOOD_BITE_THREE_BY_THREE_BE = BlockEntityType.Builder.of(FoodBiteThreeByThreeBlockEntity::new, COLD_CUT_HAM_SLICES).build(null);

    public static final BlockEntityType<ChairBlockEntity> CHAIR_BE = BlockEntityType.Builder.of(ChairBlockEntity::new,
            CHAIR_OAK, CHAIR_SPRUCE, CHAIR_ACACIA, CHAIR_BAMBOO,
            CHAIR_BIRCH, CHAIR_CHERRY, CHAIR_CRIMSON, CHAIR_DARK_OAK,
            CHAIR_JUNGLE, CHAIR_MANGROVE, CHAIR_WARPED
    ).build(null);

    public static final BlockEntityType<TableBlockEntity> TABLE_BE = BlockEntityType.Builder.of(TableBlockEntity::new,
            TABLE_OAK, TABLE_SPRUCE, TABLE_ACACIA, TABLE_BAMBOO,
            TABLE_BIRCH, TABLE_CHERRY, TABLE_CRIMSON, TABLE_DARK_OAK,
            TABLE_JUNGLE, TABLE_MANGROVE, TABLE_WARPED
    ).build(null);

    public static void registerBlocks() {
        // Kitchen blocks
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stove"), STOVE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot"), POT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"), STOCKPOT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fruit_basket"), FRUIT_BASKET);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board"), CHOPPING_BOARD);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "oil_block"), OIL_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "enamel_basin"), ENAMEL_BASIN);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchenware_racks"), KITCHENWARE_RACKS);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchen_shelf"), KITCHEN_SHELF);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chili_ristra"), CHILI_RISTRA);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "strung_mushrooms"), STRUNG_MUSHROOMS);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "straw_block"), STRAW_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "shawarma_spit"), SHAWARMA_SPIT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer"), STEAMER);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone"), MILLSTONE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_block"), RECIPE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "oil_pot"), OIL_POT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "trash_can"), TRASH_CAN);

        // Tea
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"), TEAPOT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "empty_cup"), EMPTY_CUP);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray"), BAMBOO_TRAY);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tea_banner"), TEA_BANNER);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tea_tree"), TEA_TREE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "clay_pot_milk_tea"), CLAY_POT_MILK_TEA);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "long_bench"), LONG_BENCH);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "red_lantern"), RED_LANTERN);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "eight_immortals_table"), EIGHT_IMMORTALS_TABLE);

        // Crop blocks
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tomato_crop"), TOMATO_CROP);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chili_crop"), CHILI_CROP);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "lettuce_crop"), LETTUCE_CROP);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "rice_crop"), RICE_CROP);

        // Cook stools
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_oak"), COOK_STOOL_OAK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_spruce"), COOK_STOOL_SPRUCE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_acacia"), COOK_STOOL_ACACIA);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_bamboo"), COOK_STOOL_BAMBOO);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_birch"), COOK_STOOL_BIRCH);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_cherry"), COOK_STOOL_CHERRY);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_crimson"), COOK_STOOL_CRIMSON);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_dark_oak"), COOK_STOOL_DARK_OAK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_jungle"), COOK_STOOL_JUNGLE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_mangrove"), COOK_STOOL_MANGROVE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_warped"), COOK_STOOL_WARPED);

        // Chairs
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_oak"), CHAIR_OAK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_spruce"), CHAIR_SPRUCE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_acacia"), CHAIR_ACACIA);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_bamboo"), CHAIR_BAMBOO);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_birch"), CHAIR_BIRCH);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_cherry"), CHAIR_CHERRY);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_crimson"), CHAIR_CRIMSON);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_dark_oak"), CHAIR_DARK_OAK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_jungle"), CHAIR_JUNGLE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_mangrove"), CHAIR_MANGROVE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_warped"), CHAIR_WARPED);

        // Tables
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_oak"), TABLE_OAK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_spruce"), TABLE_SPRUCE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_acacia"), TABLE_ACACIA);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_bamboo"), TABLE_BAMBOO);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_birch"), TABLE_BIRCH);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_cherry"), TABLE_CHERRY);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_crimson"), TABLE_CRIMSON);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_dark_oak"), TABLE_DARK_OAK);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_jungle"), TABLE_JUNGLE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_mangrove"), TABLE_MANGROVE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_warped"), TABLE_WARPED);

        // Feast
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cold_cut_ham_slices"), COLD_CUT_HAM_SLICES);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tube_rice"), BAMBOO_TUBE_RICE);

        // Candied skewers
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_glow_berry_skewer_upright"), CANDIED_GLOW_BERRY_SKEWER_UPRIGHT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_glow_berry_skewer_side"), CANDIED_GLOW_BERRY_SKEWER_SIDE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_sweet_berry_skewer_upright"), CANDIED_SWEET_BERRY_SKEWER_UPRIGHT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_sweet_berry_skewer_side"), CANDIED_SWEET_BERRY_SKEWER_SIDE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_chorus_fruit_skewer_upright"), CANDIED_CHORUS_FRUIT_SKEWER_UPRIGHT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_chorus_fruit_skewer_side"), CANDIED_CHORUS_FRUIT_SKEWER_SIDE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_apple_skewer_upright"), CANDIED_APPLE_SKEWER_UPRIGHT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_apple_skewer_side"), CANDIED_APPLE_SKEWER_SIDE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_caterpillar_skewer_upright"), CANDIED_CATERPILLAR_SKEWER_UPRIGHT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_caterpillar_skewer_side"), CANDIED_CATERPILLAR_SKEWER_SIDE);

        // Block entities
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray"), BAMBOO_TRAY_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tea_banner"), TEA_BANNER_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot"), POT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"), STOCKPOT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fruit_basket"), FRUIT_BASKET_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board"), CHOPPING_BOARD_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchenware_racks"), KITCHENWARE_RACKS_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchen_shelf"), KITCHEN_SHELF_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "shawarma_spit"), SHAWARMA_SPIT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair"), CHAIR_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table"), TABLE_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer"), STEAMER_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone"), MILLSTONE_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_book"), RECIPE_BLOCK_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "oil_pot"), OIL_POT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"), TEAPOT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "trash_can"), TRASH_CAN_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "food_bite_three_by_three"), FOOD_BITE_THREE_BY_THREE_BE);
    }
}
