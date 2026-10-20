package com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Arrays;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class TeapotOverlayEvent {
    private static final int BOTTOM_OFFSET = 72;
    private static final int LINE_SPACING = 2;
    private static final int ACTION_BAR_GAP = 4;

    private TeapotOverlayEvent() {
    }

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot_overlay"),
                TeapotOverlayEvent::render
        );
    }

    private static void render(GuiGraphicsExtractor guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult blockHitResult) || blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        Level level = player.level();
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is(ModBlocks.TEAPOT)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof TeapotBlockEntity teapot)) {
            return;
        }

        Font font = minecraft.font;
        Component detail = getDetailText(teapot);
        boolean hasDetail = !detail.equals(CommonComponents.EMPTY);
        int x = guiGraphics.guiWidth() / 2;
        int y = getOverlayTop(guiGraphics.guiHeight(), font.lineHeight, hasDetail,
                minecraft.gui.hud.overlayMessageTime > 0);

        drawSingleLine(guiGraphics, font, teapot.getStatusText(), x, y);
        y += font.lineHeight + LINE_SPACING;

        if (hasDetail) {
            drawSingleLine(guiGraphics, font, detail, x, y);
        }
    }

    static int getOverlayTop(int screenHeight, int lineHeight, boolean hasDetail, boolean actionBarVisible) {
        int y = screenHeight - BOTTOM_OFFSET;
        if (actionBarVisible) {
            int overlayHeight = hasDetail ? lineHeight * 2 + LINE_SPACING : lineHeight;
            y -= overlayHeight + ACTION_BAR_GAP;
        }
        return y;
    }

    private static Component getDetailText(TeapotBlockEntity teapot) {
        if (teapot.getStatus() == ITeapot.PUT_INGREDIENT) {
            Component fluidText = teapot.getTeaFluidId().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)
                    ? Component.translatable("mco.configure.world.slot.empty")
                    : TeaFluidHelper.getDisplayName(teapot.getTeaFluidId());
            ItemStack input = teapot.getInput();
            Component itemText = input.isEmpty()
                    ? Component.translatable("mco.configure.world.slot.empty")
                    : ComponentUtils.formatList(
                    Arrays.asList(input.getHoverName(), Component.literal("x%d".formatted(input.getCount()))),
                    CommonComponents.space(),
                    Function.identity()
            );
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.fluid_ingredient", fluidText, itemText);
        }
        if (teapot.getStatus() == ITeapot.FINISHED) {
            ItemStack result = teapot.getResult();
            Component resultText = result.isEmpty()
                    ? Component.translatable("mco.configure.world.slot.empty")
                    : ComponentUtils.formatList(
                    Arrays.asList(result.getHoverName(), Component.literal("x%d".formatted(result.getCount()))),
                    CommonComponents.space(),
                    Function.identity()
            );
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.result", resultText);
        }
        return CommonComponents.EMPTY;
    }

    private static void drawSingleLine(GuiGraphicsExtractor graphics, Font font, Component text, int centerX, int y) {
        FormattedCharSequence sequence = text.getVisualOrderText();
        int width = font.width(sequence);
        int availableWidth = Math.max(1, graphics.guiWidth() - 16);
        float scale = width > availableWidth ? (float) availableWidth / width : 1.0F;
        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate(centerX, y);
            graphics.pose().scale(scale, scale);
            graphics.text(font, sequence, -width / 2, 0, -1);
        } finally {
            graphics.pose().popMatrix();
        }
    }
}
