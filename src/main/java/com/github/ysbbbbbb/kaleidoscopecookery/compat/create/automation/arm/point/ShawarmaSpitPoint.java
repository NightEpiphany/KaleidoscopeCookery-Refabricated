package com.github.ysbbbbbb.kaleidoscopecookery.compat.create.automation.arm.point;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import com.zurrtum.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.zurrtum.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.zurrtum.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class ShawarmaSpitPoint extends ArmInteractionPoint {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public ShawarmaSpitPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public int getSlotCount(@NonNull ArmBlockEntity armBlockEntity) {
        return 1;
    }

    @Override
    public @NonNull ItemStack insert(@NonNull ArmBlockEntity armBlockEntity, @NonNull ItemStack stack, boolean simulate) {
        BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
        if (blockEntity instanceof ShawarmaSpitBlockEntity spit) {
            if (spit.cookingItem.isEmpty() && spit.cookedItem.isEmpty()) {
                SingleRecipeInput singleRecipeInput = new SingleRecipeInput(stack);
                if (level instanceof ServerLevel serverLevel && quickCheck.getRecipeFor(singleRecipeInput, serverLevel).isPresent()) {
                    ItemStack remainder = stack.copy();
                    ItemStack toInsert = remainder.split(8);
                    if (!simulate) {
                        spit.onPutCookingItem(level, null, toInsert);
                    }
                    return remainder;
                }
            }
        }

        return stack;
    }

    @Override
    public @NonNull ItemStack extract(@NonNull ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
        if (blockEntity instanceof ShawarmaSpitBlockEntity spit) {
            if (spit.cookTime <= 0 && !spit.cookedItem.isEmpty()) {
                ItemStack remainder = spit.cookedItem.copy();
                if (!simulate) {
                    spit.cookingItem = ItemStack.EMPTY;
                    spit.cookedItem = ItemStack.EMPTY;
                    spit.cookTime = 0;
                    spit.refresh();
                }
                return remainder;
            }
        }
        return ItemStack.EMPTY;
    }
}
