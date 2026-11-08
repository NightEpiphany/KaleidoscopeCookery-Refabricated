package com.github.ysbbbbbb.kaleidoscopecookery.api.client.render;

import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.StockpotBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;

public interface ISoupBaseRender {
    Minecraft MC = Minecraft.getInstance();
    /**
     * 工具方法，用于渲染流体贴图
     *
     * @param sprite    TextureAtlasSprite
     * @param color     流体颜色
     * @param poseStack PoseStack
     * @param light     PackedLight
     * @param y         汤底的高度
     */
    @Contract(pure = true)
    static void renderSurface(TextureAtlasSprite sprite, int color, PoseStack poseStack, int light, float y) {
        MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.entityTranslucent(sprite.atlasLocation()));
        Matrix4f matrix = poseStack.last().pose();

        // 锅内水面的位置和大小（根据实际锅模型调整）
        float min = 3 / 16f, max = 1 - 3 / 16f;

        // 渲染一个平面
        vertexConsumer.addVertex(matrix, min, y, min)
                .setColor(color)
                .setUv(sprite.getU0(), sprite.getV0())
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, min, y, max)
                .setColor(color)
                .setUv(sprite.getU0(), sprite.getV(10 / 16f))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, max, y, max)
                .setColor(color)
                .setUv(sprite.getU(10 / 16f), sprite.getV(10 / 16f))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 1, 0);
        vertexConsumer.addVertex(matrix, max, y, min)
                .setColor(color)
                .setUv(sprite.getU(10 / 16f), sprite.getV0())
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 1, 0);
    }
    /**
     * 还没有放入原料时的汤底的渲染
     *
     * @param soupHeight 汤底的高度
     */
    void renderWhenPutIngredient(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack,
                                 SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay,
                                 float soupHeight, @NonNull CameraRenderState cameraRenderState);


    /**
     * 烹饪中的汤底渲染
     *
     * @param cookingTexture 烹饪中的汤底贴图，这个是由数据包定义并传递到此
     * @param soupHeight     汤底的高度
     */
    void renderWhenCooking(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack,
                           SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay,
                           Identifier cookingTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState);

    /**
     * 烹饪完成后的汤底渲染
     *
     * @param finishedTexture 烹饪完成后的汤底贴图，这个是由数据包定义并传递到此
     * @param soupHeight      汤底的高度
     */
    void renderWhenFinished(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack,
                            SubmitNodeCollector buffer, int packedLight, int packedOverlay,
                            Identifier finishedTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState);
}
