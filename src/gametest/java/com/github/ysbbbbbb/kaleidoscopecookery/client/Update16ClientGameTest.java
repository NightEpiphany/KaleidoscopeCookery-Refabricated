package com.github.ysbbbbbb.kaleidoscopecookery.client;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEnchantments;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

public class Update16ClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        context.waitForScreen(TitleScreen.class);
        context.takeScreenshot("update-1.6-title");
        try (var world = context.worldBuilder()
                .adjustSettings(settings -> settings.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE))
                .create()) {
            world.getConnection().waitForChunksRender();
            context.runOnClient(client -> {
                var enchantments = client.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                enchantments.getOrThrow(ModEnchantments.QUICK_KNIFE);
                enchantments.getOrThrow(ModEnchantments.SWEEP);
                var milk = BuiltInRegistries.FLUID.getOptional(ModFluids.MILK_ID).orElseThrow();
                if (FluidVariantRendering.getHandler(milk) == null) {
                    throw new AssertionError("Milk rendering registration was lost");
                }
                if (TransmutationLunchBagItem.getItems(ModItems.TRANSMUTATION_LUNCH_BAG.getDefaultInstance()).getSlots() != 24) {
                    throw new AssertionError("Lunch bag must have 24 slots");
                }
                client.gui.setScreen(new CreativeModeInventoryScreen(client.player,
                        client.level.enabledFeatures(), true));
            });
            context.waitForScreen(CreativeModeInventoryScreen.class);
            context.waitTicks(20);
            context.takeScreenshot("update-1.6-creative-inventory");
            context.setScreen(() -> null);
        }
        context.waitForScreen(TitleScreen.class);
    }
}
