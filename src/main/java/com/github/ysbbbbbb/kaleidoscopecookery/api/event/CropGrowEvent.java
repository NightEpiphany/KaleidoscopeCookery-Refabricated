package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CropGrowEvent extends BlockEvent {
    public CropGrowEvent(LevelAccessor level, BlockPos pos, BlockState state) {
        super(level, pos, state);
    }

    public static class Pre extends CropGrowEvent {
        private Result result;

        public Pre(LevelAccessor level, BlockPos pos, BlockState state) {
            super(level, pos, state);
            this.result = CropGrowEvent.Pre.Result.DEFAULT;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public Result getResult() {
            return this.result;
        }


        public enum Result {
            GROW,
            DEFAULT,
            DO_NOT_GROW;

            Result() {
            }
        }
    }

    public static class Post extends CropGrowEvent {
        private final BlockState originalState;

        public Post(Level level, BlockPos pos, BlockState original, BlockState state) {
            super(level, pos, state);
            this.originalState = original;
        }

        public BlockState getOriginalState() {
            return this.originalState;
        }

        public BlockState getState() {
            return super.getState();
        }

    }
}
