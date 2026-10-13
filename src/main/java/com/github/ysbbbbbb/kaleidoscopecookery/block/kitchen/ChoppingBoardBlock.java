package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IChoppingBoard;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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

public class ChoppingBoardBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape NORTH_SOUTH = Block.box(1, 0, 2, 15, 2, 14);
    public static final VoxelShape EAST_WEST = Block.box(2, 0, 1, 14, 2, 15);
    private static final double DURABILITY_COST_PROBABILITY = 0.25;

    public ChoppingBoardBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(WATERLOGGED, false));
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder params) {
        var drops = super.getDrops(state, params);
        BlockEntity parameter = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof ChoppingBoardBlockEntity choppingBoard) {
            // 如果切制次数还是 0，那么上面的东西正常掉落
            if (choppingBoard.getCurrentCutCount() == 0 && !choppingBoard.getCurrentCutStack().isEmpty()) {
                drops.add(choppingBoard.getCurrentCutStack().copyWithCount(1));
            }
        }
        return drops;
    }

    @Override
    public @NotNull BlockState updateShape(
            @NonNull BlockState state,
            @NonNull LevelReader levelReader,
            @NonNull ScheduledTickAccess scheduledTickAccess,
            @NonNull BlockPos blockPos,
            @NonNull Direction direction,
            @NonNull BlockPos blockPos2,
            @NonNull BlockState blockState2,
            @NonNull RandomSource randomSource
    ) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return super.updateShape(state, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof IChoppingBoard choppingBoard) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if (choppingBoard.onPutItem(level, player, itemInHand)) {
                return InteractionResult.SUCCESS;
            }
            ItemStack mainHandItem = player.getMainHandItem();
            if (hand == InteractionHand.OFF_HAND) {
                return InteractionResult.PASS;
            }
            if (choppingBoard.onCutItem(level, player, itemInHand)) {
                // 切菜成功时，有 25% 的概率消耗耐久度
                if (level.getRandom().nextDouble() < DURABILITY_COST_PROBABILITY) {
                    itemInHand.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
                ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_CHOPPING_BOARD);
                return InteractionResult.SUCCESS;
            }
            if (player.isSecondaryUseActive() && choppingBoard.onTakeOut(level, player)) {
                return InteractionResult.SUCCESS;
            }
            if (mainHandItem.is(TagMod.KITCHEN_KNIFE) && player.getOffhandItem().isEmpty()) {
                choppingBoard.playParticlesSound();
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new ChoppingBoardBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource() : super.getFluidState(state);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NonNull BlockGetter blockGetter, @NonNull BlockPos pos, @NonNull CollisionContext collisionContext) {
        if (state.getValue(FACING).getAxis() == Direction.Axis.Z) {
            return NORTH_SOUTH;
        }
        return EAST_WEST;
    }
}
