package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.entity.ISittable;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.util.SitUtil;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class PlayerSitEvent {
    public static void register() {
        UseBlockCallback.EVENT.register(PlayerSitEvent::onUseBlock);
        PlayerBlockBreakEvents.AFTER.register(PlayerSitEvent::onBlockBreak);
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide() || !level.mayInteract(player, hitResult.getBlockPos()) || player.isShiftKeyDown() || SitUtil.isPlayerSitting(player) || hitResult.getDirection() != Direction.UP)
            return InteractionResult.PASS;

        BlockPos hitPos = hitResult.getBlockPos();
        BlockState s = level.getBlockState(hitPos);
        Block b = s.getBlock();

        if (b instanceof ISittable sittable && isPlayerInRange(player, hitPos) && !SitUtil.isOccupied(level, hitPos) && player.getItemInHand(hand).isEmpty()) {
            SitEntity sit = ModEntities.SIT.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
            if (sit == null) return InteractionResult.PASS;
            sit.setSitType(SitEntity.DEFAULT);
            sit.absSnapTo(hitPos.getX() + 0.5D, hitPos.getY() + sittable.renderHeightOffset(), hitPos.getZ() + 0.5D);

            if (SitUtil.addSitEntity(level, hitPos, sit)) {
                level.addFreshEntity(sit);
                player.startRiding(sit);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private static void onBlockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (!level.isClientSide()) {
            SitEntity entity = SitUtil.getSitEntity(level, pos);

            if (entity != null && SitUtil.removeSitEntity(level, pos)) {
                entity.ejectPassengers();
            }
        }
    }

    private static boolean isPlayerInRange(Player player, BlockPos pos) {
        BlockPos playerPos = player.blockPosition();
        int blockReachDistance = 4;

        AABB range = new AABB(pos.getX() + blockReachDistance, pos.getY() + blockReachDistance, pos.getZ() + blockReachDistance, pos.getX() - blockReachDistance, pos.getY() - blockReachDistance, pos.getZ() - blockReachDistance);

        return range.minX <= playerPos.getX() && range.minY <= playerPos.getY() && range.minZ <= playerPos.getZ() && range.maxX >= playerPos.getX() && range.maxY >= playerPos.getY() && range.maxZ >= playerPos.getZ();
    }
}
