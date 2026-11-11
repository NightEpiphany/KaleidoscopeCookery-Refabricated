package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceEntityMatchTool;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import static com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery.LOGGER;

public final class ExtraLootTableDrop {
    private static final Identifier DONKEY = Identifier.withDefaultNamespace("entities/donkey");
    private static final Identifier HOGLIN = Identifier.fromNamespaceAndPath("minecraft", "entities/hoglin");
    private static final Identifier PIG = Identifier.fromNamespaceAndPath("minecraft", "entities/pig");
    private static final Identifier PIGLIN = Identifier.fromNamespaceAndPath("minecraft", "entities/piglin");
    private static final Identifier PIGLIN_BRUTE = Identifier.fromNamespaceAndPath("minecraft", "entities/piglin_brute");
    private static final Identifier ZOGLIN = Identifier.withDefaultNamespace("entities/zoglin");
    private static final Identifier ZOMBIFIED_PIGLIN = Identifier.withDefaultNamespace("entities/zombified_piglin");
    private static final Identifier GRASS = Identifier.fromNamespaceAndPath("minecraft", "blocks/short_grass");

    private ExtraLootTableDrop() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register(ExtraLootTableDrop::modify);
    }

    private static void modify(ResourceKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, HolderLookup.Provider registries) {
        if (!source.isBuiltin()) {
            return;
        }

        LootPool.Builder extraPool = createExtraPool(key.identifier(), registries);
        if (extraPool != null) {
            tableBuilder.withPool(extraPool);
            LOGGER.debug("Added Cookery drops to loot table {}", key.identifier());
        }
    }

    private static LootPool.Builder createExtraPool(Identifier id, HolderLookup.Provider registries) {
        if (id.equals(DONKEY)) {
            return createEntityDropPool(ModItems.RAW_DONKEY_MEAT, 2, registries);
        }
        if (id.equals(HOGLIN) || id.equals(PIGLIN) || id.equals(PIGLIN_BRUTE) || id.equals(ZOGLIN) || id.equals(ZOMBIFIED_PIGLIN)) {
            return createEntityDropPool(ModItems.OIL, 2, registries);
        }
        if (id.equals(PIG)) {
            return createEntityDropPool(ModItems.OIL, 1, registries);
        }
        if (id.equals(GRASS)) {
            return createSeedPool(registries);
        }
        return null;
    }

    private static LootPool.Builder createEntityDropPool(ItemLike item, int rolls, HolderLookup.Provider registries) {
        // 仅在玩家主手持厨房刀击杀时追加掉落，保留原版战利品池。
        HolderGetter<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
        HolderGetter<Enchantment> enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        ItemPredicate hasKnife = ItemPredicate.Builder.item().of(itemLookup, TagMod.KITCHEN_KNIFE).build();
        LootItemCondition.Builder toolMatches = AdvanceEntityMatchTool.toolMatches(EquipmentSlot.MAINHAND, hasKnife);
        var count = SetItemCountFunction.setCount(ContextIntProviders.between(1, 2));
        var looting = EnchantedCountIncreaseFunction.lootingMultiplier(enchantmentLookup, ContextFloatProviders.between(0.0F, 1.0F));
        var drop = LootItem.lootTableItem(item).apply(count).apply(looting);

        return LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(rolls))
                .add(drop).when(toolMatches);
    }

    private static LootPool.Builder createSeedPool(HolderLookup.Provider registries) {
        HolderGetter<Enchantment> enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        HolderGetter<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
        // 穿戴草帽破坏矮草丛时有概率掉落作物种子。
        var tomato = createSeedEntry(ModItems.TOMATO_SEED, enchantmentLookup, itemLookup);
        var chili = createSeedEntry(ModItems.CHILI_SEED, enchantmentLookup, itemLookup);
        var lettuce = createSeedEntry(ModItems.LETTUCE_SEED, enchantmentLookup, itemLookup);
        var rice = createSeedEntry(ModItems.WILD_RICE_SEED, enchantmentLookup, itemLookup);
        var tea = createSeedEntry(ModItems.TEA_SEED, enchantmentLookup, itemLookup);
        var empty = EmptyLootItem.emptyItem().setWeight(2);

        return LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(1))
                .add(tomato).add(chili)
                .add(lettuce).add(rice)
                .add(tea).add(empty);
    }

    private static UniformContainerBase.Builder<?> createSeedEntry(
            ItemLike item,
            HolderGetter<Enchantment> enchantments,
            HolderGetter<Item> items
    ) {
        ItemPredicate hasHat = ItemPredicate.Builder.item().of(items, TagMod.STRAW_HAT).build();
        LootItemCondition.Builder hatMatches = AdvanceBlockMatchTool.toolMatches(EquipmentSlot.HEAD, hasHat);
        return LootItem.lootTableItem(item)
                .when(LootItemRandomChanceCondition.randomChance(0.125F)).when(hatMatches)
                .apply(ApplyBonusCount.addUniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE), 2));
    }
}
