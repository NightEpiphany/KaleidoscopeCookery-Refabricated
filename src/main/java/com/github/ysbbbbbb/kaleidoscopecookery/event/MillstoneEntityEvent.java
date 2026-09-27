package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingAttackEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public class MillstoneEntityEvent {
    public static void register() {
        LivingAttackEvent.ATTACK.register(MillstoneEntityEvent::onLivingAttack);
    }

    private static void onLivingAttack(LivingAttackEvent livingAttackEvent) {
        LivingEntity entity = livingAttackEvent.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        BlockPos origin = entity.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-5, -2, -5), origin.offset(5, 2, 5))) {
            if (entity.level().getBlockEntity(pos) instanceof MillstoneBlockEntity millstone
                    && millstone.isBoundTo(entity)) {
                millstone.unbindEntity();
                millstone.setCacheRot(millstone.getRotation(entity.level(), 0));
                millstone.refresh();
                return;
            }
        }
    }

}
