package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModConsumables;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids.ModItemIds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import org.jspecify.annotations.NonNull;

public abstract class ChiliItem extends Item {
    private final int damage;

    public ChiliItem(Properties p, int damage) {
        super(p.food(ModFoods.CHILI, ModConsumables.CHILI));
        this.damage = damage;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, Level level, LivingEntity entity) {
        entity.hurt(level.damageSources().magic(), this.damage);
        return super.finishUsingItem(stack, level, entity);
    }

    public static class GreenChiliItem extends ChiliItem {
        public GreenChiliItem(Properties p) {
            super(p.setId(ModItemIds.GREEN_CHILI).compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM), 1);
        }
    }

    public static class RedChiliItem extends ChiliItem {
        public RedChiliItem(Properties p) {
            super(p.setId(ModItemIds.RED_CHILI).compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM), 2);
        }
    }
}
