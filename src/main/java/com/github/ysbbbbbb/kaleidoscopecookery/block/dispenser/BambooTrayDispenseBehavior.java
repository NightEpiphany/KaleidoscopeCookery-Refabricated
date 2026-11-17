package com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import org.jspecify.annotations.NonNull;

public class BambooTrayDispenseBehavior implements DispenseBehavior {
    public static final MapCodec<BambooTrayDispenseBehavior> CODEC = MapCodec.unit(new BambooTrayDispenseBehavior());

    @Override
    public @NonNull DispenseResult execute(BlockSource source, @NonNull ItemStack stack) {
        ServerLevel level = source.level();
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos placePos = source.pos().relative(facing);
        if (stack.is(ModItems.BAMBOO_TRAY) && stack.getItem() instanceof BlockItem blockItem) {
            try {
                DirectionalPlaceContext context = new DirectionalPlaceContext(level, placePos, facing, stack, Direction.UP);
                if (blockItem.place(context).consumesAction()) {
                    return DispenseResult.success(stack);
                }
            } catch (Exception exception) {
                KaleidoscopeCookery.LOGGER.error("Error trying to place bamboo tray at {}", placePos, exception);
            }
        }
        return DispenseResult.stoppingFailure();
    }

    @Override
    public @NonNull MapCodec<? extends DispenseBehavior> codec() {
        return CODEC;
    }
}
