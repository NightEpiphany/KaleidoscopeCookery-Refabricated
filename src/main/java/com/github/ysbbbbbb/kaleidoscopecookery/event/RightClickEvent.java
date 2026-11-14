package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class RightClickEvent {
    public static void register() {
        UseBlockCallback.EVENT.register(RightClickEvent::onUseBlock);
        UseEntityCallback.EVENT.register(RightClickEvent::onUseEntity);
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PotBlockEntity || blockEntity instanceof StockpotBlockEntity || blockEntity instanceof TeapotBlockEntity) {
            ItemStack stack = player.getItemInHand(hand);
            InteractionResult recipeResult = RecipeItem.tryUseOnCookware(stack, level, player, hand, hitResult);
            if (recipeResult != InteractionResult.PASS) {
                return recipeResult;
            }
        }
        if (player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND
            && blockEntity instanceof FruitBasketBlockEntity fruitBasketBlock) {
            fruitBasketBlock.takeOut(player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand, Entity target, EntityHitResult hitResult) {
        if (target instanceof Chicken chicken && chicken.isBaby()
            && player.getMainHandItem().is(TagMod.CATERPILLARS)) {
            // 让鸡瞬间成年
            chicken.setAge(0);
            // 加一些特性和音效
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        chicken.getX(),
                        chicken.getY() + 0.25,
                        chicken.getZ(),
                        5,
                        0.2, 0.1, 0.2, 0.1);
                serverLevel.playSound(null, chicken.getX(), chicken.getY(), chicken.getZ(),
                        SoundEvents.PARROT_EAT, chicken.getSoundSource(),
                        1.0F, 1.0F + (serverLevel.getRandom().nextFloat() - serverLevel.getRandom().nextFloat()) * 0.2F);
            }
            if (!player.getMainHandItem().isEmpty())
                player.getMainHandItem().consume(1, player);
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_CATERPILLAR_FEED_CHICKEN);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
