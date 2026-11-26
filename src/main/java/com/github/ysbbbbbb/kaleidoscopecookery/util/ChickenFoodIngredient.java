package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class ChickenFoodIngredient extends Ingredient {
    private final Ingredient original;

    public ChickenFoodIngredient(Ingredient original) {
        super(Arrays.stream(original.getItems()).map(ItemValue::new));
        this.original = original;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && (original.test(stack) || stack.is(TagCommon.CHICKEN_FOOD));
    }
}
