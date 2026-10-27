package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ProjectileImpactEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;damageSources()Lnet/minecraft/world/damagesource/DamageSources;"), cancellable = true)
    private void kaleidoscopeCookery$onHitEntity(EntityHitResult hitResult, CallbackInfo ci) {
        ProjectileImpactEvent projectileImpactEvent = new ProjectileImpactEvent((AbstractArrow) (Object) this, hitResult);
        ModEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(projectileImpactEvent);
        if (projectileImpactEvent.isCanceled()) ci.cancel();
    }
}
