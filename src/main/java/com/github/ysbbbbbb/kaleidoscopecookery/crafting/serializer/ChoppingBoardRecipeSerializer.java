package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChoppingBoardRecipeSerializer {
    public static final Identifier EMPTY = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "empty");
    public static final Codec<List<ItemStackTemplate>> RESULTS_CODEC = Codec.either(
            ItemStackTemplate.CODEC, ItemStackTemplate.CODEC.listOf(1, Integer.MAX_VALUE)).xmap(
            either -> either.map(List::of, List::copyOf),
            list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list));
    private static final MapCodec<ChoppingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(ChoppingBoardRecipe::getIngredient),
                    RESULTS_CODEC.fieldOf("result").forGetter(ChoppingBoardRecipe::getResults),
                    Codec.INT.optionalFieldOf("cut_count", 3).forGetter(ChoppingBoardRecipe::getCutCount),
                    Identifier.CODEC.optionalFieldOf("model_id", EMPTY).forGetter(ChoppingBoardRecipe::getModelId)
            ).apply(instance, ChoppingBoardRecipe::new)
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, ChoppingBoardRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ChoppingBoardRecipe::getIngredient,
            ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), ChoppingBoardRecipe::getResults,
            ByteBufCodecs.INT, ChoppingBoardRecipe::getCutCount,
            Identifier.STREAM_CODEC, ChoppingBoardRecipe::getModelId,
            ChoppingBoardRecipe::new);


    public static @NotNull MapCodec<ChoppingBoardRecipe> codec() {
        return CODEC;
    }

    public static @NotNull StreamCodec<RegistryFriendlyByteBuf, ChoppingBoardRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
