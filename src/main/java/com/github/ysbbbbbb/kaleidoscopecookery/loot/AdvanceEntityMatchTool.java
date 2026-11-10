package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class AdvanceEntityMatchTool implements LootItemCondition.ContextUser {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "advance_entity_match_tool");
    public static final MapCodec<AdvanceEntityMatchTool> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(tool -> tool.slot),
            ItemPredicate.CODEC.fieldOf("predicate").forGetter(tool -> tool.predicate)
    ).apply(instance, AdvanceEntityMatchTool::new));

    private final EquipmentSlot slot;
    private final ItemPredicate predicate;

    public AdvanceEntityMatchTool(EquipmentSlot slot, ItemPredicate predicate) {
        this.slot = slot;
        this.predicate = predicate;
    }


    @Override
    public @NonNull Set<ContextKey<?>> requiredContextParams() {
        return ImmutableSet.of(LootContextParams.LAST_DAMAGE_PLAYER);
    }

    @Override
    public void validate(@NonNull ValidationContext context) {
    }

    @Override
    public boolean test(LootContext context) {
        if (context.hasParameter(LootContextParams.LAST_DAMAGE_PLAYER)) {
            Player player = context.getOptional(LootContextParams.LAST_DAMAGE_PLAYER);
            if (player == null)
                return false;
            ItemStack stack = player.getItemBySlot(this.slot);
            return this.predicate.test(stack);
        }
        return false;
    }

    public static Builder toolMatches(EquipmentSlot slot, ItemPredicate builder) {
        return () -> new AdvanceEntityMatchTool(slot, builder);
    }

    @Override
    public @NonNull MapCodec<AdvanceEntityMatchTool> codec() {
        return CODEC;
    }
}
