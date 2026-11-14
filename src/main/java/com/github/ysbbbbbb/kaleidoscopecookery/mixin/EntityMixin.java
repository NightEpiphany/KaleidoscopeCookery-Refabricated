package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantValue")
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
    private void kaleidoscopeCookery$suppressMillstoneHorseLandingSound(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        if (sound == SoundEvents.HORSE_LAND
                && (Object) this instanceof LivingEntity entity
                && MillstoneBlockEntity.findBoundMillstone(entity) != null) {
            ci.cancel();
        }
    }
}
