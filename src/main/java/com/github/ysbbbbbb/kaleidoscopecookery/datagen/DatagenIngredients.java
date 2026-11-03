package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.util.EmptyJsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public final class DatagenIngredients {

    @SuppressWarnings("deprecation")
    public static Ingredient tag(TagKey<Item> tagKey) {
        return Ingredient.of(HolderSet.emptyNamed(EmptyJsonOps.INSTANCE, tagKey));
    }
}
