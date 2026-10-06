package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.StockpotBlockEntityRenderState;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.MobSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class StockpotBlockEntityRender implements BlockEntityRenderer<StockpotBlockEntity, StockpotBlockEntityRenderState> {
    /*汤锅的可渲染实体只会做一层渲染，不会将真实实体属性添加到世界当中*/
    private static int nextRenderEntityId = Integer.MIN_VALUE;

    private final Function<Identifier, ISoupBaseRender> soupBaseRender;
    private final ItemModelResolver itemModelResolver;
    private final EntityRenderDispatcher entityRenderDispatcher;

    @SuppressWarnings("all")
    public StockpotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.soupBaseRender = Util.memoize(id -> {
            ISoupBase soupBase = SoupBaseManager.getSoupBase(id);
            return soupBase == null ? null : soupBase.getRender();
        });
        this.itemModelResolver = context.itemModelResolver();
        this.entityRenderDispatcher = context.entityRenderer();
    }

    @Override
    public @NonNull StockpotBlockEntityRenderState createRenderState() {
        return new StockpotBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull StockpotBlockEntity blockEntity, @NonNull StockpotBlockEntityRenderState blockEntityRenderState, float f, @NonNull Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);
        int posLong = (int) blockEntity.getBlockPos().asLong();
        blockEntityRenderState.seed = blockEntity.hashCode();
        blockEntityRenderState.soupBaseID = blockEntity.getSoupBaseId();
        blockEntityRenderState.items = new ArrayList<>();
        blockEntityRenderState.randomSeeds = new ArrayList<>();
        blockEntityRenderState.status = blockEntity.getStatus();
        blockEntityRenderState.hasLid = blockEntity.getBlockState().getValue(StockpotBlock.HAS_LID);
        blockEntityRenderState.hasLiquidIngredients = blockEntity.liquidMerged();
        for (var index = 0; index < blockEntity.getInputs().size(); index++) {
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            ItemStack itemStack = blockEntity.getInputs().get(index);
            if (itemStack.is(TagMod.SPECIAL)) {
                itemStack.set(ModDataComponents.SPECIAL_RENDER, true);
            }
            if (!(ItemUtils.getContainerItem(itemStack).getDefaultInstance().isEmpty() || itemStack.is(TagMod.SPECIAL)))
                continue;
            this.itemModelResolver.updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, posLong + index);
            blockEntityRenderState.items.add(itemStackRenderState);
            blockEntityRenderState.randomSeeds.add(itemStack.hashCode());
        }
        if (blockEntity.renderEntity != null)
            blockEntityRenderState.renderEntity = this.entityRenderDispatcher.extractEntity(blockEntity.renderEntity, f);
        else if (SoupBaseManager.getSoupBase(blockEntityRenderState.soupBaseID) instanceof MobSoupBase soupBase && blockEntity.getLevel() != null) {
            blockEntity.renderEntity = createRenderEntity(soupBase, blockEntity.getLevel());
        }
        blockEntityRenderState.cookingTexture = blockEntity.getCookingTexture();
        blockEntityRenderState.finishedTexture = blockEntity.getFinishedTexture();
        blockEntityRenderState.takeOutCount = blockEntity.getTakeoutCount();
        blockEntityRenderState.output = blockEntity.getResult();
    }

    private static @Nullable Entity createRenderEntity(MobSoupBase soupBase, Level level) {
        Entity entity = soupBase.getType().create(level, EntitySpawnReason.BUCKET);
        if (entity != null)
            entity.setId(nextRenderEntityId++);
        // 河豚随机膨胀形态
        if (entity instanceof Pufferfish pufferfish)
            pufferfish.setPuffState(level.getRandom().nextInt(3));
        return entity;
    }

    @Override
    public void submit(@NonNull StockpotBlockEntityRenderState blockEntityRenderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (blockEntityRenderState.hasLid) {
            return;
        }
        if (!SoupBaseManager.containsSoupBase(blockEntityRenderState.soupBaseID)) {
            return;
        }
        int status = blockEntityRenderState.status;
        ISoupBaseRender soupBase = this.soupBaseRender.apply(blockEntityRenderState.soupBaseID);
        if (soupBase == null) {
            return;
        }
        if (status == StockpotBlockEntity.PUT_INGREDIENT) {
            soupBase.renderWhenPutIngredient(blockEntityRenderState, 0, poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0.38f, cameraRenderState);
            renderItems(blockEntityRenderState, poseStack, submitNodeCollector);
        } else if (status == StockpotBlockEntity.COOKING) {
            soupBase.renderWhenCooking(blockEntityRenderState, 0, poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, blockEntityRenderState.cookingTexture, 0.38f, cameraRenderState);
            renderItems(blockEntityRenderState, poseStack, submitNodeCollector);
        } else if (status == StockpotBlockEntity.FINISHED) {
            int takeoutCount = blockEntityRenderState.takeOutCount;
            int maxCount = Math.min(blockEntityRenderState.output.getCount(), StockpotBlockEntity.MAX_TAKEOUT_COUNT);
            float soupHeight = 0.065f + 0.315f / maxCount * takeoutCount;
            soupBase.renderWhenFinished(blockEntityRenderState, 0, poseStack, submitNodeCollector, blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, blockEntityRenderState.finishedTexture, soupHeight, cameraRenderState);
        }
    }

    private void renderItems(StockpotBlockEntityRenderState blockEntityRenderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector) {
        for (int i = 0; i < blockEntityRenderState.items.size(); i++) {
            ItemStackRenderState itemStackRenderState = blockEntityRenderState.items.get(i);
            long random = blockEntityRenderState.randomSeeds.get(i);
            if (!itemStackRenderState.isEmpty()) {
                long time = random + System.currentTimeMillis();
                float offsetX = (random % 100) * 0.002f;
                float offsetZ = (float) (Math.sin(time * 0.0005) * 0.2);
                float offsetY = random % 50 * 0.004f;
                float yRot = (random % 2 == 0 ? -1 : 1) * 20 + random % 10;
                poseStack.pushPose();
                poseStack.rotateDegrees(Axis.XP, 85 + random % 10);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.9 + offsetX, 0.9 + offsetY, -0.5 + offsetZ);
                poseStack.rotateDegrees(Axis.YP, yRot);
                poseStack.rotateDegrees(Axis.ZP, random % 360);
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
    }
}
