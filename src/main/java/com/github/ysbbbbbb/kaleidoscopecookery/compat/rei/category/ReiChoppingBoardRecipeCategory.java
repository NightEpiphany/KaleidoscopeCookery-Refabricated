package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReiChoppingBoardRecipeCategory implements DisplayCategory<ReiChoppingBoardRecipeCategory.ChoppingBoardRecipeDisplay> {
    public static final CategoryIdentifier<ChoppingBoardRecipeDisplay> ID = CategoryIdentifier.of(KaleidoscopeCookery.MOD_ID, "plugin/chopping_board");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.chopping_board");
    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/chopping_board.png");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    @Override
    public CategoryIdentifier<? extends ChoppingBoardRecipeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public List<Widget> setupDisplay(ChoppingBoardRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.x;
        int startY = bounds.y;

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(BG, startX, startY, 0, 0, WIDTH, HEIGHT));
        widgets.add(Widgets.createSlot(new Point(startX + 38, startY + 27))
                .entries(display.getInputEntries().getFirst())
                .disableBackground()
                .markInput());
        widgets.add(Widgets.createSlot(new Point(startX + 128, startY + 30))
                .entries(display.getOutputEntries().getFirst())
                .disableBackground()
                .markOutput());

        int outputStartX = 128 - (display.getOutputEntries().size() - 2) * 12;
        for (int index = 1; index < display.getOutputEntries().size(); index++) {
            widgets.add(Widgets.createSlot(new Point(startX + outputStartX + (index - 1) * 24, startY + 54))
                    .entries(display.getOutputEntries().get(index)).markOutput());
        }
        return widgets;
    }

    @Override
    public int getDisplayWidth(ChoppingBoardRecipeDisplay display) {
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
        return EntryStacks.of(ModItems.CHOPPING_BOARD);
    }

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiChoppingBoardRecipeCategory());
        registry.addWorkstations(ReiChoppingBoardRecipeCategory.ID,
                ReiUtil.ofItem(ModItems.CHOPPING_BOARD),
                ReiUtil.ofIngredient(Ingredient.of(ModItems.DIAMOND_KITCHEN_KNIFE, ModItems.IRON_KITCHEN_KNIFE, ModItems.GOLD_KITCHEN_KNIFE, ModItems.NETHERITE_KITCHEN_KNIFE, ModItems.COPPER_KITCHEN_KNIFE)));
    }

    public static class ChoppingBoardRecipeDisplay extends BasicDisplay {

        public static final DisplaySerializer<ChoppingBoardRecipeDisplay> SERIALIZER = DisplaySerializer.of(
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Identifier.CODEC.fieldOf("location").forGetter(r -> r.getDisplayLocation().orElse(Identifier.withDefaultNamespace("air"))),
                        EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(ChoppingBoardRecipeDisplay::getInputEntries),
                        EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(ChoppingBoardRecipeDisplay::getOutputEntries)
                ).apply(instance, ChoppingBoardRecipeDisplay::new)),
                StreamCodec.composite(
                        Identifier.STREAM_CODEC, r -> r.getDisplayLocation().orElse(Identifier.withDefaultNamespace("air")),
                        EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                        ChoppingBoardRecipeDisplay::getInputEntries,
                        EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                        ChoppingBoardRecipeDisplay::getOutputEntries,
                        ChoppingBoardRecipeDisplay::new
                ));

        public ChoppingBoardRecipeDisplay(Identifier identifier, List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
            super(inputs, outputs, Optional.of(identifier));
        }

        public ChoppingBoardRecipeDisplay(RecipeHolder<ChoppingBoardRecipe> holder) {
            this(holder.id().identifier(), ReiUtil.ofIngredients(holder.value().getIngredient()), holder.value().getResults().stream().flatMap(result -> ReiUtil.ofItemStacks(result.create()).stream()).toList());
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
