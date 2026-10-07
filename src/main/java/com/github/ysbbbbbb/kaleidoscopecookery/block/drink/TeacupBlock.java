package com.github.ysbbbbbb.kaleidoscopecookery.block.drink;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeapotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.google.common.collect.Lists;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TeacupBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final VoxelShape AABB = Block.box(1, 0, 1, 15, 2, 15);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final IntegerProperty cupCount;
    protected final IntegerProperty teaCount;
    protected final int maxCount;
    protected final @Nullable FoodBiteAnimateTicks.AnimateTick animateTick;

    protected VoxelShape aabb = AABB;

    public TeacupBlock(Properties properties, int maxCount, @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
        super(properties
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(BlockSoundSets.WOOD)
                .pushReaction(PushReaction.POPPED)
                .noOcclusion());

        this.maxCount = maxCount;
        this.animateTick = animateTick;
        this.cupCount = IntegerProperty.create("cup_count", 1, maxCount);
        this.teaCount = IntegerProperty.create("tea_count", 1, maxCount);

        // 重置一遍 BlockState，因为在父类 FoodBlock 中已经创建了一个默认的 BlockStateDefinition
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createCountBlockStateDefinition(builder);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(cupCount, 1)
                .setValue(teaCount, 1)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.SOUTH));
    }

    public TeacupBlock(Properties properties) {
        this(properties, 4, null);
    }

    public TeacupBlock setAABB(VoxelShape aabb) {
        this.aabb = aabb;
        return this;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public IntegerProperty getCupCountProperty() {
        return cupCount;
    }

    public IntegerProperty getTeaCountProperty() {
        return teaCount;
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
            if (pourOut.isEmpty() || pourOut.getItem() != this.asItem()) {
                return InteractionResult.CONSUME;
            }
            // 如果茶杯茶没有满
            int count = state.getValue(teaCount);
            if (count < state.getValue(cupCount)) {
                level.setBlockAndUpdate(pos, state.setValue(teaCount, count + 1));
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                TeapotItem.pourOut(itemInHand, level);
                spawnPourParticles(level, pos);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // 如果是空杯
        if (itemInHand.is(ModItems.EMPTY_CUP)) {
            // 如果茶杯数量没满
            int count = state.getValue(cupCount);
            if (count < this.maxCount) {
                level.setBlockAndUpdate(pos, state.setValue(cupCount, count + 1));
                level.playSound(player, pos, SoundEvents.DECORATED_POT_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative())
                    itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // 如果是对应的物品类型
        if (itemInHand.getItem() instanceof TeacupItem teacupItem) {
            if (teacupItem.getBlock() != this) {
                return InteractionResult.CONSUME;
            }
            // 如果茶杯数量没满
            int cupCountNum = state.getValue(cupCount);
            int teaCountNum = state.getValue(teaCount);
            if (cupCountNum < this.maxCount) {
                level.setBlockAndUpdate(pos, state
                        .setValue(cupCount, cupCountNum + 1)
                        .setValue(teaCount, teaCountNum + 1));
                level.playSound(player, pos, SoundEvents.DECORATED_POT_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative())
                    itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // 如果是空手，优先取下茶水，再取下空杯
        if (itemInHand.isEmpty()) {
            int cupCountNum = state.getValue(cupCount);
            int teaCountNum = state.getValue(teaCount);
            int emptyCountNum = cupCountNum - teaCountNum;

            // 取下茶水
            if (teaCountNum > 0) {
                ItemStack teaStack = new ItemStack(this);
                ItemUtils.getItemToLivingEntity(player, teaStack);
                if (cupCountNum == 1) {
                    level.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
                } else if (teaCountNum == 1) {
                    level.setBlockAndUpdate(pos, ModBlocks.EMPTY_CUP.defaultBlockState()
                            .setValue(EmptyCupBlock.CUP_COUNT, cupCountNum - 1)
                            .setValue(FACING, state.getValue(FACING))
                            .setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
                } else {
                    level.setBlockAndUpdate(pos, state
                            .setValue(teaCount, teaCountNum - 1)
                            .setValue(cupCount, cupCountNum - 1));
                }
                level.playSound(player, pos, SoundEvents.DECORATED_POT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }

            // 取下空杯
            if (emptyCountNum > 0) {
                ItemStack cupStack = new ItemStack(ModItems.EMPTY_CUP);
                ItemUtils.getItemToLivingEntity(player, cupStack);
                if (cupCountNum == 1) {
                    level.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
                } else {
                    level.setBlockAndUpdate(pos, state.setValue(cupCount, cupCountNum - 1));
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
    public void animateTick(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (this.animateTick != null) {
            this.animateTick.animateTick(state, level, pos, random);
            return;
        }
        if (random.nextInt(20) != 0) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        level.addParticle(ModParticles.COOKING,
                x + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                y + random.nextDouble() / 3,
                z + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                0.3, 0.1, 0.3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    protected void createCountBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(cupCount, teaCount, FACING, WATERLOGGED);
    }

    @Override
    public @NonNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState pState, @NonNull BlockGetter pLevel, @NonNull BlockPos pPos, @NonNull CollisionContext pContext) {
        return this.aabb;
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
    public @NonNull List<ItemStack> getDrops(BlockState state, LootParams.@NonNull Builder params) {
        List<ItemStack> drops = Lists.newArrayList();
        int teaCountNum = state.getValue(teaCount);
        int cupCountNum = state.getValue(cupCount);
        int emptyCountNum = cupCountNum - teaCountNum;
        if (emptyCountNum > 0) {
            drops.add(new ItemStack(ModItems.EMPTY_CUP, emptyCountNum));
        }
        if (teaCountNum > 0) {
            drops.add(new ItemStack(this, teaCountNum));
        }
        return drops;
    }
}
