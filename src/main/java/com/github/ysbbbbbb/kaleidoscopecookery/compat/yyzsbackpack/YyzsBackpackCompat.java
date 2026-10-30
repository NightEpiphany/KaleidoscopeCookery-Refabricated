package com.github.ysbbbbbb.kaleidoscopecookery.compat.yyzsbackpack;

import com.yyz.yyzsbackpack.api.IExtendedInventory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public final class YyzsBackpackCompat {
    public static final String ID = "yyzsbackpack";
    public static final int MAX_EXTRA_SLOTS = 256;

    public static void addSlots(Inventory inventory, List<SingleSlotStorage<ItemVariant>> slots) {
        if (!(inventory instanceof IExtendedInventory extendedInventory)) {
            return;
        }

        int firstExtraSlot = inventory.getContainerSize();
        for (int extraSlot = 0; extraSlot < MAX_EXTRA_SLOTS; extraSlot++) {
            if (extendedInventory.yyzsbackpack$isExtraSlotEnabled(extraSlot)) {
                slots.add(new YyzsBackpackSlotStorage(inventory, firstExtraSlot + extraSlot));
            }
        }
    }
}
