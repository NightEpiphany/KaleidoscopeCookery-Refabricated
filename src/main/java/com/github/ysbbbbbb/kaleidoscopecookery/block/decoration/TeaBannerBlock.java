package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.sounds.BlockSoundSets;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TeaBannerBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    private static final VoxelShape FLOOR_SHAPE = box(4, 0, 4, 12, 16, 12);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape NORTH_SHAPE = box(0, 7, 4, 16, 15, 12);
    private static final VoxelShape SOUTH_SHAPE = box(0, 7, 4, 16, 15, 12);
    private static final VoxelShape WEST_SHAPE = box(4, 7, 0, 12, 15, 16);
    private static final VoxelShape EAST_SHAPE = box(4, 7, 0, 12, 15, 16);

    public TeaBannerBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.COLOR_RED)
                .strength(1.0F)
                .noCollision()
                .noOcclusion()
                .sound(BlockSoundSets.WOOD));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(FACE, AttachFace.FLOOR));
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, Level level, @NonNull BlockPos pos, Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        ItemStack heldStack = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TeaBannerBlockEntity teaBanner)) {
            return InteractionResult.PASS;
        }

        if (heldStack.getItem() instanceof DyeItem) {
            DyeColor dyeColor = heldStack.get(DataComponents.DYE);
            if (teaBanner.getColor() == dyeColor) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                teaBanner.setColor(dyeColor);
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        if (TeaBannerBlockEntity.isSupportedPattern(heldStack)) {
            if (ItemStack.isSameItemSameComponents(heldStack, teaBanner.getPatternItem())) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                ItemStack previousPattern = teaBanner.setPatternItem(heldStack.copyWithCount(1));
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                if (!previousPattern.isEmpty()) {
                    popResource(level, pos, previousPattern);
                }
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        if (heldStack.is(Items.SHEARS) && teaBanner.hasPattern()) {
            if (!level.isClientSide()) {
                popResource(level, pos, teaBanner.removePatternItem());
                heldStack.hurtAndBreak(1, player, hand);
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
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
    public @Nullable BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        if (state == null || state.getValue(FACE) == AttachFace.CEILING) {
            return null;
        }
        return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (state.getValue(FACE) == AttachFace.FLOOR) {
            return FLOOR_SHAPE;
        }
        Direction shapeFacing = state.getValue(FACING).getClockWise();
        return switch (shapeFacing) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull ItemStack getCloneItemStack(LevelReader level, @NonNull BlockPos pos, @NonNull BlockState state, boolean includeData) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TeaBannerBlockEntity teaBanner) {
            return teaBanner.createItemStack(level);
        }
        return super.getCloneItemStack(level, pos, state, includeData);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder lootParamsBuilder) {
        List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
        BlockEntity blockEntity = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof TeaBannerBlockEntity teaBanner) {
            drops.add(teaBanner.createItemStack(teaBanner.getLevel()));
        }
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, FACE, WATERLOGGED);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new TeaBannerBlockEntity(pos, state);
    }
}
