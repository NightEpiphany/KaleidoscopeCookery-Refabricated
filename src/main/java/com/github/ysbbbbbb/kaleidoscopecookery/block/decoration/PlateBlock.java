package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Supplier;

public class PlateBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final VoxelShape AABB = Block.box(1, 0, 1, 15, 2, 15);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final IntegerProperty servings;
    protected final List<Supplier<Item>> items;
    protected final int maxCount;
    protected VoxelShape aabb = AABB;

    public PlateBlock(int maxCount, List<Supplier<Item>> items, Properties properties) {
        super(properties
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(BlockSoundSets.WOOD)
                .pushReaction(PushReaction.POPPED)
                .noOcclusion());

        this.servings = IntegerProperty.create("servings", 0, maxCount);
        this.maxCount = maxCount;
        this.items = items;

        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createServingBlockStateDefinition(builder);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false)
                .setValue(servings, maxCount));
    }

    public PlateBlock setAABB(VoxelShape aabb) {
        this.aabb = aabb;
        return this;
    }

    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack itemInHand, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                                @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        int count = state.getValue(servings);

        // 尝试放回物品
        if (!itemInHand.isEmpty()) {
            if (count < getMaxCount() && canRefill(itemInHand)) {
                if (!player.isCreative())
                    itemInHand.shrink(1);
                level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlockAndUpdate(pos, state.cycle(servings));
                return InteractionResult.SUCCESS;
            }
        }

        // 尝试取出物品
        if (count > 0) {
            List<ItemStack> stacks = items.stream()
                    .map(s -> s.get().getDefaultInstance()).toList();
            if (itemInHand.isEmpty()) {
                stacks.forEach(s -> ItemUtils.giveItemToPlayer(player, s));
            } else {
                stacks.forEach(s -> Block.popResourceFromFace(level, pos, Direction.UP, s));
            }
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, state.setValue(servings, count - 1));
        } else {
            level.destroyBlock(pos, true, player);
        }

        return InteractionResult.SUCCESS;
    }

    private boolean canRefill(ItemStack itemStack) {
        if (items.size() == 1) {
            return itemStack.is(items.getFirst().get());
        }
        return false;
    }

    protected void createServingBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, servings, WATERLOGGED);
    }

    @Override
    public @NonNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull VoxelShape getShape(@NonNull BlockState pState, @NonNull BlockGetter pLevel, @NonNull BlockPos pPos, @NonNull CollisionContext pContext) {
        return this.aabb;
    }

    @Override
    protected boolean isPathfindable(@NonNull BlockState state, @NonNull PathComputationType pathComputationType) {
        return false;
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
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder params) {
        List<ItemStack> drops = Lists.newArrayList(super.getDrops(state, params));
        int count = state.getValue(servings);
        if (count > 0) {
            List<ItemStack> stacks = items.stream()
                    .map(s -> s.get().getDefaultInstance().copyWithCount(count))
                    .toList();
            drops.addAll(stacks);
        }
        return drops;
    }
}
