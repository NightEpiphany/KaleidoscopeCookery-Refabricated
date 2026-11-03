package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.BambooTrayRecipeSerializer;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class BambooTrayRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "bamboo_tray";

    private final BambooTrayRecipe.Subtype subtype;
    private ItemStackTemplate result;
    private Ingredient ingredient = Ingredient.of(Items.AIR);
    private int duration = BambooTrayRecipeSerializer.DEFAULT_DURATION;

    private BambooTrayRecipeBuilder(BambooTrayRecipe.Subtype subtype) {
        this.subtype = subtype;
    }

    public static BambooTrayRecipeBuilder wetting() {
        return new BambooTrayRecipeBuilder(BambooTrayRecipe.Subtype.WETTING);
    }

    public static BambooTrayRecipeBuilder drying() {
        return new BambooTrayRecipeBuilder(BambooTrayRecipe.Subtype.DRYING);
    }

    public BambooTrayRecipeBuilder setIngredient(ItemLike itemLike) {
        this.ingredient = Ingredient.of(itemLike);
        return this;
    }

    public BambooTrayRecipeBuilder setIngredient(TagKey<Item> tag) {
        this.ingredient = DatagenIngredients.tag(tag);
        return this;
    }

    public BambooTrayRecipeBuilder setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public BambooTrayRecipeBuilder setResult(ItemStack stack) {
        this.result = ItemStackTemplate.fromNonEmptyStack(stack);
        return this;
    }

    public BambooTrayRecipeBuilder setResult(ItemLike itemLike) {
        this.result = new ItemStackTemplate(itemLike.asItem());
        return this;
    }

    public BambooTrayRecipeBuilder setResult(ItemLike itemLike, int count) {
        this.result = new ItemStackTemplate(itemLike.asItem(), count);
        return this;
    }

    public BambooTrayRecipeBuilder setDuration(int duration) {
        this.duration = Math.max(duration, 1);
        return this;
    }

    public Item getResult() {
        return this.result.item().value();
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NonNull String criterionName, @NonNull Criterion<?> criterionTrigger) {
        return this;
    }


    @Override
    public @NotNull RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        String path = RecipeBuilder.getDefaultRecipeId((ItemInstance) this.getResult()).identifier().getPath();
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + path));
    }

    @Override
    public void save(@NonNull RecipeOutput output, @NonNull ResourceKey<Recipe<?>> location) {

    }
}
