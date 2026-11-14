package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.SteamerBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SteamerBlockEntityRender implements BlockEntityRenderer<SteamerBlockEntity, SteamerBlockEntityRenderState> {
    private final ItemModelResolver itemModelResolver;

    public SteamerBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void extractRenderState(@NonNull SteamerBlockEntity blockEntity, @NonNull SteamerBlockEntityRenderState blockEntityRenderState, float f, @NonNull Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);
        int posLong = (int) blockEntity.getBlockPos().asLong();
        blockEntityRenderState.items = new ArrayList<>();
        blockEntityRenderState.hasLid = blockEntity.getBlockState().getValue(SteamerBlock.HAS_LID);
        blockEntityRenderState.facing = blockEntity.getBlockState().getValue(SteamerBlock.FACING);
        for (var index = 0; index < blockEntity.getItems().size(); index++) {
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            ItemStack itemStack = blockEntity.getItems().get(index);
            if (!itemStack.isEmpty())
                this.itemModelResolver.updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, posLong + index);
            blockEntityRenderState.items.add(itemStackRenderState);
        }
    }

    @Override
    public @NonNull SteamerBlockEntityRenderState createRenderState() {
        return new SteamerBlockEntityRenderState();
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    public void submit(@NonNull SteamerBlockEntityRenderState blockEntityRenderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        // 盖住了就不渲染
        if (blockEntityRenderState.hasLid) {
            return;
        }
        List<ItemStackRenderState> items = blockEntityRenderState.items;
        for (int i = 0; i < items.size(); i++) {
            ItemStackRenderState stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }

            double x = (i % 2) * 0.3 + 0.35;
            double y = (i / 4) * 0.5 + 0.25 + (i % 4) * 0.01;
            double z = ((i / 2) % 2) * 0.3 + 0.35;
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            switch (blockEntityRenderState.facing) {
                case SOUTH -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
                case NORTH ->  poseStack.mulPose(Axis.XP.rotationDegrees(90));
                case EAST ->  {
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                }
                case WEST ->  {
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                }
                default -> {}
            }
            poseStack.scale(0.5F, 0.5F, 0.5F);
            stack.submit(poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
