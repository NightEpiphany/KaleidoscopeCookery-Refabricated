package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public final class ModPoi {

    public static final ResourceKey<PoiType> STOVE_KEY = createKey("stove");
    public static final ResourceKey<PoiType> POT_KEY = createKey("pot");
    public static final ResourceKey<PoiType> STOCKPOT_KEY = createKey("stockpot");
    public static final ResourceKey<PoiType> CHOPPING_BOARD_KEY = createKey("chopping_board");

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

    public static ResourceKey<PoiType> createKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name));
    }

    public static void registerPoiTypes() {
        // 确保类被加载
    }
}
