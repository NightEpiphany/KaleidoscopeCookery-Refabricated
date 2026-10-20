package com.github.ysbbbbbb.kaleidoscopecookery.mixin.accessor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(GiveGiftToHero.class)
public interface GiveGiftToHeroAccessor {
    @Accessor("GIFTS")
    static Map<ResourceKey<VillagerProfession>, ResourceKey<LootTable>> kaleidoscopeCookery$getGifts() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("GIFTS")
    static void kaleidoscopeCookery$setGifts(Map<ResourceKey<VillagerProfession>, ResourceKey<LootTable>> gifts) {
        throw new AssertionError();
    }
}
