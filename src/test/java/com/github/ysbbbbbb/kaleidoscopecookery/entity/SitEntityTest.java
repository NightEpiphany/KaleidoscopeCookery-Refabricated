package com.github.ysbbbbbb.kaleidoscopecookery.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SitEntityTest {
    @Test
    void dismountUsesFirstOpenCardinalPosition() {
        BlockPos seat = new BlockPos(3, 64, 5);
        BlockPos south = seat.south();

        assertEquals(Vec3.atBottomCenterOf(south),
                SitEntity.findCardinalDismountLocation(seat, south::equals));
    }

    @Test
    void dismountHasNoCardinalPositionWhenAllAreBlocked() {
        assertNull(SitEntity.findCardinalDismountLocation(BlockPos.ZERO, ignored -> false));
    }
}
