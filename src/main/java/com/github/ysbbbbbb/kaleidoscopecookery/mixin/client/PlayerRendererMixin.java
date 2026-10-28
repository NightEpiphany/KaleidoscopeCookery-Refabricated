package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.event.PlayerRenderEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.item.LiftBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
@Environment(EnvType.CLIENT)
public class PlayerRendererMixin {
    @Inject(method = "getArmPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private static void kaleidoscopeCookery$getArmPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        final HumanoidModel.ArmPose liftPose = HumanoidModel.ArmPose.SPYGLASS; // fallback
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.isEmpty()) cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
        if (itemstack.getItem() instanceof LiftBlockItem)
            cir.setReturnValue(liftPose);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void kaleidoscopeCookery$hideTrashCanPassenger(AbstractClientPlayer player, float entityYaw, float partialTicks,
                                                           PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                                           CallbackInfo ci) {
        if (PlayerRenderEvent.shouldCancel(player)) {
            ci.cancel();
        }
    }
}
