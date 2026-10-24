package com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings("unused")
public final class ModFluidIds {
    public static final ResourceKey<Fluid> MILK = key("milk");

    private ModFluidIds() {
    }

    @SuppressWarnings("SameParameterValue")
    private static ResourceKey<Fluid> key(String path) {
        return ResourceKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path));
    }
}
