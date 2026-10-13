package com.github.ysbbbbbb.kaleidoscopecookery.util.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public final class MilkFluid extends Fluid {
    @Override
    public @NonNull Item getBucket() {
        return Items.MILK_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(@NonNull FluidState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull FluidState other, @NonNull Direction direction) {
        return false;
    }

    @Override
    protected @NonNull Vec3 getFlow(@NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull FluidState state) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(@NonNull LevelReader level) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(@NonNull FluidState state, @NonNull BlockGetter level, @NonNull BlockPos pos) {
        return getOwnHeight(state);
    }

    @Override
    public float getOwnHeight(@NonNull FluidState state) {
        return 1;
    }

    @Override
    protected @NonNull BlockState createLegacyBlock(@NonNull FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(@NonNull FluidState state) {
        return true;
    }

    @Override
    public boolean isFalling(@NonNull FluidState fluidState) {
        return false;
    }

    @Override
    public int getAmount(@NonNull FluidState state) {
        return 8;
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull FluidState state, @NonNull BlockGetter level, @NonNull BlockPos pos) {
        return Shapes.block();
    }
}
