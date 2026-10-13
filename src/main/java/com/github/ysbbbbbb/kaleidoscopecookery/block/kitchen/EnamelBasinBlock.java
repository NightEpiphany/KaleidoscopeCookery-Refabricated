package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class EnamelBasinBlock extends Block implements SimpleWaterloggedBlock {
    public static final int MAX_OIL_COUNT = 32;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");
    public static final IntegerProperty OIL_COUNT = IntegerProperty.create("oil_count", 0, MAX_OIL_COUNT);

    private static final VoxelShape AABB_NO_LID = Block.box(3, 0, 3, 13, 5, 13);
    private static final VoxelShape AABB = Shapes.or(AABB_NO_LID,
            Block.box(2.5, 5, 2.5, 13.5, 6, 13.5),
            Block.box(7, 6, 7, 9, 7, 9));

    public EnamelBasinBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(HAS_LID, true)
                .setValue(OIL_COUNT, 0));
    }

    @Override
    protected @NonNull BlockState updateShape(@NonNull BlockState state, @NonNull LevelReader levelReader, @NonNull ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos blockPos, @NonNull Direction direction, @NonNull BlockPos neighborPos, @NonNull BlockState neighborState, @NonNull RandomSource randomSource) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return super.updateShape(state, levelReader, scheduledTickAccess, blockPos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        ItemStack mainHandItem = player.getMainHandItem();
        // 先判断棍子敲
        if (mainHandItem.is(Items.STICK)) {
            float pitch = 0.6F + (float) Math.random() * 0.2F;
            SoundEvents.LANTERN.breakSound().ifPresent(sound -> level.playSound(player, pos, sound, SoundSource.BLOCKS, 2, pitch));
            return InteractionResult.SUCCESS;
        }
        // 再判断开盖
        boolean hasLid = state.getValue(HAS_LID);
        if (hasLid) {
            SoundEvents.LANTERN.breakSound().ifPresent(sound -> level.playSound(player, pos, sound, SoundSource.BLOCKS, 0.8f, 0.8f));
            level.setBlockAndUpdate(pos, state.setValue(HAS_LID, false));
            return InteractionResult.SUCCESS;
        }
        // 没有盖子，并且是空手，那么盖上盖子
        if (mainHandItem.isEmpty()) {
            SoundEvents.LANTERN.breakSound().ifPresent(sound -> level.playSound(player, pos, sound, SoundSource.BLOCKS, 0.8f, 0.4f));
            level.setBlockAndUpdate(pos, state.setValue(HAS_LID, true));
            return InteractionResult.SUCCESS;
        }
        // 手持油脂时，消耗油脂添加进去
        if (mainHandItem.is(ModItems.OIL)) {
            int value = state.getValue(OIL_COUNT);
            // 如果油已经满了，不能再放油
            if (value >= MAX_OIL_COUNT) {
                return InteractionResult.FAIL;
            }
            // 尝试直接放满
            int needCount = MAX_OIL_COUNT - value;
            int consumeCount = Math.min(needCount, mainHandItem.getCount());
            SoundEvents.HONEY_BLOCK.breakSound().ifPresent(sound -> level.playSound(player, pos, sound, SoundSource.BLOCKS, 0.8f, 0.8f));
            if (!player.isCreative())
                mainHandItem.shrink(consumeCount);
            level.setBlockAndUpdate(pos, state.setValue(OIL_COUNT, value + consumeCount));
            return InteractionResult.SUCCESS;
        }
        // 当用铲子右击时
        if (mainHandItem.is(ModItems.KITCHEN_SHOVEL)) {
            return onShovelClick(state, level, pos, player, mainHandItem);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @NotNull
    private InteractionResult onShovelClick(BlockState state, Level level, BlockPos pos, Player player, ItemStack mainHandItem) {
        int value = state.getValue(OIL_COUNT);
        boolean shovelHasOil = KitchenShovelItem.hasOil(mainHandItem);

        // 如果铲子有油，能还回去
        if (shovelHasOil) {
            // 如果油已经满了，不能再放油
            if (value >= MAX_OIL_COUNT) {
                return InteractionResult.FAIL;
            }
            SoundEvents.HONEY_BLOCK.breakSound().ifPresent(sound -> level.playSound(player, pos, sound, SoundSource.BLOCKS, 0.8f, 0.8f));
            KitchenShovelItem.setHasOil(mainHandItem, false);
            level.setBlockAndUpdate(pos, state.setValue(OIL_COUNT, value + 1));
            return InteractionResult.SUCCESS;
        }

        // 没有油时，返回
        if (value == 0) {
            return InteractionResult.FAIL;
        }

        // 取油
        SoundEvents.HONEY_BLOCK.breakSound().ifPresent(sound -> level.playSound(player, pos, sound, SoundSource.BLOCKS, 0.8f, 1.2f));
        KitchenShovelItem.setHasOil(mainHandItem, true);
        level.setBlockAndUpdate(pos, state.setValue(OIL_COUNT, value - 1));
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource() : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, HAS_LID, OIL_COUNT);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NonNull BlockGetter blockGetter, @NonNull BlockPos pos, @NonNull CollisionContext collisionContext) {
        return state.getValue(HAS_LID) ? AABB : AABB_NO_LID;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NonNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Direction direction) {
        int count = state.getValue(OIL_COUNT);
        int baseValue = count > 0 ? 1 : 0;
        double ratio = (double) count / MAX_OIL_COUNT;
        return Mth.floor(ratio * 14.0) + baseValue;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState pState, LootParams.@NonNull Builder params) {
        List<ItemStack> stacks = super.getDrops(pState, params);
        BlockState state = params.getOptionalParameter(LootContextParams.BLOCK_STATE);
        if (state == null || !state.is(this)) {
            return stacks;
        }
        int oilCount = state.getValue(OIL_COUNT);
        if (oilCount > 0) {
            stacks.add(new ItemStack(ModItems.OIL, oilCount));
        }
        return stacks;
    }
}
