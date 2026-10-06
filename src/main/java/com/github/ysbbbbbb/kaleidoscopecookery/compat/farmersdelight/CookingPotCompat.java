package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.StockpotMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.List;
import java.util.Optional;

public class CookingPotCompat {
    static void getTransformRecipeForJei(Level level, List<StockpotRecipe> recipes) {
        if (level == null) {
            return;
        }
        RecipeManager recipeManager = level.getRecipeManager();
        RecipeType<CookingPotRecipe> cookingPotRecipeRecipeType = ModRecipeTypes.COOKING.get();
        recipeManager.getAllRecipesFor(cookingPotRecipeRecipeType).forEach(recipe -> {
            recipes.add(transformRecipe(recipe, level));
        });
    }

    static StockpotRecipe transformRecipe(CookingPotRecipe cookingPotRecipe, Level level) {
        // 默认全部使用水作为汤底
        return new StockpotRecipe(
                cookingPotRecipe.getId(),
                cookingPotRecipe.getIngredients(),
                cookingPotRecipe.getResultItem(level.registryAccess()),
                cookingPotRecipe.getCookTime(),
                cookingPotRecipe.getOutputContainer()
        );
    }

    static void afterStockpotRecipeMatch(StockpotMatchRecipeEvent.Post event) {
        if (!event.getRawOutput().equals(StockpotRecipeSerializer.EMPTY_ID)) {
            return;
        }
        afterStockpotRecipeMatch(event, event.getLevel().getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.COOKING.get()));
    }

    static void afterStockpotRecipeMatch(StockpotMatchRecipeEvent.Post event, List<CookingPotRecipe> recipes) {
        if (!event.getRawOutput().equals(StockpotRecipeSerializer.EMPTY_ID)) {
            return;
        }

        // 开始寻找农夫乐事的厨锅配方进行匹配
        findMatchingRecipe(recipes, event.getContainer().getItems()).ifPresent(recipe -> {
            // 如果找到匹配的农夫乐事厨锅配方，则将其转换为本模组汤锅配方
            event.setOutput(transformRecipe(recipe, event.getLevel()));
        });
    }

    static Optional<CookingPotRecipe> findMatchingRecipe(List<CookingPotRecipe> recipes, List<ItemStack> items) {
        StackedContents contents = new StackedContents();
        int ingredientCount = 0;
        for (int slot = 0; slot < CookingPotRecipe.INPUT_SLOTS; slot++) {
            ItemStack item = slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
            if (!item.isEmpty()) {
                ingredientCount++;
                contents.accountStack(item, 1);
            }
        }
        for (CookingPotRecipe recipe : recipes) {
            if (ingredientCount == recipe.getIngredients().size() && contents.canCraft(recipe, null)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }
}
