package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class RecipeRandomlyFunction extends LootItemConditionalFunction {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_randomly");
    public static final MapCodec<RecipeRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(
                    RecipeItem.RecipeRecord.CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(f -> f.possibleRecipes)
            ).apply(instance, RecipeRandomlyFunction::new)
    );
    private final List<RecipeItem.RecipeRecord> possibleRecipes;

    protected RecipeRandomlyFunction(List<LootItemCondition> predicates, List<RecipeItem.RecipeRecord> possibleRecipes) {
        super(predicates);
        this.possibleRecipes = ImmutableList.copyOf(possibleRecipes);
    }

    @Override
    public @NonNull MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull ItemStack run(@NonNull ItemStack stack, LootContext context) {
        RandomSource randomsource = context.getRandom();
        RecipeItem.RecipeRecord record;
        // 如果配置了配方，则从配置的配方中随机一个
        if (!this.possibleRecipes.isEmpty()) {
            record = this.possibleRecipes.get(randomsource.nextInt(this.possibleRecipes.size()));
            RecipeItem.setRecipe(stack, record);
            return stack;
        }

        // 否则从所有模型食物中随机一个
        List<Identifier> keys = FoodBiteRegistry.FOOD_DATA_MAP.keySet().stream().toList();
        if (keys.isEmpty()) {
            return stack;
        }
        Identifier randomKey = keys.get(randomsource.nextInt(keys.size()));
        Item result = FoodBiteRegistry.getItem(randomKey);
        RegistryAccess registryAccess = context.getLevel().registryAccess();

        // 炒锅配方
        var potRecipes = context.getLevel().recipeAccess().getAllOfType(ModRecipes.POT_RECIPE);
        for (var recipeHolder : potRecipes) {
            PotRecipe recipe = recipeHolder.value();
            ItemStack resultItem = recipe.getResultItem(registryAccess);
            if (!resultItem.is(result)) {
                continue;
            }
            List<ItemStack> inputs = new ArrayList<>(List.of());
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.isEmpty()) continue;
                ingredient.items().forEach(itemHolder -> inputs.add(new ItemStack(itemHolder.value())));
            }
            record = new RecipeItem.RecipeRecord(inputs, resultItem, RecipeItem.POT);
            RecipeItem.setRecipe(stack, record);
            return stack;
        }

        // 汤锅配方
        var stockpotRecipes = context.getLevel().recipeAccess().getAllOfType(ModRecipes.STOCKPOT_RECIPE);
        for (var recipeHolder : stockpotRecipes) {
            StockpotRecipe recipe = recipeHolder.value();
            ItemStack resultItem = recipe.getResultItem(registryAccess);
            if (!resultItem.is(result)) {
                continue;
            }
            List<ItemStack> inputs = new ArrayList<>(List.of());
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.isEmpty()) continue;
                ingredient.items().forEach(itemHolder -> inputs.add(new ItemStack(itemHolder.value())));
            }
            record = new RecipeItem.RecipeRecord(inputs, resultItem, RecipeItem.STOCKPOT, recipe.soupBase());
            RecipeItem.setRecipe(stack, record);
            return stack;
        }

        // 茶壶配方
        var teaPotRecipes = context.getLevel().recipeAccess().getAllOfType(ModRecipes.TEAPOT_RECIPE);
        for (var recipeHolder : teaPotRecipes) {
            TeapotRecipe recipe = recipeHolder.value();
            ItemStack resultItem = recipe.getResultItem(registryAccess);
            if (!resultItem.is(result)) {
                continue;
            }
            List<ItemStack> inputs = new ArrayList<>(List.of());
            if (!recipe.ingredient().isEmpty()) {
                // 茶壶配方需要 ingredientCount 个原料，记录真实数量以保证后续放入/扣除一致
                recipe.ingredient().items().forEach(itemHolder -> inputs.add(new ItemStack(itemHolder.value(), recipe.ingredientCount())));
            }
            record = new RecipeItem.RecipeRecord(inputs, resultItem, RecipeItem.TEAPOT, recipe.displayTeaFluid());
            RecipeItem.setRecipe(stack, record);
            return stack;
        }

        return stack;
    }

    public static Builder randomRecipe() {
        return new Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final List<RecipeItem.RecipeRecord> recipes = Lists.newArrayList();

        @Override
        protected @NotNull Builder getThis() {
            return this;
        }

        public Builder withRecord(RecipeItem.RecipeRecord record) {
            this.recipes.add(record);
            return this;
        }

        public Builder pot(ItemLike output, ItemLike... input) {
            List<ItemStack> list = Arrays.stream(input).map(ItemStack::new).toList();
            RecipeItem.RecipeRecord record = new RecipeItem.RecipeRecord(list, new ItemStack(output), RecipeItem.POT);
            return withRecord(record);
        }

        public Builder stockpot(ItemLike output, ItemLike... input) {
            List<ItemStack> list = Arrays.stream(input).map(ItemStack::new).toList();
            RecipeItem.RecipeRecord record = new RecipeItem.RecipeRecord(list, new ItemStack(output), RecipeItem.STOCKPOT);
            return withRecord(record);
        }

        public Builder teapot(ItemLike output, ItemLike... input) {
            List<ItemStack> list = Arrays.stream(input).map(ItemStack::new).toList();
            RecipeItem.RecipeRecord record = new RecipeItem.RecipeRecord(list, new ItemStack(output), RecipeItem.TEAPOT);
            return withRecord(record);
        }

        @Override
        public @NonNull LootItemFunction build() {
            return new RecipeRandomlyFunction(this.getConditions(), this.recipes);
        }
    }
}
