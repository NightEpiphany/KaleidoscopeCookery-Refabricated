package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ChoppingBoardRecipe extends SingleItemRecipe {
    private final List<ItemStackTemplate> results;
    private final int cutCount;
    private final Identifier modelId;

    public ChoppingBoardRecipe(Ingredient ingredient, ItemStackTemplate result, int cutCount, Identifier modelId) {
        this(ingredient, List.of(result), cutCount, modelId);
    }

    public ChoppingBoardRecipe(Ingredient ingredient, List<ItemStackTemplate> results, int cutCount, Identifier modelId) {
        super(BaseRecipe.NO_INFO, ingredient, results.getFirst());
        this.results = List.copyOf(results);
        this.cutCount = Math.max(cutCount, 1);
        this.modelId = modelId;
    }

    @Override
    public @NonNull RecipeSerializer<? extends SingleItemRecipe> getSerializer() {
        return ModRecipes.CHOPPING_BOARD_SERIALIZER;
    }

    @Override
    public @NonNull RecipeType<? extends SingleItemRecipe> getType() {
        return ModRecipes.CHOPPING_BOARD_RECIPE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.CHOPPING_BOARD_CATEGORY;
    }

    @Override
    public boolean matches(SingleRecipeInput inv, @NonNull Level level) {
        return this.input().test(inv.getItem(0));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NonNull String group() {
        return "chopping_board";
    }

    public Ingredient getIngredient() {
        return this.input();
    }

    public ItemStackTemplate getResult() {
        return this.result();
    }

    public List<ItemStackTemplate> getResults() {
        return results;
    }

    public int getCutCount() {
        return cutCount;
    }

    public Identifier getModelId() {
        return modelId;
    }
}
