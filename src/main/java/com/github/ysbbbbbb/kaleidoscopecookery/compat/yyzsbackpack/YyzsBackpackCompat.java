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

        // 背包槽位从原版容器末尾开始，但不会计入 getContainerSize()。
        int firstExtraSlot = inventory.getContainerSize();
        int totalSlots = extendedInventory.yyzsbackpack$getTotalSize();
        for (int slot = firstExtraSlot; slot < totalSlots; slot++) {
            if (extendedInventory.yyzsbackpack$isExtraSlotEnabled(slot - firstExtraSlot)) {
                slots.add(new YyzsBackpackSlotStorage(inventory, slot));
            }
        }
    }
}
