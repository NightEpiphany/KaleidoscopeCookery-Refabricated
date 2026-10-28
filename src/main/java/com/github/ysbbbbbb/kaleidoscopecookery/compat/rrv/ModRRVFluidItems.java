package com.github.ysbbbbbb.kaleidoscopecookery.compat.rrv;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids.ModFluidIds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceKey;

public final class ModRRVFluidItems {

    public static void register() {
        ResourceKey<Item> itemId = ResourceKey.create(Registries.ITEM, ModFluidIds.MILK.identifier());
        if (BuiltInRegistries.ITEM.containsKey(itemId)) {
            return;
        }

        Item item = new FluidItem(
                Blocks.AIR,
                new FluidItem.FluidItemProperties()
                        .fluid(BuiltInRegistries.FLUID.getValue(ModFluids.MILK_ID))
                        .setItemId(itemId)
                        .modelId(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "fluiditem"))
        );
        Registry.register(BuiltInRegistries.ITEM, itemId, item);
    }
}
