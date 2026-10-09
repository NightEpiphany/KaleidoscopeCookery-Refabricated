package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.ICustomEatEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BowlFoodBlockItem extends BlockItem implements IHasContainer, ICustomEatEffect {
    private final List<MobEffectInstance> effectInstances = Lists.newArrayList();

    private final Function<Quality, List<MobEffectInstance>> effectCache = Util.memoize(
            quality -> QualityUtils.modifyEffects(this.effectInstances, quality)
    );
    private final BiFunction<Quality, FoodProperties, FoodProperties> foodPropertiesCache = Util.memoize(
            (quality, raw) -> QualityUtils.modifyFoodProperties(raw, quality)
    );

    public BowlFoodBlockItem(Block pBlock, FoodProperties properties) {
        super(pBlock, new Item.Properties().stacksTo(16).food(properties));
        properties.getEffects().forEach(effect -> {
            if (effect.getSecond() >= 1F) {
                effectInstances.add(effect.getFirst());
            }
        });
    }

    @Override
    public @Nullable FoodProperties modifyFoodProperties(ItemStack stack) {
        FoodProperties raw = super.getFoodProperties();
        if (!QualityUtils.hasQuality(stack) || raw == null) {
            return raw;
        }
        Quality quality = QualityUtils.getQuality(stack);
        return this.foodPropertiesCache.apply(quality, raw);
    }


    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel && this.getBlock() instanceof FoodBiteBlock foodBiteBlock) {
            LootParams.Builder builder = (new LootParams.Builder(serverLevel))
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(entity.blockPosition()))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
            BlockState state = foodBiteBlock.defaultBlockState().setValue(foodBiteBlock.getBites(), foodBiteBlock.getMaxBites());
            List<ItemStack> drops = foodBiteBlock.getDrops(state, builder);
            drops.forEach(itemStack -> {
                if (itemStack.isEmpty()) {
                    return;
                }
                if (entity instanceof Player player) {
                    player.getInventory().placeItemBackInInventory(itemStack);
                } else {
                    ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), itemStack);
                    level.addFreshEntity(itemEntity);
                }
            });
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        // 描述
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

        boolean showEffect = !this.effectInstances.isEmpty()
                && ClientConfig.SHOW_FOOD_EFFECT_TOOLTIPS.get();

        // 品质
        if (QualityUtils.hasQuality(stack)) {
            Quality quality = QualityUtils.getQuality(stack);
            tooltip.add(quality.getTooltip());
            if (showEffect) {
                tooltip.add(CommonComponents.space());
                PotionUtils.addPotionTooltip(this.effectCache.apply(quality), tooltip, 1.0F);
            }
        } else {
            tooltip.add(CommonComponents.space());
            PotionUtils.addPotionTooltip(this.effectInstances, tooltip, 1.0F);
        }
    }

    @Override
    public Item getContainerItem() {
        return Items.BOWL;
    }
}
