package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class MillstoneEntityEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(MillstoneEntityEvent::onLivingAttack);
    }

    private static boolean onLivingAttack(LivingEntity entity, DamageSource source, float amount) {
        MillstoneBlockEntity millstone = MillstoneBlockEntity.findBoundMillstone(entity);
        if (millstone != null) {
            millstone.setCacheRot(millstone.getRotation(entity.level(), 0));
            millstone.unbindEntity();
            millstone.refresh();
        }
        return true;
    }
}
