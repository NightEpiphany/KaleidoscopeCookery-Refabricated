package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class FruitSkewerItem extends Item implements IHasContainer {
    public FruitSkewerItem(FoodProperties properties) {
        super(new Item.Properties().food(properties));
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        return this.returnContainerToEntity(remaining, level, entity);
    }

    @Override
    public Item getContainerItem() {
        return Items.STICK;
    }
}
