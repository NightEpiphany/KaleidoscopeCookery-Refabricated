package com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public final class KitchenShelfOverlay {
    private KitchenShelfOverlay() {
    }

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchen_shelf_overlay"),
                KitchenShelfOverlay::render
        );
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gui.hud.isHidden() || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null || !(minecraft.hitResult instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK
                || !(player.level().getBlockEntity(hit.getBlockPos()) instanceof KitchenShelfBlockEntity shelf)) {
            return;
        }

        ItemStack stored = shelf.getStoredStack();
        if (!stored.isEmpty()) {
            int x = graphics.guiWidth() / 2 - 8;
            int y = graphics.guiHeight() / 2 + 8;
            graphics.fakeItem(stored, x, y);
            graphics.itemDecorations(minecraft.font, stored, x, y);
        }
    }
}
