package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FruitBasketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class FruitBasketBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape NORTH_SOUTH = Shapes.join(
            Block.box(1, 0, 2, 15, 8, 14),
            Block.box(2, 1, 3, 14, 8, 13),
            BooleanOp.ONLY_FIRST);
    private static final VoxelShape EAST_WEST = Shapes.join(
            Block.box(2, 0, 1, 14, 8, 15),
            Block.box(3, 1, 2, 13, 8, 14),
            BooleanOp.ONLY_FIRST);

    public FruitBasketBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState blockState, @NonNull LevelReader levelReader, @NonNull ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos blockPos, @NonNull Direction direction, @NonNull BlockPos blockPos2, @NonNull BlockState blockState2, @NonNull RandomSource randomSource) {
        if (blockState.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }


    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (level.getBlockEntity(pos) instanceof FruitBasketBlockEntity fruitBasket) {
            if (player.isSecondaryUseActive()) {
                fruitBasket.takeOut(player);
                return InteractionResult.SUCCESS;
            }
            ItemStack mainHandItem = player.getMainHandItem();
            if (!mainHandItem.isEmpty()&& !mainHandItem.is(ModItems.TRANSMUTATION_LUNCH_BAG)) {
                fruitBasket.putOn(player.getMainHandItem(), player.isCreative());
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.has(ModDataComponents.FRUIT_BASKET_ITEMS) && level.getBlockEntity(pos) instanceof FruitBasketBlockEntity basket) {
            FruitBasketItem.ItemContainer handler = stack.get(ModDataComponents.FRUIT_BASKET_ITEMS);
            if (handler != null) {
                basket.setItems(handler.items());
            }
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            dropResources(state, level, pos, level.getBlockEntity(pos), player, player.getMainHandItem());
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder lootParamsBuilder) {
        List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
        BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof FruitBasketBlockEntity fruitBasket) {
            drops.stream().filter(stack -> stack.is(ModItems.FRUIT_BASKET)).findFirst()
                    .ifPresent(stack -> stack.set(ModDataComponents.FRUIT_BASKET_ITEMS, new FruitBasketItem.ItemContainer(fruitBasket.getItems())));
        }
        return drops;
    }


    @Override
    protected @NonNull ItemStack getCloneItemStack(@NonNull LevelReader levelReader, @NonNull BlockPos blockPos, @NonNull BlockState blockState, boolean bl) {
        ItemStack cloneItemStack = super.getCloneItemStack(levelReader, blockPos, blockState, bl);
        levelReader.getBlockEntity(blockPos, ModBlocks.FRUIT_BASKET_BE)
                .ifPresent(e -> cloneItemStack.set(ModDataComponents.FRUIT_BASKET_ITEMS, new FruitBasketItem.ItemContainer(e.getItems())));
        return cloneItemStack;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new FruitBasketBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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