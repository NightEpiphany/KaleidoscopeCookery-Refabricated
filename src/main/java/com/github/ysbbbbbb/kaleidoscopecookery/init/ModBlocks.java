package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.ChiliCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.LettuceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.TeaTreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.EmptyCupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.ClayPotMilkTeaBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.*;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.ChairBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.*;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StrawBedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.sounds.BlockSoundSet;
import net.minecraft.world.level.block.sounds.BlockSoundSets;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.function.Function;

public final class ModBlocks {
    // Kitchen blocks
    public static final Block STOVE = commonReg("stove", StoveBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .sound(BlockSoundSets.STONE)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(StoveBlock.LIT) ? 13 : 0)
            .randomTicks()
            .strength(1.5F, 6.0F));

    public static final Block POT = commonReg("pot", PotBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(ModBlockSoundSets.POT)
            .noOcclusion()
            .strength(1.5F, 6.0F));

    public static final Block STOCKPOT = commonReg("stockpot", StockpotBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .sound(ModBlockSoundSets.POT)
            .noOcclusion()
            .strength(1.5F, 6.0F));

    public static final Block FRUIT_BASKET = commonReg("fruit_basket", FruitBasketBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .sound(BlockSoundSets.BAMBOO));

    public static final Block CHOPPING_BOARD = commonReg("chopping_board", ChoppingBoardBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F)
            .sound(BlockSoundSets.WOOD)
            .ignitedByLava());

    public static final Block OIL_BLOCK = commonReg("oil_block", OilBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.ICE)
            .friction(0.985f)
            .sound(BlockSoundSets.SLIME_BLOCK)
            .noOcclusion()
            .isValidSpawn(Blocks::never));

    public static final Block ENAMEL_BASIN = commonReg("enamel_basin", EnamelBasinBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BELL)
            .strength(1.0F, 1.5F)
            .sound(BlockSoundSets.LANTERN));

    public static final Block KITCHENWARE_RACKS = commonReg("kitchenware_racks", KitchenwareRacksBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F, 3.0F)
            .sound(BlockSoundSets.WOOD)
            .ignitedByLava());

    public static final Block CHILI_RISTRA = commonReg("chili_ristra", ChiliRistraBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .noCollision()
            .instabreak()
            .sound(BlockSoundSets.GRASS)
            .pushReaction(PushReaction.POPPED));

    public static final Block STRUNG_MUSHROOMS  = commonReg("strung_mushrooms", StrungMushroomsBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .noCollision()
            .instabreak()
            .sound(BlockSoundSets.GRASS)
            .pushReaction(PushReaction.POPPED));

    public static final Block STRAW_BLOCK = commonReg("straw_block", StrawBlocks::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.BANJO)
            .strength(0.5F)
            .sound(BlockSoundSets.GRASS));

    public static final Block SHAWARMA_SPIT = commonReg("shawarma_spit", ShawarmaSpitBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .noOcclusion()
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F, 3.0F)
            .lightLevel(state -> state.getValue(ShawarmaSpitBlock.POWERED) ? 8 : 0)
            .sound(BlockSoundSets.METAL));

    public static final Block MILLSTONE = commonReg("millstone", MillstoneBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(BlockSoundSets.STONE)
            .forceSolidOn()
            .noOcclusion());

    public static final Block STEAMER = commonReg("steamer", SteamerBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .instabreak()
            .noOcclusion()
            .pushReaction(PushReaction.POPPED)
            .sound(BlockSoundSets.BAMBOO));

    public static final Block RECIPE_BLOCK = commonReg("recipe_block", RecipeBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .instabreak()
            .noOcclusion()
            .sound(ModBlockSoundSets.RECIPE_BLOCK));

    public static final Block OIL_POT = commonReg("oil_pot", OilPotBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.BELL)
            .instabreak()
            .pushReaction(PushReaction.POPPED)
            .sound(BlockSoundSets.LANTERN));

    public static final Block TRASH_CAN = commonReg("trash_can", TrashCanBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.BASS)
            .strength(1.5F, 6.0F)
            .sound(BlockSoundSets.LANTERN)
            .noOcclusion());

    // Tea
    public static final Block TEAPOT = commonReg("teapot", TeapotBlock::new, BlockBehaviour.Properties.of());
    public static final Block EMPTY_CUP = commonReg("empty_cup", EmptyCupBlock::new, BlockBehaviour.Properties.of());
    public static final Block CLAY_POT_MILK_TEA = commonReg("clay_pot_milk_tea", ClayPotMilkTeaBlock::new, BlockBehaviour.Properties.of());
    public static final Block BAMBOO_TRAY = commonReg("bamboo_tray", BambooTrayBlock::new, BlockBehaviour.Properties.of());
    public static final Block TEA_BANNER = commonReg("tea_banner", TeaBannerBlock::new, BlockBehaviour.Properties.of());
    public static final Block LONG_BENCH = commonReg("long_bench", LongBenchBlock::new, BlockBehaviour.Properties.of());
    public static final Block RED_LANTERN = commonReg("red_lantern", RedLanternBlock::new, BlockBehaviour.Properties.of());
    public static final Block EIGHT_IMMORTALS_TABLE = commonReg("eight_immortals_table", EightImmortalsTableBlock::new, BlockBehaviour.Properties.of());
    public static final Block TEA_TREE = commonReg("tea_tree", TeaTreeBlock::new, BlockBehaviour.Properties.of());

    // Crop blocks
    public static final Block TOMATO_CROP = cropReg("tomato_crop",p -> new BaseCropBlock(p, () -> ModItems.TOMATO, () -> ModItems.TOMATO_SEED));
    public static final Block CHILI_CROP = cropReg("chili_crop", ChiliCropBlock::new);
    public static final Block LETTUCE_CROP = cropReg("lettuce_crop", LettuceCropBlock::new);
    public static final Block RICE_CROP = cropReg("rice_crop", RiceCropBlock::new);

    // Cook stools
    public static final Block COOK_STOOL_OAK = stoolReg("cook_stool_oak");
    public static final Block COOK_STOOL_POPLAR = stoolReg("cook_stool_poplar");
    public static final Block COOK_STOOL_PALE_OAK = stoolReg("cook_stool_pale_oak");
    public static final Block COOK_STOOL_SPRUCE = stoolReg("cook_stool_spruce");
    public static final Block COOK_STOOL_ACACIA = stoolReg("cook_stool_acacia");
    public static final Block COOK_STOOL_BAMBOO = stoolReg("cook_stool_bamboo", BlockSoundSets.BAMBOO);
    public static final Block COOK_STOOL_BIRCH = stoolReg("cook_stool_birch");
    public static final Block COOK_STOOL_CHERRY = stoolReg("cook_stool_cherry", BlockSoundSets.CHERRY_WOOD);
    public static final Block COOK_STOOL_CRIMSON = stoolReg("cook_stool_crimson", BlockSoundSets.NETHER_WOOD);
    public static final Block COOK_STOOL_DARK_OAK = stoolReg("cook_stool_dark_oak");
    public static final Block COOK_STOOL_JUNGLE = stoolReg("cook_stool_jungle");
    public static final Block COOK_STOOL_MANGROVE = stoolReg("cook_stool_mangrove");
    public static final Block COOK_STOOL_WARPED = stoolReg("cook_stool_warped", BlockSoundSets.NETHER_WOOD);

    // Chairs
    public static final Block CHAIR_OAK = chairReg("chair_oak");
    public static final Block CHAIR_POPLAR = chairReg("chair_poplar");
    public static final Block CHAIR_PALE_OAK = chairReg("chair_pale_oak");
    public static final Block CHAIR_SPRUCE = chairReg("chair_spruce");
    public static final Block CHAIR_ACACIA = chairReg("chair_acacia");
    public static final Block CHAIR_BAMBOO = chairReg("chair_bamboo", BlockSoundSets.BAMBOO);
    public static final Block CHAIR_BIRCH = chairReg("chair_birch");
    public static final Block CHAIR_CHERRY = chairReg("chair_cherry", BlockSoundSets.CHERRY_WOOD);
    public static final Block CHAIR_CRIMSON = chairReg("chair_crimson", BlockSoundSets.NETHER_WOOD);
    public static final Block CHAIR_DARK_OAK = chairReg("chair_dark_oak");
    public static final Block CHAIR_JUNGLE = chairReg("chair_jungle");
    public static final Block CHAIR_MANGROVE = chairReg("chair_mangrove");
    public static final Block CHAIR_WARPED = chairReg("chair_warped", BlockSoundSets.NETHER_WOOD);

    // Tables
    public static final Block TABLE_OAK = tableReg("table_oak");
    public static final Block TABLE_POPLAR = tableReg("table_poplar");
    public static final Block TABLE_PALE_OAK = tableReg("table_pale_oak");
    public static final Block TABLE_SPRUCE = tableReg("table_spruce");
    public static final Block TABLE_ACACIA = tableReg("table_acacia");
    public static final Block TABLE_BAMBOO = tableReg("table_bamboo", BlockSoundSets.BAMBOO);
    public static final Block TABLE_BIRCH = tableReg("table_birch");
    public static final Block TABLE_CHERRY = tableReg("table_cherry", BlockSoundSets.CHERRY_WOOD);
    public static final Block TABLE_CRIMSON = tableReg("table_crimson", BlockSoundSets.NETHER_WOOD);
    public static final Block TABLE_DARK_OAK = tableReg("table_dark_oak");
    public static final Block TABLE_JUNGLE = tableReg("table_jungle");
    public static final Block TABLE_MANGROVE = tableReg("table_mangrove");
    public static final Block TABLE_WARPED = tableReg("table_warped", BlockSoundSets.NETHER_WOOD);

    //Feast
    public static final Block COLD_CUT_HAM_SLICES = commonReg("cold_cut_ham_slices", p -> new FoodBiteThreeByThreeBlock(p , ModFoods.COLD_CUT_HAM_SLICES_BLOCK, ModConsumables.COLD_CUT_HAM_SLICES_BLOCK, 8, null), BlockBehaviour.Properties.of()
            .forceSolidOn()
            .instabreak()
            .mapColor(MapColor.WOOD)
            .sound(BlockSoundSets.WOOD)
            .pushReaction(PushReaction.POPPED)
            .noOcclusion());

    public static final Block BAMBOO_TUBE_RICE = commonReg("bamboo_tube_rice", p ->
        StackableFoodBlock.create(p)
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
                ).build(), BlockBehaviour.Properties.of().setId(PortHelper.createBlockId("bamboo_tube_rice"))
    );

    public static final Block STRAW_BED = commonReg("straw_bed",
            StrawBedBlock::new,
            BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(BlockSoundSets.STRAW_BED)
            .strength(0.2F)
            .noOcclusion()
            .ignitedByLava()
            .pushReaction(PushReaction.POPPED)
    );

    // Block entities
    public static final BlockEntityType<PotBlockEntity> POT_BE = FabricBlockEntityTypeBuilder.create(PotBlockEntity::new, POT).build();
    public static final BlockEntityType<StockpotBlockEntity> STOCKPOT_BE = FabricBlockEntityTypeBuilder.create(StockpotBlockEntity::new, STOCKPOT).build();
    public static final BlockEntityType<FruitBasketBlockEntity> FRUIT_BASKET_BE = FabricBlockEntityTypeBuilder.create(FruitBasketBlockEntity::new, FRUIT_BASKET).build();
    public static final BlockEntityType<ChoppingBoardBlockEntity> CHOPPING_BOARD_BE = FabricBlockEntityTypeBuilder.create(ChoppingBoardBlockEntity::new, CHOPPING_BOARD).build();
    public static final BlockEntityType<KitchenwareRacksBlockEntity> KITCHENWARE_RACKS_BE = FabricBlockEntityTypeBuilder.create(KitchenwareRacksBlockEntity::new, KITCHENWARE_RACKS).build();
    public static final BlockEntityType<ShawarmaSpitBlockEntity> SHAWARMA_SPIT_BE = FabricBlockEntityTypeBuilder.create(ShawarmaSpitBlockEntity::new, SHAWARMA_SPIT).build();
    public static final BlockEntityType<SteamerBlockEntity> STEAMER_BE = FabricBlockEntityTypeBuilder.create(SteamerBlockEntity::new, STEAMER).build();
    public static final BlockEntityType<MillstoneBlockEntity> MILLSTONE_BE = FabricBlockEntityTypeBuilder.create(MillstoneBlockEntity::new, MILLSTONE).build();
    public static final BlockEntityType<RecipeBlockEntity> RECIPE_BLOCK_BE = FabricBlockEntityTypeBuilder.create(RecipeBlockEntity::new, RECIPE_BLOCK).build();
    public static final BlockEntityType<OilPotBlockEntity> OIL_POT_BE = FabricBlockEntityTypeBuilder.create(OilPotBlockEntity::new, OIL_POT).build();
    public static final BlockEntityType<com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity> TRASH_CAN_BE = FabricBlockEntityTypeBuilder.create(com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity::new, TRASH_CAN).build();
    public static final BlockEntityType<TeapotBlockEntity> TEAPOT_BE = FabricBlockEntityTypeBuilder.create(TeapotBlockEntity::new, TEAPOT).build();
    public static final BlockEntityType<FoodBiteThreeByThreeBlockEntity> FOOD_BITE_THREE_BY_THREE_BE = FabricBlockEntityTypeBuilder.create(FoodBiteThreeByThreeBlockEntity::new, COLD_CUT_HAM_SLICES).build();
    public static final BlockEntityType<BambooTrayBlockEntity> BAMBOO_TRAY_BE = FabricBlockEntityTypeBuilder.create(BambooTrayBlockEntity::new, BAMBOO_TRAY).build();
    public static final BlockEntityType<TeaBannerBlockEntity> TEA_BANNER_BE = FabricBlockEntityTypeBuilder.create(TeaBannerBlockEntity::new, TEA_BANNER).build();

    public static final BlockEntityType<ChairBlockEntity> CHAIR_BE = FabricBlockEntityTypeBuilder.create(ChairBlockEntity::new,
            CHAIR_OAK, CHAIR_SPRUCE, CHAIR_ACACIA, CHAIR_BAMBOO,
            CHAIR_BIRCH, CHAIR_CHERRY, CHAIR_CRIMSON, CHAIR_DARK_OAK,
            CHAIR_JUNGLE, CHAIR_MANGROVE, CHAIR_WARPED, CHAIR_PALE_OAK, CHAIR_POPLAR
    ).build();

    public static final BlockEntityType<TableBlockEntity> TABLE_BE = FabricBlockEntityTypeBuilder.create(TableBlockEntity::new,
            TABLE_OAK, TABLE_SPRUCE, TABLE_ACACIA, TABLE_BAMBOO,
            TABLE_BIRCH, TABLE_CHERRY, TABLE_CRIMSON, TABLE_DARK_OAK,
            TABLE_JUNGLE, TABLE_MANGROVE, TABLE_WARPED, TABLE_PALE_OAK, TABLE_POPLAR
    ).build();

    public static void registerBlocks() {

        // Block entities
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot"), POT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"), STOCKPOT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fruit_basket"), FRUIT_BASKET_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board"), CHOPPING_BOARD_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchenware_racks"), KITCHENWARE_RACKS_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "shawarma_spit"), SHAWARMA_SPIT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair"), CHAIR_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table"), TABLE_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer"), STEAMER_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone"), MILLSTONE_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_book"), RECIPE_BLOCK_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "oil_pot"), OIL_POT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "trash_can"), TRASH_CAN_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"), TEAPOT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "food_bite_three_by_three"), FOOD_BITE_THREE_BY_THREE_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray"), BAMBOO_TRAY_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tea_banner"), TEA_BANNER_BE);
    }
    public static Block register(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        Block block = function.apply(properties.setId(resourceKey));
        return Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
    }
    private static Block commonReg(String string, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        return register(PortHelper.createBlockId(string), function, properties);
    }

    private static Block stoolReg(String string) {
        return stoolReg(string, BlockSoundSets.WOOD);
    }

    private static Block chairReg(String string) {
        return chairReg(string, BlockSoundSets.WOOD);
    }

    private static Block tableReg(String string) {
        return tableReg(string, BlockSoundSets.WOOD);
    }

    private static Block stoolReg(String string, ResourceKey<BlockSoundSet> soundType) {
        return commonReg(string, CookStoolBlock::new, BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(soundType)
                .ignitedByLava());
    }

    private static Block chairReg(String string, ResourceKey<BlockSoundSet> soundType) {
        return commonReg(string, ChairBlock::new, BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(soundType)
                .noOcclusion()
                .ignitedByLava());
    }
    private static Block tableReg(String string, ResourceKey<BlockSoundSet> soundType) {
        return commonReg(string, TableBlock::new, BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(soundType)
                .noOcclusion()
                .ignitedByLava());
    }

    private static Block cropReg(String string, Function<BlockBehaviour.Properties, Block> function) {
        return commonReg(string, function, BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollision()
                .randomTicks()
                .instabreak()
                .sound(BlockSoundSets.CROP)
                .pushReaction(PushReaction.POPPED));
    }
}
