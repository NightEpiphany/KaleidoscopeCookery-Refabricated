package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class RightClickEvent {
    public static void register() {
        UseBlockCallback.EVENT.register(RightClickEvent::onUseBlock);
        UseEntityCallback.EVENT.register(RightClickEvent::onUseEntity);
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        ItemStack itemInHand = player.getItemInHand(hand);
        Item item = itemInHand.getItem();
        // 需兼容 Touhou Little Maid: Orihime
        Item maidItem = BuiltInRegistries.ITEM.get(new ResourceLocation("touhou_little_maid", "smart_slab_has_maid"));
        if (player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND
                && !itemInHand.is(Items.DEBUG_STICK)
                && !itemInHand.is(Items.FIREWORK_ROCKET)
                && !itemInHand.is(maidItem)
                && level.getBlockEntity(pos) instanceof FruitBasketBlockEntity fruitBasketBlock) {
            if (item instanceof BlockItem) {
                Direction clickedFace = hitResult.getDirection();
                if (!(clickedFace == Direction.UP)) {
                    return InteractionResult.PASS;
                }
            }
            fruitBasketBlock.takeOut(player);
            return InteractionResult.SUCCESS;
        }
        // 厨架潜行存入物品
        if (!player.isSpectator() && player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND
                && !itemInHand.isEmpty() && !itemInHand.is(ModItems.KITCHEN_SHELF)
                && !(item instanceof RecipeItem && RecipeItem.hasRecipe(itemInHand))
                && level.getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf) {
            if (!level.isClientSide) {
                shelf.interact(player, true);
                level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand, Entity target, EntityHitResult hitResult) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (target instanceof Chicken chicken && chicken.isBaby() && mainHandItem.is(TagMod.CATERPILLARS)) {
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
                        1.0F, 1.0F + (serverLevel.random.nextFloat() - serverLevel.random.nextFloat()) * 0.2F);
            }
            if (!player.isCreative())
                player.getMainHandItem().shrink(1);
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_CATERPILLAR_FEED_CHICKEN);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
