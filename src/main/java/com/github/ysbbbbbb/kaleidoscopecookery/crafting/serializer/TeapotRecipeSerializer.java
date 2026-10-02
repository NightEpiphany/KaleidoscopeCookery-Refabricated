package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class TeapotRecipeSerializer {
    // Recipe "time" and the mystery tea fallback use ticks (20 ticks per second).
    public static final int DEFAULT_TIME = 2400;
    public static final int DEFAULT_INGREDIENT_COUNT = 12;
    public static final Identifier EMPTY_TEA_FLUID = Identifier.withDefaultNamespace("empty");

    private static final MapCodec<TeapotRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.optionalFieldOf("tea_fluid", EMPTY_TEA_FLUID).forGetter(TeapotRecipe::teaFluid),
            TagKey.codec(Registries.FLUID).optionalFieldOf("tea_fluid_tag").forGetter(TeapotRecipe::teaFluidTag),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(TeapotRecipe::ingredient),
            Codec.INT.optionalFieldOf("ingredient_count", DEFAULT_INGREDIENT_COUNT).forGetter(TeapotRecipe::ingredientCount),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(TeapotRecipe::time),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(TeapotRecipe::result)
    ).apply(inst, TeapotRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<TagKey<Fluid>>> FLUID_TAG_STREAM_CODEC = StreamCodec.of(
            (buf, tag) -> {
                buf.writeBoolean(tag.isPresent());
                tag.ifPresent(value -> Identifier.STREAM_CODEC.encode(buf, value.location()));
            },
            buf -> buf.readBoolean()
                    ? Optional.of(TagKey.create(Registries.FLUID, Identifier.STREAM_CODEC.decode(buf)))
                    : Optional.empty());

    private static final StreamCodec<RegistryFriendlyByteBuf, TeapotRecipe> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TeapotRecipe::teaFluid,
            FLUID_TAG_STREAM_CODEC, TeapotRecipe::teaFluidTag,
            Ingredient.CONTENTS_STREAM_CODEC, TeapotRecipe::ingredient,
            ByteBufCodecs.VAR_INT, TeapotRecipe::ingredientCount,
            ByteBufCodecs.VAR_INT, TeapotRecipe::time,
            ItemStackTemplate.STREAM_CODEC, TeapotRecipe::result,
            TeapotRecipe::new
    );

    public static @NonNull MapCodec<TeapotRecipe> codec() {
        return CODEC;
    }

    public static @NonNull StreamCodec<RegistryFriendlyByteBuf, TeapotRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
