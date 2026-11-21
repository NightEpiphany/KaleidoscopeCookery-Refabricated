package com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public final class KitchenShelfOverlay {
    private KitchenShelfOverlay() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register(KitchenShelfOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.gameMode == null
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
            graphics.renderFakeItem(stored, x, y);
            graphics.renderItemDecorations(minecraft.font, stored, x, y);
        }
    }
}
