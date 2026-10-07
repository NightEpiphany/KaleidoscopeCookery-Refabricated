package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.sounds.BlockSoundSet;

public final class ModBlockSoundSets {
    public static final ResourceKey<BlockSoundSet> POT = key("pot");
    public static final ResourceKey<BlockSoundSet> RECIPE_BLOCK = key("recipe_block");

    private static ResourceKey<BlockSoundSet> key(final String id) {
        return ResourceKey.create(Registries.BLOCK_SOUND_SET, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, id));
    }

    public static void init() {}
}
