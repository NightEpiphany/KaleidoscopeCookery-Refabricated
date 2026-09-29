package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.DatagenIngredients;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;

public class ChoppingBoardBuilder implements RecipeBuilder {
    private static final String NAME = "chopping_board";

    private Ingredient ingredient = Ingredient.of();
    private List<ItemStackTemplate> results = List.of();
    private int cutCount = 3;
    private Identifier modelId;

    public static ChoppingBoardBuilder builder() {
        return new ChoppingBoardBuilder();
    }

    public ChoppingBoardBuilder setIngredient(ItemLike itemLike) {
        this.ingredient = Ingredient.of(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setIngredient(TagKey<Item> itemLike) {
        this.ingredient = DatagenIngredients.tag(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemStack stack) {
        this.results = List.of(ItemStackTemplate.fromNonEmptyStack(stack));
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemLike itemLike) {
        this.results = List.of(new ItemStackTemplate(itemLike.asItem()));
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemLike itemLike, int count) {
        this.results = List.of(new ItemStackTemplate(itemLike.asItem(), count));
        return this;
    }

    public ChoppingBoardBuilder setResults(ItemStack... stacks) {
        this.results = Arrays.stream(stacks).map(ItemStackTemplate::fromNonEmptyStack).toList();
        return this;
    }

    public ChoppingBoardBuilder setCutCount(int cutCount) {
        this.cutCount = Math.max(cutCount, 1);
        return this;
    }

    public ChoppingBoardBuilder setModelId(Identifier modelId) {
        this.modelId = modelId;
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
        return this.results.getFirst().item().value();
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        String path = RecipeBuilder.getDefaultRecipeId((ItemInstance) this.getResult()).identifier().getPath();
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + path));
    }

    @Override
    public void save(@NonNull RecipeOutput output) {
        String path = RecipeBuilder.getDefaultRecipeId((ItemInstance) this.getResult()).identifier().getPath();
        Identifier filePath = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + path);
        this.save(output, filePath);
    }

    @Override
    public void save(@NonNull RecipeOutput output, @NonNull String recipeId) {
        Identifier filePath = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + recipeId);
        this.save(output, filePath);
    }

    public void save(RecipeOutput recipeOutput, Identifier id) {
        this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, id));
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NonNull ResourceKey<Recipe<?>> id) {
        ChoppingBoardRecipe recipe = new ChoppingBoardRecipe(this.ingredient, this.results, this.cutCount, this.modelId);
        recipeOutput.accept(id, recipe, null);
    }
}
