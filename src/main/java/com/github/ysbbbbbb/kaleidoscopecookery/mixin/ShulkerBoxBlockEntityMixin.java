package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.FruitBasketBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin {
    @ModifyReturnValue(method = "canPlaceItemThroughFace", at = @At("RETURN"))
    private boolean kaleidoscopeCookery$blockFruitBasketInsertion(boolean original, @Local(argsOnly = true, name = "itemStack") ItemStack itemStack) {
        return original && !(Block.byItem(itemStack.getItem()) instanceof FruitBasketBlock);
    }
}
