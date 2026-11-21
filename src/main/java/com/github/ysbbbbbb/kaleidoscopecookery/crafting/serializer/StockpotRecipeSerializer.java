package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

public class StockpotRecipeSerializer {
    public static final int DEFAULT_TIME = 300;
    public static final int DEFAULT_COOKING_BUBBLE_COLOR = 0xFFECC3;
    public static final int DEFAULT_FINISHED_BUBBLE_COLOR = 0xF4AA8B;
    public static final Ingredient DEFAULT_CARRIER = Ingredient.of(Items.BOWL);
    public static final Identifier DEFAULT_SOUP_BASE = ModSoupBases.WATER;
    public static final Identifier EMPTY_ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot/empty");
    public static final ResourceKey<Recipe<?>> EMPTY_RECIPE_KEY = ResourceKey.create(Registries.RECIPE, EMPTY_ID);
    public static final Identifier DEFAULT_COOKING_TEXTURE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot/default_cooking");
    public static final Identifier DEFAULT_FINISHED_TEXTURE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot/default_finished");

    public static RecipeHolder<StockpotRecipe> getEmptyRecipe() {
        StockpotRecipe stockpotRecipe = new StockpotRecipe(Lists.newArrayList(), DEFAULT_SOUP_BASE,
                new ItemStackTemplate(Items.BARRIER), DEFAULT_TIME, DEFAULT_CARRIER,
                DEFAULT_COOKING_TEXTURE, DEFAULT_FINISHED_TEXTURE,
                DEFAULT_COOKING_BUBBLE_COLOR,
                DEFAULT_FINISHED_BUBBLE_COLOR);
        return new RecipeHolder<>(EMPTY_RECIPE_KEY, stockpotRecipe);
    }

    private static final MapCodec<StockpotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients().stream().toList()),
            Identifier.CODEC.optionalFieldOf("soup_base", DEFAULT_SOUP_BASE).forGetter(StockpotRecipe::soupBase),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(StockpotRecipe::result),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(StockpotRecipe::time),
            Ingredient.CODEC.optionalFieldOf("carrier", DEFAULT_CARRIER).forGetter(StockpotRecipe::carrier),
            Identifier.CODEC.optionalFieldOf("cooking_texture", DEFAULT_COOKING_TEXTURE).forGetter(StockpotRecipe::cookingTexture),
            Identifier.CODEC.optionalFieldOf("finished_texture", DEFAULT_FINISHED_TEXTURE).forGetter(StockpotRecipe::finishedTexture),
            Codec.INT.optionalFieldOf("cooking_bubble_color", DEFAULT_COOKING_BUBBLE_COLOR).forGetter(StockpotRecipe::cookingBubbleColor),
            Codec.INT.optionalFieldOf("finished_bubble_color", DEFAULT_FINISHED_BUBBLE_COLOR).forGetter(StockpotRecipe::finishedBubbleColor),
            Codec.BOOL.optionalFieldOf("syrup", false).forGetter(StockpotRecipe::syrup)
    ).apply(instance, StockpotRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, StockpotRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getIngredients());
                Identifier.STREAM_CODEC.encode(buf, recipe.soupBase());
                ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result());
                buf.writeInt(recipe.time());
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.carrier());
                Identifier.STREAM_CODEC.encode(buf, recipe.cookingTexture());
                Identifier.STREAM_CODEC.encode(buf, recipe.finishedTexture());
                buf.writeInt(recipe.cookingBubbleColor());
                buf.writeInt(recipe.finishedBubbleColor());
                buf.writeBoolean(recipe.syrup());
            },
            buf -> new StockpotRecipe(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                    Identifier.STREAM_CODEC.decode(buf),
                    ItemStackTemplate.STREAM_CODEC.decode(buf),
                    buf.readInt(),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                    Identifier.STREAM_CODEC.decode(buf),
                    Identifier.STREAM_CODEC.decode(buf),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean()
            ));

    public static @NotNull MapCodec<StockpotRecipe> codec() {
        return CODEC;
    }

    public static @NotNull StreamCodec<RegistryFriendlyByteBuf, StockpotRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
