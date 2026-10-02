package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
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

public class ReiSteamerRecipeCategory implements DisplayCategory<ReiSteamerRecipeCategory.SteamerRecipeDisplay> {
    public static final CategoryIdentifier<SteamerRecipeDisplay> ID = CategoryIdentifier.of(KaleidoscopeCookery.MOD_ID, "plugin/steamer");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.steamer");
    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/steamer.png");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    @Override
    public CategoryIdentifier<? extends SteamerRecipeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public List<Widget> setupDisplay(SteamerRecipeDisplay display, Rectangle bounds) {
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

        return widgets;
    }

    @Override
    public int getDisplayWidth(SteamerRecipeDisplay display) {
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
        return EntryStacks.of(ModItems.STEAMER);
    }

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiSteamerRecipeCategory());
        registry.addWorkstations(ReiSteamerRecipeCategory.ID,
                ReiUtil.ofItem(ModItems.STEAMER),
                ReiUtil.ofIngredient(Ingredient.of(ModItems.DIAMOND_KITCHEN_KNIFE, ModItems.IRON_KITCHEN_KNIFE, ModItems.GOLD_KITCHEN_KNIFE, ModItems.NETHERITE_KITCHEN_KNIFE, ModItems.COPPER_KITCHEN_KNIFE)));
    }

    public static class SteamerRecipeDisplay extends BasicDisplay {

        public static final DisplaySerializer<SteamerRecipeDisplay> SERIALIZER = DisplaySerializer.of(
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Identifier.CODEC.fieldOf("location").forGetter(r -> r.getDisplayLocation().orElse(Identifier.withDefaultNamespace("air"))),
                        EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(SteamerRecipeDisplay::getInputEntries),
                        EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(SteamerRecipeDisplay::getOutputEntries)
                ).apply(instance, SteamerRecipeDisplay::new)),
                StreamCodec.composite(
                        Identifier.STREAM_CODEC, r -> r.getDisplayLocation().orElse(Identifier.withDefaultNamespace("air")),
                        EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                        SteamerRecipeDisplay::getInputEntries,
                        EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                        SteamerRecipeDisplay::getOutputEntries,
                        SteamerRecipeDisplay::new
                ));

        public SteamerRecipeDisplay(Identifier identifier, List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
            super(inputs, outputs, Optional.of(identifier));
        }

        public SteamerRecipeDisplay(RecipeHolder<SteamerRecipe> holder) {
            this(holder.id().identifier(), ReiUtil.ofIngredients(holder.value().getIngredient()), ReiUtil.ofItemStacks(holder.value().getResult().create()));
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
