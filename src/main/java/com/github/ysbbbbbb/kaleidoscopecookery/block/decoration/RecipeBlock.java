package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class RecipeBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape CEILING_AABB_X = Block.box(1.5D, 15.75D, 3.0D, 14.5D, 16.0D, 13.0D);
    private static final VoxelShape CEILING_AABB_Z = Block.box(3.0D, 15.75D, 1.5D, 13.0D, 16.0D, 14.5D);
    private static final VoxelShape FLOOR_AABB_X = Block.box(1.5D, 0.0D, 3.0D, 14.5D, 0.25D, 13.0D);
    private static final VoxelShape FLOOR_AABB_Z = Block.box(3.0D, 0.0D, 1.5D, 13.0D, 0.25D, 14.5D);

    private static final VoxelShape NORTH_AABB = Block.box(3.0D, 1.5D, 15.75D, 13.0D, 14.5D, 16.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(3.0D, 1.5D, 0.0D, 13.0D, 14.5D, 0.25D);
    private static final VoxelShape WEST_AABB = Block.box(15.75D, 1.5D, 3.0D, 16.0D, 14.5D, 13.0D);
    private static final VoxelShape EAST_AABB = Block.box(0.0D, 1.5D, 3.0D, 0.25D, 14.5D, 13.0D);

    public RecipeBlock(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.WALL)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        // 空手右击取下来
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (!mainHandItem.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RecipeBlockEntity recipeBlockEntity) {
            ItemStack itemStack = recipeBlockEntity.getItems().getStackInSlot(0);
            if (itemStack.isEmpty()) {
                return InteractionResult.PASS;
            }
            player.setItemInHand(hand, itemStack.copy());
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            level.playSound(null, pos, ModSounds.BLOCK_RECIPE_BLOCK, player.getSoundSource(), 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @NotNull BlockState updateShape(
            @NonNull BlockState state,
            @NonNull LevelReader levelReader,
            @NonNull ScheduledTickAccess scheduledTickAccess,
            @NonNull BlockPos blockPos,
            @NonNull Direction direction,
            @NonNull BlockPos neighborPos,
            @NonNull BlockState neighborState,
            @NonNull RandomSource randomSource
    ) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }

        return super.updateShape(state, levelReader, scheduledTickAccess, blockPos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NonNull BlockGetter pLevel, @NonNull BlockPos pPos, @NonNull CollisionContext pContext) {
        Direction facing = state.getValue(FACING);
        return switch (state.getValue(FACE)) {
            case FLOOR -> facing.getAxis() == Direction.Axis.X ? FLOOR_AABB_X : FLOOR_AABB_Z;
            case WALL -> switch (facing) {
                case EAST -> EAST_AABB;
                case WEST -> WEST_AABB;
                case SOUTH -> SOUTH_AABB;
                case NORTH, UP, DOWN -> NORTH_AABB;
            };
            default -> facing.getAxis() == Direction.Axis.X ? CEILING_AABB_X : CEILING_AABB_Z;
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement != null) {
            if (stateForPlacement.getValue(FACE) == AttachFace.FLOOR)
                stateForPlacement = stateForPlacement.setValue(FACING,
                        context.getHorizontalDirection().getOpposite());
            return stateForPlacement.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level pLevel, @NonNull BlockPos pPos, @NonNull BlockState pState, @Nullable LivingEntity livingEntity, @NonNull ItemStack stack) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof RecipeBlockEntity recipeBlockEntity) {
                recipeBlockEntity.getItems().setStackInSlot(0, stack.copyWithCount(1));
            }
        }
    }



    @Override
    protected @NonNull ItemStack getCloneItemStack(LevelReader levelReader, @NonNull BlockPos blockPos, @NonNull BlockState blockState, boolean bl) {
        BlockEntity blockEntity = levelReader.getBlockEntity(blockPos);
        if (blockEntity instanceof RecipeBlockEntity recipeBlockEntity) {
            ItemStack itemStack = recipeBlockEntity.getItems().getStackInSlot(0);
            if (!itemStack.isEmpty()) {
                return itemStack.copy();
            }
        }
        return super.getCloneItemStack(levelReader, blockPos, blockState, bl);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, FACE, WATERLOGGED);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder lootParamsBuilder) {
        List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
        BlockEntity parameter = lootParamsBuilder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof RecipeBlockEntity recipeBlock) {
            drops.add(recipeBlock.getItems().getStackInSlot(0).copyWithCount(1));
        }
        return drops;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos pPos, @NonNull BlockState pState) {
        return new RecipeBlockEntity(pPos, pState);
    }
}
