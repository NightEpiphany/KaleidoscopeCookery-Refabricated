package com.github.ysbbbbbb.kaleidoscopecookery.util.neo;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

//From Neoforge
@Deprecated(forRemoval = true)
public interface IBlockEntityRendererExtension<T extends BlockEntity> {
    default AABB getRenderBoundingBox(T blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }
}
