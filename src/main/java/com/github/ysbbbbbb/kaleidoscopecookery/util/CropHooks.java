package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.CropGrowEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface CropHooks {
    static boolean onCropsGrowPre(Level level, BlockPos pos, BlockState state, boolean def) {
        CropGrowEvent.Pre event = new CropGrowEvent.Pre(level, pos, state);
        event.post();
        return event.getResult() == CropGrowEvent.Pre.Result.GROW
                || event.getResult() == CropGrowEvent.Pre.Result.DEFAULT && def;
    }

    static void onCropsGrowPost(Level level, BlockPos pos, BlockState state) {
        CropGrowEvent.Post event = new CropGrowEvent.Post(level, pos, state, level.getBlockState(pos));
        event.post();
    }
}
