package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.event.ExtraLootTableDrop;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Decoder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.resources.RegistryLoadTask$PendingRegistration")
public abstract class LootTableRegistryLoadMixin {
    @Inject(method = "loadFromResource", at = @At("RETURN"))
    private static <T> void kaleidoscopeCookery$kaleidoscopeCookery$modifyLootTable(
            Decoder<T> elementDecoder,
            RegistryOps<JsonElement> ops,
            ResourceKey<T> elementKey,
            Resource thunk,
            CallbackInfoReturnable<Either<T, Exception>> cir
    ) {
        elementKey.cast(Registries.LOOT_TABLE).ifPresent(lootTableKey ->
                cir.getReturnValue().ifLeft(value -> {
                    if (value instanceof LootTable lootTable) {
                        ExtraLootTableDrop.modify(lootTableKey, lootTable, ops);
                    }
                })
        );
    }
}
