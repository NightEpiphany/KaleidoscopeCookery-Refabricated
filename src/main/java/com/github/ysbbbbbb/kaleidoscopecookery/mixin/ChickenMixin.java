package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ChickenFoodIngredient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Chicken.class)
public abstract class ChickenMixin {
    @ModifyReturnValue(method = "isFood", at = @At("RETURN"))
    private boolean kaleidoscopeCookery$addChickenFood(boolean original, ItemStack stack) {
        return original || stack.is(TagCommon.CHICKEN_FOOD);
    }

    @ModifyExpressionValue(method = "registerGoals", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/animal/Chicken;FOOD_ITEMS:Lnet/minecraft/world/item/crafting/Ingredient;", opcode = Opcodes.GETSTATIC))
    private Ingredient kaleidoscopeCookery$addTemptingFood(Ingredient original) {
        return new ChickenFoodIngredient(original);
    }
}
