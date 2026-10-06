package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.StockpotBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class SimpleSoupBaseRender implements ISoupBaseRender {
    private final Identifier soupBaseTexture;

    public SimpleSoupBaseRender(Identifier soupBaseTexture) {
        this.soupBaseTexture = soupBaseTexture;
    }


    @SuppressWarnings("deprecation")
    private TextureAtlasSprite getSprite(Identifier texture) {
        return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, texture));
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        ISoupBaseRender.renderSurface(this.getSprite(this.soupBaseTexture), 0xFFFFFFFF, poseStack, packedLight, soupHeight);
    }

    @Override
    public void renderWhenCooking(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, Identifier cookingTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        ISoupBaseRender.renderSurface(this.getSprite(cookingTexture), 0xFFFFFFFF, poseStack, packedLight, soupHeight);
    }

    @Override
    public void renderWhenFinished(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, Identifier finishedTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        ISoupBaseRender.renderSurface(this.getSprite(finishedTexture), 0xFFFFFFFF, poseStack, packedLight, soupHeight);
    }
}