package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.FarmlandTrampleEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ScarecrowFarmlandTrampleEvent {

    public static void onFarmlandTrample(FarmlandTrampleEvent event) {
        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();
        if (level instanceof ServerLevel serverLevel) {
            List<ScarecrowEntity> entities = serverLevel.getEntitiesOfClass(ScarecrowEntity.class, new AABB(pos).inflate(16));
            if (!entities.isEmpty()) {
                event.setCanceled(true);
            }
        }
    }
}
