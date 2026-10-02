package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReiTeapotRecipeCategory implements DisplayCategory<ReiTeapotRecipeCategory.TeapotRecipeDisplay> {
    public static final CategoryIdentifier<TeapotRecipeDisplay> ID = CategoryIdentifier.of(KaleidoscopeCookery.MOD_ID, "plugin/teapot");
    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/teapot.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.teapot");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    @Override
    public CategoryIdentifier<TeapotRecipeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public List<Widget> setupDisplay(TeapotRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>(7);
        int startX = bounds.x;
        int startY = bounds.y;
        Component brewTime = Component.translatable("jei.kaleidoscope_cookery.teapot.time", display.brewTime / 20);

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(BG, startX, startY, 0, 0, WIDTH, HEIGHT));
        widgets.add(Widgets.withTranslate(Widgets.createDrawableWidget((guiGraphics, _, _, _) -> drawCenteredString(guiGraphics, brewTime)), startX, startY));
        widgets.add(Widgets.createSlot(new Point(startX + 65, startY))
                .entries(display.getInputEntries().get(0))
                .markInput());
        widgets.add(Widgets.createSlot(new Point(startX + 83, startY))
                .entries(display.getInputEntries().get(1))
                .markInput());
        widgets.add(Widgets.createSlot(new Point(startX + 122, startY))
                .entry(EntryStacks.of(ModItems.EMPTY_CUP))
                .disableBackground());
        widgets.add(Widgets.createSlot(new Point(startX + 128, startY + 45))
                .entries(display.getOutputEntries().getFirst())
                .backgroundEnabled(false)
                .markOutput());

        return widgets;
    }

    private void drawCenteredString(GuiGraphics guiGraphics, Component text) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, text, 88 - font.width(text) / 2, 70, 0x555555, false);
    }

    @Override
    public int getDisplayWidth(TeapotRecipeDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModItems.TEAPOT);
    }

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiTeapotRecipeCategory());
        registry.addWorkstations(ReiTeapotRecipeCategory.ID, ReiUtil.ofItem(ModItems.TEAPOT));
    }

    public static class TeapotRecipeDisplay extends BasicDisplay {
        public final int brewTime;
        protected final List<EntryIngredient> fluidInput;
        protected final List<EntryIngredient> ingredientInput;

        public static final DisplaySerializer<TeapotRecipeDisplay> SERIALIZER = DisplaySerializer.of(
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        Identifier.CODEC.fieldOf("location").forGetter(r -> r.getDisplayLocation().orElse(Identifier.withDefaultNamespace("air"))),
                        EntryIngredient.codec().listOf().fieldOf("tea_fluid").forGetter(TeapotRecipeDisplay::getFluidInput),
                        EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(TeapotRecipeDisplay::getIngredientInput),
                        EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(TeapotRecipeDisplay::getOutputEntries),
                        Codec.INT.fieldOf("brew_time").forGetter(TeapotRecipeDisplay::getBrewTime)

                ).apply(inst, TeapotRecipeDisplay::new)),
                StreamCodec.composite(
                        Identifier.STREAM_CODEC, r -> r.getDisplayLocation().orElse(Identifier.withDefaultNamespace("air")),
                        EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), TeapotRecipeDisplay::getFluidInput,
                        EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), TeapotRecipeDisplay::getIngredientInput,
                        EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), TeapotRecipeDisplay::getOutputEntries,
                        ByteBufCodecs.INT, TeapotRecipeDisplay::getBrewTime,
                        TeapotRecipeDisplay::new
                ));

        public TeapotRecipeDisplay(Identifier location,
                                   List<EntryIngredient> fluidInput,
                                   List<EntryIngredient> inputs,
                                   List<EntryIngredient> outputs, int brewTime) {
            super(List.of(fluidInput.getFirst(), getRecipeIngredientInput(inputs)), outputs, Optional.of(location));
            this.fluidInput = fluidInput;
            this.ingredientInput = List.of(getRecipeIngredientInput(inputs));
            this.brewTime = brewTime;
        }

        public TeapotRecipeDisplay(RecipeHolder<TeapotRecipe> holder) {
            this(holder.id().identifier(), ReiUtil.ofItemStacks(TeaFluidHelper.getFilledContainer(holder.value().displayTeaFluid())), getIngredientInputs(holder.value()), ReiUtil.ofItemStacks(holder.value().result().create().copyWithCount(TeapotRecipe.OUTPUT_COUNT)), holder.value().time());
        }

        public List<EntryIngredient> getFluidInput() {
            return fluidInput;
        }

        public List<EntryIngredient> getIngredientInput() {
            return ingredientInput;
        }

        public int getBrewTime() {
            return brewTime;
        }

        private static EntryIngredient getRecipeIngredientInput(List<EntryIngredient> inputs) {
            return inputs.size() > 1 ? inputs.get(1) : inputs.getFirst();
        }

        @SuppressWarnings("deprecation")
        private static List<EntryIngredient> getIngredientInputs(TeapotRecipe recipe) {
            List<ItemStack> inputs = recipe.ingredient().items()
                    .map(stack -> stack.value().getDefaultInstance().copyWithCount(recipe.ingredientCount()))
                    .toList();
            return ReiUtil.ofItemStacks(inputs.toArray(ItemStack[]::new));
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return ID;
        }

        @Override
        public @Nullable DisplaySerializer<? extends Display> getSerializer() {
            return SERIALIZER;
        }
    }
}
