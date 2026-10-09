package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.StockpotBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

public class MobSoupBaseRender extends FluidSoupBaseRender {
    private final EntityType<?> mobType;

    public MobSoupBaseRender(Fluid fluid, EntityType<?> mobType) {
        super(fluid);
        this.mobType = mobType;
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        super.renderWhenPutIngredient(stockpot, partialTick, poseStack, submitNodeCollector, packedLight, packedOverlay, soupHeight, cameraRenderState);
        this.renderInputEntity(stockpot, poseStack, packedLight, cameraRenderState, submitNodeCollector);
    }

    @Override
    public void renderWhenCooking(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, Identifier cookingTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        super.renderWhenCooking(stockpot, partialTick, poseStack, submitNodeCollector, packedLight, packedOverlay, cookingTexture, soupHeight, cameraRenderState);
        this.renderInputEntity(stockpot, poseStack, packedLight, cameraRenderState, submitNodeCollector);
    }

    private void renderInputEntity(StockpotBlockEntityRenderState stockpot, PoseStack poseStack, int packedLight, CameraRenderState cameraRenderState, SubmitNodeCollector submitNodeCollector) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        EntityRenderState renderEntity = stockpot.renderEntity;
        boolean shouldRefreshCache = renderEntity == null || renderEntity.entityType != this.mobType;
        if (shouldRefreshCache) {
            return;
        }

        int random = stockpot.seed;
        float entityY = (float) (Math.sin(random + System.currentTimeMillis() * 0.0005) * 0.125f);

        poseStack.pushPose();
        poseStack.translate(0.5f, -0.05f, 0.5f);
        poseStack.rotateDegrees(Axis.YP, random % 360);
        poseStack.scale(0.565f, 0.565f, 0.565f);
        renderEntity.lightCoords = packedLight;
        Minecraft.getInstance().getEntityRenderDispatcher().submit(renderEntity, cameraRenderState, 0, 0.525f + entityY, 0, poseStack, submitNodeCollector);
        poseStack.popPose();
    }
}
