package com.github.ysbbbbbb.kaleidoscopecookery.event.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ProjectileImpactEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileDodgeEvent {
    /**
     * 闪避消耗的持续时间 (tick)
     */
    private static final int DODGE_COST = 200;
    public static void register() {
        ModEvents.PROJECTILE_IMPACT.register(ProjectileDodgeEvent::onProjectileHit);
    }

    public static void onProjectileHit(ProjectileImpactEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        HitResult hit = event.getRayTraceResult();
        if (hit instanceof EntityHitResult hitResult
                && hitResult.getEntity() instanceof LivingEntity living
                && living.hasEffect(ModEffects.PROJECTILE_DODGE)
        ) {
            // 取消弹射物碰撞并随机传送
            event.setCanceled(true);
            randomTeleport(living.level(), living, 3, 16);

            // 消耗持续时间
            MobEffectInstance instance = living.getEffect(ModEffects.PROJECTILE_DODGE);
            if (instance != null) {
                // 如果是无限时间，不扣除
                if (instance.isInfiniteDuration()){
                    return;
                }
                int remainingDuration = durationAfterDodge(instance.getDuration());
                if (remainingDuration == 0) {
                    living.removeEffect(ModEffects.PROJECTILE_DODGE);
                } else {
                    instance.duration = remainingDuration;
                    living.forceAddEffect(instance, null);
                }
            }
        }
    }

    static int durationAfterDodge(int duration) {
        if (duration == MobEffectInstance.INFINITE_DURATION) {
            return duration;
        }
        return duration <= DODGE_COST ? 0 : duration - DODGE_COST;
    }

    /**
     * 随机传送目标
     *
     * @param level       目标所在的世界
     * @param living      目标
     * @param range       传送半径
     * @param maxAttempts 最大尝试次数
     */
    public static void randomTeleport(Level level, LivingEntity living, double range, int maxAttempts) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        double x = living.getX();
        double y = living.getY();
        double z = living.getZ();
        int minH = level.getMinY();
        int maxH = serverLevel.getLogicalHeight();

        for (int i = 0; i < maxAttempts; ++i) {
            double targetX = x + (living.getRandom().nextDouble() - 0.5) * range;
            double targetY = Math.clamp(y + (living.getRandom().nextDouble() - 0.5) * range, minH, minH + maxH - 1);
            double targetZ = z + (living.getRandom().nextDouble() - 0.5) * range;

            if (living.isPassenger()) {
                living.stopRiding();
            }

            Vec3 previousPos = living.position();
            if (living.randomTeleport(targetX, targetY, targetZ, true, BlockTags.ENDERMAN_DOES_NOT_TELEPORT_TO)) {
                level.gameEvent(GameEvent.TELEPORT, previousPos, GameEvent.Context.of(living));
                serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        previousPos.x,
                        previousPos.y + living.getBbHeight() * 0.5,
                        previousPos.z,
                        32,
                        living.getBbWidth() * 0.5,
                        living.getBbHeight() * 0.5,
                        living.getBbWidth() * 0.5,
                        0.1
                );
                SoundEvent soundEvent = SoundEvents.ENDERMAN_TELEPORT;
                level.playSound(null, x, y, z, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                living.playSound(soundEvent, 1.0F, 1.0F);
                break;
            }
        }
    }
}
