package com.github.ysbbbbbb.kaleidoscopecookery.compat.appleskin;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.food.FoodProperties;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.FoodValuesEvent;

@Environment(EnvType.CLIENT)
public final class AppleSkinCompat implements AppleSkinApi {
    @Override
    public void registerEvents() {
        FoodValuesEvent.EVENT.register(AppleSkinCompat::onFoodValues);
        KaleidoscopeCookery.LOGGER.info("Registered AppleSkin lunch bag food preview");
    }

    private static void onFoodValues(FoodValuesEvent event) {
        if (!event.itemStack.is(ModItems.TRANSMUTATION_LUNCH_BAG)) return;

        FoodProperties food = firstFood(event.itemStack);
        if (food != null) {
            event.modifiedFoodComponent = food;
        }
    }

    private static FoodProperties firstFood(ItemStack bag) {
        var items = TransmutationLunchBagItem.getItems(bag);
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stack = items.getStackInSlot(i);
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food != null && stack.has(DataComponents.CONSUMABLE)) {
                return food;
            }
        }
        return null;
    }
}
