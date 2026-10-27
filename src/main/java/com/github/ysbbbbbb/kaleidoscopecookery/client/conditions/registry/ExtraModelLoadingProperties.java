package com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.ModelDisplayCondition;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.prop.ExtraModelLoadingProperty;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.registry.v0.FabricClientRegistryBuilder;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import static com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery.MOD_ID;

@SuppressWarnings("UnusedReturnValue")
@Environment(EnvType.CLIENT)
public class ExtraModelLoadingProperties {
    public static final ResourceKey<Registry<MapCodec<? extends ExtraModelLoadingProperty>>> REGISTRY_KEY = ResourceKey.createRegistryKey(
            ClientRegistries.ROOT_REGISTRY_NAME, Identifier.fromNamespaceAndPath(MOD_ID, "extra_model_loading_property_type")
    );
    public static final Registry<MapCodec<? extends ExtraModelLoadingProperty>> REGISTRY = FabricClientRegistryBuilder.create(REGISTRY_KEY)
            .buildAndRegister();
    public static final MapCodec<ExtraModelLoadingProperty> MAP_CODEC = REGISTRY.byNameCodec()
            .dispatchMap("property", ExtraModelLoadingProperty::type, c -> c);

    public static Object bootstrap(Registry<MapCodec<? extends ExtraModelLoadingProperty>> registry) {
        return Registry.register(registry, Identifier.fromNamespaceAndPath(MOD_ID, "model_display/model"), ModelDisplayCondition.MAP_CODEC);
    }
}
