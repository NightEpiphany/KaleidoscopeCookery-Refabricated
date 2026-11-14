package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IKitchenwareRacks;
import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ServerThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class KitchenwareRacksBlockEntity extends BaseBlockEntity implements IKitchenwareRacks {
    private static final String LEFT_ITEM = "LeftItem";
    private static final String RIGHT_ITEM = "RightItem";

    private ItemStack itemLeft = ItemStack.EMPTY;
    private ItemStack itemRight = ItemStack.EMPTY;

    public KitchenwareRacksBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.KITCHENWARE_RACKS_BE, pPos, pBlockState);
    }

    @Override
    public boolean onClick(LivingEntity user, ItemStack stack, boolean isLeft) {
        ItemStack stackInRacks = isLeft ? itemLeft : itemRight;
        // 取出物品
        if (stack.isEmpty() && !stackInRacks.isEmpty()) {
            ItemUtils.getItemToLivingEntity(user, stackInRacks);
            if (isLeft) {
                itemLeft = ItemStack.EMPTY;
            } else {
                itemRight = ItemStack.EMPTY;
            }
            user.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            this.refresh();
            return true;
        }
        // 放入物品
        if (stack.isDamageableItem() && stackInRacks.isEmpty()) {
            if (isLeft) {
                itemLeft = ItemUtils.separate(stack, user, 1);
            } else {
                itemRight = ItemUtils.separate(stack, user, 1);
            }
            user.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
            this.refresh();
            return true;
        }
        return false;
    }

    @ServerThreadSafe
    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        if (!this.itemLeft.isEmpty())
            valueOutput.storeNullable(LEFT_ITEM, ItemStack.CODEC, this.itemLeft);
        if (!this.itemRight.isEmpty())
            valueOutput.storeNullable(RIGHT_ITEM, ItemStack.CODEC, this.itemRight);
    }

    @ServerThreadSafe
    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.itemLeft = valueInput.read(LEFT_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.itemRight = valueInput.read(RIGHT_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemLeft() {
        return itemLeft;
    }

    @Override
    public ItemStack getItemRight() {
        return itemRight;
    }

    public void setItemLeft(ItemStack itemLeft) {
        this.itemLeft = itemLeft;
    }

    public void setItemRight(ItemStack itemRight) {
        this.itemRight = itemRight;
    }
}
