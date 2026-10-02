package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
public record TeapotRecipe(ResourceLocation teaFluid,
                           Optional<TagKey<Fluid>> teaFluidTag,
                           Ingredient ingredient, int ingredientCount,
                           int time, ItemStack result
) implements BaseRecipe<TeapotInput> {
    /**
     * 配方强制输出 12 个
     */
    public static final int OUTPUT_COUNT = 12;

    public TeapotRecipe(ResourceLocation teaFluid, Ingredient ingredient, int ingredientCount,
                        int time, ItemStack result) {
        this(teaFluid, Optional.empty(), ingredient, ingredientCount, time, result);
    }

    @Override
    public boolean matches(TeapotInput container, Level level) {
        ItemStack stack = container.getItemStack();
        ResourceLocation fluid = container.getTeaFluid();
        boolean fluidMatches = teaFluidTag.map(tag -> ModFluids.matchesFluidTag(fluid, tag))
                .orElseGet(() -> ModFluids.matchesTeaFluid(teaFluid, fluid));
        return fluidMatches && ingredient.test(stack) && stack.getCount() >= ingredientCount;
    }

    public ResourceLocation displayTeaFluid() {
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
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result;
    }

    @Override
    public @NotNull ItemStack assemble(TeapotInput container, HolderLookup.Provider registryAccess) {
        return getResultItem(registryAccess).copyWithCount(OUTPUT_COUNT);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.TEAPOT_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.TEAPOT_RECIPE;
    }
}
