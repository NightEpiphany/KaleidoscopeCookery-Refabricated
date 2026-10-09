package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.ICustomEatEffect;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin {
    @WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private <T> T getQualityComponent(DataComponentMap components, DataComponentType<? extends T> type, Operation<T> original) {
        return applyQuality(type, original.call(components, type));
    }

    @WrapOperation(method = "getOrDefault", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <T> T getQualityComponentOrDefault(DataComponentMap components, DataComponentType<? extends T> type, T fallback, Operation<T> original) {
        T raw = original.call(components, type, fallback);
        return components.has(type) ? applyQuality(type, raw) : raw;
    }

    @Unique
    @SuppressWarnings("all")
    private <T> T applyQuality(DataComponentType<? extends T> type, T raw) {
        if (raw != null && (type == DataComponents.FOOD || type == DataComponents.CONSUMABLE)
                && (Object) this instanceof ItemStack stack && stack.getItem() instanceof ICustomEatEffect food) {
            return (T) (type == DataComponents.FOOD
                    ? food.modifyFoodProperties(stack) : food.modifyConsumables(stack));
        }
        return raw;
    }
}
