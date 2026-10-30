package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.LivingChangeTargetEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ChangeTargetEvent {

    public static void onTarget(LivingChangeTargetEvent event) {
        LivingEntity newTarget = event.getNewTarget();
        if (newTarget instanceof Player player && player.getVehicle() instanceof SitEntity sitEntity && sitEntity.getSitType() == SitEntity.TRASH_CAN) {
            // 怪物直接无视坐在垃圾桶里的人
            event.setCanceled(true);
        }
    }
}
