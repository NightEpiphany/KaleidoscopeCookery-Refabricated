package com.github.ysbbbbbb.kaleidoscopecookery.compat.yyzsbackpack;

import com.yyz.yyzsbackpack.api.IExtendedInventory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class YyzsBackpackSlotStorage extends SingleStackStorage {
    private final Inventory inventory;
    private final int slot;

    public YyzsBackpackSlotStorage(Inventory inventory, int slot) {
        this.inventory = inventory;
        this.slot = slot;
    }

    @Override
    protected @NonNull ItemStack getStack() {
        return inventory.getItem(slot);
    }

    @Override
    protected void setStack(@NonNull ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    protected boolean canInsert(@NonNull ItemVariant variant) {
        return isEnabled() && variant.getItem().canFitInsideContainerItems()
                && inventory.canPlaceItem(slot, variant.toStack());
    }

    @Override
    protected boolean canExtract(@NonNull ItemVariant variant) {
        return isEnabled();
    }

    @Override
    protected void onFinalCommit() {
        inventory.setChanged();
    }

    private boolean isEnabled() {
        return inventory instanceof IExtendedInventory extendedInventory
                && extendedInventory.yyzsbackpack$isExtraSlotEnabled(slot - inventory.getContainerSize());
    }
}
