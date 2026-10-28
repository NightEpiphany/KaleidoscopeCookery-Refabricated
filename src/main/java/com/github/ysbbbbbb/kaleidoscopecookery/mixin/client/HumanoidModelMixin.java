package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.item.LiftBlockItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> {

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    public void kaleidoscopeCookery$poseRightArm(T livingEntity, CallbackInfo ci) {
        boolean bl3 = livingEntity.getMainArm() == HumanoidArm.RIGHT;
        if (livingEntity instanceof AbstractClientPlayer player) {
            if (!player.getItemInHand(bl3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND).isEmpty() && player.getItemInHand(bl3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND).getItem() instanceof LiftBlockItem) {
                rightArm.xRot = -Mth.PI;
                rightArm.zRot = -Mth.PI * 0.025f;
                ci.cancel();
            }
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    public void kaleidoscopeCookery$poseLeftArm(T livingEntity, CallbackInfo ci) {
        boolean bl3 = livingEntity.getMainArm() == HumanoidArm.LEFT;
        if (livingEntity instanceof AbstractClientPlayer player) {
            if (!player.getItemInHand(bl3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND).isEmpty() && player.getItemInHand(bl3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND).getItem() instanceof LiftBlockItem) {
                leftArm.xRot = -Mth.PI * 0.5f;
                leftArm.zRot = Mth.PI * 0.025f;
                ci.cancel();
            }
        }
    }
}
