package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {

    @Inject(
            method = "fallOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", by = -1, shift = At.Shift.BY), cancellable = true
    )
    private static void kaleidoscopeCookery$fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel) {
            List<ScarecrowEntity> entities = serverLevel.getEntitiesOfClass(ScarecrowEntity.class, new AABB(pos).inflate(16));
            if (!entities.isEmpty()) {
                ci.cancel();
            }
        }
    }
}
