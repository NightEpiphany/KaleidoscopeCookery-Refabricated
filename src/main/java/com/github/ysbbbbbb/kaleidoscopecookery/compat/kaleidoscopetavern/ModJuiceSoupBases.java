package com.github.ysbbbbbb.kaleidoscopecookery.compat.kaleidoscopetavern;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopetavern.KaleidoscopeTavern;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import net.minecraft.resources.ResourceLocation;

public final class ModJuiceSoupBases {
    public static final String ID = "kaleidoscope_tavern";
    public static final ResourceLocation GRAPE_BUCKET = id("grape_bucket");
    public static final ResourceLocation ICE_GRAPE_BUCKET = id("ice_grape_bucket");
    public static final ResourceLocation GOLD_GRAPE_BUCKET = id("gold_grape_bucket");
    public static final ResourceLocation GREEN_GRAPE_BUCKET = id("green_grape_bucket");
    public static final ResourceLocation SWEET_BERRIES_BUCKET = id("sweet_berries_bucket");
    public static final ResourceLocation GLOW_BERRIES_BUCKET = id("glow_berries_bucket");

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTavern.MOD_ID, path);
    }

    public static void registerSoupBases() {
        SoupBaseManager.registerFluidSoupBase(GRAPE_BUCKET, ModItems.GRAPE_BUCKET, 0x9420AE);
        SoupBaseManager.registerFluidSoupBase(ICE_GRAPE_BUCKET, ModItems.ICE_GRAPE_BUCKET, 0x249BBC);
        SoupBaseManager.registerFluidSoupBase(GOLD_GRAPE_BUCKET, ModItems.GOLD_GRAPE_BUCKET, 0xD8BE18);
        SoupBaseManager.registerFluidSoupBase(GREEN_GRAPE_BUCKET, ModItems.GREEN_GRAPE_BUCKET, 0x35D818);
        SoupBaseManager.registerFluidSoupBase(SWEET_BERRIES_BUCKET, ModItems.SWEET_BERRIES_BUCKET, 0xD81818);
        SoupBaseManager.registerFluidSoupBase(GLOW_BERRIES_BUCKET, ModItems.GLOW_BERRIES_BUCKET, 0xD8CB18);
    }
}
