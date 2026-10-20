package com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.aglets.arch;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public sealed interface Trinket permits StrawHatTrinketItem {
    static Holder<SoundEvent> getEquipSound(ItemStack stack) {
        if (stack.has(DataComponents.EQUIPPABLE)) {
            return Objects.requireNonNull(stack.get(DataComponents.EQUIPPABLE)).equipSound().orElse(null);
        }
        return SoundEvents.ARMOR_EQUIP_CHAIN;
    }
}
