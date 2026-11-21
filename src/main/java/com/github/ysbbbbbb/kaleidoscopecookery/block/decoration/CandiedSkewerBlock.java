package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Prediction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.sounds.BlockSoundSets;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class CandiedSkewerBlock extends StackableFoodBlock {
    private static final VoxelShape UPRIGHT = Block.box(0, 0, 0, 16, 20, 16);
    private static final VoxelShape WIDE_UPRIGHT = Block.box(-4, 0, -4, 20, 20, 20);
    private static final VoxelShape SIDE = Block.box(0, 0, 0, 16, 24, 16);

    private final int lightLevel;
    private final boolean upright;

    public CandiedSkewerBlock(BlockBehaviour.Properties properties, int maxCount, Supplier<Item> item,
                              int lightLevel, boolean upright) {
        super(properties.forceSolidOn().instabreak().mapColor(MapColor.WOOD).sound(BlockSoundSets.WOOD)
                .pushReaction(PushReaction.POPPED).lightLevel(_ -> lightLevel).noCollision(), maxCount, item);
        this.lightLevel = lightLevel;
        this.upright = upright;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && !this.upright && context.getClickedFace().getAxis().isHorizontal()) {
            state = state.setValue(FACING, context.getClickedFace());
        }
        return state;
    }

    @Override
    protected @NonNull BlockState updateShape(@NonNull BlockState state, @NonNull LevelReader level,
                                               @NonNull ScheduledTickAccess scheduledTicks, @NonNull BlockPos pos,
                                               @NonNull Direction direction, @NonNull BlockPos neighborPos,
                                               @NonNull BlockState neighborState, @NonNull RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTicks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, scheduledTicks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NonNull InteractionResult useItemOn(@NonNull ItemStack held, @NonNull BlockState state,
                                                 @NonNull Level level, @NonNull BlockPos pos,
                                                 @NonNull Player player, @NonNull InteractionHand hand,
                                                 @NonNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        int count = state.getValue(this.countProperty);
        if (held.is(this.item.get())) {
            if (count >= this.maxCount) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, state.setValue(this.countProperty, count + 1));
                state.getSounds(level).placeSound().ifPresent(sound ->
                        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F));
                held.consume(1, player);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        if (!level.isClientSide()) {
            ItemStack removed = this.item.get().getDefaultInstance();
            if (held.isEmpty()) {
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false, Prediction.SERVER_ONLY);
                }
            } else {
                Block.popResourceFromFace(level, pos, Direction.UP, removed);
            }
            if (count > 1) {
                level.setBlockAndUpdate(pos, state.setValue(this.countProperty, count - 1));
            } else {
                level.removeBlock(pos, false);
            }
            state.getSounds(level).breakSound().ifPresent(sound ->
                    level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F));
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level,
                                       @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (!this.upright) {
            return SIDE;
        }
        return state.getValue(this.countProperty) > 4 ? WIDE_UPRIGHT : UPRIGHT;
    }
}
