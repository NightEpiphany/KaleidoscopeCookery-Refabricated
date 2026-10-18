package com.github.ysbbbbbb.kaleidoscopecookery.mixin.accessor;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(PoiTypes.class)
public interface PoiTypesAccessor {
    @Invoker("register")
    static PoiType kaleidoscopeCookery$registerPoi(final Registry<PoiType> registry, final ResourceKey<PoiType> id, final Set<BlockState> matchingStates, final int maxTickets, final int validRange) {
        throw new AssertionError("Poi register method should not be called.");
    }
}
