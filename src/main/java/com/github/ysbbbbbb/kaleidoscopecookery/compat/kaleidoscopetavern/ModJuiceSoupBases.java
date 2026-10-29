package com.github.ysbbbbbb.kaleidoscopecookery.compat.kaleidoscopetavern;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopetavern.KaleidoscopeTavern;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import net.minecraft.resources.Identifier;

public final class ModJuiceSoupBases {

    public static final Identifier GRAPE_BUCKET = Identifier.fromNamespaceAndPath(KaleidoscopeTavern.MOD_ID, "grape_bucket");
    public static final Identifier ICE_GRAPE_BUCKET = Identifier.fromNamespaceAndPath(KaleidoscopeTavern.MOD_ID, "ice_grape_bucket");
    public static final Identifier GOLD_GRAPE_BUCKET = Identifier.fromNamespaceAndPath(KaleidoscopeTavern.MOD_ID, "gold_grape_bucket");
    public static final Identifier GREEN_GRAPE_BUCKET = Identifier.fromNamespaceAndPath(KaleidoscopeTavern.MOD_ID, "green_grape_bucket");
    public static final Identifier SWEET_BERRIES_BUCKET = Identifier.fromNamespaceAndPath(KaleidoscopeTavern.MOD_ID, "sweet_berries_bucket");
    public static final Identifier GLOW_BERRIES_BUCKET = Identifier.fromNamespaceAndPath(KaleidoscopeTavern.MOD_ID, "glow_berries_bucket");


    public static void registerSoupBases() {
        SoupBaseManager.registerFluidSoupBase(GRAPE_BUCKET, ModItems.GRAPE_BUCKET, 0x9420AE);
        SoupBaseManager.registerFluidSoupBase(ICE_GRAPE_BUCKET, ModItems.ICE_GRAPE_BUCKET, 0x249BBC);
        SoupBaseManager.registerFluidSoupBase(GOLD_GRAPE_BUCKET, ModItems.GOLD_GRAPE_BUCKET, 0xD8BE18);
        SoupBaseManager.registerFluidSoupBase(GREEN_GRAPE_BUCKET, ModItems.GREEN_GRAPE_BUCKET, 0x35D818);
        SoupBaseManager.registerFluidSoupBase(SWEET_BERRIES_BUCKET, ModItems.SWEET_BERRIES_BUCKET, 0xD81818);
        SoupBaseManager.registerFluidSoupBase(GLOW_BERRIES_BUCKET, ModItems.GLOW_BERRIES_BUCKET, 0xD8CB18);
    }
}
