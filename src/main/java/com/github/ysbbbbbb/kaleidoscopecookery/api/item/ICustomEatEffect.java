package com.github.ysbbbbbb.kaleidoscopecookery.api.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 根据实际食物物品栈调整食用属性。
 */
public interface ICustomEatEffect {
    @Nullable
    FoodProperties modifyFoodProperties(ItemStack stack);
}
