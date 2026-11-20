package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CandiedSkewerItem extends WithTooltipsBlockItem implements IHasContainer {
    private static final UUID KNOCKBACK_MODIFIER_UUID = UUID.fromString("d0190f53-13a1-4c8c-baa2-d3d0059911d4");

    private static final Multimap<Attribute, AttributeModifier> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ATTACK_KNOCKBACK,
            new AttributeModifier(
                    KNOCKBACK_MODIFIER_UUID,
                    "Candied skewer knockback",
                    3,
                    AttributeModifier.Operation.ADDITION
            )
    );

    private final @Nullable MobEffect attackEffect;
    private final int attackEffectDuration;
    private final Block sideBlock;

    public CandiedSkewerItem(Block uprightBlock, Block sideBlock, FoodProperties foodProperties) {
        this(uprightBlock, sideBlock, foodProperties, null, 0);
    }

    public CandiedSkewerItem(Block uprightBlock, Block sideBlock, FoodProperties foodProperties,
                             @Nullable MobEffect attackEffect, int attackEffectDuration) {
        super(uprightBlock, new Properties().food(foodProperties), "");
        this.attackEffect = attackEffect;
        this.attackEffectDuration = attackEffectDuration;
        this.sideBlock = sideBlock;
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
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (face == Direction.UP) {
            BlockState state = getBlock().getStateForPlacement(context);
            return state == null ? null : state.setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        if (face.getAxis().isHorizontal()) {
            BlockState state = sideBlock.getStateForPlacement(context);
            return state == null ? null : state.setValue(FACING, face);
        }
        return null;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @Nullable Level level, @NonNull List<Component> tooltip, @NonNull TooltipFlag flag) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(this);
        tooltip.add(Component.translatable(key.toLanguageKey("tooltip"))
                .withStyle(ChatFormatting.DARK_GRAY)
                .withStyle(ChatFormatting.ITALIC));
    }

    @Override
    public @NonNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NonNull EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? ATTRIBUTE_MODIFIERS : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hurtEnemy(@NonNull ItemStack stack, @NonNull LivingEntity target, @NonNull LivingEntity attacker) {
        if (this.attackEffect != null && !target.level().isClientSide) {
            target.addEffect(new MobEffectInstance(this.attackEffect, this.attackEffectDuration), attacker);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        return this.returnContainerToEntity(remaining, level, entity);
    }

    @Override
    public Item getContainerItem() {
        return Items.STICK;
    }
}
