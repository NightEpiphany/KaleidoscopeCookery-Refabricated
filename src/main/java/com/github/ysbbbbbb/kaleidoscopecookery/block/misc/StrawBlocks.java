package com.github.ysbbbbbb.kaleidoscopecookery.block.misc;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class StrawBlocks extends RotatedPillarBlock {
    public StrawBlocks(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public void fallOn(Level level, @NonNull BlockState state, @NonNull BlockPos pos, @NonNull Entity entity, double fallDistance) {
        if (level.isClientSide()) {
            return;
        }
        level.playSound(null, pos, SoundEvents.GRASS_FALL, entity.getSoundSource(), 1.0F, 1.0F);
        // 三格高不损失稻草
        if (fallDistance < 3) {
            return;
        }
        // 完全免伤，但是稻草有几率会被破坏
        float possibility = (float) Math.clamp(fallDistance / 30F, 0F, 1F);
        if (level.getRandom().nextFloat() < possibility) {
            level.destroyBlock(pos, false);
            popResource(level, pos, new ItemStack(ModItems.RICE_PANICLE, 5));
            popResource(level, pos, new ItemStack(ModItems.RICE_SEED, 4));
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        10, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }
}
