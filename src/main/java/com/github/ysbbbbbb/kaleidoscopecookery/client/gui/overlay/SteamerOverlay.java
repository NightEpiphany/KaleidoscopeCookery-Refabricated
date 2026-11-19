package com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
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

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
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
        int storeSize = steamer.getStoreSize();
        if (storeSize == 0) {
            return;
        }
        int x = graphics.guiWidth() / 2 - 10 * (storeSize - 1) - 8;
        int y = graphics.guiHeight() / 2 + 4;
        for (ItemStack stack : steamer.getItems()) {
            if (!stack.isEmpty()) {
                graphics.renderFakeItem(stack, x, y);
                graphics.renderItemDecorations(minecraft.font, stack, x, y);
                x += 20;
            }
        }
    }
}
