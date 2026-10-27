package com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public final class TrashCanOverlay {
    private static final Identifier TRASH_CAN_OVERLAY =
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/trash_can_overlay.png");

    private TrashCanOverlay() {
    }

    public static void register() {
        HudElementRegistry.addFirst(
                Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "trash_can_overlay"),
                TrashCanOverlay::render
        );
    }

    private static void render(GuiGraphicsExtractor guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        if (player.getVehicle() instanceof SitEntity sitEntity && sitEntity.getSitType() == SitEntity.TRASH_CAN) {
            if (minecraft.options.getCameraType().isFirstPerson()) {
                renderTextureOverlay(guiGraphics);
            }
            return;
        }

        renderTrashCanTip(guiGraphics, guiGraphics.guiWidth(), guiGraphics.guiHeight(), minecraft, player);
    }

    private static void renderTrashCanTip(GuiGraphicsExtractor guiGraphics, int screenWidth, int screenHeight, Minecraft minecraft, LocalPlayer player) {
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult result)) {
            return;
        }
        Level level = player.level();
        BlockPos blockPos = result.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is(ModBlocks.TRASH_CAN)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof TrashCanBlockEntity trashCan)) {
            return;
        }

        Font font = minecraft.font;
        int x = screenWidth / 2 - 28;
        int y = screenHeight / 2 + 4;

        ItemStackHandler storage = trashCan.getStorage();
        for (int i = 0; i < storage.getSlots(); i++) {
            ItemStack stack = storage.getStackInSlot(i);
            if (!stack.isEmpty()) {
                guiGraphics.fakeItem(stack, x, y);
                guiGraphics.itemDecorations(font, stack, x, y);
                x += 20;
            }
        }
    }

    private static void renderTextureOverlay(GuiGraphicsExtractor guiGraphics) {
        int i = ARGB.white((float) 1.0);
        guiGraphics.blit(GuiPipeline.TEXTURED, TrashCanOverlay.TRASH_CAN_OVERLAY, 0, 0, 0.0F, 0.0F, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight(), i);
    }
}
