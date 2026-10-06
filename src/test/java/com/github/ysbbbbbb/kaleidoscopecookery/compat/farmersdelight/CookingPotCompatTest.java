package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CookingPotCompatTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void matchesIngredientsWithoutDependingOnRecipeWrapperPackage() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(Items.RED_MUSHROOM));
        ingredients.add(Ingredient.of(Items.BROWN_MUSHROOM));
        CookingPotRecipe recipe = new CookingPotRecipe(new ResourceLocation("test", "mushroom_stew"),
                "", null, ingredients, new ItemStack(Items.MUSHROOM_STEW), ItemStack.EMPTY, 0, 200);

        List<ItemStack> matching = List.of(new ItemStack(Items.BROWN_MUSHROOM),
                new ItemStack(Items.RED_MUSHROOM), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        assertEquals(recipe, CookingPotCompat.findMatchingRecipe(List.of(recipe), matching).orElseThrow());

        NonNullList<Ingredient> otherIngredients = NonNullList.create();
        otherIngredients.add(Ingredient.of(Items.RED_MUSHROOM));
        otherIngredients.add(Ingredient.of(Items.RED_MUSHROOM));
        CookingPotRecipe otherRecipe = new CookingPotRecipe(new ResourceLocation("test", "wrong_stew"),
                "", null, otherIngredients, new ItemStack(Items.MUSHROOM_STEW), ItemStack.EMPTY, 0, 200);
        assertEquals(recipe, CookingPotCompat.findMatchingRecipe(List.of(otherRecipe, recipe), matching).orElseThrow());

        List<ItemStack> wrongCount = List.of(new ItemStack(Items.BROWN_MUSHROOM),
                new ItemStack(Items.RED_MUSHROOM), new ItemStack(Items.RED_MUSHROOM),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        assertTrue(CookingPotCompat.findMatchingRecipe(List.of(recipe), wrongCount).isEmpty());

        List<ItemStack> wrongIngredient = List.of(new ItemStack(Items.BROWN_MUSHROOM),
                new ItemStack(Items.BROWN_MUSHROOM), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        assertTrue(CookingPotCompat.findMatchingRecipe(List.of(recipe), wrongIngredient).isEmpty());
    }
}
