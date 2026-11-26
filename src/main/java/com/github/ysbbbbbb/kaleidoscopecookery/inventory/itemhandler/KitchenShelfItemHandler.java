package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class KitchenShelfItemHandler extends ItemStackHandler {
    public static final int CAPACITY = 256;
    private static final String ITEM_KEY = "item";
    private static final String COUNT_KEY = "count";

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

    /**
     * 元数据codec存储
     */
    public void save(@NonNull ValueOutput output) {
        ItemStack stack = stacks.getFirst();
        if (!stack.isEmpty()) {
            output.store(ITEM_KEY, ItemStack.CODEC, stack.copyWithCount(1));
            output.putInt(COUNT_KEY, stack.getCount());
        }
    }

    public void load(@NonNull ValueInput input) {
        ItemStack stack = input.read(ITEM_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        int count = Math.clamp(input.getIntOr(COUNT_KEY, 0), 0, CAPACITY);
        stacks.set(0, stack.isEmpty() || count == 0 ? ItemStack.EMPTY : stack.copyWithCount(count));
        this.onLoad();
    }

    public void setTransferStack(ItemStack stack) {
        this.stacks.set(0, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.blockEntity.refresh();
    }
}
