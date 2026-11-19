package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class KitchenShelfItemStorage extends SingleStackStorage {
    private final KitchenShelfItemHandler inventory;
    private final Runnable onCommit;

    public KitchenShelfItemStorage(KitchenShelfItemHandler inventory, Runnable onCommit) {
        this.inventory = inventory;
        this.onCommit = onCommit;
    }

    @Override
    protected @NonNull ItemStack getStack() {
        return inventory.getStackInSlot(0);
    }

    @Override
    protected void setStack(@NonNull ItemStack stack) {
        inventory.setTransferStack(stack);
    }

    @Override
    protected boolean canInsert(@NonNull ItemVariant variant) {
        ItemStack stored = getStack();
        return stored.isEmpty() || variant.matches(stored);
    }

    @Override
    protected int getCapacity(@NonNull ItemVariant variant) {
        return KitchenShelfItemHandler.CAPACITY;
    }

    @Override
    protected void onFinalCommit() {
        onCommit.run();
    }
}
