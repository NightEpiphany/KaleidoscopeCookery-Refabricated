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
import net.minecraft.resources.Identifier;
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
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(net.minecraft.data.registries.VanillaRegistries.createLookup())
                .forEach(net.minecraft.core.component.DataComponentInitializers.PendingComponents::apply);
        registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        ops = RegistryOps.create(JsonOps.INSTANCE, registries);
    }

    @Test
    void readsLegacySingleResult() {
        ChoppingBoardRecipe recipe = parse("""
                {"ingredient":"minecraft:chicken","result":{"id":"minecraft:bone","count":2}}
                """);
        assertEquals(1, recipe.getResults().size());
        assertEquals(2, recipe.getResults().getFirst().count());
        assertTrue(recipe.getResult().create().is(Items.BONE));
    }

    @Test
    void readsAndWritesMultipleResults() {
        ChoppingBoardRecipe recipe = parse("""
                {"ingredient":"minecraft:chicken","cut_count":4,
                 "result":[{"id":"minecraft:chicken","count":3},{"id":"minecraft:bone"}]}
                """);
        assertEquals(2, recipe.getResults().size());
        assertEquals(3, recipe.getResults().getFirst().count());
        JsonElement encoded = ChoppingBoardRecipeSerializer.codec().codec().encodeStart(ops, recipe).getOrThrow();
        ChoppingBoardRecipe decoded = ChoppingBoardRecipeSerializer.codec().codec().parse(ops, encoded).getOrThrow();
        assertEquals(4, decoded.getCutCount());
        assertTrue(decoded.getResults().get(1).create().is(Items.BONE));
    }

    @Test
    void networkRoundTripPreservesAllOutputs() {
        ChoppingBoardRecipe recipe = parse("""
                {"ingredient":"minecraft:chicken","cut_count":4,
                 "model_id":"kaleidoscope_cookery:raw_chicken",
                 "result":[{"id":"minecraft:chicken","count":3},{"id":"minecraft:bone"}]}
                """);
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        try {
            ChoppingBoardRecipeSerializer.streamCodec().encode(buffer, recipe);
            ChoppingBoardRecipe decoded = ChoppingBoardRecipeSerializer.streamCodec().decode(buffer);
            assertEquals(recipe.getModelId(), decoded.getModelId());
            assertEquals(recipe.getCutCount(), decoded.getCutCount());
            assertEquals(2, decoded.getResults().size());
            assertEquals(3, decoded.getResults().getFirst().count());
            assertTrue(decoded.getResults().get(1).create().is(Items.BONE));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void resultsCannotMutateTheRecipe() {
        ItemStack result = new ItemStack(Items.BONE, 2);
        ChoppingBoardRecipe recipe = new ChoppingBoardRecipe(Ingredient.of(Items.CHICKEN),
                List.of(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(result)), 4, Identifier.withDefaultNamespace("empty"));
        result.shrink(1);
        recipe.getResults().getFirst().create().shrink(1);
        assertEquals(2, recipe.getResults().getFirst().count());
        assertEquals(2, recipe.getResult().count());
    }

    private static ChoppingBoardRecipe parse(String json) {
        return ChoppingBoardRecipeSerializer.codec().codec().parse(ops, JsonParser.parseString(json)).getOrThrow();
    }

    @Test
    void emptyResultListIsRejected() {
        var result = ChoppingBoardRecipeSerializer.codec().codec().parse(ops,
                JsonParser.parseString("""
                        {"ingredient":"minecraft:chicken","result":[]}
                        """));
        assertTrue(result.error().isPresent());
    }

    @Test
    void minimumCutCountAndPrimaryAssemblyStayCompatible() {
        var recipe = parse("""
                {"ingredient":"minecraft:chicken","cut_count":0,
                 "result":[{"id":"minecraft:chicken","count":3},{"id":"minecraft:bone"}]}
                """);
        assertEquals(1, recipe.getCutCount());
        var input = new net.minecraft.world.item.crafting.SingleRecipeInput(new ItemStack(Items.CHICKEN));
        var assembled = recipe.assemble(input);
        assembled.shrink(1);
        assertEquals(3, recipe.assemble(input).getCount());
        assertThrows(UnsupportedOperationException.class, () -> recipe.getResults().clear());
    }
}
