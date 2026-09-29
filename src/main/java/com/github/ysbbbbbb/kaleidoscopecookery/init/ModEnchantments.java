package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

public interface ModEnchantments {
    ResourceKey<Enchantment> QUICK_KNIFE = ResourceKey.create(Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "quick_knife"));
    ResourceKey<Enchantment> SWEEP = ResourceKey.create(Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "sweep"));
}
