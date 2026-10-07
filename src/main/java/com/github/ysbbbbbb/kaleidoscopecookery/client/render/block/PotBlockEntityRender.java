package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.PotBlockEntityRenderState;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PotBlockEntityRender implements BlockEntityRenderer<PotBlockEntity, PotBlockEntityRenderState> {
    private final ItemModelResolver itemModelResolver;

    public PotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public @NonNull PotBlockEntityRenderState createRenderState() {
        return new PotBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull PotBlockEntity blockEntity, @NonNull PotBlockEntityRenderState blockEntityRenderState, float f, @NonNull Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);
        blockEntityRenderState.data = blockEntity.animationData;
        blockEntityRenderState.seed = blockEntity.getSeed();
        blockEntityRenderState.status = blockEntity.getStatus();
        int posLong = (int) blockEntity.getBlockPos().asLong();
        blockEntityRenderState.inputs = new ArrayList<>();
        blockEntityRenderState.output = new ItemStackRenderState();
        blockEntityRenderState.rotation = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90;
        for (var index = 0; index < blockEntity.getInputs().size(); index++) {
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            ItemStack itemStack = blockEntity.getInputs().get(index);
            if (itemStack.is(TagMod.SPECIAL)) {
                itemStack.set(ModDataComponents.SPECIAL_RENDER, true);
            }
            this.itemModelResolver.updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, posLong + index);
            blockEntityRenderState.inputs.add(itemStackRenderState);
        }
        this.itemModelResolver.updateForTopItem(blockEntityRenderState.output, blockEntity.getResult(), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, posLong - 1);
        blockEntityRenderState.hasCarrier = blockEntity.hasCarrier();
        blockEntityRenderState.currentTick = blockEntity.getCurrentTick();
    }

    @Override
    public void submit(PotBlockEntityRenderState blockEntityRenderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        RandomSource source = RandomSource.create(blockEntityRenderState.seed);
        var data = blockEntityRenderState.data;
        long time = System.currentTimeMillis() - data.timestamp;

        if (data.preSeed == -1L) {
            data.preSeed = blockEntityRenderState.seed;
        }
        if (data.preSeed != blockEntityRenderState.seed) {
            data.preSeed = blockEntityRenderState.seed;
            if (time > 1000) {
                data.timestamp = System.currentTimeMillis();
                data.randomHeights = new float[9];
                for (int i = 0; i < 9; i++) {
                    data.randomHeights[i] = 0.25f + source.nextFloat() * 1;
                }
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.1, 0.5);
        poseStack.rotateDegrees(Axis.YN, blockEntityRenderState.rotation);
        poseStack.rotateDegrees(Axis.XN, 90);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        // 炒菜阶段，或者炒完，但是需要碗才能装的菜，只渲染原材料
        boolean showInputs = blockEntityRenderState.status != PotBlockEntity.FINISHED && blockEntityRenderState.status != PotBlockEntity.BURNT;
        if (showInputs || blockEntityRenderState.hasCarrier) {
            List<ItemStackRenderState> items = blockEntityRenderState.inputs;
            for (int i = 0; i < items.size(); i++) {
                ItemStackRenderState item = items.get(i);
                if (!item.isEmpty()) {
                    poseStack.pushPose();

                    int count = 90 + source.nextInt(90);
                    poseStack.rotateDegrees(Axis.ZN, i * count);
                    if (time < 1000) {
                        poseStack.translate(0, 0, data.randomHeights[i] * Mth.sin(Mth.PI * time / 1000f));
                        poseStack.rotateDegrees(Axis.XN, 720f / 1000 * time);
                    }
                    // 焦糊程度，菜变黑
                    if (blockEntityRenderState.status == PotBlockEntity.BURNT) {
                        int tick = blockEntityRenderState.currentTick;
                        int burntLevel = Math.clamp(tick / 25, 0, 16);
                        blockEntityRenderState.lightCoords = OverlayTexture.u(burntLevel);
                    }

                    item.submit(
                            poseStack,
                            submitNodeCollector,
                            blockEntityRenderState.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            0
                    );

                    poseStack.popPose();
                    poseStack.translate(0, 0, 0.025);
                }
            }
        } else {
            // 结束阶段，并且不需要碗的菜，直接渲染结果
            poseStack.pushPose();

            poseStack.rotateDegrees(Axis.ZN, 0);
            if (time < 1000) {
                poseStack.translate(0, 0, data.randomHeights[0] * Mth.sin(Mth.PI * time / 1000f));
                poseStack.rotateDegrees(Axis.XN, 720f / 1000 * time);
            }
            // 焦糊程度，菜变黑
            if (blockEntityRenderState.status == PotBlockEntity.BURNT) {
                int tick = blockEntityRenderState.currentTick;
                int burntLevel = Math.clamp(tick / 25, 0, 16);
                blockEntityRenderState.lightCoords = OverlayTexture.u(burntLevel);
            }

            blockEntityRenderState.output.submit(poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
