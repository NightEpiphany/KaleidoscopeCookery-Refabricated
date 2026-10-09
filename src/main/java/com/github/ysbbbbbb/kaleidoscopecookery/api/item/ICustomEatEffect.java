package com.github.ysbbbbbb.kaleidoscopecookery.api.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;

/**
 * 修改食物食用后的特定属性
 */
public interface ICustomEatEffect {
    /**
     * 修改物品食物属性值
     *
     * @param stack 物品
     * @return 修改后的食物属性
     */
    FoodProperties modifyFoodProperties(ItemStack stack);

    /**
     * 改物品食用效果属性
     *
     * @param stack 物品
     * @return 修改后的食用效果属性
     */
    Consumable modifyConsumables(ItemStack stack);
}
