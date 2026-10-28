package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FlourItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends AbstractCauldronBlock {

    public LayeredCauldronBlockMixin(Properties properties, Map<Item, CauldronInteraction> interactions) {
        super(properties, interactions);
    }

    @Inject(method = "entityInside", at = @At("TAIL"))
    private void kaleidoscopeCookery$entityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof FlourItem && this.isEntityInsideContent(state, pos, entity)) {
            itemEntity.setItem(new ItemStack(ModItems.RAW_DOUGH, itemEntity.getItem().getCount()));
        }
    }
}
