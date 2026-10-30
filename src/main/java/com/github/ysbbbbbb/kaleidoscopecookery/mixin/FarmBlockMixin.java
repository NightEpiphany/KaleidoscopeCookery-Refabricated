package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.FarmlandTrampleEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class FarmBlockMixin {
    @SuppressWarnings("DiscouragedShift")
    @Inject(
            method = "fallOn",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/FarmlandBlock;turnToBaseBlock(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    private void kaleidoscopeCookery$onFallOn(Level level, BlockState state, BlockPos pos, Entity entity, double d, CallbackInfo ci) {
        FarmlandTrampleEvent farmlandTrampleEvent = new FarmlandTrampleEvent(level, pos, state, (float) d, entity);
        farmlandTrampleEvent.post();
        if (farmlandTrampleEvent.isCanceled()) ci.cancel();

    }
}
