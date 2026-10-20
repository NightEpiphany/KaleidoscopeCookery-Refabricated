package com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class PotOverlayEvent {
    public static void register() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot_overlay"), PotOverlayEvent::render);
    }

    private static void render(GuiGraphicsExtractor guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult result)) {
            return;
        }
        if (result.getType() != HitResult.Type.BLOCK) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        Level level = player.level();
        BlockPos blockPos = result.getBlockPos();
        BlockState blockState = player.level().getBlockState(blockPos);
        if (!blockState.is(ModBlocks.POT)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof PotBlockEntity pot)) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int x = screenWidth / 2;
        int y = screenHeight - 72;
        if (minecraft.gui.hud.overlayMessageTime > 0) {
            y = y - 12;
        }

        if (blockState.getValue(PotBlock.HAS_OIL) && pot.hasHeatSource(level)) {
            int status = pot.getStatus();
            if (status == PotBlockEntity.PUT_INGREDIENT) {
                drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.add_ingredient"), x, y);
                return;
            }
            if (status == PotBlockEntity.COOKING) {
                drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.need_stir_fry"), x, y);
                return;
            }
            if (status == PotBlockEntity.FINISHED) {
                drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.done").withStyle(ChatFormatting.RED), x, y);
            }
        }
    }

    private static void drawWordWrap(GuiGraphicsExtractor graphics, Font font, MutableComponent text, int pX, int pY) {
        for (FormattedCharSequence sequence : font.split(text, 100)) {
            graphics.text(font, sequence, pX - font.width(sequence) / 2, pY, -1);
            pY += font.lineHeight;
        }
    }
}
