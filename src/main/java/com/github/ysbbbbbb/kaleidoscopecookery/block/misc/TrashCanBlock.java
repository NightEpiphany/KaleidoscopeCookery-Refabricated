package com.github.ysbbbbbb.kaleidoscopecookery.block.misc;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TrashCanBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final VoxelShape SUCK_ZONE = Block.box(0, 15, 0, 16, 16, 16);
    public static final VoxelShape AABB = Shapes.or(
            Block.box(2, 0, 2, 14, 15, 14),
            Block.box(1, 12, 1, 15, 15, 15)
    );
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public TrashCanBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(WATERLOGGED, false));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return createTickerHelper(blockEntityType, ModBlocks.TRASH_CAN_BE,
                    (lvl, blockPos, blockState, trashCan) -> trashCan.clientTick(lvl));
        }
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new TrashCanBlockEntity(pos, state);
    }

    @Override
    protected @NonNull BlockState updateShape(@NonNull BlockState state, @NonNull LevelReader levelReader, @NonNull ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos blockPos, @NonNull Direction direction, @NonNull BlockPos blockPos2, @NonNull BlockState blockState2, @NonNull RandomSource randomSource) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return super.updateShape(state, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                                @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack itemInHand = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof TrashCanBlockEntity trashCan) {
            if (itemInHand.isEmpty() && player.isSecondaryUseActive()) {
                trashCan.withdrawItem(player);
                return InteractionResult.SUCCESS;
            }
            if (!itemInHand.isEmpty()) {
                trashCan.putItem(itemInHand);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, @NonNull BlockPos pos, @NonNull BlockState state) {
        levelAccessor.getEntitiesOfClass(SitEntity.class, new AABB(pos)).forEach(Entity::discard);
    }

    @Override
    public void fallOn(@NonNull Level level, @NonNull BlockState state, @NonNull BlockPos pos, @NonNull Entity entity, double fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        if (entity instanceof Player player && player.getVehicle() == null && fallDistance > 1f) {
            List<SitEntity> entities = level.getEntitiesOfClass(SitEntity.class, new AABB(pos));
            if (!entities.isEmpty()) {
                return;
            }
            if (!level.isClientSide()) {
                SitEntity entitySit = new SitEntity(level, pos, 0.875, SitEntity.TRASH_CAN);
                entitySit.setYRot(state.getValue(FACING).toYRot());
                level.addFreshEntity(entitySit);
                player.startRiding(entitySit);
            }
            level.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(32)).forEach(e -> {
                if (e.getTarget() == player) {
                    e.setTarget(null);
                }
            });
            if (level.getBlockEntity(pos) instanceof TrashCanBlockEntity trashCan) {
                trashCan.enterState.start((int) level.getGameTime());
            }
        }
    }

    @Override
    protected void entityInside(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Entity entity, @NonNull InsideBlockEffectApplier insideBlockEffectApplier, boolean bl) {
        if (blockState.getValue(POWERED) && level.getBlockEntity(blockPos) instanceof TrashCanBlockEntity trashCan) {
            trashCan.entityInside(level, blockPos, entity);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, @NonNull BlockPos pos, @NonNull Block neighborBlock, @Nullable Orientation orientation, boolean isMoving) {
        boolean isPowered = level.hasNeighborSignal(pos);
        if (isPowered != state.getValue(POWERED)) {
            level.setBlockAndUpdate(pos, state.setValue(POWERED, isPowered));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, level.getFluidState(context.getClickedPos()).is(Fluids.WATER));
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public @NotNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter blockGetter, @NonNull BlockPos pos, @NonNull CollisionContext collisionContext) {
        return AABB;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.@NonNull Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        BlockEntity parameter = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof TrashCanBlockEntity trashCanBlock) {
            for (int i = 0; i < trashCanBlock.getStorage().getSlots(); i++) {
                ItemStack stack = trashCanBlock.getStorage().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    drops.add(stack);
                }
            }
        }
        return drops;
    }
}
