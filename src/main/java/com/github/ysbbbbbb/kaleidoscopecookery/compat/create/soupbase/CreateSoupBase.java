package com.github.ysbbbbbb.kaleidoscopecookery.compat.create.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.simibubi.create.AllFluids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class CreateSoupBase {
    public static final String ID = "create";
    public static final ResourceLocation CHOCOLATE = id("chocolate");
    public static final ResourceLocation HONEY = id("honey");

    private static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }

    public static void registerSoupBases() {
        SoupBaseManager.registerFluidSoupBase(CHOCOLATE, BuiltInRegistries.ITEM.get(id("chocolate_bucket")), AllFluids.CHOCOLATE.getSource().getSource(), 0xA04A3C);
        SoupBaseManager.registerFluidSoupBase(HONEY, BuiltInRegistries.ITEM.get(id("honey_bucket")), AllFluids.HONEY.getSource().getSource(), 0xEAAE30);
    }
}
