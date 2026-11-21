package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.util.RecipeMatcher;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer.*;
import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;

public record StockpotRecipe(NonNullList<Ingredient> ingredients,
                             Identifier soupBase,
                             ItemStackTemplate result,
                             int time,
                             Ingredient carrier,
                             Identifier cookingTexture,
                             Identifier finishedTexture,
                             int cookingBubbleColor,
                             int finishedBubbleColor,
                             boolean syrup) implements BaseRecipe<StockpotInput> {

    public StockpotRecipe(List<Ingredient> ingredients, Identifier soupBase, ItemStackTemplate result,
                          int time, Ingredient carrier, Identifier cookingTexture, Identifier finishedTexture,
                          int cookingBubbleColor, int finishedBubbleColor) {
        this(createNonNullList(ingredients),
                soupBase, result, time, carrier, cookingTexture, finishedTexture,
                cookingBubbleColor, finishedBubbleColor, false);
    }

    public StockpotRecipe(List<Ingredient> ingredients, Identifier soupBase, ItemStackTemplate result,
                          int time, Ingredient carrier, Identifier cookingTexture, Identifier finishedTexture,
                          int cookingBubbleColor, int finishedBubbleColor, boolean syrup) {
        this(createNonNullList(ingredients), soupBase, result, time, carrier, cookingTexture, finishedTexture,
                cookingBubbleColor, finishedBubbleColor, syrup);
    }

    private static NonNullList<Ingredient> createNonNullList(List<Ingredient> ingredients) {
        // 直接转换，不进行任何验证
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }

    public StockpotRecipe(NonNullList<Ingredient> ingredients, ItemStackTemplate result, int time, ItemStack container) {
        this(ingredients, DEFAULT_SOUP_BASE, result, time, Ingredient.of(container.getItem()),
                DEFAULT_COOKING_TEXTURE, DEFAULT_FINISHED_TEXTURE,
                DEFAULT_COOKING_BUBBLE_COLOR, DEFAULT_FINISHED_BUBBLE_COLOR, false);
    }

    @Override
    public boolean matches(StockpotInput container, @NonNull Level level) {
        // 检查汤底是否匹配
        if (!container.getSoupBase().equals(this.soupBase)) {
            return false;
        }

        // 只考虑非空的输入槽位
        List<ItemStack> nonEmptyInputs = container.getInputs().stream()
                .filter(stack -> !stack.isEmpty())
                .toList();

        // 创建临时的Ingredient列表进行匹配（过滤掉已知的占位符）
        List<Ingredient> recipeIngredients = this.ingredients.stream()
                .filter(ing -> !isKnownPlaceholder(ing))
                .toList();

        // 数量必须匹配
        if (nonEmptyInputs.size() != recipeIngredients.size()) {
            return false;
        }

        return RecipeMatcher.findMatches(nonEmptyInputs, recipeIngredients) != null;
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull StockpotInput input) {
        return this.result.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "stockpot";
    }

    @SuppressWarnings("deprecation")
    private static boolean isKnownPlaceholder(Ingredient ingredient) {
        // 只检查是否是明确的屏障方块，避免触发标签绑定
        try {
            var optionalHolder = ingredient.items().findFirst();
            if (optionalHolder.isPresent()) {
                var holder = optionalHolder.get();
                return holder.value() == Items.AIR;
            }
        } catch (Exception e) {
            // 如果出错，认为是标签，不是占位符
            return false;
        }
        return false;
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return this.result.create();
    }

    @Override
    public @NonNull RecipeSerializer<? extends Recipe<StockpotInput>> getSerializer() {
        return ModRecipes.STOCKPOT_SERIALIZER;
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<StockpotInput>> getType() {
        return ModRecipes.STOCKPOT_RECIPE;
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.STOCKPOT_CATEGORY;
    }
}
