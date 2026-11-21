package com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public final class ModBlockIds {
    public static final ResourceKey<Block> CANDIED_GLOW_BERRY_SKEWER_SIDE = key("candied_glow_berry_skewer_side");
    public static final ResourceKey<Block> CANDIED_SWEET_BERRY_SKEWER_SIDE = key("candied_sweet_berry_skewer_side");
    public static final ResourceKey<Block> CANDIED_CHORUS_FRUIT_SKEWER_SIDE = key("candied_chorus_fruit_skewer_side");
    public static final ResourceKey<Block> CANDIED_APPLE_SKEWER_SIDE = key("candied_apple_skewer_side");
    public static final ResourceKey<Block> CANDIED_CATERPILLAR_SKEWER_SIDE = key("candied_caterpillar_skewer_side");

    private static ResourceKey<Block> key(String path) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path));
    }
}
