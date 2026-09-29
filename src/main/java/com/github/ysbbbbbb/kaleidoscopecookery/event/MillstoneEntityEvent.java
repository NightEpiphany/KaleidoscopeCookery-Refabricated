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

    @SuppressWarnings("deprecation")
    private static boolean onLivingAttack(LivingEntity entity, DamageSource source, float amount) {
        BlockPos origin = entity.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-5, -2, -5), origin.offset(5, 2, 5))) {
            if (entity.level().hasChunkAt(pos)
                    && entity.level().getBlockEntity(pos) instanceof MillstoneBlockEntity millstone
                    && millstone.isBoundTo(entity)) {
                millstone.setCacheRot(millstone.getRotation(entity.level(), 0));
                millstone.unbindEntity();
                millstone.refresh();
                break;
            }
        }
        return true;
    }
}
