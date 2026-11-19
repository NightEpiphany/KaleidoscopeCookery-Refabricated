package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.util.RecipeMatcher;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer.DEFAULT_SOUP_BASE;

public record StockpotRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients,
                             ResourceLocation soupBase, ItemStack result, int time,
                             Ingredient carrier, StockpotVisuals visuals,
                             boolean syrup) implements BaseRecipe<StockpotContainer> {

    public StockpotRecipe(ResourceLocation id, List<Ingredient> ingredients,
                          ResourceLocation soupBase, ItemStack result,
                          int time, Ingredient carrier, StockpotVisuals visuals,
                          boolean syrup
    ) {
        this(id, NonNullList.of(Ingredient.EMPTY, BaseRecipe.fillInputs(ingredients)),
                soupBase, result, time, carrier, visuals, syrup);
    }

    public StockpotRecipe(ResourceLocation id, List<Ingredient> ingredients,
                          ResourceLocation soupBase, ItemStack result,
                          int time, Ingredient carrier, StockpotVisuals visuals
    ) {
        this(id, ingredients, soupBase, result, time, carrier, visuals, false);
    }

    public StockpotRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients,
                          ItemStack result, int time, ItemStack container) {
        this(id, ingredients, DEFAULT_SOUP_BASE, result,
                time, Ingredient.of(container), StockpotVisuals.DEFAULT, false
        );
    }

    public ResourceLocation cookingTexture() {
        return this.visuals.cookingTexture();
    }

    public ResourceLocation finishedTexture() {
        return this.visuals.finishedTexture();
    }

    public int cookingBubbleColor() {
        return this.visuals.cookingBubbleColor();
    }

    public int finishedBubbleColor() {
        return this.visuals.finishedBubbleColor();
    }

    @Override
    public boolean matches(StockpotContainer container, @NotNull Level level) {
        return container.getSoupBase().equals(this.soupBase)
                && RecipeMatcher.findMatches(container.items, ingredients) != null;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return this.result;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.STOCKPOT_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.STOCKPOT_RECIPE;
    }
}
