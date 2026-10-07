package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public final class ModPoi {
    public static final ResourceKey<PoiType> STOVE_KEY = PortHelper.createPoiId("stove");
    public static final ResourceKey<PoiType> POT_KEY = PortHelper.createPoiId("pot");
    public static final ResourceKey<PoiType> STOCKPOT_KEY = PortHelper.createPoiId("stockpot");
    public static final ResourceKey<PoiType> CHOPPING_BOARD_KEY = PortHelper.createPoiId("chopping_board");

    public static final Holder<PoiType> STOVE = registerPoiType(STOVE_KEY, ModBlocks.STOVE);
    public static final Holder<PoiType> POT = registerPoiType(POT_KEY, ModBlocks.POT);
    public static final Holder<PoiType> STOCKPOT = registerPoiType(STOCKPOT_KEY, ModBlocks.STOCKPOT);
    public static final Holder<PoiType> CHOPPING_BOARD = registerPoiType(CHOPPING_BOARD_KEY, ModBlocks.CHOPPING_BOARD);

    private static Holder<PoiType> registerPoiType(ResourceKey<PoiType> key, Block block) {
        return PoiTypes.register(key, getBlockStates(block), 1, 1);
    }

    private static Set<BlockState> getBlockStates(final Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    public static void registerPoiTypes() {
        // 确保类被加载
    }
}
