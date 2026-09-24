package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.MillstoneModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.renderstates.MillstoneBlockEntityRenderState;
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
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MillstoneBlockEntityRender implements BlockEntityRenderer<MillstoneBlockEntity, MillstoneBlockEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/block/millstone.png");

    private final MillstoneModel bodyModel;
    private final ItemModelResolver itemModelResolver;

    public MillstoneBlockEntityRender(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
        this.bodyModel = new MillstoneModel(context.bakeLayer(MillstoneModel.LAYER_LOCATION));
    }


    @Override
    public void extractRenderState(@NonNull MillstoneBlockEntity blockEntity, @NonNull MillstoneBlockEntityRenderState blockEntityRenderState, float f, @NonNull Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);
        Direction facing = blockEntityRenderState.blockState.getValue(MillstoneBlock.FACING);
        int facingDeg = facing.get2DDataValue() * 90;
        int i = (int)blockEntity.getBlockPos().asLong();
        blockEntityRenderState.levelAccessor = blockEntity.getLevel();
        blockEntityRenderState.hasEntity = blockEntity.hasEntity();
        blockEntityRenderState.cacheRot = blockEntity.getCacheRot();
        blockEntityRenderState.rot = blockEntity.getLevel() != null ? facingDeg + blockEntity.getRotation(blockEntity.getLevel(), f) : 0f;
        blockEntityRenderState.liftAngle = blockEntity.getLiftAngle();
        blockEntityRenderState.randomSeed = blockEntity.hashCode();
        blockEntityRenderState.input = blockEntity.getInput();
        blockEntityRenderState.output = blockEntity.getOutput();
        int maxCount = Math.min(blockEntityRenderState.input.getCount(), MillstoneBlockEntity.MAX_INPUT_COUNT);
        blockEntityRenderState.inputs = new ArrayList<>();
        blockEntityRenderState.outputs = new ArrayList<>();
        for (int j = 0; j < maxCount; j++) {
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            ItemStackRenderState itemStackRenderState2 = new ItemStackRenderState();
            this.itemModelResolver
                    .updateForTopItem(itemStackRenderState, blockEntityRenderState.input, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, i + j);
            this.itemModelResolver
                    .updateForTopItem(itemStackRenderState2, blockEntityRenderState.output, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, i + j + 1);
            blockEntityRenderState.inputs.add(itemStackRenderState);
            blockEntityRenderState.outputs.add(itemStackRenderState2);
        }
    }

    @Override
    public @NonNull MillstoneBlockEntityRenderState createRenderState() {
        return new MillstoneBlockEntityRenderState();
    }

    @Override
    public void submit(MillstoneBlockEntityRenderState blockEntityRenderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (blockEntityRenderState.levelAccessor == null) return;

        Direction facing = blockEntityRenderState.blockState.getValue(MillstoneBlock.FACING);

        int facingDeg = facing.get2DDataValue() * 90;
        MillstoneModel.State state = new MillstoneModel.State(
                blockEntityRenderState.levelAccessor,
                blockEntityRenderState.hasEntity,
                blockEntityRenderState.cacheRot,
                blockEntityRenderState.rot,
                blockEntityRenderState.input,
                blockEntityRenderState.liftAngle,
                facingDeg
        );
        this.bodyModel.setupAnim(state);
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - facingDeg));
        RenderType renderType = RenderTypes.entityCutoutNoCull(TEXTURE);
        submitNodeCollector.submitModel(
                bodyModel,
                state,
                poseStack,
                renderType,
                blockEntityRenderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0,
                null
        );
        poseStack.popPose();
        this.bodyModel.getWheel().yRot = 0;
        this.bodyModel.getRoll().zRot = 0;
        this.bodyModel.getRotStick().xRot = 0;

        if (!blockEntityRenderState.input.isEmpty()) {
            renderItems(blockEntityRenderState.inputs, blockEntityRenderState.input, blockEntityRenderState, poseStack, submitNodeCollector);
        }else if (!blockEntityRenderState.output.isEmpty()) {
            renderItems(blockEntityRenderState.outputs, blockEntityRenderState.output, blockEntityRenderState, poseStack, submitNodeCollector);
        }
    }

    private static void renderItems(
            List<ItemStackRenderState> list,
            ItemStack renderItem,
            MillstoneBlockEntityRenderState blockEntityRenderState,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector
    ) {
        if (list.isEmpty()) return;
        RandomSource source = RandomSource.create(blockEntityRenderState.randomSeed);
        int maxCount = Math.min(renderItem.getCount(), MillstoneBlockEntity.MAX_INPUT_COUNT);
        for (int i = 0; i < maxCount; i++) {
            ItemStackRenderState itemStackRenderState = list.get(i);
            poseStack.pushPose();
            poseStack.translate(0, 0.875, 0);
            poseStack.rotateAround(Axis.YP.rotationDegrees(i * 45 + source.nextInt(15)), 0.5f, 0, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(source.nextInt(20)));
            poseStack.mulPose(Axis.XN.rotationDegrees(80 + source.nextInt(20)));
            poseStack.scale(0.65F, 0.65F, 0.65F);
            itemStackRenderState.submit(
                    poseStack,
                    submitNodeCollector,
                    blockEntityRenderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public AABB getRenderBoundingBox(MillstoneBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return getAABB(pos.offset(-3, 0, -3), pos.offset(3, 1, 3));
    }

    private static AABB getAABB(BlockPos pStart, BlockPos pEnd) {
        return new AABB(pStart.getX(), pStart.getY(), pStart.getZ(), pEnd.getX(), pEnd.getY(), pEnd.getZ());
    }
}
