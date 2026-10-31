package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.ThrowableProjectile;

/**
 * 1.20.1 already receives these vanilla hooks from Porting Lib. Forward them to
 * the cookery event bus so each action is dispatched only once.
 */
public final class PortingLibEventBridge {
    private PortingLibEventBridge() {
    }

    public static void register() {
        io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent.PROJECTILE_IMPACT.register(event -> {
            Projectile projectile = event.getProjectile();
            if (!(projectile instanceof AbstractHurtingProjectile
                    || projectile instanceof FishingHook
                    || projectile instanceof LlamaSpit
                    || projectile instanceof ShulkerBullet
                    || projectile instanceof ThrowableProjectile)) {
                return;
            }
            ProjectileImpactEvent cookeryEvent = new ProjectileImpactEvent(projectile, event.getRayTraceResult());
            cookeryEvent.setCanceled(event.isCanceled());
            cookeryEvent.post();
            event.setCanceled(cookeryEvent.isCanceled());
        });
    }
}
