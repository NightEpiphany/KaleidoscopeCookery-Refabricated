package com.github.ysbbbbbb.kaleidoscopecookery.compat.yyzsbackpack;

import com.yyz.yyzsbackpack.api.IExtendedInventory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class YyzsBackpackSlotStorage extends SingleStackStorage {
    private final Inventory inventory;
    private final int slot;

    public YyzsBackpackSlotStorage(Inventory inventory, int slot) {
        this.inventory = inventory;
        this.slot = slot;
    }

    @Override
    protected ItemStack getStack() {
        return inventory.getItem(slot);
    }

    public ItemStack getItemStack() {
        return getStack();
    }

    @Override
    protected void setStack(ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    protected boolean canInsert(ItemVariant variant) {
        return isEnabled() && variant.getItem().canFitInsideContainerItems()
                && inventory.canPlaceItem(slot, variant.toStack());
    }

    @Override
    protected boolean canExtract(ItemVariant variant) {
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
