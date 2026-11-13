package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModConsumables;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ClayPotMilkTeaItem extends WithTooltipsBlockItem implements IHasContainer {
    public ClayPotMilkTeaItem(Block block, Item.Properties properties) {
        super(block, properties
                .stacksTo(16)
                .component(DataComponents.CONSUMABLE, ModConsumables.CLAY_POT_MILK_TEA)
        ,"clay_pot_milk_tea.effect");
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity entity) {
        return 32;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public @NotNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!level.isClientSide()) {
            List<MobEffectInstance> activeEffects = new ArrayList<>(entity.getActiveEffects().size());
            activeEffects.addAll(entity.getActiveEffects());
            for (MobEffectInstance effect : activeEffects) {
                if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                    entity.removeEffect(effect.getEffect());
                }
            }
        }
        if (entity instanceof Player player && player.isCreative()) {
            return stack;
        }
        stack.consume(1, entity);
        return returnContainerToEntity(stack, level, entity);
    }

    @Override
    public Item getContainerItem() {
        return Items.FLOWER_POT;
    }
}
