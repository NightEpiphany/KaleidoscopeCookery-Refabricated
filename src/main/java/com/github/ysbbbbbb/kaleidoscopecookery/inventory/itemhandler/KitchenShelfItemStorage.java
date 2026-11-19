package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.util.forge.ItemHandlerHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public final class KitchenShelfItemStorage extends SingleStackStorage {
    private final KitchenShelfItemHandler inventory;
    private final Runnable onCommit;

    public KitchenShelfItemStorage(KitchenShelfItemHandler inventory, Runnable onCommit) {
        this.inventory = inventory;
        this.onCommit = onCommit;
    }

    @Override
    protected ItemStack getStack() {
        return inventory.getStackInSlot(0);
    }

    @Override
    protected void setStack(ItemStack stack) {
        // 快照存储
        inventory.getStacks().set(0, stack);
    }

    @Override
    protected boolean canInsert(ItemVariant variant) {
        ItemStack stored = getStack();
        return stored.isEmpty() || ItemHandlerHelper.canItemStacksStack(stored, variant.toStack());
    }

    @Override
    protected int getCapacity(ItemVariant variant) {
        return KitchenShelfItemHandler.CAPACITY;
    }

    @Override
    protected void onFinalCommit() {
        onCommit.run();
    }
}
