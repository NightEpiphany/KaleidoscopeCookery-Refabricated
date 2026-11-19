package com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
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
public class SteamerOverlay {
    public static void register() {
        HudRenderCallback.EVENT.register(SteamerOverlay::render);
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
        if (!player.level().getBlockState(pos).is(ModBlocks.STEAMER)
                || !(player.level().getBlockEntity(pos) instanceof SteamerBlockEntity steamer)) {
            return;
        }
        if (steamer.getStoreSize() < 1) return;
        int x = minecraft.getWindow().getGuiScaledWidth() / 2 - 10 * (steamer.getStoreSize() - 1) - 8;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2 + 4;
        for (ItemStack stack : steamer.getItems()) {
            if (!stack.isEmpty()) {
                graphics.renderFakeItem(stack, x, y);
                graphics.renderItemDecorations(minecraft.font, stack, x, y);
                x += 20;
            }
        }
    }
}
