package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ConfigGetter;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import static com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem.hasOil;
import static com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem.setHasOil;

public class StoveBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<StoveBlock> CODEC = simpleCodec(StoveBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public StoveBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(LIT, false));
    }

    @Override
    protected @NonNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F, false);
            }

            level.addParticle(ParticleTypes.SMOKE,
                    x + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    y + 0.5 + random.nextDouble() / 3,
                    z + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    0, 0.02, 0);

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double offsetRandom = random.nextDouble() * 0.6 - 0.3;
            double xOffset = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : offsetRandom;
            double yOffset = 0.25 + random.nextDouble() * 6.0 / 16.0;
            double zOffset = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : offsetRandom;
            level.addParticle(ParticleTypes.FLAME,
                    x + xOffset,
                    pos.getY() + yOffset,
                    z + zOffset,
                    0, 0, 0);
        }
    }

    @Override
    public void randomTick(BlockState blockState, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (blockState.getValue(LIT) && level.isRainingAt(pos.above())) {
            level.setBlockAndUpdate(pos, blockState.setValue(LIT, false));
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (ConfigGetter.getStoveFiringEnabled()
                && state.getValue(LIT)
                && level instanceof ServerLevel serverLevel
                && entity instanceof LivingEntity livingEntity
                && !livingEntity.isSteppingCarefully()
                && !livingEntity.isInvulnerable()
                && livingEntity.invulnerableTime <= 10) {
            // 排除创造模式玩家
            if (livingEntity instanceof Player player && player.isCreative()) {
                return;
            }
            if (!livingEntity.isSteppingCarefully()) {
                livingEntity.hurt(livingEntity.damageSources().hotFloor(), 1.0F);
                serverLevel.broadcastDamageEvent(livingEntity, livingEntity.damageSources().hotFloor());
            }
        }
        super.stepOn(level, pos, state, entity);
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
        if (state.getValue(LIT) && levelAccessor.isWaterAt(pos.above()) && levelAccessor instanceof ServerLevel serverLevel) {
            serverLevel.setBlockAndUpdate(pos, state.setValue(LIT, false));
            serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return super.updateShape(state, levelAccessor, scheduledTickAccess, pos, direction, neighborPos, neighborState, randomSource);
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NonNull ItemStack stack, BlockState state, @NonNull Level level, @NonNull BlockPos pos, Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        // 点燃炉灶
        if (!state.getValue(LIT) && itemInHand.is(TagMod.LIT_STOVE)) {
            level.setBlockAndUpdate(pos, state.setValue(LIT, true));
            if (itemInHand.is(Items.FIRE_CHARGE)) {
                level.playSound(player, pos,
                        SoundEvents.FIRECHARGE_USE,
                        SoundSource.BLOCKS, 1.0F,
                        level.getRandom().nextFloat() * 0.4F + 0.8F);
                itemInHand.consume(1, player);
            } else {
                level.playSound(player, pos,
                        SoundEvents.FLINTANDSTEEL_USE,
                        SoundSource.BLOCKS, 1.0F,
                        level.getRandom().nextFloat() * 0.4F + 0.8F);
                itemInHand.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.LIT_THE_STOVE);
            return InteractionResult.SUCCESS;
        }
        // 熄灭
        if (state.getValue(LIT) && itemInHand.is(TagMod.EXTINGUISH_STOVE)) {
            if (itemInHand.is(ModItems.KITCHEN_SHOVEL) && hasOil(itemInHand)) {
                setHasOil(itemInHand, false);
            }
            level.setBlockAndUpdate(pos, state.setValue(LIT, false));
            level.playSound(player, pos,
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS, 0.5F,
                    2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
            itemInHand.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onProjectileHit(Level level, @NonNull BlockState state, BlockHitResult hitResult, @NonNull Projectile projectile) {
        BlockPos hitBlockPos = hitResult.getBlockPos();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && projectile.isOnFire() && projectile.mayInteract(serverLevel, hitBlockPos) && !state.getValue(LIT)) {
            level.setBlock(hitBlockPos, state.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL_IMMEDIATE);
            if (projectile.getOwner() instanceof Player player) {
                ModTrigger.EVENT.trigger(player, ModEventTriggerType.LIT_THE_STOVE);
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }
}
