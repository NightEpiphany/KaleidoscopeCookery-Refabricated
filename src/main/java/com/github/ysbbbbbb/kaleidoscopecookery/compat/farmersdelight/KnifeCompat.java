package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.item.KnifeItem;

public class KnifeCompat {
    public static boolean isKnife(ItemStack stack) {
        return stack.getItem() instanceof KnifeItem;
    }
}
