package com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class KitchenShelfOverlay {
    public static void register() {
        HudRenderCallback.EVENT.register(KitchenShelfOverlay::render);
    }

    private static void render(GuiGraphics graphics, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null || !(minecraft.hitResult instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = hit.getBlockPos();
        if (!player.level().getBlockState(pos).is(ModBlocks.KITCHEN_SHELF)
                || !(player.level().getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf)) {
            return;
        }

        ItemStack stored = shelf.getStoredStack();
        if (!stored.isEmpty()) {
            int x = minecraft.getWindow().getGuiScaledWidth() / 2 - 8;
            int y = minecraft.getWindow().getGuiScaledHeight() / 2 + 8;
            graphics.renderFakeItem(stored, x, y);
            graphics.renderItemDecorations(minecraft.font, stored, x, y);
        }
    }
}
