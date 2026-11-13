package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TeacupItem extends BlockItem implements IHasContainer {
    private final List<Pair<Supplier<MobEffectInstance>, Float>> effects;
    private final List<MobEffectInstance> showEffects = Lists.newArrayList();

    public TeacupItem(Block block, List<Pair<Supplier<MobEffectInstance>, Float>> effects, Properties properties) {
        super(block, properties
                .stacksTo(16)
                .useBlockDescriptionPrefix()
                .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK)
        );
        this.effects = effects;
        this.effects.forEach(effect -> {
            if (effect.getSecond() >= 1F) {
                showEffects.add(effect.getFirst().get());
            }
        });
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity entity) {
        return 32;
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        this.addTeaEffect(level, entity);
        stack.consume(1, entity);
        ItemStack container = this.getContainerItem().getDefaultInstance();
        if (stack.isEmpty()) {
            return container;
        }
        if (entity instanceof Player player) {
            player.getInventory().placeItemBackInInventory(container, Prediction.PREDICTED);
        } else {
            ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), container);
            level.addFreshEntity(itemEntity);
        }
        return stack;
    }

    protected void addTeaEffect(Level level, LivingEntity entity) {
        for (var entry : this.effects) {
            MobEffectInstance instance = entry.getFirst().get();
            float probability = entry.getSecond();
            if (!level.isClientSide() && level.getRandom().nextFloat() < probability) {
                entity.addEffect(instance);
            }
        }
    }

    @Override
    public Item getContainerItem() {
        return ModItems.EMPTY_CUP;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag pFlag) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String key = "tooltip.%s.%s.maxim".formatted(id.getNamespace(), id.getPath());
        MutableComponent full = Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
        // 先拿到纯文本，再按 \n 切
        String text = full.getString();
        for (String line : text.split("\n")) {
            if (!line.isEmpty()) {
                consumer.accept(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            } else {
                consumer.accept(CommonComponents.EMPTY);
            }
        }
        if (!this.showEffects.isEmpty()) {
            consumer.accept(CommonComponents.space());
            PotionContents.addPotionTooltip(this.showEffects, consumer, 1.0F, context.tickRate());
        }
    }
}
