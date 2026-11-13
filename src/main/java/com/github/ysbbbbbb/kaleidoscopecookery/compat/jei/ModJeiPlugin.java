package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.*;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.RiceBowlRecipeMaker;
import com.github.ysbbbbbb.kaleidoscopecookery.util.recipes.ModRecipesLibrary;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "jei");

    @Override
    public void registerCategories(@NonNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FlexPotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ChoppingBoardRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new StockpotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FlexStockpotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MillstoneRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SteamerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new TeapotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new BambooTrayRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NonNull IRecipeRegistration registration) {
        registration.addRecipes(PotRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.potRecipes());
        registration.addRecipes(FlexPotRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.flexPotRecipes());
        registration.addRecipes(ChoppingBoardRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.choppingBoardRecipes());
        registration.addRecipes(StockpotRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.stockpotRecipes());
        registration.addRecipes(FlexStockpotRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.flexStockpotRecipes());
        registration.addRecipes(MillstoneRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.millstoneRecipes());
        registration.addRecipes(SteamerRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.steamerRecipes());
        registration.addRecipes(TeapotRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.teapotRecipes());
        registration.addRecipes(BambooTrayRecipeCategory.TYPE, ModRecipesLibrary.INSTANCE.bambooTrayRecipes());

        registration.addRecipes(RecipeTypes.CRAFTING, RiceBowlRecipeMaker.createRecipes());
    }

    @Override
    public void registerRecipeCatalysts(@NonNull IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(PotRecipeCategory.TYPE, ModItems.POT.getDefaultInstance());
        registration.addCraftingStation(FlexPotRecipeCategory.TYPE, ModItems.POT.getDefaultInstance());
        registration.addCraftingStation(ChoppingBoardRecipeCategory.TYPE, ModItems.CHOPPING_BOARD.getDefaultInstance());
        registration.addCraftingStation(StockpotRecipeCategory.TYPE, ModItems.STOCKPOT.getDefaultInstance());
        registration.addCraftingStation(FlexStockpotRecipeCategory.TYPE, ModItems.STOCKPOT.getDefaultInstance());
        registration.addCraftingStation(MillstoneRecipeCategory.TYPE, ModItems.MILLSTONE.getDefaultInstance());
        registration.addCraftingStation(SteamerRecipeCategory.TYPE, ModItems.STEAMER.getDefaultInstance());
        registration.addCraftingStation(TeapotRecipeCategory.TYPE, ModItems.TEAPOT.getDefaultInstance());
        registration.addCraftingStation(BambooTrayRecipeCategory.TYPE, ModItems.BAMBOO_TRAY.getDefaultInstance());
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return UID;
    }
}
