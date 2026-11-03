package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.DatagenIngredients;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import com.google.common.collect.Lists;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class StockpotRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "stockpot";
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private ItemStackTemplate result;
    private int time = StockpotRecipeSerializer.DEFAULT_TIME;
    private Ingredient carrier = StockpotRecipeSerializer.DEFAULT_CARRIER;
    private boolean emptyCarrier = false;
    private Identifier soupBase = StockpotRecipeSerializer.DEFAULT_SOUP_BASE;
    private Identifier cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
    private Identifier finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
    private int cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
    private int finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;

    public static StockpotRecipeBuilder builder() {
        return new StockpotRecipeBuilder();
    }

    @SuppressWarnings("all")
    public StockpotRecipeBuilder addInput(Object... ingredients) {
        for (Object ingredient : ingredients) {
            if (ingredient instanceof ItemLike itemLike) {
                this.ingredients.add(Ingredient.of(itemLike));
            } else if (ingredient instanceof ItemStack stack) {
                this.ingredients.add(Ingredient.of(stack.getItem()));
            } else if (ingredient instanceof TagKey tagKey) {
                this.ingredients.add(DatagenIngredients.tag(tagKey));
            } else if (ingredient instanceof Ingredient ingredientObj) {
                this.ingredients.add(ingredientObj);
            }
        }
        return this;
    }

    public StockpotRecipeBuilder setSoupBase(Identifier soupBase) {
        this.soupBase = soupBase;
        return this;
    }

    public StockpotRecipeBuilder setResult(Item result) {
        this.result = new ItemStackTemplate(result.asItem(), 3);
        return this;
    }

    public StockpotRecipeBuilder setResult(Item result, int count) {
        return this.setResult(new ItemStack(result, count));
    }

    public StockpotRecipeBuilder setResult(Identifier result) {
        this.result = new ItemStackTemplate(Objects.requireNonNull(BuiltInRegistries.ITEM.getValue(result)));
        return this;
    }

    public StockpotRecipeBuilder setResult(ItemStack result) {
        this.result = ItemStackTemplate.fromNonEmptyStack(result);
        return this;
    }

    public StockpotRecipeBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public StockpotRecipeBuilder setCarrier(ItemLike carrier) {
        this.carrier = Ingredient.of(carrier);
        this.emptyCarrier = false;
        return this;
    }

    public StockpotRecipeBuilder setEmptyCarrier() {
        this.carrier = Ingredient.of(Items.AIR);
        this.emptyCarrier = true;
        return this;
    }

    public StockpotRecipeBuilder setCookingTexture(Identifier cookingTexture) {
        this.cookingTexture = cookingTexture;
        return this;
    }

    public StockpotRecipeBuilder setFinishedTexture(Identifier finishedTexture) {
        this.finishedTexture = finishedTexture;
        return this;
    }

    public StockpotRecipeBuilder setCookingBubbleColor(int cookingBubbleColor) {
        this.cookingBubbleColor = cookingBubbleColor;
        return this;
    }

    public StockpotRecipeBuilder setFinishedBubbleColor(int finishedBubbleColor) {
        this.finishedBubbleColor = finishedBubbleColor;
        return this;
    }

    public StockpotRecipeBuilder setBubbleColors(int cookingBubbleColor, int finishedBubbleColor) {
        this.cookingBubbleColor = cookingBubbleColor;
        this.finishedBubbleColor = finishedBubbleColor;
        return this;
    }

    @Override
    public @NonNull RecipeBuilder unlockedBy(@NonNull String name, @NonNull Criterion<?> criterion) {
        return this;
    }

    @Override
    public @NonNull RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    public Item getResult() {
        return this.result.item().value();
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        String path = RecipeBuilder.getDefaultRecipeId((ItemInstance) this.getResult()).identifier().getPath();
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + path));
    }

    @Override
    public void save(@NonNull RecipeOutput output) {
        String path = RecipeBuilder.getDefaultRecipeId((ItemInstance) this.getResult()).identifier().getPath();
        Identifier filePath = Identifier.fromNamespaceAndPath(
                KaleidoscopeCookery.MOD_ID, NAME + "/" + path
        );
        this.save(output, filePath);
    }

    @Override
    public void save(@NonNull RecipeOutput output, @NonNull String recipeId) {
        Identifier filePath = Identifier.fromNamespaceAndPath(
                KaleidoscopeCookery.MOD_ID, NAME + "/" + recipeId
        );
        this.save(output, filePath);
    }

    public void save(RecipeOutput recipeOutput, Identifier id) {
        this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, id));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NonNull ResourceKey<Recipe<?>> id) {
        StockpotRecipe recipe = new StockpotRecipe(
                this.ingredients, this.soupBase, this.result,
                this.time, this.carrier, this.cookingTexture, this.finishedTexture,
                this.cookingBubbleColor, this.finishedBubbleColor
        );
        recipeOutput.accept(id, recipe, null);
    }

}
