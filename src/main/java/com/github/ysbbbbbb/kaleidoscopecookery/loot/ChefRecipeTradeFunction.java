package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ChefRecipeTradeFunction extends LootItemConditionalFunction {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "trade_custom_recipe");
    public static final MapCodec<ChefRecipeTradeFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).and(
                    RecipeItem.RecipeTemplate.CODEC.optionalFieldOf("recipe", RecipeItem.RecipeTemplate.EMPTY).forGetter(function -> function.recipe)
            ).apply(instance, ChefRecipeTradeFunction::new)
    );
    private final RecipeItem.RecipeTemplate recipe;

    protected ChefRecipeTradeFunction(final Optional<Holder<LootItemCondition>> predicates, RecipeItem.RecipeTemplate recipe) {
        super(predicates);
        this.recipe = recipe;
    }

    @Override
    public @NonNull MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull ItemStack run(@NonNull ItemStack itemStack, @NonNull LootContext context) {
        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof RecipeItem) || this.recipe == RecipeItem.RecipeTemplate.EMPTY) {
            return itemStack;
        }
        RecipeItem.setRecipe(itemStack, this.recipe.toRecipeRecord());
        return itemStack;
    }
}
