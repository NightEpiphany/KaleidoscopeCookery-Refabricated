package com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IChoppingBoard;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.jspecify.annotations.NonNull;

public class ChoppingBoardDispenseBehavior implements DispenseBehavior {
    public static final MapCodec<ChoppingBoardDispenseBehavior> CODEC = Codec.BOOL.fieldOf("knife")
            .xmap(ChoppingBoardDispenseBehavior::new, behavior -> behavior.knife);

    private final boolean knife;

    public ChoppingBoardDispenseBehavior(boolean knife) {
        this.knife = knife;
    }

    @Override
    public @NonNull DispenseResult execute(BlockSource source, @NonNull ItemStack stack) {
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos boardPos = source.pos().relative(facing);
        if (source.level().getBlockEntity(boardPos) instanceof IChoppingBoard board) {
            // onPutItem consumes the ingredient itself.
            boolean success = this.knife
                    ? board.onCutItem(source.level(), null, stack)
                    : board.onPutItem(source.level(), null, stack);
            if (this.knife && board.hasItemOnBoard())
                if (stack.getDamageValue() >= stack.getMaxDamage())
                    stack.shrink(1);
                else
                    stack.setDamageValue(stack.getDamageValue() + 1);
            return success ? DispenseResult.success(stack) : DispenseResult.stoppingFailure();
        }
        return DispenseResult.stoppingFailure();
    }

    @Override
    public @NonNull MapCodec<? extends DispenseBehavior> codec() {
        return CODEC;
    }
}
