package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class AdvanceBlockMatchTool implements LootItemCondition.ContextUser {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "advance_block_match_tool");
    public static final MapCodec<AdvanceBlockMatchTool> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(tool -> tool.slot),
            ItemPredicate.CODEC.fieldOf("predicate").forGetter(tool -> tool.predicate)
    ).apply(instance, AdvanceBlockMatchTool::new));

    private final EquipmentSlot slot;
    private final ItemPredicate predicate;

    public AdvanceBlockMatchTool(EquipmentSlot slot, ItemPredicate predicate) {
        this.slot = slot;
        this.predicate = predicate;
    }

    @Override
    public @NonNull Set<ContextKey<?>> requiredContextParams() {
        return ImmutableSet.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public void validate(@NonNull ValidationContext context) {
    }

    @Override
    public boolean test(LootContext context) {
        if (context.hasParameter(LootContextParams.THIS_ENTITY)) {
            Entity entity = context.getOptional(LootContextParams.THIS_ENTITY);
            if (entity instanceof LivingEntity livingEntity) {
                ItemStack stack = livingEntity.getItemBySlot(this.slot);
                return this.predicate.test(stack);
            }
        }
        return false;
    }

    public static Builder toolMatches(EquipmentSlot slot, ItemPredicate builder) {
        return () -> new AdvanceBlockMatchTool(slot, builder);
    }

    @Override
    public @NonNull MapCodec<AdvanceBlockMatchTool> codec() {
        return CODEC;
    }
}
