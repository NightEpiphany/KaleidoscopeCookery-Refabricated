package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.mixin.accessor.PoiTypesAccessor;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class ModPoi {
    public static final PoiType STOVE = registerPoiType("stove", ModBlocks.STOVE);
    public static final PoiType POT = registerPoiType("pot", ModBlocks.POT);
    public static final PoiType STOCKPOT = registerPoiType("stockpot", ModBlocks.STOCKPOT);
    public static final PoiType CHOPPING_BOARD = registerPoiType("chopping_board", ModBlocks.CHOPPING_BOARD);

    private static PoiType registerPoiType(String name, Block block) {
        final ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();
        builder.addAll(block.getStateDefinition().getPossibleStates());
        return PoiTypesAccessor.kaleidoscopeCookery$registerPoi(
                BuiltInRegistries.POINT_OF_INTEREST_TYPE,
                ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE,Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name)),
                builder.build(),
                1,
                1
        );
    }

    public static void registerPoiTypes() {
        // 确保类被加载
    }
}
