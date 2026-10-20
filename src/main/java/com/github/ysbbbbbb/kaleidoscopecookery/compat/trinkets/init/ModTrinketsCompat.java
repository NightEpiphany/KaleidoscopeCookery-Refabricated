package com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.aglets.arch.StrawHatTrinketItem;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.aglets.StrawHatTrinketsLootCondition;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids.ModItemIds;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.*;

public class ModTrinketsCompat {

    @Contract(pure = true)
    static void init() {
            STRAW_HAT = registerStrawHatsTrinket("straw_hat", new StrawHatTrinketItem(false, new Item.Properties().setId(ModItemIds.STRAW_HAT)));
            STRAW_HAT_FLOWER = registerStrawHatsTrinket("straw_hat_flower", new StrawHatTrinketItem(true, new Item.Properties().setId(ModItemIds.STRAW_HAT_FLOWER)));
                Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, StrawHatTrinketsLootCondition.ID, StrawHatTrinketsLootCondition.CODEC);
                LootTableEvents.MODIFY.register((key, tableBuilder, _, wrapperLookup) -> {
                    Identifier id = key.identifier();
                    HolderLookup.RegistryLookup<Enchantment> enchantment = wrapperLookup.lookupOrThrow(Registries.ENCHANTMENT);
                    if (id.equals(Identifier.withDefaultNamespace("blocks/short_grass"))) {
                        HolderLookup<Item> holderLookup = wrapperLookup.lookupOrThrow(Registries.ITEM);
                        var predicate = ItemPredicate.Builder.item().of(holderLookup, TagMod.STRAW_HAT).build();
                        var enchantments = ApplyBonusCount.addUniformBonusCount(enchantment.getOrThrow(Enchantments.FORTUNE), 2);
                        var chance = LootItemRandomChanceCondition.randomChance(0.125F);
                        var match = StrawHatTrinketsLootCondition.toolMatches(predicate);
                        tableBuilder.withPool(LootPool.lootPool()
                                .setRolls(ContextIntProviders.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.TOMATO_SEED)
                                        .when(chance).when(match)
                                        .apply(enchantments))
                                .add(LootItem.lootTableItem(ModItems.CHILI_SEED)
                                        .when(chance).when(match)
                                        .apply(enchantments))
                                .add(LootItem.lootTableItem(ModItems.LETTUCE_SEED)
                                        .when(chance).when(match)
                                        .apply(enchantments))
                                .add(LootItem.lootTableItem(ModItems.WILD_RICE_SEED)
                                        .when(chance).when(match)
                                        .apply(enchantments))
                                .add(LootItem.lootTableItem(TEA_SEED)
                                        .when(chance).when(match)
                                        .apply(enchantments))
                                .add(EmptyLootItem.emptyItem().setWeight(2)));
                    }
                });
    }

    public static Supplier<Item> registerStrawHatsTrinket(String string, Item item) {
        return () -> {
            if (BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, string)))
                return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, string));
            else return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, string), item);
        };
    }
}
