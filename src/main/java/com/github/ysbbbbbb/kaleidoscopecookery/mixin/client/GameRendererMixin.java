package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.client.ViewportEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    private Camera mainCamera;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void kaleidoscopeCookery$renderLevel(float f, long l, PoseStack poseStack, CallbackInfo ci) {
        var event = new ViewportEvent.ComputeCameraAngles(((GameRenderer) (Object) this), this.mainCamera, f, this.mainCamera.getYRot(), this.mainCamera.getXRot(), 0);
        event.post();
        // setup() 会覆盖相机旋转，所以要在其执行后再应用事件修改。
        this.mainCamera.yRot = event.getYaw();
        this.mainCamera.xRot = event.getPitch();
        poseStack.mulPose(Axis.ZP.rotationDegrees(event.getRoll()));
    }
}
