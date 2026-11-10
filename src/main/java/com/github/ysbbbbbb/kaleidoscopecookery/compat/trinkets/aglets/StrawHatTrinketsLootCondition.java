package com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.aglets;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.Set;

public class StrawHatTrinketsLootCondition implements LootItemCondition.ContextUser {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "straw_hat_trinkets_loot_condition");

    private final ItemPredicate predicate;

    public static final MapCodec<StrawHatTrinketsLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemPredicate.CODEC.fieldOf("predicate").forGetter(tool -> tool.predicate)
    ).apply(instance, StrawHatTrinketsLootCondition::new));

    public StrawHatTrinketsLootCondition(ItemPredicate predicate) {
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
    public boolean test(LootContext lootContext) {
        if (lootContext.hasParameter(LootContextParams.THIS_ENTITY)) {
            Entity entity = lootContext.getOptional(LootContextParams.THIS_ENTITY);
            if (entity instanceof LivingEntity livingEntity) {
                Optional<TrinketAttachment> optionalComponent = Optional.ofNullable(TrinketsApi.getAttachment(livingEntity));
                if (optionalComponent.isPresent()) {
                    TrinketAttachment component = optionalComponent.get();
                    return component.isEquipped(itemStack -> itemStack.is(TagMod.STRAW_HAT));
                }
            }
        }
        return false;
    }

    public static Builder toolMatches(ItemPredicate builder) {
        return () -> new StrawHatTrinketsLootCondition(builder);
    }

    @Override
    public @NonNull MapCodec<StrawHatTrinketsLootCondition> codec() {
        return CODEC;
    }
}
