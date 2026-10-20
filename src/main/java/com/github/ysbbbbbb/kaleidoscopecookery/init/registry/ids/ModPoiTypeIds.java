package com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

@SuppressWarnings("unused")
public final class ModPoiTypeIds {
    public static final ResourceKey<PoiType> STOVE_KEY = createKey("stove");
    public static final ResourceKey<PoiType> POT_KEY = createKey("pot");
    public static final ResourceKey<PoiType> STOCKPOT_KEY = createKey("stockpot");
    public static final ResourceKey<PoiType> CHOPPING_BOARD_KEY = createKey("chopping_board");

    public static ResourceKey<PoiType> createKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name));
    }
}
