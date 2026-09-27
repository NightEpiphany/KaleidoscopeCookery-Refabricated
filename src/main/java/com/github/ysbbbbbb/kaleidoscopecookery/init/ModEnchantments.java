package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.enchantment.QuickKnifeEnchantment;
import com.github.ysbbbbbb.kaleidoscopecookery.enchantment.SweepEnchantment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public interface ModEnchantments {

    Enchantment QUICK_KNIFE = new QuickKnifeEnchantment();
    Enchantment SWEEP = new SweepEnchantment();

    static void registerEnchantments() {
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(KaleidoscopeCookery.MOD_ID, "quick_knife"), QUICK_KNIFE);
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(KaleidoscopeCookery.MOD_ID, "sweep"), SWEEP);
    }
}
