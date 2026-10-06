package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.ICustomEatEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ConfigGetter;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Prediction;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BowlFoodBlockItem extends BlockItem implements ICustomEatEffect {
    private final BiFunction<Quality, FoodProperties, FoodProperties> foodPropertiesCache = Util.memoize(
            (quality, raw) -> QualityUtils.modifyFoodProperties(raw, quality)
    );
    private final BiFunction<Quality, Consumable, Consumable> foodConsumableCache = Util.memoize(
            (quality, raw) -> QualityUtils.modifyFoodConsumables(raw, quality)
    );
    @SuppressWarnings("all")
    private final Optional<ItemLike> usingConvertsTo;

    public BowlFoodBlockItem(Block block, FoodProperties properties, Consumable consumable, @Nullable ItemLike usingConvertsTo, String name) {
        super(block, new Properties().stacksTo(16).useBlockDescriptionPrefix().usingConvertsTo(Items.BOWL)
                .food(properties, consumable).setId(PortHelper.createItemId(name))
        );
        this.usingConvertsTo = Optional.ofNullable(usingConvertsTo);
    }

    @Override
    public @NonNull InteractionResult place(@NonNull BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result instanceof InteractionResult.Success success) {
            if (context.getPlayer() instanceof ServerPlayer player) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }
            return success.withoutItem();
        }
        return result;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
        if (foodProperties != null) {
            if (player.canEat(foodProperties.canAlwaysEat())) {
                itemStack.set(DataComponents.FOOD, modifyFoodProperties(itemStack));
                itemStack.set(DataComponents.CONSUMABLE, modifyConsumables(itemStack));
                player.startUsingItem(interactionHand);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public @Nullable FoodProperties modifyFoodProperties(ItemStack stack) {
        FoodProperties raw = stack.get(DataComponents.FOOD);
        if (!QualityUtils.hasQuality(stack) || raw == null) {
            return raw;
        }
        // 如果有品质，那么依据品质
        Quality quality = QualityUtils.getQuality(stack);
        return this.foodPropertiesCache.apply(quality, raw);
    }

    @Override
    public Consumable modifyConsumables(ItemStack stack) {
        Consumable raw = stack.get(DataComponents.CONSUMABLE);
        if (!QualityUtils.hasQuality(stack) || raw == null) {
            return raw;
        }
        // 需要剔除 usingConvertsTo，因为已经给过了
        Quality quality = QualityUtils.getQuality(stack);
        return this.foodConsumableCache.apply(quality, raw);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity) {
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
                if (this.usingConvertsTo.isPresent() && ItemStack.isSameItemSameComponents(itemStack, this.usingConvertsTo.get().asItem().getDefaultInstance())) {
                    return;
                }
                if (entity instanceof Player player) {
                    player.getInventory().placeItemBackInInventory(itemStack, Prediction.PREDICTED);
                } else {
                    ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), itemStack);
                    level.addFreshEntity(itemEntity);
                }
            });
        }
        return super.finishUsingItem(stack, level, entity);
    }

    private List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getBlock().getLootTable().isEmpty()) {
            return Collections.emptyList();
        } else {
            ResourceKey<LootTable> resourcekey = state.getBlock().getLootTable().get();
            LootParams lootParams = params.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
            ServerLevel serverLevel = lootParams.getLevel();
            LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(resourcekey);
            return lootTable.getRandomItems(lootParams);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, @NonNull TooltipContext tooltip, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String key = "tooltip.%s.%s.maxim".formatted(id.getNamespace(), id.getPath());
        MutableComponent full = Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
        String text = full.getString();
        for (String line : text.split("\n")) {
            if (!line.isEmpty()) {
                consumer.accept(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            } else {
                consumer.accept(CommonComponents.EMPTY);
            }
        }

        Consumable consumable = modifyConsumables(stack);
        List<MobEffectInstance> effects = QualityUtils.getStatusEffects(consumable);
        boolean showEffect = !effects.isEmpty()
                && ConfigGetter.Client.getShowFoodEffectTooltips();

        if (QualityUtils.hasQuality(stack)) {
            Quality quality = QualityUtils.getQuality(stack);
            consumer.accept(quality.getTooltip());
        }
        if (showEffect) {
            consumer.accept(CommonComponents.space());
            PotionContents.addPotionTooltip(effects, consumer, 1.0F, tooltip.tickRate());
        }
    }
}
