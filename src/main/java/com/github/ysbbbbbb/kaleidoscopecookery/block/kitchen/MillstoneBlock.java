package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IMillstone;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class MillstoneBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<MillstoneBlock> CODEC = simpleCodec(MillstoneBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<NinePart> PART = EnumProperty.create("part", NinePart.class);

    private static final VoxelShape CENTER = Block.box(-2, 0, -2, 18, 15, 18);

    private static final VoxelShape LEFT_UP = Shapes.or(
            Block.box(11, 0, 11, 16, 6, 16),
            Block.box(8, 6, 8, 16, 14, 16));

    private static final VoxelShape UP = Shapes.or(
            Block.box(0, 0, 11, 16, 6, 16),
            Block.box(0, 6, 8, 16, 14, 16));

    private static final VoxelShape RIGHT_UP = Shapes.or(
            Block.box(0, 0, 11, 5, 6, 16),
            Block.box(0, 6, 8, 8, 14, 16));

    private static final VoxelShape LEFT_CENTER = Shapes.or(
            Block.box(11, 0, 0, 16, 6, 16),
            Block.box(8, 6, 0, 16, 14, 16));

    private static final VoxelShape RIGHT_CENTER = Shapes.or(
            Block.box(0, 0, 0, 5, 6, 16),
            Block.box(0, 6, 0, 8, 14, 16));

    private static final VoxelShape LEFT_DOWN = Shapes.or(
            Block.box(11, 0, 0, 16, 6, 5),
            Block.box(8, 6, 0, 16, 14, 8));

    private static final VoxelShape DOWN = Shapes.or(
            Block.box(0, 0, 0, 16, 6, 5),
            Block.box(0, 6, 0, 16, 14, 8));

    private static final VoxelShape RIGHT_DOWN = Shapes.or(
            Block.box(0, 0, 0, 5, 6, 5),
            Block.box(0, 6, 0, 8, 14, 8));

    public MillstoneBlock(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, NinePart.CENTER)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, @NonNull Direction directionToNeighbour, @NonNull BlockPos neighbourPos, @NonNull BlockState neighbourState, @NonNull RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    private static void handleRemove(Level world, BlockPos pos, BlockState state, @Nullable Player player) {
        if (world.isClientSide()) {
            return;
        }
        BlockPos centerPos = findCenterPos(world, pos, state);
        if (centerPos == null) {
            return;
        }
        BlockEntity te = world.getBlockEntity(centerPos);
        if (!(te instanceof MillstoneBlockEntity millstone)) {
            return;
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos offsetPos = centerPos.offset(i, 0, j);
                if (world.getBlockState(offsetPos).is(ModBlocks.MILLSTONE)) {
                    world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
                }
            }
        }
        if (player != null && !player.isCreative()) {
            Block.popResource(world, pos, ModItems.MILLSTONE.getDefaultInstance());
        }
        for (int i = 0; i < millstone.getOutputs().getSlots(); i++) {
            ItemStack output = millstone.getOutputs().getStackInSlot(i);
            if (!output.isEmpty()) {
                Block.popResource(world, pos, output);
            }
        }
        if (!millstone.getInput().isEmpty()) {
            Block.popResource(world, pos, millstone.getInput());
        }
    }

    @Nullable
    private static BlockPos findCenterPos(Level world, BlockPos pos, BlockState state) {
        if (state.is(ModBlocks.MILLSTONE)) {
            NinePart part = state.getValue(PART);
            BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
            if (world.getBlockEntity(centerPos) instanceof MillstoneBlockEntity) {
                return centerPos;
            }
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = pos.offset(i, 0, j);
                BlockState searchState = world.getBlockState(searchPos);
                if (!searchState.is(ModBlocks.MILLSTONE)) {
                    continue;
                }
                NinePart part = searchState.getValue(PART);
                BlockPos centerPos = searchPos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
                if (world.getBlockEntity(centerPos) instanceof MillstoneBlockEntity) {
                    return centerPos;
                }
            }
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("all")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(blockEntityType, ModBlocks.MILLSTONE_BE,
                (levelIn, pos, stateIn, millstone) -> millstone.tick(levelIn));
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        NinePart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        BlockEntity te = level.getBlockEntity(centerPos);
        if (!(te instanceof IMillstone millstone)) {
            return InteractionResult.PASS;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (millstone.onPutItem(level, mainHandItem)) {
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void stepOn(@NonNull Level pLevel, @NonNull BlockPos pPos, @NonNull BlockState pState, @NonNull Entity pEntity) {
        // 每 5 tick 检查一次
        if (pEntity instanceof LivingEntity entity && !pLevel.isClientSide() && pLevel.getGameTime() % 5 == 4) {
            NinePart part = pState.getValue(PART);
            BlockPos centerPos = pPos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
            BlockEntity blockEntity = pLevel.getBlockEntity(centerPos);
            if (blockEntity instanceof MillstoneBlockEntity millstone && !millstone.hasEntity() && millstone.canBindEntity(entity)) {
                millstone.bindEntity(entity);
                if (entity instanceof OwnableEntity ownable && ownable.getOwner() instanceof ServerPlayer player) {
                    ModTrigger.EVENT.trigger(player, ModEventTriggerType.DRIVE_THE_MILLSTONE);
                }
            }
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NonNull Level world, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull Player player) {
        handleRemove(world, pos, state, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void wasExploded(@NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull Explosion explosion) {
        handleRemove(level, pos, level.getBlockState(pos), null);
        super.wasExploded(level, pos, explosion);
    }

    @Override
    public @NonNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos centerPos = context.getClickedPos();
        boolean underWater = true;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = centerPos.offset(i, 0, j);
                FluidState fluidState = context.getLevel().getFluidState(searchPos);
                if (fluidState.getType() != Fluids.WATER && underWater)
                    underWater = false;
                if (!context.getLevel().getBlockState(searchPos).canBeReplaced(context)) {
                    return null;
                }
            }
        }
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, underWater);
    }

    @Override
    public void setPlacedBy(@NonNull Level worldIn, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide()) {
            return;
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = pos.offset(i, 0, j);
                NinePart part = NinePart.getPartByPos(i, j);
                if (part != null && !part.isCenter()) {
                    worldIn.setBlock(searchPos, state.setValue(PART, part), Block.UPDATE_ALL);
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PART, FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, BlockState state) {
        if (state.getValue(PART).isCenter()) {
            return new MillstoneBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NonNull BlockGetter pLevel, @NonNull BlockPos pPos, @NonNull CollisionContext pContext) {
        NinePart value = pState.getValue(PART);
        return switch (value) {
            case LEFT_UP -> LEFT_UP;
            case UP -> UP;
            case RIGHT_UP -> RIGHT_UP;
            case LEFT_CENTER -> LEFT_CENTER;
            case CENTER -> CENTER;
            case RIGHT_CENTER -> RIGHT_CENTER;
            case LEFT_DOWN -> LEFT_DOWN;
            case DOWN -> DOWN;
            case RIGHT_DOWN -> RIGHT_DOWN;
        };
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
