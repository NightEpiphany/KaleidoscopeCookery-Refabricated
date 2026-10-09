package com.github.ysbbbbbb.kaleidoscopecookery.item.quality;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import org.jetbrains.annotations.Nullable;

/**
 * 品质食物的自定义食物属性
 */
public final class QualityFoodComponents {
    private final FoodProperties[] foods;
    private final Consumable[] consumables;

    public QualityFoodComponents(FoodProperties food, Consumable consumable) {
        Quality[] qualities = Quality.values();
        this.foods = new FoodProperties[qualities.length];
        this.consumables = new Consumable[qualities.length];
        for (Quality quality : qualities) {
            this.foods[quality.getId()] = QualityUtils.modifyFoodProperties(food, quality);
            this.consumables[quality.getId()] = QualityUtils.modifyFoodConsumables(consumable, quality);
        }
    }

    public @Nullable FoodProperties food(ItemStack stack) {
        FoodProperties raw = stack.getComponents().get(DataComponents.FOOD);
        Quality quality = stack.getComponents().get(ModDataComponents.QUALITY);
        return raw == null || quality == null ? raw : this.foods[quality.getId()];
    }

    public @Nullable Consumable consumable(ItemStack stack) {
        Consumable raw = stack.getComponents().get(DataComponents.CONSUMABLE);
        Quality quality = stack.getComponents().get(ModDataComponents.QUALITY);
        return raw == null || quality == null ? raw : this.consumables[quality.getId()];
    }
}
