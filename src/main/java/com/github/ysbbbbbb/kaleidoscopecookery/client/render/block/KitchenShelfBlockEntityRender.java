package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.KitchenShelfBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class KitchenShelfBlockEntityRender implements BlockEntityRenderer<KitchenShelfBlockEntity, KitchenShelfBlockEntityRenderState> {
    private final ItemModelResolver itemModelResolver;

    public KitchenShelfBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void extractRenderState(@NonNull KitchenShelfBlockEntity shelf,
                                   @NonNull KitchenShelfBlockEntityRenderState renderState,
                                   float partialTick, @NonNull Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(shelf, renderState, partialTick, cameraPosition, crumblingOverlay);
        ItemStack stored = shelf.getStoredStack();
        renderState.displayCount = Math.min(16, stored.getCount());
        renderState.rotation = shelf.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180;
        this.itemModelResolver.updateForTopItem(renderState.item,
                stored.isEmpty() ? ItemStack.EMPTY : stored.copyWithCount(1),
                ItemDisplayContext.FIXED, shelf.getLevel(), null, (int) shelf.getBlockPos().asLong());
    }

    @Override
    public @NonNull KitchenShelfBlockEntityRenderState createRenderState() {
        return new KitchenShelfBlockEntityRenderState();
    }

    @Override
    public void submit(@NonNull KitchenShelfBlockEntityRenderState renderState, @NonNull PoseStack poseStack,
                       @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState cameraRenderState) {
        if (renderState.item.isEmpty() || renderState.displayCount == 0) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.rotateDegrees(Axis.YN, renderState.rotation);
        for (int index = 0; index < renderState.displayCount; index++) {
            int layer = index / 8;
            int slot = index % 8;
            int row = slot / 2;
            double offset = slot * 0.001;

            poseStack.pushPose();
            poseStack.translate((slot % 2 == 0 ? -0.25 : 0.25) + offset,
                    0.7 - layer * 0.5 + offset, 0.3 - row * 0.20);
            poseStack.rotateDegrees(Axis.XP, 45);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            renderState.item.submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
