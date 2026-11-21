package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class CandiedSkewerItem extends WithTooltipsBlockItem implements IHasContainer {
    private static final ItemAttributeModifiers ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_KNOCKBACK,
                    new AttributeModifier(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "candied_skewer_knockback"),
                            3.0, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
            .build();

    private final Block sideBlock;
    private final @Nullable Holder<MobEffect> attackEffect;
    private final int attackEffectDuration;

    public CandiedSkewerItem(String name, Block uprightBlock, Block sideBlock, Properties properties,
                             FoodProperties food, Consumable consumable) {
        this(name, uprightBlock, sideBlock, properties, food, consumable, null, 0);
    }

    public CandiedSkewerItem(String name, Block uprightBlock, Block sideBlock, Properties properties,
                             FoodProperties food, Consumable consumable,
                             @Nullable Holder<MobEffect> attackEffect, int attackEffectDuration) {
        super(uprightBlock, properties.food(food, consumable).attributes(ATTRIBUTES).useItemDescriptionPrefix(), name);
        this.sideBlock = sideBlock;
        this.attackEffect = attackEffect;
        this.attackEffectDuration = attackEffectDuration;
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        return player == null || player.isSecondaryUseActive() ? super.useOn(context) : InteractionResult.PASS;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.UP) {
            return super.getPlacementState(context);
        }
        if (context.getClickedFace().getAxis().isHorizontal()) {
            BlockState state = this.sideBlock.getStateForPlacement(context);
            return state != null && this.canPlace(context, state) ? state : null;
        }
        return null;
    }

    @Override
    public void registerBlocks(@NonNull Map<Block, Item> blockItems, @NonNull Item item) {
        super.registerBlocks(blockItems, item);
        blockItems.put(this.sideBlock, item);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context,
                                @NonNull TooltipDisplay display, @NonNull Consumer<Component> consumer,
                                @NonNull TooltipFlag flag) {
        consumer.accept(Component.translatable(this.getKey()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public void postHurtEnemy(@NonNull ItemStack stack, @NonNull LivingEntity target, @NonNull LivingEntity attacker) {
        if (this.attackEffect != null && !target.level().isClientSide()) {
            target.addEffect(new MobEffectInstance(this.attackEffect, this.attackEffectDuration), attacker);
        }
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level,
                                               @NonNull LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        return level.isClientSide() || entity.hasInfiniteMaterials()
                ? remaining : this.returnContainerToEntity(remaining, level, entity);
    }

    @Override
    public Item getContainerItem() {
        return Items.STICK;
    }
}
