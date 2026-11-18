package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ICustomModel;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.TableBlockEntityRenderState;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.TableItemStackRenderState;
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
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class TableBlockEntityRender implements BlockEntityRenderer<TableBlockEntity, TableBlockEntityRenderState>, ICustomModel {

    public static final double RENDER_HEIGHT = 1.27175D;
    private static final float ITEM_SCALE = 0.65F;
    private final ItemModelResolver itemModelResolver;
    private static final String MODEL_KEY_PREFIX = "carpet/table/";
    private static final String SINGLE = "_single";
    private static final String MIDDLE = "_middle";
    private static final String LEFT = "_left";
    private static final String RIGHT = "_right";


    private static final Function<Integer, String> CACHE_POS = Util.memoize((position) -> {
        if (position == TableBlock.SINGLE) {
            return SINGLE;
        }
        if (position == TableBlock.MIDDLE) {
            return MIDDLE;
        }
        if (position == TableBlock.LEFT) {
            return LEFT;
        }
        return RIGHT;
    });

    public TableBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }


    private void rotation(PoseStack poseStack, Direction.Axis axis) {
        if (axis == Direction.Axis.X) {
            poseStack.rotateDegrees(Axis.YP, 180);
        } else {
            poseStack.rotateDegrees(Axis.YP, 90);
        }
    }

    private static double getRenderHeight(TableItemStackRenderState item) {
        return item.hasThreeDimensionalModel() ? RENDER_HEIGHT - 0.25D * ITEM_SCALE : RENDER_HEIGHT;
    }

    @Override
    public @NonNull TableBlockEntityRenderState createRenderState() {
        return new TableBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull TableBlockEntity blockEntity, @NonNull TableBlockEntityRenderState blockEntityRenderState, float f, @NonNull Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);
        int posLong = (int) blockEntity.getBlockPos().asLong();
        blockEntityRenderState.items = new ArrayList<>(4);
        blockEntityRenderState.hasCarpet = blockEntity.getBlockState().getValue(TableBlock.HAS_CARPET);
        blockEntityRenderState.axis = blockEntity.getBlockState().getValue(TableBlock.AXIS);
        if (blockEntityRenderState.hasCarpet) {
            ItemStack carpet = getBaseModelDisplay(MODEL_KEY_PREFIX + blockEntity.getColor().getName() + CACHE_POS.apply(blockEntity.getBlockState().getValue(TableBlock.POSITION)));
            this.itemModelResolver.updateForTopItem(blockEntityRenderState.carpetModel, carpet, ItemDisplayContext.NONE, blockEntity.getLevel(), null, 0);
        }
        for (var index = 0; index < 4; index++) {
            TableItemStackRenderState itemStackRenderState = new TableItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemStackRenderState, blockEntity.getItems().get(index), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, posLong + index);
            blockEntityRenderState.items.add(itemStackRenderState);
        }
    }

    @Override
    public void submit(TableBlockEntityRenderState blockEntityRenderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (blockEntityRenderState.hasCarpet) {
            int rotation = blockEntityRenderState.axis == Direction.Axis.X ? 180 : 270;
            poseStack.pushPose();
            if (blockEntityRenderState.axis == Direction.Axis.X)
                poseStack.translate(0, 0, 1);
            poseStack.rotateDegrees(Axis.YP, -rotation);
            poseStack.translate(-0.5f, 0.5f, 0.5f);
            if (!blockEntityRenderState.carpetModel.isEmpty()) {
                blockEntityRenderState.carpetModel.submit(
                        poseStack,
                        submitNodeCollector,
                        blockEntityRenderState.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        0
                );
            }
            poseStack.popPose();
        }

        var items = blockEntityRenderState.items;

        int count = 0;
        for (var item : blockEntityRenderState.items) {
            if (item.isEmpty()) {
                continue;
            }
            count++;
        }

        if (count == 0) {
            return;
        }


        if (count == 1) {
            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.getFirst()), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            ItemStackRenderState stack1 = items.getFirst();
            stack1.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();
        } else if (count == 2) {
            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.getFirst()), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(-0.25, 0, 0.1);
            ItemStackRenderState stack1 = items.getFirst();
            stack1.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.get(1)), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(0.25, 0.01, -0.1);
            ItemStackRenderState stack2 = items.get(1);
            stack2.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();
        } else if (count == 3) {
            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.getFirst()), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(0.25, 0, -0.2);
            ItemStackRenderState stack1 = items.getFirst();
            stack1.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.get(1)), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(-0.25, 0.01, 0);
            ItemStackRenderState stack2 = items.get(1);
            stack2.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.get(2)), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(0.24, 0.02, 0.2);
            ItemStackRenderState stack3 = items.get(2);
            stack3.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.getFirst()), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(0.25, 0, -0.3);
            ItemStackRenderState stack1 = items.getFirst();
            stack1.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.get(1)), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(-0.24, 0.01, -0.1);
            ItemStackRenderState stack2 = items.get(1);
            stack2.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.get(2)), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(0.24, 0.02, 0.1);
            ItemStackRenderState stack3 = items.get(2);
            stack3.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.5, getRenderHeight(items.get(3)), 0.5);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            this.rotation(poseStack, blockEntityRenderState.axis);
            poseStack.translate(-0.25, 0.03, 0.3);
            ItemStackRenderState stack4 = items.get(3);
            stack4.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();
        }
    }
}
