package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.DatagenIngredients;
import net.minecraft.advancements.triggers.Criterion;
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

public class SteamerBuilder implements RecipeBuilder {
    private static final String NAME = "steamer";

    private Ingredient ingredient = Ingredient.of(Items.AIR);
    private ItemStackTemplate result;
    private int cookTick = 60 * 20;

    public static SteamerBuilder builder() {
        return new SteamerBuilder();
    }

    public SteamerBuilder setIngredient(ItemLike itemLike) {
        this.ingredient = Ingredient.of(itemLike);
        return this;
    }

    public SteamerBuilder setIngredient(TagKey<Item> itemLike) {
        this.ingredient = DatagenIngredients.tag(itemLike);
        return this;
    }

    public SteamerBuilder setResult(ItemStack stack) {
        this.result = ItemStackTemplate.fromNonEmptyStack(stack);
        return this;
    }

    public SteamerBuilder setResult(ItemLike itemLike) {
        this.result = new ItemStackTemplate(itemLike.asItem());
        return this;
    }

    public SteamerBuilder setResult(ItemLike itemLike, int count) {
        this.result = new ItemStackTemplate(itemLike.asItem(), count);
        return this;
    }

    public SteamerBuilder setCookTick(int cookTick) {
        this.cookTick = Math.max(cookTick, 1);
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
        SteamerRecipe recipe = new SteamerRecipe(this.ingredient, this.result, this.cookTick);
        recipeOutput.accept(id, recipe, null);
    }
}
