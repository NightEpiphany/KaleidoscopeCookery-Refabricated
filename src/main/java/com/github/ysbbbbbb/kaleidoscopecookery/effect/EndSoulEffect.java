package com.github.ysbbbbbb.kaleidoscopecookery.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModAttachmentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/*恭喜你发现彩蛋！*/
public final class EndSoulEffect extends BaseEffect {
    private static final double CRYSTAL_SEARCH_RANGE = 12;
    private static final int HEALING_INTERVAL = 10;

    public EndSoulEffect(int color) {
        super(color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NonNull ServerLevel level, @NonNull LivingEntity entity, int amplifier) {
        if (!entity.isAlive() || entity.tickCount % HEALING_INTERVAL != 0) {
            return true;
        }

        EndCrystal crystal = findNearestCrystal(level, entity, amplifier);
        Integer crystalId = crystal == null ? null : crystal.getId();
        if (!Objects.equals(crystalId, entity.getAttached(ModAttachmentType.END_SOUL_CRYSTAL_ID))) {
            entity.setAttached(ModAttachmentType.END_SOUL_CRYSTAL_ID, crystalId);
        }
        if (crystal != null && entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.0F);
        }
        return true;
    }

    @Override
    public void onEffectRemoved(@NonNull MobEffectInstance instance, LivingEntity entity) {
        if (entity.hasAttached(ModAttachmentType.END_SOUL_CRYSTAL_ID)) {
            entity.removeAttached(ModAttachmentType.END_SOUL_CRYSTAL_ID);
        }
    }

    private static EndCrystal findNearestCrystal(ServerLevel level, LivingEntity entity, int amplifier) {
        EndCrystal nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (EndCrystal crystal : level.getEntitiesOfClass(EndCrystal.class,
                entity.getBoundingBox().inflate(CRYSTAL_SEARCH_RANGE * Math.min(amplifier + 1, 4)), EndCrystal::isAlive)) {
            double distance = entity.distanceToSqr(crystal);
            if (distance < nearestDistance) {
                nearest = crystal;
                nearestDistance = distance;
            }
        }
        return nearest;
    }
}
