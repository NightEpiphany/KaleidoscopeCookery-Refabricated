package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.ICustomEatEffect;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @WrapOperation(
            method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Item;getFoodProperties()Lnet/minecraft/world/food/FoodProperties;"
            )
    )
    private FoodProperties useStackFoodProperties(
            Item item,
            Operation<FoodProperties> original,
            @Local(argsOnly = true) ItemStack stack
    ) {
        if (item instanceof ICustomEatEffect customEatEffect) {
            return customEatEffect.modifyFoodProperties(stack);
        }
        return original.call(item);
    }
}
