package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(ConditionalItemModelProperties.class)
public class ConditionalItemModelPropertiesMixin {

    @Unique
    private static final BiMap<String, MapCodec<? extends ConditionalItemModelProperty>> EXTRA_PROPERTIES = ImmutableBiMap.of(
            "kitchen_shovel/has_oil", KitchenShovelCondition.MAP_CODEC,
            "stockpot_lid/using", StockpotLidCondition.MAP_CODEC,
            "steamer/has_item", SteamerCondition.MAP_CODEC,
            "recipe_item/has_recipe", RecipeItemCondition.MAP_CODEC,
            "oil_pot/has_oil", OilPotBlockCondition.MAP_CODEC,
            "transmutation_lunch_bag/has_food", TransmutationLunchBagItemCondition.MAP_CODEC,
            "ingredient/special_render", SpecialRenderCondition.MAP_CODEC
    );

    @WrapMethod(method = "bootstrap")
    private static Object kaleidoscopeCookery$bootstrap(Registry<MapCodec<? extends ConditionalItemModelProperty>> registry, Operation<Object> original) {
        extraReg(registry);
        return original.call(registry);
    }

    @Unique
    private static void extraReg(Registry<MapCodec<? extends ConditionalItemModelProperty>> registry) {
        EXTRA_PROPERTIES.forEach((k, v) ->
                Registry.register(registry, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, k), v)
        );
    }
}
