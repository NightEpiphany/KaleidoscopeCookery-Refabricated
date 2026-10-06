package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class RiceBowlRecipe extends CustomRecipe {
    public static final TagKey<Item> COOKED_RICE = TagCommon.COOKED_RICE;

    private final Ingredient ingredient;
    private final ItemStackTemplate result;
    private final CraftingBookCategory category;

    public RiceBowlRecipe(CraftingBookCategory category, Ingredient ingredient, ItemStackTemplate result) {
        super();
        this.category = category;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingInput container, @NotNull Level level) {
        List<ItemStack> items = container.items();

        // 有且只能有一个物品匹配 ingredient
        if (items.stream().filter(ingredient).count() != 1) {
            return false;
        }

        // 有且只能有一个物品匹配 COOKED_RICE
        return items.stream().filter(stack -> stack.is(COOKED_RICE)).count() == 1;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput container) {
        ItemStack assembled = result.create();
        copyBestQuality(container, assembled);
        return assembled;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NonNull CraftingInput container) {
        NonNullList<ItemStack> remainingItems = CraftingRecipe.defaultCraftingReminder(container);
        for (int i = 0; i < container.size(); i++) {
            if (container.getItem(i).is(COOKED_RICE)) {
                // 米饭的碗成为盖浇饭容器，不应作为合成剩余物返还。
                remainingItems.set(i, Items.BOWL.getDefaultInstance());
            }
        }
        return remainingItems;
    }


    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NonNull CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public @NotNull RecipeType<CraftingRecipe> getType() {
        return super.getType();
    }


    public @NotNull NonNullList<Ingredient> getIngredients(HolderLookup.Provider registries) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.ingredient);
        ingredients.add(Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(COOKED_RICE)));
        return ingredients;
    }


    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.create();
    }

    public ItemStackTemplate getResult() {
        return result;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.RICE_BOWL_SERIALIZER;
    }

    private static void copyBestQuality(CraftingInput container, ItemStack result) {
        boolean hasQuality = false;
        Quality bestQuality = Quality.POOR;

        for (int i = 0; i < container.size(); i++) {
            ItemStack ingredient = container.getItem(i);
            if (!QualityUtils.hasQuality(ingredient)) {
                continue;
            }

            Quality quality = QualityUtils.getQuality(ingredient);
            if (!hasQuality || quality.getScore() > bestQuality.getScore()) {
                bestQuality = quality;
                hasQuality = true;
            }
        }

        if (hasQuality) {
            QualityUtils.setQuality(result, bestQuality);
        }
    }
}
