package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class KitchenShelfBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<KitchenShelfBlock> CODEC = simpleCodec(KitchenShelfBlock::new);
    public static final BooleanProperty SNEAK_PLACED = BooleanProperty.create("sneak_placed");
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** 模拟原版物品发送延迟刻 */
    private static final int DISPENSE_DELAY = 4;

    public KitchenShelfBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(SNEAK_PLACED, false)
                .setValue(WATERLOGGED, false)
                .setValue(PART, Part.SINGLE));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(SNEAK_PLACED, context.getPlayer() != null && context.getPlayer().isShiftKeyDown());
        return updatePart(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, SNEAK_PLACED, PART, WATERLOGGED);
    }

    @Override
    protected @NonNull BlockState updateShape(@NonNull BlockState state, @NonNull LevelReader levelReader,
                                              @NonNull ScheduledTickAccess scheduledTickAccess,
                                              @NonNull BlockPos blockPos, @NonNull Direction direction,
                                              @NonNull BlockPos neighborPos, @NonNull BlockState neighborState,
                                              @NonNull RandomSource randomSource) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return updatePart(state, levelReader, blockPos);
    }

    private BlockState updatePart(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(SNEAK_PLACED)) {
            return state.setValue(PART, Part.SINGLE);
        }

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean left = isConnectable(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);
        boolean right = isConnectable(level.getBlockState(pos.relative(facing.getClockWise())), facing);

        Part part;
        if (left && right) {
            part = Part.MIDDLE;
        } else if (left) {
            part = Part.RIGHT;
        } else if (right) {
            part = Part.LEFT;
        } else {
            part = Part.SINGLE;
        }
        return state.setValue(PART, part);
    }

    private boolean isConnectable(BlockState state, Direction facing) {
        return state.is(this) && !state.getValue(SNEAK_PLACED)
                && state.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing;
    }

    public static List<BlockPos> getConnectedShelves(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof KitchenShelfBlock shelf) || state.getValue(SNEAK_PLACED)) {
            return List.of(pos);
        }

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();
        BlockPos first = pos;
        while (shelf.isConnectable(level.getBlockState(first.relative(left)), facing)) {
            first = first.relative(left);
        }

        List<BlockPos> shelves = new ArrayList<>();
        for (BlockPos current = first; shelf.isConnectable(level.getBlockState(current), facing);
             current = current.relative(right)) {
            shelves.add(current);
        }
        return shelves;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void onPlace(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(this)) {
            updateRedstone(level, pos);
        }
    }

    @Override
    protected void neighborChanged(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Block neighborBlock,
                                   @Nullable Orientation orientation, boolean isMoving) {
        updateRedstone(level, pos);
    }

    private void updateRedstone(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel
                && serverLevel.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf
                && shelf.updatePower(serverLevel.hasNeighborSignal(pos))) {
            serverLevel.scheduleTick(pos, this, DISPENSE_DELAY);
        }
    }

    @Override
    protected void tick(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        List<BlockPos> shelves = getConnectedShelves(level, pos);
        BlockPos leader = shelves.stream()
                .filter(level::hasNeighborSignal)
                .min(BlockPos::compareTo)
                .orElse(null);
        if (!pos.equals(leader)) {
            return;
        }
        for (BlockPos shelfPos : shelves) {
            if (level.getBlockEntity(shelfPos) instanceof KitchenShelfBlockEntity shelf) {
                shelf.dispense();
            }
        }
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state,
                                                @NonNull Level level, @NonNull BlockPos pos,
                                                @NonNull Player player, @NonNull InteractionHand hand,
                                                @NonNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || stack.is(ModItems.KITCHEN_SHELF)
                || (stack.getItem() instanceof RecipeItem && RecipeItem.hasRecipe(stack))) {
            return InteractionResult.PASS;
        }
        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return interact(level, pos, player) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return interact(level, pos, player) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    private boolean interact(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf
                && shelf.interact(player, player.isShiftKeyDown())) {
            level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new KitchenShelfBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state,
                                                                            @NonNull BlockEntityType<T> type) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        return createTickerHelper(type, ModBlocks.KITCHEN_SHELF_BE,
                (_, pos, blockState, shelf) ->
                        KitchenShelfBlockEntity.serverTick(serverLevel, pos, blockState, shelf));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof KitchenShelfBlockEntity shelf) {
            ItemStack remaining = shelf.getStoredStack().copy();
            if (!remaining.isEmpty()) {
                drops = new ArrayList<>(drops);
                while (!remaining.isEmpty()) {
                    drops.add(remaining.split(remaining.getMaxStackSize()));
                }
            }
        }
        return drops;
    }
}
