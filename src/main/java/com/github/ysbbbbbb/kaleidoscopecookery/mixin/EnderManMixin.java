package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EnderManMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void kaleidoscopeCookery$addTemptRange(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(Attributes.TEMPT_RANGE);
    }
}
