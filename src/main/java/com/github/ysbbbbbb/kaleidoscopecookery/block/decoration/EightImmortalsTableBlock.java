package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.sounds.BlockSoundSets;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class EightImmortalsTableBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SELECTION_SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape COLLISION_SHAPE = Block.box(0, 12, 0, 16, 16, 16);

    public EightImmortalsTableBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(BlockSoundSets.BAMBOO_WOOD)
                .pushReaction(PushReaction.PUSH)
                .noOcclusion()
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(PART, Part.RIGHT_BOTTOM));
    }

    @Override
    public @NonNull VoxelShape getShape(
            @NonNull BlockState state,
            @NonNull BlockGetter level,
            @NonNull BlockPos pos,
            @NonNull CollisionContext context
    ) {
        return SELECTION_SHAPE;
    }

    @Override
    public @NonNull VoxelShape getCollisionShape(
            @NonNull BlockState state,
            @NonNull BlockGetter level,
            @NonNull BlockPos pos,
            @NonNull CollisionContext context
    ) {
        return COLLISION_SHAPE;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = this.defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        BlockPos anchor = context.getClickedPos();
        Level level = context.getLevel();

        for (Part part : Part.values()) {
            BlockPos partPos = getPartPos(anchor, facing, part);
            BlockState partState = state.setValue(PART, part);
            if (level.isOutsideBuildHeight(partPos)
                || !level.getWorldBorder().isWithinBounds(partPos)
                || !level.getBlockState(partPos).canBeReplaced(context)
                || !level.isUnobstructed(partState, partPos, CollisionContext.empty())
                || context.getPlayer() != null && !context.getPlayer().mayUseItemAt(partPos, Direction.UP, context.getItemInHand())) {
                return null;
            }
        }
        return state;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, @NotNull LevelReader levelReader,
                                           @NotNull ScheduledTickAccess scheduledTickAccess, @NotNull BlockPos blockPos,
                                           @NotNull Direction direction, @NotNull BlockPos blockPos2,
                                           @NotNull BlockState blockState2, @NotNull net.minecraft.util.RandomSource randomSource) {
        if (blockState.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        Direction facing = state.getValue(FACING);
        for (Part part : Part.values()) {
            if (part != Part.RIGHT_BOTTOM) {
                level.setBlock(getPartPos(pos, facing, part), state.setValue(PART, part), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, boolean isMoving) {
        if (!isMoving) {
            Direction facing = state.getValue(FACING);
            BlockPos anchor = getAnchorPos(pos, facing, state.getValue(PART));
            for (Part part : Part.values()) {
                BlockPos partPos = getPartPos(anchor, facing, part);
                if (partPos.equals(pos)) {
                    continue;
                }
                BlockState partState = level.getBlockState(partPos);
                if (partState.is(this)
                        && partState.getValue(FACING) == facing
                        && partState.getValue(PART) == part) {
                    level.setBlock(partPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Override
    public float getShadeBrightness(@NonNull BlockState pState, @NonNull BlockGetter pLevel, @NonNull BlockPos pPos) {
        return 1.0F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, WATERLOGGED);
    }

    private static BlockPos getPartPos(BlockPos anchor, Direction facing, Part part) {
        Direction left = facing.getCounterClockWise();
        return switch (part) {
            case RIGHT_BOTTOM -> anchor;
            case LEFT_BOTTOM -> anchor.relative(left);
            case RIGHT_TOP -> anchor.relative(facing);
            case LEFT_TOP -> anchor.relative(left).relative(facing);
        };
    }

    private static BlockPos getAnchorPos(BlockPos pos, Direction facing, Part part) {
        Direction right = facing.getClockWise();
        Direction bottom = facing.getOpposite();
        return switch (part) {
            case RIGHT_BOTTOM -> pos;
            case LEFT_BOTTOM -> pos.relative(right);
            case RIGHT_TOP -> pos.relative(bottom);
            case LEFT_TOP -> pos.relative(right).relative(bottom);
        };
    }

    public enum Part implements StringRepresentable {
        RIGHT_BOTTOM("right_bottom"),
        LEFT_BOTTOM("left_bottom"),
        RIGHT_TOP("right_top"),
        LEFT_TOP("left_top");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
