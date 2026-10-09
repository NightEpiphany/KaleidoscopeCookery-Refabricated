package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.ICustomEatEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @WrapOperation(
            method = "addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Item;getFoodProperties()Lnet/minecraft/world/food/FoodProperties;"
            )
    )
    private FoodProperties kaleidoscope$useStackFoodProperties(
            Item item,
            Operation<FoodProperties> original,
            @Local(argsOnly = true) ItemStack stack
    ) {
        if (item instanceof ICustomEatEffect customEatEffect) {
            return customEatEffect.modifyFoodProperties(stack);
        }
        return original.call(item);
    }

    @Inject(method = "getBlockSpeedFactor()F", at = @At("HEAD"), cancellable = true)
    private void onGetBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(ModEffects.TUNDRA_STRIDER.get())) {
            BlockState blockState = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
            if (blockState.is(TagMod.TUNDRA_STRIDER_SPEED_BLOCKS)) {
                float friction = blockState.getBlock().getFriction();
                cir.setReturnValue(1.1f + Math.max(1 - friction, 0) * 0.5f);
            }
        }
    }
}
