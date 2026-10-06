package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.api.entity.ISittable;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class LongBenchBlock extends Block implements SimpleWaterloggedBlock, ISittable {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int SINGLE = 0;
    public static final int LEFT = 1;
    public static final int MIDDLE = 2;
    public static final int RIGHT = 3;

    private static final VoxelShape EAST_WEST = Block.box(0, 0, 3, 16, 8, 13);
    private static final VoxelShape NORTH_SOUTH = Block.box(3, 0, 0, 13, 8, 16);

    public LongBenchBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.X)
                .setValue(WATERLOGGED, false)
                .setValue(POSITION, SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, POSITION, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = context.getHorizontalDirection().getClockWise().getAxis();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return updateConnections(context.getLevel(), context.getClickedPos(), this.defaultBlockState().setValue(AXIS, axis).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER));
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull LevelReader levelReader,
                                           @NotNull ScheduledTickAccess scheduledTickAccess, @NotNull BlockPos pos,
                                           @NotNull Direction direction, @NotNull BlockPos neighborPos,
                                           @NotNull BlockState neighborState, @NotNull net.minecraft.util.RandomSource randomSource) {
        if (direction.getAxis() == state.getValue(AXIS)) {
            return updateConnections(levelReader, pos, state);
        }
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return super.updateShape(state, levelReader, scheduledTickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }

    private BlockState updateConnections(LevelReader level, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        Direction negative = axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
        Direction positive = negative.getOpposite();
        boolean connectsNegative = connectsTo(level.getBlockState(pos.relative(negative)), axis);
        boolean connectsPositive = connectsTo(level.getBlockState(pos.relative(positive)), axis);

        if (connectsNegative && connectsPositive) {
            return state.setValue(POSITION, MIDDLE);
        }
        if (connectsNegative) {
            return state.setValue(POSITION, LEFT);
        }
        if (connectsPositive) {
            return state.setValue(POSITION, RIGHT);
        }
        return state.setValue(POSITION, SINGLE);
    }

    private boolean connectsTo(BlockState state, Direction.Axis axis) {
        return state.is(this) && state.getValue(AXIS) == axis;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void destroy(LevelAccessor level, @NonNull BlockPos pos, @NonNull BlockState state) {
        level.getEntitiesOfClass(SitEntity.class, new AABB(pos)).forEach(Entity::discard);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? EAST_WEST : NORTH_SOUTH;
    }

    @Override
    public float renderHeightOffset() {
        return 0.486F;
    }
}

