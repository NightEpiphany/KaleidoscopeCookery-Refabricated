package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ProjectileImpactEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingHook.class)
public class FishingHookMixin {
    @WrapOperation(method = "checkCollision", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/FishingHook;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"
    ))
    private ProjectileDeflection kaleidoscopeCookery$onImpact(FishingHook projectile, HitResult result, Operation<ProjectileDeflection> original) {
        if (result.getType() == HitResult.Type.MISS)
            return original.call(projectile, result);
        ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, result);
        ModEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(event);
        if (!event.isCanceled()) {
            return original.call(projectile, result);
        } else {
            return null;
        }
    }
}
