package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CandiedSkewerBlock extends StackableFoodBlock {
    private static final MapCodec<CandiedSkewerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(2, 64).fieldOf("max_count").forGetter(block -> block.maxCount),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(block -> block.item.get()),
            Codec.intRange(0, 15).fieldOf("light_level").forGetter(block -> block.lightLevel),
            Codec.BOOL.fieldOf("upright").forGetter(block -> block.upright)
    ).apply(instance, (maxCount, item, lightLevel, upright) -> new CandiedSkewerBlock(maxCount, () -> item, lightLevel, upright)));

    private static final VoxelShape UPRIGHT = Block.box(0, 0, 0, 16, 20, 16);
    private static final VoxelShape WIDE_UPRIGHT = Block.box(-4, 0, -4, 20, 20, 20);
    private static final VoxelShape SIDE = Block.box(0, 0, 0, 16, 24, 16);

    private final int lightLevel;
    private final boolean upright;

    public CandiedSkewerBlock(int maxCount, Supplier<Item> item, int lightLevel, boolean upright) {
        super(Properties.of()
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(state -> lightLevel)
                .noCollission(), maxCount, item);
        this.lightLevel = lightLevel;
        this.upright = upright;
    }

    @Override
    protected @NotNull MapCodec<CandiedSkewerBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Direction face = context.getClickedFace();
        return !this.upright && face.getAxis().isHorizontal() ? state.setValue(FACING, face) : state;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                           LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                                    Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        Item skewer = this.item.get();
        int count = state.getValue(this.countProperty);
        boolean stacking = held.is(skewer);
        if (stacking && count >= this.maxCount) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            SoundType sound = state.getSoundType();
            if (stacking) {
                level.setBlockAndUpdate(pos, state.setValue(this.countProperty, count + 1));
                level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                held.consume(1, player);
            } else {
                ItemStack removed = skewer.getDefaultInstance();
                if (held.isEmpty()) {
                    if (!player.getInventory().add(removed)) {
                        player.drop(removed, false);
                    }
                } else {
                    Block.popResourceFromFace(level, pos, Direction.UP, removed);
                }
                if (count > 1) {
                    level.setBlockAndUpdate(pos, state.setValue(this.countProperty, count - 1));
                } else {
                    level.removeBlock(pos, false);
                }
                level.playSound(null, pos, sound.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!this.upright) {
            return SIDE;
        }
        return state.getValue(this.countProperty) > 4 ? WIDE_UPRIGHT : UPRIGHT;
    }
}
