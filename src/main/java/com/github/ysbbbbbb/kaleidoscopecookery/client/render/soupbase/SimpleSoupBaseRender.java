package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.StockpotBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class SimpleSoupBaseRender implements ISoupBaseRender {
    private final Identifier soupBaseTexture;

    public SimpleSoupBaseRender(Identifier soupBaseTexture) {
        this.soupBaseTexture = soupBaseTexture;
    }



    private TextureAtlasSprite getSprite() {
        return Minecraft.getInstance().getModelManager().atlasManager.getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS).getSprite(this.soupBaseTexture);
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        ISoupBaseRender.renderSurface(this.getSprite(), 0xFFFFFFFF, poseStack, submitNodeCollector, packedLight, soupHeight);
    }

    @Override
    public void renderWhenCooking(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, Identifier cookingTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        var atlas = Minecraft.getInstance().getModelManager().atlasManager.getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS);
        TextureAtlasSprite sprite = atlas.getSprite(cookingTexture);
        ISoupBaseRender.renderSurface(sprite, 0xFFFFFFFF, poseStack, submitNodeCollector, packedLight, soupHeight);
    }

    @Override
    public void renderWhenFinished(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, Identifier finishedTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        var atlas = Minecraft.getInstance().getModelManager().atlasManager.getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS);
        TextureAtlasSprite sprite = atlas.getSprite(finishedTexture);
        ISoupBaseRender.renderSurface(sprite, 0xFFFFFFFF, poseStack, submitNodeCollector, packedLight, soupHeight);
    }
}
