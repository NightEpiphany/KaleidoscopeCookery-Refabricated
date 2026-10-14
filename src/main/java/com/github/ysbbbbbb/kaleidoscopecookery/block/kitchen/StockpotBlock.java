package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

@SuppressWarnings({"unchecked"})
public class StockpotBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");
    public static final BooleanProperty HAS_BASE = BooleanProperty.create("has_base");
    public static final BooleanProperty HAS_CHAINS = BooleanProperty.create("has_chains");

    private static final VoxelShape AABB = Shapes.or(
            Block.box(2, 0, 2, 14, 5, 14),
            Block.box(1, 5, 1, 15, 7, 15));
    private static final VoxelShape AABB_WITH_LID = Shapes.or(
            Block.box(2, 0, 2, 14, 9, 14),
            Block.box(1, 5, 1, 15, 7, 15));

    public StockpotBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false)
                .setValue(HAS_LID, false)
                .setValue(HAS_BASE, false)
                .setValue(HAS_CHAINS, false));
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        if (placer instanceof Player player && level.getBlockEntity(pos) instanceof IStockpot stockpot && stockpot.hasHeatSource(level)) {
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_STOCKPOT_ON_HEAT_SOURCE);
        }
    }

    @Override
    public @NotNull BlockState updateShape(
            @NonNull BlockState state,
            @NonNull LevelReader levelAccessor,
            @NonNull ScheduledTickAccess scheduledTickAccess,
            @NonNull BlockPos pos,
            @NonNull Direction direction,
            @NonNull BlockPos neighborPos,
            @NonNull BlockState neighborState,
            @NonNull RandomSource randomSource
    ) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        // 上方无法支撑，取消锁链
        // 下方无法支撑，添加基座
        if (direction == Direction.DOWN && !state.getValue(HAS_CHAINS)) {
            return state.setValue(HAS_CHAINS, canSupportCenter(levelAccessor, pos.above(), Direction.DOWN))
                    .setValue(HAS_BASE, !neighborState.isFaceSturdy(levelAccessor, neighborPos, Direction.UP));
        }
        if (direction == Direction.UP && !state.getValue(HAS_BASE)) {
            BlockState belowState = levelAccessor.getBlockState(pos.below());
            return state.setValue(HAS_CHAINS, canSupportCenter(levelAccessor, neighborPos, Direction.DOWN))
                    .setValue(HAS_BASE, !belowState.isFaceSturdy(levelAccessor, pos.below(), Direction.UP));
        }
        return super.updateShape(state, levelAccessor, scheduledTickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        InteractionResult recipeResult = RecipeItem.tryUseOnCookware(stack, level, player, hand, hitResult);
        if (recipeResult != InteractionResult.PASS) {
            return recipeResult;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IStockpot stockpot)) {
            return InteractionResult.PASS;
        }
        // 先检查盖子
        ItemStack mainHandItem = player.getMainHandItem();
        if (stockpot.onLidClick(level, player, mainHandItem)) {
            return InteractionResult.SUCCESS;
        }
        // 加入汤底
        if (stockpot.addSoupBase(level, player, mainHandItem)) {
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.PUT_SOUP_BASE_IN_STOCKPOT);
            return InteractionResult.SUCCESS;
        }
        // 取出汤底
        if (stockpot.removeSoupBase(level, player, mainHandItem)) {
            return InteractionResult.SUCCESS;
        }
        // 加入原料
        if (!mainHandItem.isEmpty() && stockpot.addIngredient(level, player, mainHandItem)) {
            return InteractionResult.SUCCESS;
        }
        // 取出原料
        if ((mainHandItem.isEmpty() || mainHandItem.is(TagMod.INGREDIENT_CONTAINER)) && stockpot.removeIngredient(level, player)) {
            return InteractionResult.SUCCESS;
        }
        // 取出成品
        if (stockpot.takeOutProduct(level, player, mainHandItem)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new StockpotBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return createTickerHelper(blockEntityType, ModBlocks.STOCKPOT_BE,
                    (_, _, _, pot) -> pot.clientTick());
        }
        return createTickerHelper(blockEntityType, ModBlocks.STOCKPOT_BE,
                (lvl, _, _, pot) -> pot.tick(lvl));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        FluidState fluidState = level.getFluidState(context.getClickedPos());
        Direction clickFace = context.getClickedFace();
        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

        // 如果点击的是上方，那么依据是否是可支持方块添加锁链
        BlockPos abovePos = context.getClickedPos().above();
        if (clickFace == Direction.DOWN && canSupportCenter(level, abovePos, Direction.DOWN)) {
            return blockState.setValue(HAS_CHAINS, true);
        }

        // 如果下方是不完整方块，则添加基座
        BlockPos belowPos = context.getClickedPos().below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!belowState.isFaceSturdy(level, belowPos, Direction.UP)) {
            return blockState.setValue(HAS_BASE, true);
        }
        return blockState;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, HAS_LID, HAS_BASE, HAS_CHAINS);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NonNull BlockGetter blockGetter, @NonNull BlockPos pos, @NonNull CollisionContext collisionContext) {
        if (state.getValue(HAS_LID)) {
            return AABB_WITH_LID;
        }
        return AABB;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder lootParamsBuilder) {
        List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
        if (state.getValue(HAS_LID)) {
            BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (parameter instanceof StockpotBlockEntity stockpot && !stockpot.getLidItem().isEmpty()) {
                drops.add(stockpot.getLidItem().copy());
            } else {
                drops.add(new ItemStack(ModItems.STOCKPOT_LID));
            }
        }
        BlockEntity parameter = lootParamsBuilder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof StockpotBlockEntity stockpotBlock && stockpotBlock.getStatus() == StockpotBlockEntity.PUT_INGREDIENT) {
            stockpotBlock.getInputs().forEach(stack -> {
                if (!stack.isEmpty()) {
                    drops.add(stack);
                }
            });
        }
        return drops;
    }
}
