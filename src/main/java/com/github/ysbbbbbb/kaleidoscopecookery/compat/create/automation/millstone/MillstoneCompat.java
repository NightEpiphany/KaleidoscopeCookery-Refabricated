package com.github.ysbbbbbb.kaleidoscopecookery.compat.create.automation.millstone;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.MillstoneMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.output.RandomOutput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.MillstoneRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.util.event.IKCEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.content.kinetics.millstone.MillingRecipe;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class MillstoneCompat {
    public static void register(){
        IKCEvent.CALLBACK.register(event -> {
            if (event instanceof MillstoneMatchRecipeEvent.Post postEvent) {
                afterMillstoneRecipeMatch(postEvent);
            }
        });
    }
    // 供 JEI、REI、EMI 查询的工具类
    public static void getTransformRecipeForSearch(Level level, List<RecipeHolder<MillstoneRecipe>> recipes) {
        if (level.recipeAccess() instanceof RecipeManager recipeManager && !level.isClientSide()) {
            RecipeType<MillingRecipe> type = AllRecipeTypes.MILLING;
            recipeManager.getSynchronizedRecipes().getAllOfType(type).forEach(recipe -> {
                Ingredient ingredient = recipe.value().ingredient();
                for (ItemStack stack : ingredient.items().map(s -> s.value().getDefaultInstance()).toList()) {
                    SimpleInput input = new SimpleInput(List.of(stack));
                    // 如果机械动力的配方和本模组配方有重合，优先选择本模组的配方
                    if (recipeManager.getRecipeFor(ModRecipes.MILLSTONE_RECIPE, input, level).isPresent()) {
                        return;
                    }
                }
                recipes.add(transformRecipe(recipe));
            });
        }
    }

    public static RecipeHolder<MillstoneRecipe> transformRecipe(RecipeHolder<MillingRecipe> holder) {
        List<RandomOutput> outputs = holder.value()
                .results()
                .stream()
                .map(getOutputFunction())
                .toList();
        Ingredient ingredient = holder.value().ingredient();
        MillstoneRecipe recipe = new MillstoneRecipe(ingredient, outputs);
        return new RecipeHolder<>(holder.id(), recipe);
    }

    @NotNull
    private static Function<ProcessingOutput, RandomOutput> getOutputFunction() {
        return output -> new RandomOutput(ItemStackTemplate.fromNonEmptyStack(output.item().value().getDefaultInstance()), output.chance());
    }


    private static void afterMillstoneRecipeMatch(MillstoneMatchRecipeEvent.Post event) {
        RecipeHolder<MillstoneRecipe> rawOutput = event.getRawOutput();
        if (rawOutput.id().identifier() != MillstoneRecipeSerializer.EMPTY_ID) {
            return;
        }

        ItemStack inputStack = event.getInput().getItem(0);
        if (inputStack.isEmpty()) {
            return;
        }

        event.getLevel().recipeAccess().getSynchronizedRecipes().getAllOfType(AllRecipeTypes.MILLING).forEach(recipe -> {
            Ingredient ingredient = recipe.value().ingredient();
            if (ingredient.test(inputStack)) {
                event.setOutput(transformRecipe(recipe));
            }
        });
    }
}
