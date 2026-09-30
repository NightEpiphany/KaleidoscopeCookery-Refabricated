package com.github.ysbbbbbb.kaleidoscopecookery.enchantment;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight.KnifeCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenKnifeItem;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class QuickKnifeEnchantment extends Enchantment implements CustomEnchantingTableBehaviorEnchantment {
    public QuickKnifeEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof KitchenKnifeItem;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof KitchenKnifeItem
                || FabricLoader.getInstance().isModLoaded("farmersdelight") && KnifeCompat.isKnife(stack);
    }
}
