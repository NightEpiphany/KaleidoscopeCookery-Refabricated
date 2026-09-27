package com.github.ysbbbbbb.kaleidoscopecookery.crafting;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.ChoppingBoardRecipeSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChoppingBoardRecipeTest {
    private static RegistryAccess registries;
    private static RegistryOps<JsonElement> ops;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        ops = RegistryOps.create(JsonOps.INSTANCE, registries);
    }

    @Test
    void readsLegacySingleResult() {
        ChoppingBoardRecipe recipe = parse("""
                {"ingredient":{"item":"minecraft:chicken"},"result":{"id":"minecraft:bone","count":2}}
                """);
        assertEquals(1, recipe.getResults().size());
        assertEquals(2, recipe.getResults().getFirst().getCount());
        assertTrue(recipe.getResult().is(Items.BONE));
    }

    @Test
    void readsAndWritesMultipleResults() {
        ChoppingBoardRecipe recipe = parse("""
                {"ingredient":{"item":"minecraft:chicken"},"cut_count":4,
                 "result":[{"id":"minecraft:chicken","count":3},{"id":"minecraft:bone"}]}
                """);
        assertEquals(2, recipe.getResults().size());
        assertEquals(3, recipe.getResults().getFirst().getCount());
        JsonElement encoded = ChoppingBoardRecipeSerializer.CODEC.codec().encodeStart(ops, recipe).getOrThrow();
        ChoppingBoardRecipe decoded = ChoppingBoardRecipeSerializer.CODEC.codec().parse(ops, encoded).getOrThrow();
        assertEquals(4, decoded.getCutCount());
        assertTrue(decoded.getResults().get(1).is(Items.BONE));
    }

    @Test
    void networkRoundTripPreservesAllOutputs() {
        ChoppingBoardRecipe recipe = parse("""
                {"ingredient":{"item":"minecraft:chicken"},"cut_count":4,
                 "model_id":"kaleidoscope_cookery:raw_chicken",
                 "result":[{"id":"minecraft:chicken","count":3},{"id":"minecraft:bone"}]}
                """);
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        try {
            ChoppingBoardRecipeSerializer.STREAM_CODEC.encode(buffer, recipe);
            ChoppingBoardRecipe decoded = ChoppingBoardRecipeSerializer.STREAM_CODEC.decode(buffer);
            assertEquals(recipe.getModelId(), decoded.getModelId());
            assertEquals(recipe.getCutCount(), decoded.getCutCount());
            assertEquals(2, decoded.getResults().size());
            assertEquals(3, decoded.getResults().getFirst().getCount());
            assertTrue(decoded.getResults().get(1).is(Items.BONE));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void resultsCannotMutateTheRecipe() {
        ItemStack result = new ItemStack(Items.BONE, 2);
        ChoppingBoardRecipe recipe = new ChoppingBoardRecipe(Ingredient.of(Items.CHICKEN),
                List.of(result), 4, ResourceLocation.withDefaultNamespace("empty"));
        result.shrink(1);
        recipe.getResults().getFirst().shrink(1);
        assertEquals(2, recipe.getResults().getFirst().getCount());
        assertEquals(2, recipe.getResult().getCount());
    }

    private static ChoppingBoardRecipe parse(String json) {
        return ChoppingBoardRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString(json)).getOrThrow();
    }
}
