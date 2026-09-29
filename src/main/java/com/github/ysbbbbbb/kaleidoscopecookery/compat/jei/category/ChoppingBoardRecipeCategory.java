package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ChoppingBoardRecipeCategory implements IRecipeCategory<RecipeHolder<ChoppingBoardRecipe>> {
    public static final IRecipeHolderType<ChoppingBoardRecipe> TYPE = IRecipeType.create(ModRecipes.CHOPPING_BOARD_RECIPE);

    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/chopping_board.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.chopping_board");

    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    private final IDrawable bgDraw;
    private final IDrawable iconDraw;

    public ChoppingBoardRecipeCategory(IGuiHelper guiHelper) {
        this.bgDraw = guiHelper.createDrawable(BG, 0, 0, WIDTH, HEIGHT);
        this.iconDraw = guiHelper.createDrawableItemStack(ModItems.CHOPPING_BOARD.getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChoppingBoardRecipe> recipe, @NonNull IFocusGroup focuses) {
        Ingredient input = recipe.value().getIngredient();
        var outputs = recipe.value().getResults();

        builder.addSlot(RecipeIngredientRole.INPUT, 38, 27).add(input);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 30).add(outputs.getFirst().create());
        int startX = 128 - (outputs.size() - 2) * 12;
        for (int index = 1; index < outputs.size(); index++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (index - 1) * 24, 54)
                    .setStandardSlotBackground().add(outputs.get(index).create());
        }
    }

    @Override
    public void draw(@NonNull RecipeHolder<ChoppingBoardRecipe> recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.bgDraw.draw(guiGraphics);
    }


    @Override
    public @NonNull IRecipeType<RecipeHolder<ChoppingBoardRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NonNull Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return iconDraw;
    }
}
