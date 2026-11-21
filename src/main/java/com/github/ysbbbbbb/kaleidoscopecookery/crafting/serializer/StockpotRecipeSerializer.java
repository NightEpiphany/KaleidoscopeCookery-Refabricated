package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotVisuals;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StockpotRecipeSerializer implements RecipeSerializer<StockpotRecipe> {
    public static final int DEFAULT_TIME = 300;
    public static final Ingredient DEFAULT_CARRIER = Ingredient.of(Items.BOWL);
    public static final ResourceLocation DEFAULT_SOUP_BASE = ModSoupBases.WATER;
    public static final ResourceLocation EMPTY_ID = ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot/empty");

    public static RecipeHolder<StockpotRecipe> getEmptyRecipe() {
        StockpotRecipe stockpotRecipe = new StockpotRecipe(
                Lists.newArrayList(),
                DEFAULT_SOUP_BASE,
                ItemStack.EMPTY,
                DEFAULT_TIME,
                DEFAULT_CARRIER,
                StockpotVisuals.DEFAULT
        );
        return new RecipeHolder<>(EMPTY_ID, stockpotRecipe);
    }

    public static final MapCodec<StockpotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").xmap(
                    list -> list,
                    list -> list.stream().filter(i -> !i.isEmpty()).toList()
            ).forGetter(StockpotRecipe::getIngredients),
            ResourceLocation.CODEC.optionalFieldOf("soup_base", DEFAULT_SOUP_BASE).forGetter(StockpotRecipe::soupBase),
            ItemStack.CODEC.fieldOf("result").forGetter(StockpotRecipe::result),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(StockpotRecipe::time),
            Ingredient.CODEC.optionalFieldOf("carrier", DEFAULT_CARRIER).forGetter(StockpotRecipe::carrier),
            StockpotVisuals.CODEC.forGetter(StockpotRecipe::visuals),
            Codec.BOOL.optionalFieldOf("syrup", false).forGetter(StockpotRecipe::syrup)
    ).apply(instance, StockpotRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENTS_STREAM_CODEC =
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, StockpotRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                INGREDIENTS_STREAM_CODEC.encode(buf, recipe.getIngredients());
                ResourceLocation.STREAM_CODEC.encode(buf, recipe.soupBase());
                ItemStack.STREAM_CODEC.encode(buf, recipe.result());
                buf.writeInt(recipe.time());
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.carrier());
                StockpotVisuals.STREAM_CODEC.encode(buf, recipe.visuals());
                buf.writeBoolean(recipe.syrup());
            },
            buf -> new StockpotRecipe(
                    INGREDIENTS_STREAM_CODEC.decode(buf),
                    ResourceLocation.STREAM_CODEC.decode(buf),
                    ItemStack.STREAM_CODEC.decode(buf),
                    buf.readInt(),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                    StockpotVisuals.STREAM_CODEC.decode(buf),
                    buf.readBoolean()
            )
    );

    @Override
    public @NotNull MapCodec<StockpotRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, StockpotRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
