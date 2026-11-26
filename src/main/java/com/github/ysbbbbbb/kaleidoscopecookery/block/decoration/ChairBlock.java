package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.ChairBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.util.BlockDrop;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.util.CarpetColor.getCarpetByColor;
import static com.github.ysbbbbbb.kaleidoscopecookery.util.CarpetColor.getColorByCarpet;

public class ChairBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<ChairBlock> CODEC = simpleCodec(p -> new ChairBlock(SoundType.WOOD));
    public static final BooleanProperty HAS_CARPET = BooleanProperty.create("has_carpet");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final int SEAT_HEIGHT = 9;
    private static final double CAT_REACH = 1.25;
    private static final VoxelShape BASE = Block.box(2, 0, 2, 14, SEAT_HEIGHT, 14);
    private static final VoxelShape NORTH = Shapes.or(BASE, Block.box(2, 0, 12, 14, 19, 14));
    private static final VoxelShape SOUTH = Shapes.or(BASE, Block.box(2, 0, 2, 14, 19, 4));
    private static final VoxelShape WEST = Shapes.or(BASE, Block.box(12, 0, 2, 14, 19, 14));
    private static final VoxelShape EAST = Shapes.or(BASE, Block.box(2, 0, 2, 4, 19, 14));

    public ChairBlock(SoundType soundType) {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(soundType)
                .noOcclusion()
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(HAS_CARPET, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND) {
            if (itemInHand.is(ItemTags.WOOL_CARPETS))
                return useWithCarpets(state, level, pos, player, itemInHand);
            else if (itemInHand.is(Items.SHEARS) && state.getValue(HAS_CARPET) && level.getBlockEntity(pos) instanceof ChairBlockEntity chairBlockEntity) {
                level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, false));
                DyeColor originalColor = chairBlockEntity.getColor();
                ItemStack carpetItem = getCarpetByColor(originalColor).getDefaultInstance();
                BlockDrop.popResource(level, pos, 0.25, carpetItem);
                level.playSound(null, pos, SoundEvents.SNOW_GOLEM_SHEAR, player.getSoundSource(), 1.0F, 1.0F);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return tryToSitOn(state, level, pos, player);
    }

    @NotNull
    private ItemInteractionResult tryToSitOn(BlockState state, Level level, BlockPos pos, Player player) {
        if (!isSeatOccupied(level, pos)) {
            SitEntity entitySit = new SitEntity(level, pos, 0.5125);
            entitySit.setYRot(state.getValue(FACING).toYRot());
            level.addFreshEntity(entitySit);
            player.startRiding(entitySit, true);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static boolean isSeatOccupied(Level level, BlockPos pos) {
        return !level.getEntitiesOfClass(SitEntity.class, new AABB(pos)).isEmpty();
    }

    public static boolean tryToLieOn(BlockState state, BlockPos pos, Cat cat) {
        Level level = cat.level();
        if (level.isClientSide || cat.isPassenger() || cat.isLeashed() || cat.isOrderedToSit()) {
            return false;
        }

        Direction facing = state.getValue(FACING);
        Vec3 restPosition = Vec3.atBottomCenterOf(pos).add(facing.getStepX() / 16.0, SEAT_HEIGHT / 16.0, facing.getStepZ() / 16.0);
        if (cat.distanceToSqr(restPosition) > CAT_REACH * CAT_REACH || isSeatOccupied(level, pos)
                || !level.noCollision(cat, cat.getBoundingBox().move(restPosition.subtract(cat.position())))) {
            return false;
        }

        SitEntity seat = new SitEntity(SitEntity.TYPE, level);
        seat.setSitType(SitEntity.CAT_CHAIR);
        seat.setPos(restPosition);
        seat.setYRot(facing.toYRot());
        if (!level.addFreshEntity(seat)) {
            return false;
        }
        if (!cat.startRiding(seat)) {
            seat.discard();
            return false;
        }

        cat.getNavigation().stop();
        cat.setYRot(seat.getYRot());
        cat.setYHeadRot(seat.getYRot());
        cat.setYBodyRot(seat.getYRot());
        seat.positionRider(cat);
        return true;
    }

    @NotNull
    private ItemInteractionResult useWithCarpets(BlockState state, Level level, BlockPos pos, Player player, ItemStack itemInHand) {
        @Nullable DyeColor dyeColor = getColorByCarpet(itemInHand.getItem());
        boolean hasCarpet = state.getValue(HAS_CARPET);
        if (dyeColor == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // 第一种情况，椅子上没有地毯
        if (!hasCarpet) {
            level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true));
            if (level.getBlockEntity(pos) instanceof ChairBlockEntity chairBlockEntity) {
                level.playSound(null, pos, SoundType.WOOL.getPlaceSound(), player.getSoundSource(), 1.0F, 1.0F);
                chairBlockEntity.setColor(dyeColor);
                chairBlockEntity.refresh();
                if (!player.isCreative())
                    itemInHand.shrink(1);
                return ItemInteractionResult.SUCCESS;
            }
        }

        // 第二种情况：有地毯，但是颜色不一致
        if (hasCarpet && level.getBlockEntity(pos) instanceof ChairBlockEntity chairBlockEntity && chairBlockEntity.getColor() != dyeColor) {
            // 掉落原地毯
            DyeColor originalColor = chairBlockEntity.getColor();
            ItemStack carpetItem = getCarpetByColor(originalColor).getDefaultInstance();
            BlockDrop.popResource(level, pos, 0.25, carpetItem);
            level.playSound(null, pos, SoundType.WOOL.getPlaceSound(), player.getSoundSource(), 1.0F, 1.0F);

            chairBlockEntity.setColor(dyeColor);
            chairBlockEntity.refresh();
            level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true));
            if (!player.isCreative())
                itemInHand.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            level.getEntitiesOfClass(SitEntity.class, new AABB(pos)).forEach(Entity::discard);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
        BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (state.getValue(HAS_CARPET) && parameter instanceof ChairBlockEntity chairBlockEntity) {
            Item carpet = getCarpetByColor(chairBlockEntity.getColor());
            drops.add(new ItemStack(carpet));
        }
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_CARPET, WATERLOGGED);
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
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HAS_CARPET)) {
            return new ChairBlockEntity(pos, state);
        }
        return null;
    }
}
