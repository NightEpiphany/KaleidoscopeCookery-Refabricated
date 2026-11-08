package com.github.ysbbbbbb.kaleidoscopecookery.api.client.render;

import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.StockpotBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public interface ISoupBaseRender {
    /**
     * 工具方法，用于渲染流体贴图
     *
     * @param sprite    TextureAtlasSprite
     * @param color     流体颜色
     * @param poseStack PoseStack
     * @param submitNodeCollector 节点收集器
     * @param light     PackedLight
     * @param y         汤底的高度
     */
    @Contract(pure = true)
    static void renderSurface(TextureAtlasSprite sprite, int color, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, float y) {
        // 26.2 起自定义几何需要先提交到收集器，再由底层新渲染管线统一分组执行。
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(sprite.atlasLocation()), (pose, vertexConsumer) -> {
            float min = 3 / 16f;
            float max = 1 - 3 / 16f;
            renderVertex(vertexConsumer, pose, min, y, min, color, sprite.getU0(), sprite.getV0(), light);
            renderVertex(vertexConsumer, pose, min, y, max, color, sprite.getU0(), sprite.getV(10 / 16f), light);
            renderVertex(vertexConsumer, pose, max, y, max, color, sprite.getU(10 / 16f), sprite.getV(10 / 16f), light);
            renderVertex(vertexConsumer, pose, max, y, min, color, sprite.getU(10 / 16f), sprite.getV0(), light);
        });
    }

    private static void renderVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, float x, float y, float z, int color, float u, float v, int light) {
        vertexConsumer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0, 1, 0);
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
                            SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay,
                            Identifier finishedTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState);
}
