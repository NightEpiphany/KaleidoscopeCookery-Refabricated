package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.model.ExtraLoadingItemModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.registry.ExtraModelLoadingProperties;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(ItemModels.class)
public class ItemModelsMixin {

    @Unique
    private static final BiMap<String, MapCodec<? extends ItemModel.Unbaked>> EXTRA_PROPERTIES = ImmutableBiMap.of(
            "model_arguments", ExtraLoadingItemModel.Unbaked.MAP_CODEC
    );

    @WrapMethod(method = "bootstrap")
    private static Object kaleidoscopeCookery$bootstrap(Registry<MapCodec<? extends ItemModel.Unbaked>> registry, Operation<Object> original) {
        ExtraModelLoadingProperties.bootstrap(ExtraModelLoadingProperties.REGISTRY);
        extraReg(registry);
        return original.call(registry);
    }

    @Unique
    private static void extraReg(Registry<MapCodec<? extends ItemModel.Unbaked>> registry) {
        EXTRA_PROPERTIES.forEach((k, v) ->
                Registry.register(registry, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, k), v)
        );
    }
}
