package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.DatagenIngredients;
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

public class FlexPotRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "flex_pot";
    private int time = 200;
    private int stirFryCount = 3;
    private Ingredient carrier = Ingredient.of(Items.AIR);
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private ItemStackTemplate result;

    public static FlexPotRecipeBuilder builder() {
        return new FlexPotRecipeBuilder();
    }

    public FlexPotRecipeBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public FlexPotRecipeBuilder setStirFryCount(int stirFryCount) {
        this.stirFryCount = stirFryCount;
        return this;
    }

    public FlexPotRecipeBuilder setCarrier(Ingredient ingredient) {
        this.carrier = ingredient;
        return this;
    }

    public FlexPotRecipeBuilder setCarrier(TagKey<Item> tagKey) {
        this.carrier = DatagenIngredients.tag(tagKey);
        return this;
    }

    public FlexPotRecipeBuilder setCarrier(ItemLike itemLike) {
        this.carrier = Ingredient.of(itemLike);
        return this;
    }

    public FlexPotRecipeBuilder setBowlCarrier() {
        this.carrier = Ingredient.of(Items.BOWL);
        return this;
    }

    @SuppressWarnings({"varargs", "all"})
    public FlexPotRecipeBuilder addInput(Object... ingredients) {
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

    public FlexPotRecipeBuilder setResult(Item result) {
        this.result = new ItemStackTemplate(result.asItem());
        return this;
    }

    public FlexPotRecipeBuilder setResult(Identifier result) {
        this.result = new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(result));
        return this;
    }

    public FlexPotRecipeBuilder setResult(Item result, int count) {
        this.result = new ItemStackTemplate(result.asItem(), count);
        return this;
    }

    public FlexPotRecipeBuilder setResult(Identifier result, int count) {
        this.result = new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(result), count);
        return this;
    }

    public FlexPotRecipeBuilder setResult(ItemStack result) {
        this.result = ItemStackTemplate.fromNonEmptyStack(result);
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
        FlexPotRecipe recipe = new FlexPotRecipe(this.time, this.stirFryCount, this.carrier, this.ingredients, this.result);
        recipeOutput.accept(id, recipe, null);
    }
}
