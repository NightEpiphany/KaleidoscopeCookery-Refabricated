package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.item.CandiedSkewerItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"
            )
    )
    private int kaleidoscopeCookery$addCandiedSkewerKnockback(LivingEntity attacker, Operation<Integer> original) {
        int knockback = original.call(attacker);
        if (attacker.getMainHandItem().getItem() instanceof CandiedSkewerItem) {
            knockback += (int) attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        }
        return knockback;
    }
}
