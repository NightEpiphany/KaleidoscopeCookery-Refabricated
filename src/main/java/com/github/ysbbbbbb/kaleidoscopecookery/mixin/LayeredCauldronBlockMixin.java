package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FlourItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends AbstractCauldronBlock {

    public LayeredCauldronBlockMixin(Properties properties, CauldronInteraction.Dispatcher interactions) {
        super(properties, interactions);
    }

    @Unique
    protected boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
        return entity.getY() < pos.getY() + this.getContentHeight(state) && entity.getBoundingBox().maxY > pos.getY() + 0.25;
    }
    
    @Inject(method = "entityInside", at = @At("TAIL"))
    private void kaleidoscopeCookery$entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci) {
        if (!level.isClientSide() && entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof FlourItem && this.isEntityInsideContent(state, pos, entity)) {
            itemEntity.setItem(new ItemStack(ModItems.RAW_DOUGH, itemEntity.getItem().getCount()));
        }
    }

}
