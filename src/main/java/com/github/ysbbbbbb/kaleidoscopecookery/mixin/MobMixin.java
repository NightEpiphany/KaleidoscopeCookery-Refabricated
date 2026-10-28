package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.LivingChangeTargetEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    public void kaleidoscopeCookery$setTarget(LivingEntity livingEntity, CallbackInfo ci) {
        var event = new LivingChangeTargetEvent(this, livingEntity, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        ModEvents.ENTITY_CHANGE_TARGET.invoker().onEntityChangeTarget(event);
        if (event.isCanceled()) ci.cancel();
    }
}
