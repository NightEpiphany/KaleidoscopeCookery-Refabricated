package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class CandiedSkewerBlock extends HorizontalDirectionalBlock {
    private static final VoxelShape UPRIGHT_SHAPE_LITTLE = Block.box(0, 0, 0, 16, 20, 16);
    private static final VoxelShape UPRIGHT_SHAPE_HUGE = Block.box(-4, 0, -4, 20, 20, 20);
    private static final VoxelShape SIDE_SHAPE = Block.box(0, 0, 0, 16, 24, 16);

    private final int maxCount;
    private final Supplier<Item> item;
    private final IntegerProperty countProperty;
    private final boolean upright;

    public CandiedSkewerBlock(int maxCount, Supplier<Item> item, int lightLevel, boolean upright) {
        super(BlockBehaviour.Properties.of()
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(state -> lightLevel)
                .noCollission());

        this.maxCount = maxCount;
        this.item = item;
        this.countProperty = IntegerProperty.create("count", 1, maxCount);
        this.upright = upright;

        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        builder.add(FACING, countProperty);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(countProperty, 1));
    }

    public IntegerProperty getCountProperty() {
        return countProperty;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public Supplier<Item> getItem() {
        return item;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player,
                                          @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        Item skewer = item.get();
        ItemStack held = player.getItemInHand(hand);
        int count = state.getValue(countProperty);

        if (held.is(skewer)) {
            if (count >= maxCount) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, state.setValue(countProperty, count + 1));
                level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            ItemStack drop = new ItemStack(skewer);
            if (held.isEmpty()) {
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            } else {
                Block.popResourceFromFace(level, pos, Direction.UP, drop);
            }

            if (count <= 1) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else {
                level.setBlockAndUpdate(pos, state.setValue(countProperty, count - 1));
            }
            level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @NonNull List<ItemStack> getDrops(BlockState state, LootParams.@NonNull Builder params) {
        return List.of(new ItemStack(item.get(), state.getValue(countProperty)));
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (!upright) {
            return SIDE_SHAPE;
        }
        return state.getValue(countProperty) > 4 ? UPRIGHT_SHAPE_HUGE : UPRIGHT_SHAPE_LITTLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
