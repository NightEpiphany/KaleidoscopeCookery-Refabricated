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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import org.jspecify.annotations.NonNull;

public class OilPotDispenseBehavior implements DispenseBehavior {
    public static final MapCodec<OilPotDispenseBehavior> CODEC = MapCodec.unit(new OilPotDispenseBehavior());

    @Override
    public @NonNull DispenseResult execute(BlockSource source, @NonNull ItemStack stack) {
        Item item = stack.getItem();
        ServerLevel level = source.level();
        if (stack.is(ModItems.OIL_POT) && item instanceof BlockItem blockItem) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            BlockPos placePos = source.pos().relative(facing);
            Direction placeDirection = facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : facing;
            try {
                DirectionalPlaceContext context = new DirectionalPlaceContext(level, placePos, facing, stack, placeDirection);
                if (blockItem.place(context).consumesAction()) {
                    return DispenseResult.success(stack);
                }
            } catch (Exception exception) {
                KaleidoscopeCookery.LOGGER.error("Error trying to place oil pot at {}", placePos, exception);
            }
        }
        return DispenseResult.stoppingFailure();
    }

    @Override
    public @NonNull MapCodec<? extends DispenseBehavior> codec() {
        return CODEC;
    }
}
