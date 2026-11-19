package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class KitchenShelfItemHandler extends ItemStackHandler {
    public static final int CAPACITY = 256;
    private final KitchenShelfBlockEntity blockEntity;

    public KitchenShelfItemHandler(KitchenShelfBlockEntity blockEntity) {
        super(1);
        this.blockEntity = blockEntity;
    }

    @Override
    public int getSlotLimit(int slot) {
        return CAPACITY;
    }

    @Override
    protected int getStackLimit(int slot, ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        ItemStack stack = stacks.getFirst();
        CompoundTag root = new CompoundTag();
        // 样本与数量分开保存，避免物品编解码器限制超大堆叠。
        root.put("item", stack.copyWithCount(1).saveOptional(registries));
        root.putInt("count", stack.getCount());
        return root;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
        int count = Mth.clamp(nbt.getInt("count"), 0, CAPACITY);
        ItemStack stack = ItemStack.parseOptional(registries, nbt.getCompound("item"));
        this.stacks.set(0, stack.copyWithCount(count));
        this.onLoad();
    }

    void setTransferStack(ItemStack stack) {
        // 快照恢复及事务模拟期间不触发同步。
        this.stacks.set(0, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.blockEntity.refresh();
    }
}
