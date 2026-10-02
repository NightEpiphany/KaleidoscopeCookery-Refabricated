package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TeapotRecipeCategory implements IRecipeCategory<RecipeHolder<TeapotRecipe>> {
    public static final IRecipeHolderType<TeapotRecipe> TYPE = IRecipeType.create(ModRecipes.TEAPOT_RECIPE);

    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/teapot.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.teapot");

    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    private final IDrawable bgDraw;
    private final IDrawable iconDraw;

    public TeapotRecipeCategory(IGuiHelper guiHelper) {
        this.bgDraw = guiHelper.createDrawable(BG, 0, 0, WIDTH, HEIGHT);
        this.iconDraw = guiHelper.createDrawableItemLike(ModItems.TEAPOT);
    }

    @Override
    public void draw(RecipeHolder<TeapotRecipe> recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.bgDraw.draw(guiGraphics);
        Component brewTime = Component.translatable("jei.kaleidoscope_cookery.teapot.time", recipe.value().time() / 20);
        drawCenteredString(guiGraphics, brewTime);
    }

    private void drawCenteredString(GuiGraphicsExtractor guiGraphics, Component text) {
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = text.getVisualOrderText();
        guiGraphics.text(font, sequence, 88 - font.width(sequence) / 2, 70, 0x555555, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TeapotRecipe> holder, @NonNull IFocusGroup focuses) {
        TeapotRecipe recipe = holder.value();
        List<ItemStack> inputs = recipe.ingredient().items()
                .map(stack -> stack.value().getDefaultInstance().copyWithCount(recipe.ingredientCount()))
                .toList();
        ItemStack output = recipe.result().create().copyWithCount(TeapotRecipe.OUTPUT_COUNT);

        Item bucket = TeaFluidHelper.getFilledContainer(recipe.displayTeaFluid()).getItem();

        builder.addSlot(RecipeIngredientRole.INPUT, 65, 0).setStandardSlotBackground().add(bucket);
        builder.addSlot(RecipeIngredientRole.INPUT, 83, 0).setStandardSlotBackground().addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 122, 0).add(ModItems.EMPTY_CUP);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 45).add(output);
    }

    @Override
    public @NonNull IRecipeType<RecipeHolder<TeapotRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
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
