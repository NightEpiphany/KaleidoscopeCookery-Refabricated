package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("deprecation")
public class KitchenShelfBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty SNEAK_PLACED = BooleanProperty.create("sneak_placed");
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * 学习原版投掷器，4 tick 延迟发射
     */
    private static final int DISPENSE_DELAY = 4;

    public KitchenShelfBlock() {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(SNEAK_PLACED, false)
                .setValue(WATERLOGGED, false)
                .setValue(PART, Part.SINGLE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(SNEAK_PLACED, context.getPlayer() != null && context.getPlayer().isShiftKeyDown());
        return updatePart(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, SNEAK_PLACED, PART, WATERLOGGED);
    }

    @Override
    public @NonNull BlockState updateShape(@NonNull BlockState state, @NonNull Direction direction, @NonNull BlockState neighborState,
                                           @NonNull LevelAccessor level, @NonNull BlockPos pos, @NonNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return updatePart(state, level, pos);
    }

    private BlockState updatePart(BlockState state, LevelAccessor level, BlockPos pos) {
        if (state.getValue(SNEAK_PLACED)) {
            return state.setValue(PART, Part.SINGLE);
        }

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean left = isConnectable(level.getBlockState(pos.relative(facing.getCounterClockWise())));
        boolean right = isConnectable(level.getBlockState(pos.relative(facing.getClockWise())));

        Part part;
        if (left && right) {
            part = Part.MIDDLE;
        } else if (left) {
            part = Part.RIGHT;
        } else if (right) {
            part = Part.LEFT;
        } else {
            part = Part.SINGLE;
        }
        return state.setValue(PART, part);
    }

    private boolean isConnectable(BlockState state) {
        return state.getBlock() == this && !state.getValue(SNEAK_PLACED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void onPlace(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(this)) {
            updateRedstone(level, pos);
        }
    }

    @Override
    public void neighborChanged(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                @NonNull Block neighborBlock, @NonNull BlockPos fromPos, boolean isMoving) {
        updateRedstone(level, pos);
    }

    private void updateRedstone(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel
                && serverLevel.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf
                && shelf.updatePower(serverLevel.hasNeighborSignal(pos))) {
            serverLevel.scheduleTick(pos, this, DISPENSE_DELAY);
        }
    }

    @Override
    public void tick(@NonNull BlockState state, ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (level.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf) {
            shelf.dispense(state);
        }
    }

    @Override
    public @NonNull InteractionResult use(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand,
                                          @NonNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack itemInHand = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf
                && !itemInHand.is(ModItems.KITCHEN_SHELF)
                && shelf.interact(player, player.isShiftKeyDown())) {
            level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new KitchenShelfBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        return createTickerHelper(type, ModBlocks.KITCHEN_SHELF_BE,
                (tickLevel, pos, blockState, shelf) -> KitchenShelfBlockEntity.serverTick(serverLevel, pos, blockState, shelf));
    }

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, @NonNull Level level, @NonNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !level.isClientSide
                && level.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf) {
            shelf.dropContents();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
