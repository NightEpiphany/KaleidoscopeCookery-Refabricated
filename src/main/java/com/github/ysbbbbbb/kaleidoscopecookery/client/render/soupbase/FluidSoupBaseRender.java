package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.StockpotBlockEntityRenderState;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderingRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.NonNull;

public class FluidSoupBaseRender implements ISoupBaseRender {
    private final Fluid fluid;

    public FluidSoupBaseRender(Fluid fluid) {
        this.fluid = fluid;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderWhenPutIngredient(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        if (stockpot.hasLiquidIngredients) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot/default_preparing_soup")));
            ISoupBaseRender.renderSurface(sprite, 0xFFFFFFFF, poseStack, submitNodeCollector, packedLight, soupHeight);
        }else
            ISoupBaseRender.renderSurface(getStillFluidSprite(fluid), getFluidColor(fluid), poseStack, submitNodeCollector, packedLight, soupHeight);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderWhenCooking(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, Identifier cookingTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, cookingTexture));
        ISoupBaseRender.renderSurface(sprite, 0xFFFFFFFF, poseStack, submitNodeCollector, packedLight, soupHeight);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderWhenFinished(StockpotBlockEntityRenderState stockpot, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int packedOverlay, Identifier finishedTexture, float soupHeight, @NonNull CameraRenderState cameraRenderState) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, finishedTexture));
        ISoupBaseRender.renderSurface(sprite, 0xFFFFFFFF, poseStack, submitNodeCollector, packedLight, soupHeight);
    }
    @SuppressWarnings("all")
    private TextureAtlasSprite getStillFluidSprite(Fluid fluid) {
        FluidModel.Unbaked unbaked = FluidRenderingRegistryImpl.getUnbakedModels().get(fluid);
        if (unbaked != null) {
            Identifier sprite = unbaked.stillMaterial().sprite();
            return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, sprite));
        }
        if (fluid == Fluids.WATER) {
            return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/water_still")));
        }
        if (fluid == Fluids.LAVA) {
            return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/lava_still")));
        }
        if (fluid == BuiltInRegistries.FLUID.getValue(ModFluids.MILK_ID)) {
            return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot/milk")));
        }
        // 如果没有找到渲染处理器，使用默认水纹理作为后备
        return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath("minecraft", "block/water_still")));
    }

    private static int getFluidColor(Fluid fluid) {
        if (fluid == Fluids.WATER) return -12618012;
        FluidVariantRenderHandler handler = FluidVariantRendering.getHandler(fluid);
        if (handler == null) {
            // 默认颜色（白色）
            return 0xFFFFFFFF;
        }
        return handler.getColor(FluidVariant.of(fluid), null, null);
    }
}
