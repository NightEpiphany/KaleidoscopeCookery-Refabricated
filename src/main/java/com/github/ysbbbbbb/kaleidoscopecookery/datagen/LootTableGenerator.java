package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable.BlockLootTables;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable.ChestLootTables;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable.GiftLootTables;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableGenerator extends LootTableProvider {
    public LootTableGenerator(net.fabricmc.fabric.api.datagen.v1.FabricPackOutput output,
                              java.util.concurrent.CompletableFuture<HolderLookup.Provider> registries) {
        super(Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ChestLootTables::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(GiftLootTables::new, LootContextParamSets.GIFT)
        ));
    }
}
