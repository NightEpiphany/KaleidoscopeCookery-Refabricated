package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.util.event.CancellableKCEvent;
import net.minecraft.world.entity.LivingEntity;

public class LivingChangeTargetEvent extends CancellableKCEvent {
    private final LivingEntity entity;
    private final ILivingTargetType targetType;
    private final LivingEntity originalTarget;
    private LivingEntity newTarget;


    public LivingChangeTargetEvent(LivingEntity entity, LivingEntity originalTarget, ILivingTargetType targetType)
    {
        this.entity = entity;
        this.originalTarget = originalTarget;
        this.newTarget = originalTarget;
        this.targetType = targetType;
    }

    public LivingEntity getNewTarget()
    {
        return newTarget;
    }

    public void setNewTarget(LivingEntity newTarget)
    {
        this.newTarget = newTarget;
    }

    public ILivingTargetType getTargetType()
    {
        return targetType;
    }

    public LivingEntity getOriginalTarget()
    {
        return originalTarget;
    }

    public static interface ILivingTargetType { }

    public static enum LivingTargetType implements ILivingTargetType
    {

        MOB_TARGET,

        BEHAVIOR_TARGET;
    }
}
