package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.client.RenderPlayerEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.item.LiftBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
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
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

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
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setModelProperties(Lnet/minecraft/client/player/AbstractClientPlayer;)V"
            ), cancellable = true
    )
    private void kaleidoscopeCookery$setModelProperties(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        var preEvent = new RenderPlayerEvent.Pre(abstractClientPlayer, ((PlayerRenderer) (Object)this) , g, poseStack, multiBufferSource, i);
        preEvent.post();
        if (preEvent.isCanceled()) ci.cancel();
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL")
    )
    private void kaleidoscopeCookery$render(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        var postEvent = new RenderPlayerEvent.Post(abstractClientPlayer, ((PlayerRenderer) (Object)this) , g, poseStack, multiBufferSource, i);
        postEvent.post();
    }
}
