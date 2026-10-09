package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;

@Environment(EnvType.CLIENT)
public class MobSoupBaseRender extends FluidSoupBaseRender {

    private final EntityType<?> mobType;

    public MobSoupBaseRender(Fluid fluid, EntityType<?> mobType) {
        super(fluid);
        this.mobType = mobType;
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                        MultiBufferSource buffer, int packedLight, int packedOverlay,
                                        float soupHeight) {
        super.renderWhenPutIngredient(stockpot, partialTick, poseStack, buffer, packedLight, packedOverlay, soupHeight);
        this.renderInputEntity(stockpot, poseStack, buffer, packedLight);
    }

    @Override
    public void renderWhenCooking(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, int packedOverlay,
                                  ResourceLocation cookingTexture, float soupHeight) {
        super.renderWhenCooking(stockpot, partialTick, poseStack, buffer, packedLight, packedOverlay, cookingTexture, soupHeight);
        this.renderInputEntity(stockpot, poseStack, buffer, packedLight);
    }

    private void renderInputEntity(StockpotBlockEntity stockpot, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ClientLevel world = Minecraft.getInstance().level;
        var soupInputBase = stockpot.getSoupBaseItem();
        if (world == null) {
            return;
        }
        Entity renderEntity = stockpot.renderEntity;
        boolean shouldRefreshCache = renderEntity == null || renderEntity.getType() != mobType;
        if (shouldRefreshCache) {
            renderEntity = mobType.create(world);
            stockpot.renderEntity = renderEntity;
            if (renderEntity != null) {
                if (renderEntity instanceof Pufferfish pufferfish)
                    pufferfish.setPuffState(world.random.nextInt(3));
                if (renderEntity instanceof Bucketable bucketable) {
                    CustomData customData = soupInputBase.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
                    CompoundTag bucketData = customData.copyTag();
                    if (renderEntity instanceof TropicalFish
                            && !bucketData.contains(TropicalFish.BUCKET_VARIANT_TAG, Tag.TAG_INT)) {
                        bucketData.merge(setRandomTropicalFishVariant(stockpot).copyTag());
                    }
                    bucketable.loadFromBucketTag(bucketData);
                    bucketable.setFromBucket(true);
                }
                renderEntity.setOnGround(true);
            }
        }

        if (renderEntity != null) {
            int random = renderEntity.hashCode();
            float entityY = (float) (Math.sin(random + System.currentTimeMillis() * 0.0005) * 0.25);

            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(random % 360));
            poseStack.translate(-0.5, -0.5, -0.5);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getEntityRenderDispatcher().render(renderEntity, 1, 0.375f + entityY, 1,
                    0, 0, poseStack, buffer, packedLight);
            poseStack.popPose();
        }
    }

    private CustomData setRandomTropicalFishVariant(StockpotBlockEntity stockpot) {
        if (stockpot.getLevel() == null) return CustomData.EMPTY;
        RandomSource randomSource = stockpot.getLevel().random;
        TropicalFish.Pattern[] patterns = TropicalFish.Pattern.values();
        DyeColor[] dyeColors = DyeColor.values();
        TropicalFish.Pattern pattern = Util.getRandom(patterns, randomSource);
        DyeColor dyeColor = Util.getRandom(dyeColors, randomSource);
        DyeColor dyeColor2 = Util.getRandom(dyeColors, randomSource);
        var variant = new TropicalFish.Variant(pattern, dyeColor, dyeColor2);

        CompoundTag bucketData = new CompoundTag();
        bucketData.putInt(TropicalFish.BUCKET_VARIANT_TAG, variant.getPackedId());
        return CustomData.of(bucketData);
    }
}
