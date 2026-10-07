package com.github.ysbbbbbb.kaleidoscopecookery.block.drink;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeapotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
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
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.sounds.BlockSoundSets;
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
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class EmptyCupBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final VoxelShape AABB = Block.box(1, 0, 1, 15, 2, 15);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int MAX_COUNT = 4;
    public static final IntegerProperty CUP_COUNT = IntegerProperty.create("cup_count", 1, MAX_COUNT);

    public EmptyCupBlock(Properties properties) {
        super(properties
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.CLAY)
                .sound(BlockSoundSets.DECORATED_POT)
                .pushReaction(PushReaction.POPPED)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CUP_COUNT, 1)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.SOUTH));
    }

    @Override
    public @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack itemInHand = player.getItemInHand(hand);
        player.swing(hand, SwingAnimation.DEFAULT, false);
        // 如果是茶壶
        if (itemInHand.is(ModItems.TEAPOT)) {
            ItemStack pourOut = TeapotItem.getPourOut(itemInHand, level);
            if (pourOut.isEmpty()) {
                return InteractionResult.CONSUME;
            }
            // 必须是茶
            if (!(pourOut.getItem() instanceof TeacupItem teacupItem && teacupItem.getBlock() instanceof TeacupBlock teacupBlock)) {
                return InteractionResult.CONSUME;
            }
            // 检查当前茶杯数量，是否超过转换的茶的上限，否则需要退回
            int currentCount = state.getValue(CUP_COUNT);
            if (currentCount > teacupBlock.getMaxCount()) {
                ItemStack returnStack = new ItemStack(ModItems.EMPTY_CUP, currentCount - teacupBlock.getMaxCount());
                ItemUtils.getItemToLivingEntity(player, returnStack);
            }
            // 将茶转换
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
            TeapotItem.pourOut(itemInHand, level);
            spawnPourParticles(level, pos);
            level.setBlockAndUpdate(pos, teacupBlock.defaultBlockState()
                    .setValue(teacupBlock.getCupCountProperty(), Math.min(currentCount, teacupBlock.getMaxCount()))
                    .setValue(teacupBlock.getTeaCountProperty(), 1)
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED))
                    .setValue(FACING, state.getValue(FACING)));
            return InteractionResult.SUCCESS;
        }

        // 如果是茶杯，将空杯转换为对应的茶杯
        if (itemInHand.getItem() instanceof TeacupItem teacupItem
                && teacupItem.getBlock() instanceof TeacupBlock teacupBlock) {
            int currentCount = state.getValue(CUP_COUNT);
            if (currentCount < teacupBlock.getMaxCount()) {
                level.setBlockAndUpdate(pos, teacupBlock.defaultBlockState()
                        .setValue(teacupBlock.getCupCountProperty(), currentCount + 1)
                        .setValue(teacupBlock.getTeaCountProperty(), 1)
                        .setValue(WATERLOGGED, state.getValue(WATERLOGGED))
                        .setValue(FACING, state.getValue(FACING)));
                level.playSound(player, pos, SoundEvents.DECORATED_POT_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative())
                    itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // 如果是空杯
        if (itemInHand.is(ModItems.EMPTY_CUP)) {
            // 如果茶杯数量没满
            int count = state.getValue(CUP_COUNT);
            if (count < MAX_COUNT) {
                level.setBlockAndUpdate(pos, state.setValue(CUP_COUNT, count + 1));
                level.playSound(player, pos, SoundEvents.DECORATED_POT_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative())
                    itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // 如果是空手，先取下茶杯，空杯
        if (itemInHand.isEmpty()) {
            int cupCountNum = state.getValue(CUP_COUNT);
            if (cupCountNum > 0) {
                // 取下空杯
                ItemStack cupStack = new ItemStack(ModItems.EMPTY_CUP);
                ItemUtils.getItemToLivingEntity(player, cupStack);
                if (cupCountNum == 1) {
                    level.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
                } else {
                    level.setBlockAndUpdate(pos, state.setValue(CUP_COUNT, cupCountNum - 1));
                }
                level.playSound(player, pos, SoundEvents.DECORATED_POT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private static void spawnPourParticles(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        RandomSource random = level.getRandom();
        serverLevel.sendParticles(ModParticles.COOKING,
                pos.getX() + 0.5,
                pos.getY() + 0.35,
                pos.getZ() + 0.5,
                4,
                0.12 + random.nextDouble() * 0.04,
                0.08,
                0.12 + random.nextDouble() * 0.04,
                0.02);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CUP_COUNT, FACING, WATERLOGGED);
    }

    @Override
    public @NonNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return AABB;
    }

    @Override
    public @NonNull List<ItemStack> getDrops(BlockState state, LootParams.@NonNull Builder params) {
        int cupCount = state.getValue(CUP_COUNT);
        if (cupCount > 0) {
            return List.of(new ItemStack(ModItems.EMPTY_CUP, cupCount));
        }
        return super.getDrops(state, params);
    }
}
