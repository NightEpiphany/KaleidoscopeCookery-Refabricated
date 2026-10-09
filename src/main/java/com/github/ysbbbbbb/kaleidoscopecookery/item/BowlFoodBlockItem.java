package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.ICustomEatEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityFoodComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BowlFoodBlockItem extends BlockItem implements IHasContainer, ICustomEatEffect {

    private final List<MobEffectInstance> effectInstances = Lists.newArrayList();

    private final Function<Quality, List<MobEffectInstance>> effectCache = Util.memoize(
            quality -> QualityUtils.modifyEffects(this.effectInstances, quality)
    );

    private final QualityFoodComponents qualityComponents;

    @SuppressWarnings("all")
    private final Optional<ItemStack> usingConvertsTo;

    public BowlFoodBlockItem(Block block, FoodProperties properties, @Nullable ItemLike usingConvertsTo) {
        super(block, new Properties().stacksTo(16).food(
                new FoodProperties(
                        properties.nutrition(),
                        properties.saturation(),
                        properties.canAlwaysEat(),
                        properties.eatSeconds(),
                        usingConvertsTo == null ? Optional.empty() : Optional.of(new ItemStack(usingConvertsTo)),
                        properties.effects())
        ));
        this.usingConvertsTo = usingConvertsTo == null ? Optional.empty() : Optional.of(new ItemStack(usingConvertsTo));
        properties.effects().forEach(effect -> {
            if (effect.probability() >= 1F) {
                effectInstances.add(effect.effect());
            }
        });
        this.qualityComponents = new QualityFoodComponents(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
        if (foodProperties != null) {
            if (player.canEat(foodProperties.canAlwaysEat())) {
                itemStack.set(DataComponents.FOOD, modifyFoodProperties(itemStack));
                player.startUsingItem(interactionHand);
                return InteractionResultHolder.consume(itemStack);
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }
    }

    @Override
    public @Nullable FoodProperties modifyFoodProperties(ItemStack stack) {
        return this.qualityComponents.food(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel && this.getBlock() instanceof FoodBiteBlock foodBiteBlock) {
            LootParams.Builder builder = (new LootParams.Builder(serverLevel))
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(entity.blockPosition()))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
            BlockState state = foodBiteBlock.defaultBlockState().setValue(foodBiteBlock.getBites(), foodBiteBlock.getMaxBites());
            List<ItemStack> drops = getDrops(state, builder);
            drops.forEach(itemStack -> {
                if (itemStack.isEmpty()) {
                    return;
                }
                // 需要剔除 usingConvertsTo，因为已经给过了
                if (this.usingConvertsTo.isPresent() && ItemStack.isSameItem(itemStack, this.usingConvertsTo.get())) {
                    return;
                }
                if (entity instanceof Player player) {
                    ItemUtils.giveItemToPlayer(player, itemStack);
                } else {
                    ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), itemStack);
                    level.addFreshEntity(itemEntity);
                }
            });
        }
        return super.finishUsingItem(stack, level, entity);
    }

    private List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ResourceKey<LootTable> resourcekey = state.getBlock().getLootTable();
        if (resourcekey == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootParams = params.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
            ServerLevel serverLevel = lootParams.getLevel();
            LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(resourcekey);
            return lootTable.getRandomItems(lootParams);
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
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
                PotionContents.addPotionTooltip(this.effectCache.apply(quality), tooltip::add, 1.0F, context.tickRate());
            }
        } else {
            tooltip.add(CommonComponents.space());
            PotionContents.addPotionTooltip(this.effectInstances, tooltip::add, 1.0F, context.tickRate());
        }
    }

    @Override
    public Item getContainerItem() {
        return Items.BOWL;
    }
}
