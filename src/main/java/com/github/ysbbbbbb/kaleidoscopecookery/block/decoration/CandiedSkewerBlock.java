package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({"deprecation", "unused"})
public class CandiedSkewerBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    private static final VoxelShape UPRIGHT_SHAPE_LITTLE = Block.box(0, 0, 0, 16, 20, 16);
    private static final VoxelShape UPRIGHT_SHAPE_HUGE = Block.box(-4, 0, -4, 20, 20, 20);
    private static final VoxelShape SIDE_SHAPE = Block.box(0, 0, 0, 16, 24, 16);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final int maxCount;
    private final Supplier<Item> item;
    private final IntegerProperty countProperty;
    private final boolean upright;

    public CandiedSkewerBlock(int maxCount, Supplier<Item> item, int lightLevel, boolean upright) {
        super(BlockBehaviour.Properties.of()
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(state -> lightLevel)
                .noCollission());

        this.maxCount = maxCount;
        this.item = item;
        this.countProperty = IntegerProperty.create("count", 1, maxCount);
        this.upright = upright;

        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        builder.add(FACING, countProperty, WATERLOGGED);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(countProperty, 1));
    }

    @Override
    public boolean canSurvive(@NonNull BlockState state, @NonNull LevelReader level, BlockPos pos) {
        Direction supportDirection = getSupportDirection(state);
        BlockPos supportPos = pos.relative(supportDirection);
        Direction supportFace = supportDirection.getOpposite();
        return this.upright ? canSupportCenter(level, supportPos, supportFace)
                : level.getBlockState(supportPos).isFaceSturdy(level, supportPos, supportFace);
    }

    private Direction getSupportDirection(BlockState state) {
        return this.upright ? Direction.DOWN : state.getValue(FACING).getOpposite();
    }

    public IntegerProperty getCountProperty() {
        return countProperty;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public Supplier<Item> getItem() {
        return item;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NonNull BlockState updateShape(@NonNull BlockState blockState, @NonNull Direction direction, @NonNull BlockState blockState2, @NonNull LevelAccessor levelAccessor, @NonNull BlockPos blockPos, @NonNull BlockPos blockPos2) {
        if (direction == getSupportDirection(blockState) && !blockState.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player,
                                          @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        Item skewer = item.get();
        ItemStack held = player.getItemInHand(hand);
        int count = state.getValue(countProperty);

        if (held.is(skewer)) {
            if (count >= maxCount) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, state.setValue(countProperty, count + 1));
                level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            ItemStack drop = new ItemStack(skewer);
            if (held.isEmpty()) {
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            } else {
                Block.popResourceFromFace(level, pos, Direction.UP, drop);
            }

            if (count <= 1) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else {
                level.setBlockAndUpdate(pos, state.setValue(countProperty, count - 1));
            }
            level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @NonNull List<ItemStack> getDrops(BlockState state, LootParams.@NonNull Builder params) {
        return List.of(new ItemStack(item.get(), state.getValue(countProperty)));
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (!upright) {
            return SIDE_SHAPE;
        }
        return state.getValue(countProperty) > 4 ? UPRIGHT_SHAPE_HUGE : UPRIGHT_SHAPE_LITTLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
}
