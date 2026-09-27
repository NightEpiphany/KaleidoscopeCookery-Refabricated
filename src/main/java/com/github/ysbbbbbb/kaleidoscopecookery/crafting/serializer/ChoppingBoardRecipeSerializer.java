package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.util.forge.CraftingHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ChoppingBoardRecipeSerializer implements RecipeSerializer<ChoppingBoardRecipe> {
    @Override
    public @NonNull ChoppingBoardRecipe fromJson(@NonNull ResourceLocation recipeId, @NonNull JsonObject json) {
        Ingredient ingredient;
        if (GsonHelper.isArrayNode(json, "ingredient")) {
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"), false);
        } else {
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"), false);
        }
        List<ItemStack> results = new ArrayList<>();
        if (GsonHelper.isArrayNode(json, "result")) {
            JsonArray resultArray = GsonHelper.getAsJsonArray(json, "result");
            resultArray.forEach(element -> results.add(CraftingHelper.getItemStack(element.getAsJsonObject(), true, true)));
        } else {
            results.add(CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true, true));
        }
        int cutCount = GsonHelper.getAsInt(json, "cut_count", 3);
        ResourceLocation modelId = new ResourceLocation(GsonHelper.getAsString(json, "model_id", ""));
        return new ChoppingBoardRecipe(recipeId, ingredient, results, cutCount, modelId);
    }

    @Override
    public @NonNull ChoppingBoardRecipe fromNetwork(@NonNull ResourceLocation recipeId, @NonNull FriendlyByteBuf buffer) {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        int resultCount = buffer.readVarInt();
        List<ItemStack> results = new ArrayList<>();
        for (int i = 0; i < resultCount; i++) {
            results.add(buffer.readItem());
        }
        int cutCount = buffer.readVarInt();
        ResourceLocation modelId = buffer.readResourceLocation();
        return new ChoppingBoardRecipe(recipeId, ingredient, results, cutCount, modelId);
    }

    @Override
    public void toNetwork(@NonNull FriendlyByteBuf buffer, ChoppingBoardRecipe recipe) {
        recipe.getIngredient().toNetwork(buffer);
        buffer.writeVarInt(recipe.getResults().size());
        recipe.getResults().forEach(buffer::writeItem);
        buffer.writeVarInt(recipe.getCutCount());
        buffer.writeResourceLocation(recipe.getModelId());
    }

    @Override
    public String toString() {
        return "chopping_board";
    }
}
