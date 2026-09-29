package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunchBagComponentTest {
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
    void oldSavedComponentExpandsTo24Slots() {
        JsonArray oldItems = new JsonArray(16);
        for (int i = 0; i < 16; i++) {
            oldItems.add(new JsonObject());
        }
        JsonObject apple = new JsonObject();
        apple.addProperty("id", "minecraft:apple");
        apple.addProperty("count", 8);
        oldItems.set(15, apple);
        var restored = TransmutationLunchBagItem.ItemContainer.CODEC.parse(ops, oldItems).getOrThrow();
        assertEquals(24, restored.items().getSlots());
        assertEquals(8, restored.items().getStackInSlot(15).getCount());
        assertTrue(restored.items().getStackInSlot(23).isEmpty());
    }

    @Test
    void oldNetworkContainerExpandsWithoutLosingContents() {
        ItemStackHandler oldItems = new ItemStackHandler(16);
        oldItems.setStackInSlot(15, new ItemStack(Items.APPLE, 8));
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        try {
            buffer.writeNbt(oldItems.serializeNBT(net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, registries)));
            var restored = TransmutationLunchBagItem.ItemContainer.STREAM_CODEC.decode(buffer);
            assertEquals(24, restored.items().getSlots());
            assertEquals(8, restored.items().getStackInSlot(15).getCount());
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void lastSlotSurvivesSaveAndNetworkRoundTrips() {
        ItemStackHandler items = new ItemStackHandler(24);
        items.setStackInSlot(23, new ItemStack(Items.BREAD, 12));
        var container = TransmutationLunchBagItem.ItemContainer.of(items);
        JsonElement saved = TransmutationLunchBagItem.ItemContainer.CODEC.encodeStart(ops, container).getOrThrow();
        var restored = TransmutationLunchBagItem.ItemContainer.CODEC.parse(ops, saved).getOrThrow();
        assertEquals(12, restored.items().getStackInSlot(23).getCount());
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        try {
            TransmutationLunchBagItem.ItemContainer.STREAM_CODEC.encode(buffer, restored);
            var synced = TransmutationLunchBagItem.ItemContainer.STREAM_CODEC.decode(buffer);
            assertEquals(24, synced.items().getSlots());
            assertTrue(synced.items().getStackInSlot(23).is(Items.BREAD));
            assertEquals(12, synced.items().getStackInSlot(23).getCount());
        } finally {
            buffer.release();
        }
    }

    @Test
    void componentSnapshotDoesNotShareMutableStacks() {
        ItemStackHandler items = new ItemStackHandler(16);
        items.setStackInSlot(0, new ItemStack(Items.APPLE, 8));
        var snapshot = TransmutationLunchBagItem.ItemContainer.of(items);
        items.extractItem(0, 1, false);
        assertEquals(8, snapshot.items().getStackInSlot(0).getCount());
        assertEquals(24, snapshot.items().getSlots());
    }
}
