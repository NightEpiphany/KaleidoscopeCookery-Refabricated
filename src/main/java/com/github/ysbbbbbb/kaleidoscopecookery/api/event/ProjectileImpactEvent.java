package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.util.event.CancellableKCEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

public class ProjectileImpactEvent extends CancellableKCEvent implements IActionCancelable {
    private final HitResult ray;
    private final Projectile projectile;
    protected final Entity entity;

    public ProjectileImpactEvent(Projectile projectile, HitResult ray) {
        this.entity = projectile;
        this.ray = ray;
        this.projectile = projectile;
    }

    public Entity getEntity() {
        return entity;
    }

    public HitResult getRayTraceResult() {
        return ray;
    }

    public Projectile getProjectile() {
        return projectile;
    }

}
