package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.*;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.*;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {
    public static final RecipeSerializer<PotRecipe> POT_SERIALIZER = new RecipeSerializer<>(PotRecipeSerializer.codec(), PotRecipeSerializer.streamCodec());
    public static final RecipeSerializer<FlexPotRecipe> FLEX_POT_SERIALIZER = new RecipeSerializer<>(FlexPotRecipeSerializer.codec(), FlexPotRecipeSerializer.streamCodec());
    public static final RecipeSerializer<ChoppingBoardRecipe> CHOPPING_BOARD_SERIALIZER = new RecipeSerializer<>(ChoppingBoardRecipeSerializer.codec(), ChoppingBoardRecipeSerializer.streamCodec());
    public static final RecipeSerializer<StockpotRecipe> STOCKPOT_SERIALIZER = new RecipeSerializer<>(StockpotRecipeSerializer.codec(), StockpotRecipeSerializer.streamCodec());
    public static final RecipeSerializer<FlexStockpotRecipe> FLEX_STOCKPOT_SERIALIZER = new RecipeSerializer<>(FlexStockpotRecipeSerializer.codec(), FlexStockpotRecipeSerializer.streamCodec());
    public static final RecipeSerializer<SteamerRecipe> STEAMER_SERIALIZER = new RecipeSerializer<>(SteamerRecipeSerializer.codec(), SteamerRecipeSerializer.streamCodec());
    public static final RecipeSerializer<MillstoneRecipe> MILLSTONE_SERIALIZER = new RecipeSerializer<>(MillstoneRecipeSerializer.codec(), MillstoneRecipeSerializer.streamCodec());
    public static final RecipeSerializer<TeapotRecipe> TEAPOT_SERIALIZER = new RecipeSerializer<>(TeapotRecipeSerializer.codec(), TeapotRecipeSerializer.streamCodec());
    public static final RecipeSerializer<BambooTrayRecipe> BAMBOO_TRAY_SERIALIZER = new RecipeSerializer<>(BambooTrayRecipeSerializer.codec(), BambooTrayRecipeSerializer.streamCodec());
    public static final RecipeSerializer<RiceBowlRecipe> RICE_BOWL_SERIALIZER  = new RecipeSerializer<>(RiceBowlRecipeSerializer.codec(), RiceBowlRecipeSerializer.streamCodec());


    public static final RecipeType<PotRecipe> POT_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot"));
    public static final RecipeType<FlexPotRecipe> FLEX_POT_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_pot"));
    public static final RecipeType<ChoppingBoardRecipe> CHOPPING_BOARD_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board"));
    public static final RecipeType<StockpotRecipe> STOCKPOT_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"));
    public static final RecipeType<FlexStockpotRecipe> FLEX_STOCKPOT_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_stockpot"));
    public static final RecipeType<SteamerRecipe> STEAMER_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer"));
    public static final RecipeType<MillstoneRecipe> MILLSTONE_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone"));
    public static final RecipeType<TeapotRecipe> TEAPOT_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"));
    public static final RecipeType<BambooTrayRecipe> BAMBOO_TRAY_RECIPE = simple(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray"));

    public static final RecipeBookCategory POT_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory FLEX_POT_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory CHOPPING_BOARD_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory STOCKPOT_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory FLEX_STOCKPOT_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory STEAMER_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory MILLSTONE_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory TEAPOT_CATEGORY = new RecipeBookCategory();
    public static final RecipeBookCategory BAMBOO_TRAY_CATEGORY = new RecipeBookCategory();

    private static void syncRecipes() {
        // 投料交互需要在客户端匹配配方，避免继续执行食材自身的右键使用逻辑。
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.POT_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.FLEX_POT_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.CHOPPING_BOARD_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.STOCKPOT_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.FLEX_STOCKPOT_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.STEAMER_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.MILLSTONE_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.TEAPOT_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.RICE_BOWL_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.BAMBOO_TRAY_SERIALIZER);
    }

    public static void registerRecipes() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot"), POT_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_pot"), FLEX_POT_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board"), CHOPPING_BOARD_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"), STOCKPOT_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_stockpot"), FLEX_STOCKPOT_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer"), STEAMER_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone"), MILLSTONE_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"), TEAPOT_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray"), BAMBOO_TRAY_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "rice_bowl"), RICE_BOWL_SERIALIZER);


        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot"), POT_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_pot"), FLEX_POT_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board"), CHOPPING_BOARD_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"), STOCKPOT_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_stockpot"), FLEX_STOCKPOT_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer"), STEAMER_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone"), MILLSTONE_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"), TEAPOT_RECIPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray"), BAMBOO_TRAY_RECIPE);

        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot"), POT_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_pot"), FLEX_POT_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board"), CHOPPING_BOARD_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"), STOCKPOT_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_stockpot"), FLEX_STOCKPOT_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer"), STEAMER_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone"), MILLSTONE_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"), TEAPOT_CATEGORY);
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray"), BAMBOO_TRAY_CATEGORY);

        syncRecipes();
    }

    private static <T extends Recipe<?>> RecipeType<T> simple(final Identifier id) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
    }
}
