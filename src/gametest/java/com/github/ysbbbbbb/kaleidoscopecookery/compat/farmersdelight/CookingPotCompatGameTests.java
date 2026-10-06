package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.StockpotMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.List;

public class CookingPotCompatGameTests implements FabricGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void cookingRecipeTransformsWithoutRecipeWrapperCast(GameTestHelper helper) {
        ResourceLocation recipeId = new ResourceLocation("test", "mushroom_stew");
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(Items.RED_MUSHROOM));
        ingredients.add(Ingredient.of(Items.BROWN_MUSHROOM));
        CookingPotRecipe recipe = new CookingPotRecipe(recipeId, "", null, ingredients,
                new ItemStack(Items.MUSHROOM_STEW), new ItemStack(Items.BOWL), 0, 200);
        StockpotContainer container = new StockpotContainer(List.of(
                new ItemStack(Items.BROWN_MUSHROOM), new ItemStack(Items.RED_MUSHROOM),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY),
                new ResourceLocation("minecraft", "water"));
        StockpotMatchRecipeEvent.Post event = new StockpotMatchRecipeEvent.Post(
                helper.getLevel(), null, container, StockpotRecipeSerializer.EMPTY_ID);

        CookingPotCompat.afterStockpotRecipeMatch(event, List.of(recipe));

        helper.assertTrue(event.getOutput() != null && event.getOutput().getId().equals(recipeId),
                "Farmer's Delight recipe must transform without a RecipeWrapper cast");
        helper.succeed();
    }
}
