package com.github.ysbbbbbb.kaleidoscopecookery.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.Hooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.sounds.BlockSoundSets;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;


public class TeaTreeBlock extends BushBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    public static final int MAX_AGE = 5;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(3, 0, 3, 13, 4, 13),
            Block.box(3, 0, 3, 13, 5, 13),
            Block.box(3, 0, 3, 13, 6, 13),
            Block.box(3, 0, 3, 13, 7, 13),
            Block.box(3, 0, 3, 13, 8, 13),
            Block.box(3, 0, 3, 13, 9, 13)
    };

    public TeaTreeBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.PLANT)
                .noCollision()
                .randomTicks()
                .instabreak()
                .sound(BlockSoundSets.CROP)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.POPPED));
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(WATERLOGGED, false));
    }

    public int getAge(BlockState state) {
        return state.getValue(AGE);
    }

    public BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(AGE, age);
    }

    public boolean isMaxAge(BlockState state) {
        return this.getAge(state) >= MAX_AGE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(BlockTags.GRASS_BLOCKS);
    }

    @Override
    public boolean canSurvive(@NonNull BlockState state, @NonNull LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return this.mayPlaceOn(level.getBlockState(below), level, below);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, @NotNull LevelReader levelReader, @NotNull ScheduledTickAccess scheduledTickAccess,
                                           @NotNull BlockPos blockPos, @NotNull Direction direction, @NotNull BlockPos blockPos2,
                                           @NotNull BlockState blockState2, @NotNull RandomSource randomSource) {
        if (blockState.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, blockPos2, blockState2, randomSource);
    }

    @Override
    protected @NotNull ItemStack getCloneItemStack(@NonNull LevelReader level, @NonNull BlockPos pos, @NonNull BlockState state, boolean includeData) {
        return new ItemStack(ModItems.TEA_SEED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public boolean isRandomlyTicking(@NonNull BlockState state) {
        return !this.isMaxAge(state);
    }

    @Override
    public void randomTick(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        int age = this.getAge(state);
        int growthChance = this.isPlantedInRow(level, pos) ? 4 : 5;
        if (age < MAX_AGE
            && level.getRawBrightness(pos.above(), 0) >= 9
            && Hooks.onCropsGrowPre(level, pos, state, random.nextInt(growthChance) == 0)
        ) {
            BlockState grownState = state.setValue(AGE, age + 1);
            level.setBlock(pos, grownState, Block.UPDATE_CLIENTS);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grownState));
        }
    }

    private boolean isPlantedInRow(LevelReader level, BlockPos pos) {
        boolean hasXNeighbor = level.getBlockState(pos.west()).is(this) || level.getBlockState(pos.east()).is(this);
        boolean hasZNeighbor = level.getBlockState(pos.north()).is(this) || level.getBlockState(pos.south()).is(this);
        return hasXNeighbor != hasZNeighbor;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, WATERLOGGED);
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                                Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        // 骨粉催熟未成熟茶树由方块接管并消费交互，否则紧接着到达的空手副手包会立刻触发下面的采摘分支
        if (BonemealInteraction.growCrop(stack, state, level, pos, player, this)) {
            return InteractionResult.SUCCESS;
        }
        if (player.getItemInHand(hand).is(ModItems.SICKLE)) {
            return InteractionResult.PASS;
        }
        if (!this.isMaxAge(state)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        Block.popResource(level, pos, ModItems.FRESH_TEA_LEAVES.getDefaultInstance());
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        BlockState harvestedState = state.setValue(AGE, 3);
        level.setBlock(pos, harvestedState, Block.UPDATE_CLIENTS);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, harvestedState));
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public @NotNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }

    @Override
    public boolean isValidBonemealTarget(@NonNull LevelReader levelReader, @NonNull BlockPos blockPos, @NonNull BlockState state, @NonNull BonemealSource source) {
        return !this.isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(@NonNull Level level, @NonNull RandomSource random, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull BonemealSource source) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NonNull RandomSource random, @NonNull BlockPos pos, BlockState state, @NonNull BonemealSource source) {
        level.setBlock(pos, state.setValue(AGE, Math.min(MAX_AGE, this.getAge(state) + 1)), Block.UPDATE_CLIENTS);
    }

}
