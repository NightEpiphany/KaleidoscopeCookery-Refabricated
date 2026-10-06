package com.github.ysbbbbbb.kaleidoscopecookery.crafting;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.RiceBowlRecipe;
import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

import java.util.List;

import static net.minecraft.world.item.crafting.CraftingBookCategory.MISC;

/**
 * 给 JEI EMI 和 REI 添加配方用的
 */
public class RiceBowlRecipeMaker {
    public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
        List<RecipeHolder<CraftingRecipe>> recipes = Lists.newArrayList();

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }

        level.recipeAccess()
                .getSynchronizedRecipes().getAllOfType(RecipeType.CRAFTING)
                .forEach(holder ->
                        addRiceBowlRecipe(recipes, holder, level.registryAccess())
                );

        return recipes;
    }

    private static void addRiceBowlRecipe(List<RecipeHolder<CraftingRecipe>> recipes, RecipeHolder<CraftingRecipe> recipe,
                                          HolderLookup.Provider registries) {
        if (recipe.value() instanceof RiceBowlRecipe riceBowlRecipe) {
            var ingredients = riceBowlRecipe.getIngredients(registries);
            ItemStackTemplate result = riceBowlRecipe.getResult();
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(
                    new Recipe.CommonInfo(false),
                    new CraftingRecipe.CraftingBookInfo(MISC, "rice_bowl"),
                    result,
                    ingredients
            );
            recipes.add(new RecipeHolder<>(recipe.id(), shapelessRecipe));
        }
    }
}