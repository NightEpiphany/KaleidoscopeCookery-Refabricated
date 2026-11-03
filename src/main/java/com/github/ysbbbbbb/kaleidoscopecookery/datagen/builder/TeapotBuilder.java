package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.DatagenIngredients;
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
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class TeapotBuilder implements RecipeBuilder {
    private static final String NAME = "teapot";

    private Identifier teaFluid = TeapotRecipeSerializer.EMPTY_TEA_FLUID;
    private Ingredient ingredient = Ingredient.of(Items.AIR);
    private int ingredientCount = TeapotRecipeSerializer.DEFAULT_INGREDIENT_COUNT;
    private int time = TeapotRecipeSerializer.DEFAULT_TIME;
    private ItemStackTemplate result;

    public static TeapotBuilder builder() {
        return new TeapotBuilder();
    }

    public TeapotBuilder setTeaFluid(Identifier baseTeaFluid) {
        this.teaFluid = baseTeaFluid;
        return this;
    }

    public TeapotBuilder setTeaFluid(Fluid fluid) {
        this.teaFluid = BuiltInRegistries.FLUID.getKey((fluid));
        return this;
    }

    @SuppressWarnings("all")
    public TeapotBuilder setIngredient(Object ingredient) {
        if (ingredient instanceof ItemLike itemLike) {
            this.ingredient = Ingredient.of(itemLike);
        } else if (ingredient instanceof ItemStack stack) {
            this.ingredient = Ingredient.of(stack.getItem());
        } else if (ingredient instanceof TagKey tagKey) {
            this.ingredient = DatagenIngredients.tag(tagKey);
        } else if (ingredient instanceof Ingredient ingredientObj) {
            this.ingredient = ingredientObj;
        } else {
            throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient.getClass().getName());
        }
        return this;
    }

    public TeapotBuilder setIngredientCount(int ingredientCount) {
        this.ingredientCount = ingredientCount;
        return this;
    }

    public TeapotBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public TeapotBuilder setResult(ItemStack result) {
        this.result = ItemStackTemplate.fromNonEmptyStack(result);
        return this;
    }

    public TeapotBuilder setResult(ItemLike result) {
        this.result = new ItemStackTemplate(result.asItem());
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
        recipeOutput.accept(id, new TeapotRecipe(this.teaFluid, this.ingredient, this.ingredientCount, this.time, this.result), null);
    }
}
