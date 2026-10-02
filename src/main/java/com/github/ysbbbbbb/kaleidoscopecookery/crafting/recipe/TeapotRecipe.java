package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record TeapotRecipe(Identifier teaFluid,
                           Optional<TagKey<Fluid>> teaFluidTag,
                           Ingredient ingredient,
                           int ingredientCount,
                           int time,
                           ItemStackTemplate result) implements BaseRecipe<TeapotInput> {
    public static final int OUTPUT_COUNT = 12;
    public static final int MYSTERY_OUTPUT_COUNT = 4;

    public TeapotRecipe(Identifier teaFluid, Ingredient ingredient, int ingredientCount,
                        int time, ItemStackTemplate result) {
        this(teaFluid, Optional.empty(), ingredient, ingredientCount, time, result);
    }

    @Override
    public boolean matches(TeapotInput container, @NonNull Level level) {
        ItemStack stack = container.getItemStack();
        Identifier fluid = container.getTeaFluid();
        boolean fluidMatches = teaFluidTag.map(tag -> ModFluids.matchesFluidTag(fluid, tag))
                .orElseGet(() -> ModFluids.matchesTeaFluid(teaFluid, fluid));
        return fluidMatches
                && ingredient.test(stack)
                && stack.getCount() >= ingredientCount;
    }

    public Identifier displayTeaFluid() {
        if (teaFluidTag.isEmpty()) {
            return teaFluid;
        }
        return BuiltInRegistries.FLUID.entrySet().stream()
                .filter(entry -> entry.getValue() != Fluids.EMPTY && entry.getValue().is(teaFluidTag.get()))
                .map(entry -> BuiltInRegistries.FLUID.getKey(entry.getValue()))
                .findFirst()
                .orElse(teaFluid);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result.create();
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull TeapotInput container) {
        return this.result.create().copyWithCount(OUTPUT_COUNT);
    }

    @Override
    public @NonNull RecipeSerializer<? extends Recipe<TeapotInput>> getSerializer() {
        return ModRecipes.TEAPOT_SERIALIZER;
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<TeapotInput>> getType() {
        return ModRecipes.TEAPOT_RECIPE;
    }

    @Override
    public @NonNull String group() {
        return "teapot";
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.create(this.ingredient);
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.TEAPOT_CATEGORY;
    }
}
