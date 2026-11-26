package com.github.ysbbbbbb.kaleidoscopecookery.entity.ai;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.ChairBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;
import java.util.Set;

public class CatLieOnBlockGoal extends MoveToBlockGoal {
    private final Cat cat;

    public CatLieOnBlockGoal(Cat cat, double speedModifier, int searchRange) {
        super(cat, speedModifier, searchRange, 6);
        this.cat = cat;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.cat.isPassenger() && this.cat.isTame() && !this.cat.isOrderedToSit() && !this.cat.isLying() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.cat.isPassenger() && super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        this.cat.setInSittingPose(false);
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return 40;
    }

    @Override
    public void stop() {
        super.stop();
        if (!SitEntity.isCatChairPassenger(this.cat)) {
            this.cat.setLying(false);
        }
    }

    @Override
    public double acceptedDistance() {
        return 1.5;
    }

    @Override
    public void tick() {
        if (this.cat.isPassenger()) {
            return;
        }

        BlockState state = this.cat.level().getBlockState(this.blockPos);
        if (state.getBlock() instanceof ChairBlock) {
            if (ChairBlock.tryToLieOn(state, this.blockPos, this.cat)) {
                return;
            }
            this.tryTicks++;
            if (this.shouldRecalculatePath()) {
                this.moveMobToBlock();
            }
            Path path = this.cat.getNavigation().getPath();
            if (path != null && path.isDone() && path.canReach()) {
                BlockPos approach = path.getTarget();
                this.cat.getMoveControl().setWantedPosition(approach.getX() + 0.5, this.cat.getY(), approach.getZ() + 0.5, this.speedModifier);
            }
            return;
        }

        super.tick();
        this.cat.setInSittingPose(false);
        if (!this.isReachedTarget()) {
            this.cat.setLying(false);
        } else if (!this.cat.isLying()) {
            this.cat.setLying(true);
            this.cat.setPos(this.blockPos.getX() + 0.5, this.blockPos.getY() + 0.5, this.blockPos.getZ() + 0.5);
        }
    }

    @Override
    protected void moveMobToBlock() {
        if (this.cat.level().getBlockState(this.blockPos).getBlock() instanceof ChairBlock) {
            BlockPos pos = this.blockPos;
            PathNavigation navigation = this.cat.getNavigation();
            navigation.moveTo(navigation.createPath(Set.of(pos.north(), pos.south(), pos.west(), pos.east()), 0), this.speedModifier);
        } else {
            super.moveMobToBlock();
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(TagMod.CAT_LIE_ON_BLOCKS) || !level.isEmptyBlock(pos.above())) {
            return false;
        }
        return !(state.getBlock() instanceof ChairBlock)
                || (!this.cat.isLeashed() && !ChairBlock.isSeatOccupied(this.cat.level(), pos));
    }
}
