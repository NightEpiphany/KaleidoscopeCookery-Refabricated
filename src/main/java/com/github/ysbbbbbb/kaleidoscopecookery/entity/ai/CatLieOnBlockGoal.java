package com.github.ysbbbbbb.kaleidoscopecookery.entity.ai;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.ChairBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class CatLieOnBlockGoal extends MoveToBlockGoal {
    private final Cat cat;

    public CatLieOnBlockGoal(Cat cat, double speedModifier, int searchRange) {
        super(cat, speedModifier, searchRange, 6);
        this.cat = cat;
    }

    @Override
    public boolean canUse() {
        return this.canApproach() && !this.cat.isLying() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canApproach() && super.canContinueToUse();
    }

    private boolean canApproach() {
        return this.cat.isTame() && !this.cat.isOrderedToSit() && !this.cat.isPassenger();
    }

    @Override
    public void start() {
        super.start();
        this.cat.setInSittingPose(false);
    }

    @Override
    protected int nextStartTick(@NonNull PathfinderMob mob) {
        return 40;
    }

    @Override
    public void stop() {
        super.stop();
        if (!SitEntity.isCatLyingOnChair(this.cat)) {
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
        if (state.getBlock() instanceof ChairBlock && this.cat.level() instanceof ServerLevel level) {
            this.tickChair(level, state);
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
            var navigation = this.cat.getNavigation();
            navigation.moveTo(navigation.createPath(Set.of(this.blockPos), 1), this.speedModifier);
        } else {
            super.moveMobToBlock();
        }
    }

    private void tickChair(ServerLevel level, BlockState state) {
        this.tryTicks++;
        Vec3 seat = ChairBlock.catLiePosition(this.blockPos, state);
        double range = this.acceptedDistance();
        if (this.cat.distanceToSqr(seat) <= range * range && this.isValidTarget(level, this.blockPos)) {
            BlockHitResult hit = level.clip(new ClipContext(this.cat.getEyePosition(), seat,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.cat));
            if ((hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(this.blockPos))
                    && SitEntity.trySeatCat(level, this.cat, seat)) {
                return;
            }
        }
        if (this.shouldRecalculatePath()) {
            this.moveMobToBlock();
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader pLevel, @NonNull BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos);
        return state.is(TagMod.CAT_LIE_ON_BLOCKS) && pLevel.isEmptyBlock(pPos.above())
                && (!(state.getBlock() instanceof ChairBlock) || pLevel instanceof ServerLevel level
                && !this.cat.isLeashed() && !SitEntity.isSeatAt(level, pPos));
    }
}
