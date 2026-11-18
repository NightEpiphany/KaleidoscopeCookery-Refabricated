package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.storage.loot.LootTable;

@SuppressWarnings("all")
public final class ModVillager {

    public static final Identifier CHEF_GIFT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "gameplay/hero_of_the_village/chef_gift");

    public static final ResourceKey<LootTable> CHEF_GIFT_LOOT_KEY = ResourceKey.create(Registries.LOOT_TABLE, CHEF_GIFT);

    private static final ResourceKey<TradeSet> CHEF_LEVEL_1 = ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef/level_1"));
    private static final ResourceKey<TradeSet> CHEF_LEVEL_2 = ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef/level_2"));
    private static final ResourceKey<TradeSet> CHEF_LEVEL_3 = ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef/level_3"));
    private static final ResourceKey<TradeSet> CHEF_LEVEL_4 = ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef/level_4"));
    private static final ResourceKey<TradeSet> CHEF_LEVEL_5 = ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef/level_5"));

    public static final VillagerProfession CHEF_VALUE = new VillagerProfession(Component.translatable("entity.minecraft.villager.chef"),
            poi -> poi.is(ModPoi.STOVE_KEY),
            poi -> poi.is(ModPoi.STOVE_KEY),
            ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_BUTCHER,
            Int2ObjectMap.ofEntries(
                    Int2ObjectMap.entry(1, CHEF_LEVEL_1),
                    Int2ObjectMap.entry(2, CHEF_LEVEL_2),
                    Int2ObjectMap.entry(3, CHEF_LEVEL_3),
                    Int2ObjectMap.entry(4, CHEF_LEVEL_4),
                    Int2ObjectMap.entry(5, CHEF_LEVEL_5)
            ));

    public static final ResourceKey<VillagerProfession> CHEF = ResourceKey.create(Registries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef"));

    public static void registerVillagerProfessions() {
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef"), CHEF_VALUE);
    }
}
