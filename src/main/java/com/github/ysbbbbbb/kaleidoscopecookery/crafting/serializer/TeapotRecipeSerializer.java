package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.util.forge.CraftingHelper;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeapotRecipeSerializer implements RecipeSerializer<TeapotRecipe> {
    public static final int DEFAULT_TIME = 2400;
    public static final int DEFAULT_INGREDIENT_COUNT = 12;

    public static final ResourceLocation EMPTY_TEA_FLUID = new ResourceLocation("empty");

    @Override
    public @NotNull TeapotRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
        Ingredient ingredient = Ingredient.EMPTY;
        ResourceLocation baseTeaFluid = EMPTY_TEA_FLUID;
        Optional<TagKey<Fluid>> teaFluidTag = Optional.empty();
        if (json.has("tea_fluid")) {
            baseTeaFluid = new ResourceLocation(GsonHelper.getAsString(json, "tea_fluid"));
        }
        if (json.has("tea_fluid_tag")) {
            teaFluidTag = Optional.of(TagKey.create(Registries.FLUID,
                    new ResourceLocation(GsonHelper.getAsString(json, "tea_fluid_tag"))));
        }
        if (json.has("ingredient")) {
            ingredient = Ingredient.fromJson(json.get("ingredient"));
        }
        int ingredientCount = GsonHelper.getAsInt(json, "ingredient_count", DEFAULT_INGREDIENT_COUNT);
        int time = GsonHelper.getAsInt(json, "time", DEFAULT_TIME);
        ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true, true);
        return new TeapotRecipe(recipeId, baseTeaFluid, teaFluidTag, ingredient, ingredientCount, time, result);
    }

    @Override
    public @NotNull TeapotRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buf) {
        ResourceLocation baseTeaFluid = buf.readResourceLocation();
        Optional<TagKey<Fluid>> teaFluidTag = buf.readBoolean()
                ? Optional.of(TagKey.create(Registries.FLUID, buf.readResourceLocation()))
                : Optional.empty();
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        int ingredientCount = buf.readVarInt();
        int time = buf.readVarInt();
        ItemStack result = buf.readItem();
        return new TeapotRecipe(recipeId, baseTeaFluid, teaFluidTag, ingredient, ingredientCount, time, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, TeapotRecipe recipe) {
        buf.writeResourceLocation(recipe.teaFluid());
        buf.writeBoolean(recipe.teaFluidTag().isPresent());
        recipe.teaFluidTag().ifPresent(tag -> buf.writeResourceLocation(tag.location()));
        recipe.ingredient().toNetwork(buf);
        buf.writeVarInt(recipe.ingredientCount());
        buf.writeVarInt(recipe.time());
        buf.writeItem(recipe.result());
    }

    @Override
    public String toString() {
        return "teapot";
    }
}
