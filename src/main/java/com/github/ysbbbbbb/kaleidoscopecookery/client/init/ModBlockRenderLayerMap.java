package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public final class ModBlockRenderLayerMap {
    public static void register() {
        TeacupRegistry.TEACUP_DATA_MAP.forEach((resourceLocation, data) -> BlockRenderLayerMap.INSTANCE.putBlock(TeacupRegistry.getBlock(resourceLocation), RenderType.cutout()));
        PlateRegistry.PLATE_DATA_MAP.forEach((resourceLocation, data) -> BlockRenderLayerMap.INSTANCE.putBlock(PlateRegistry.getBlock(resourceLocation), RenderType.cutout()));
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                ModBlocks.POT,
                ModBlocks.KITCHENWARE_RACKS,
                ModBlocks.SHAWARMA_SPIT,
                ModBlocks.CHILI_CROP,
                ModBlocks.TOMATO_CROP,
                ModBlocks.LETTUCE_CROP,
                ModBlocks.RICE_CROP,
                ModBlocks.CHILI_RISTRA,
                ModBlocks.STEAMER,
                ModBlocks.STOCKPOT,
                ModBlocks.STRUNG_MUSHROOMS,
                ModBlocks.EMPTY_CUP,
                ModBlocks.TEAPOT,
                ModBlocks.BAMBOO_TRAY,
                ModBlocks.TEA_TREE,
                ModBlocks.RED_LANTERN,
                ModBlocks.LONG_BENCH,
                ModBlocks.EIGHT_IMMORTALS_TABLE,
                ModBlocks.CLAY_POT_MILK_TEA,
                ModBlocks.CANDIED_GLOW_BERRY_SKEWER_UPRIGHT,
                ModBlocks.CANDIED_GLOW_BERRY_SKEWER_SIDE,
                ModBlocks.CANDIED_SWEET_BERRY_SKEWER_UPRIGHT,
                ModBlocks.CANDIED_SWEET_BERRY_SKEWER_SIDE,
                ModBlocks.CANDIED_CHORUS_FRUIT_SKEWER_UPRIGHT,
                ModBlocks.CANDIED_CHORUS_FRUIT_SKEWER_SIDE,
                ModBlocks.CANDIED_APPLE_SKEWER_UPRIGHT,
                ModBlocks.CANDIED_APPLE_SKEWER_SIDE,
                ModBlocks.CANDIED_CATERPILLAR_SKEWER_UPRIGHT,
                ModBlocks.CANDIED_CATERPILLAR_SKEWER_SIDE
        );

        FoodBiteRegistry.FOOD_DATA_MAP.keySet().forEach(id -> {
            Block block = FoodBiteRegistry.getBlock(id);
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
        });
    }
}
