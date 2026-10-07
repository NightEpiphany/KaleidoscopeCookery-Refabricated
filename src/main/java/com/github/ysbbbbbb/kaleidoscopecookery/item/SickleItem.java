package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.TeaTreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEnchantments;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;



public class SickleItem extends WithTooltipsItem {

    public static final ToolMaterial SICKLE = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 535, 3.5F, 1.0F, 15, ItemTags.WOODEN_TOOL_MATERIALS);

    public SickleItem(Properties p) {
        super(p.stacksTo(1).sword(SICKLE, 3.0F, -2.4F), "sickle");
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        // 生成挥动音效和粒子
        Player player = context.getPlayer();
        if (player == null) {
            return super.useOn(context);
        }
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        int sweepLevel = Math.clamp(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ModEnchantments.SWEEP)
                .map(enchantment -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack)).orElse(0), 0, 3);
        int radius = 2 + sweepLevel;
        // 默认 5x5x2，每级清扫扩大半径，最大 11x11x2。
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    harvest(pos, x, y, z, serverLevel, player, stack);
                }
            }
        }

        serverLevel.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(),
                1.0F, 1.0F);
        double d = -Mth.sin(player.getYRot() * (float) (Math.PI / 180.0));
        double e = Mth.cos(player.getYRot() * (float) (Math.PI / 180.0));
        if (player.level() instanceof ServerLevel) {
            ((ServerLevel)player.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d, player.getY(0.5), player.getZ() + e, 0, d, 0.0, e, 0.0);
        }
        stack.hurtAndBreak(1 << sweepLevel, player, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        player.getCooldowns().addCooldown(stack, 10);
        return InteractionResult.SUCCESS;
    }

    private boolean harvest(BlockPos pos, int x, int y, int z, ServerLevel level, Player player, ItemStack stack) {
        BlockPos newPos = pos.offset(x, y, z);
        if (!level.mayInteract(player, newPos)) {
            return false;
        }
        BlockState blockState = level.getBlockState(newPos);
        if (blockState.isAir()) {
            return false;
        }
        // 黑名单
        if (blockState.is(TagMod.SICKLE_HARVEST_BLACKLIST)) {
            return false;
        }

        Block block = blockState.getBlock();
        // 触发事件
        SickleHarvestEvent event = new SickleHarvestEvent(player, stack, newPos, blockState);
        ModEvents.SICKLE_HARVEST.invoker().onSickleHarvest(event);
        if (event.isCanceled()) {
            return event.isCostDurability();
        }

        // 如果是茶树，那么只收割成熟的，未成熟的保持原样（茶树属于植被方块，不特判会被直接破坏）
        if (block instanceof TeaTreeBlock teaTreeBlock) {
            if (teaTreeBlock.isMaxAge(blockState) && player instanceof ServerPlayer serverPlayer) {
                teaTreeBlock.playerDestroy(level, serverPlayer, newPos, blockState, null, ItemStack.EMPTY);
                level.setBlock(newPos, teaTreeBlock.getStateForAge(0), Block.UPDATE_ALL);
                level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, newPos, Block.getId(blockState));
                return true;
            }
            return false;
        }

        // 如果是作物，那么检查是否成熟
        if (block instanceof CropBlock cropBlock) {
            // 水稻特判
            if (block instanceof RiceCropBlock) {
                int position = blockState.getValue(RiceCropBlock.LOCATION);
                newPos = newPos.below(position);
                blockState = level.getBlockState(newPos);
            }
            if (cropBlock.isMaxAge(blockState)) {
                // 成熟则收割
                if (player instanceof ServerPlayer serverPlayer)
                    cropBlock.playerDestroy(level, serverPlayer, newPos, blockState, null, ItemStack.EMPTY);
                BlockState stateForAge = cropBlock.getStateForAge(0);
                // 同步水属性状态
                BooleanProperty waterlogged = BlockStateProperties.WATERLOGGED;
                if (stateForAge.hasProperty(waterlogged)) {
                    stateForAge = stateForAge.setValue(waterlogged, blockState.getValue(waterlogged));
                }
                level.setBlock(newPos, stateForAge, Block.UPDATE_ALL);
                level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, newPos, Block.getId(blockState));
                return true;
            }
            return false;
        }

        // 如果是灌木，直接破坏
        if (block instanceof VegetationBlock) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.gameMode.destroyBlock(newPos);
                level.setBlock(newPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, newPos, Block.getId(blockState));
                return true;
            }
        }
        return false;
    }
}
