package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TeacupItem extends BlockItem implements IHasContainer {
    private final List<Pair<Supplier<MobEffectInstance>, Float>> effects;
    private final List<MobEffectInstance> showEffects = Lists.newArrayList();

    public TeacupItem(Block block, List<Pair<Supplier<MobEffectInstance>, Float>> effects) {
        super(block, new Properties().stacksTo(16));
        this.effects = effects;
        this.effects.forEach(effect -> {
            if (effect.getSecond() >= 1F) {
                showEffects.add(effect.getFirst().get());
            }
        });
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return net.minecraft.world.item.ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        this.addTeaEffect(level, entity);
        ItemUtils.consume(stack, entity, 1);
        return returnContainerToEntity(stack, level, entity);
    }

    protected void addTeaEffect(Level level, LivingEntity entity) {
        for (var entry : this.effects) {
            MobEffectInstance instance = entry.getFirst().get();
            float probability = entry.getSecond();
            if (!level.isClientSide && level.random.nextFloat() < probability) {
                entity.addEffect(instance);
            }
        }
    }

    @Override
    public Item getContainerItem() {
        return ModItems.EMPTY_CUP;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String key = "tooltip.%s.%s.maxim".formatted(id.getNamespace(), id.getPath());
        MutableComponent full = Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
        // 先拿到纯文本，再按 \n 切
        String text = full.getString();
        for (String line : text.split("\n")) {
            if (!line.isEmpty()) {
                tooltip.add(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            } else {
                tooltip.add(CommonComponents.EMPTY);
            }
        }
        if (!this.showEffects.isEmpty()) {
            tooltip.add(CommonComponents.space());
            PotionUtils.addPotionTooltip(this.showEffects, tooltip, 1.0F);
        }
    }
}
