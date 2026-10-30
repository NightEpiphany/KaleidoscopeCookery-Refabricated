package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.util.event.CancellableKCEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class FarmlandTrampleEvent extends CancellableKCEvent {
    private final Entity entity;
    private final float fallDistance;
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState state;

    public FarmlandTrampleEvent(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
        this.entity = entity;
        this.fallDistance = fallDistance;
        this.level = level;
        this.pos = pos;
        this.state = state;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public float getFallDistance() {
        return this.fallDistance;
    }

    public LevelAccessor getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }
}
