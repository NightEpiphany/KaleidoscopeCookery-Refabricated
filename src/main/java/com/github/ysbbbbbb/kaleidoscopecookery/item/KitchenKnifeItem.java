package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.google.common.base.Suppliers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class KitchenKnifeItem extends TieredItem {
    public KitchenKnifeItem(Tier tier) {
        super(tier, new Item.Properties().attributes(createAttributes(tier, 0, -4.0F)));
    }

    public KitchenKnifeItem(Tier tier, Item.Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, 0, -2.0F)));
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, int attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed + tier.getSpeed(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.kaleidoscope_cookery.kitchen_knife").withStyle(ChatFormatting.GRAY));
    }


    public enum KnifeTiers implements Tier {
        IRON(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 1.6F, 5.0F, 14, () -> Ingredient.of(Items.IRON_INGOT)),
        DIAMOND(BlockTags.INCORRECT_FOR_IRON_TOOL, 1561, 1.5F, 6.0F, 10, () -> Ingredient.of(Items.DIAMOND)),
        GOLD(BlockTags.INCORRECT_FOR_IRON_TOOL, 32, 1.7F, 3.0F, 22, () -> Ingredient.of(Items.GOLD_INGOT)),
        NETHERITE(BlockTags.INCORRECT_FOR_IRON_TOOL, 2031, 1.5F, 7.0F, 15, () -> Ingredient.of(Items.NETHERITE_INGOT));

        private final TagKey<Block> incorrectBlocksForDrops;
        private final int uses;
        private final float speed;
        private final float damage;
        private final int enchantmentValue;
        private final Supplier<Ingredient> repairIngredient;

        KnifeTiers(final TagKey<Block> tagKey, final int j, final float f, final float g, final int k, final Supplier<Ingredient> supplier) {
            this.incorrectBlocksForDrops = tagKey;
            this.uses = j;
            this.speed = f;
            this.damage = g;
            this.enchantmentValue = k;
            Objects.requireNonNull(supplier);
            this.repairIngredient = Suppliers.memoize(supplier::get);
        }

        @Override
        public int getUses() {
            return this.uses;
        }

        @Override
        public float getSpeed() {
            return this.speed;
        }

        @Override
        public float getAttackDamageBonus() {
            return this.damage;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return this.incorrectBlocksForDrops;
        }

        @Override
        public int getEnchantmentValue() {
            return this.enchantmentValue;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return this.repairIngredient.get();
        }

    }
}
