package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.kaleidoscopetavern.ModJuiceSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public final class ModSoupBases {
    public static final Identifier WATER = Identifier.fromNamespaceAndPath("minecraft", "water");
    public static final Identifier LAVA = Identifier.fromNamespaceAndPath("minecraft", "lava");
    public static final Identifier MILK = ModFluids.MILK_ID;
    public static final Identifier AXOLOTL_BUCKET = Identifier.fromNamespaceAndPath("minecraft", "axolotl_bucket");
    public static final Identifier COD_BUCKET = Identifier.fromNamespaceAndPath("minecraft", "cod_bucket");
    public static final Identifier SALMON_BUCKET = Identifier.fromNamespaceAndPath("minecraft", "salmon_bucket");
    public static final Identifier TROPICAL_FISH_BUCKET = Identifier.fromNamespaceAndPath("minecraft", "tropical_fish_bucket");
    public static final Identifier PUFFERFISH_BUCKET = Identifier.fromNamespaceAndPath("minecraft", "pufferfish_bucket");
    public static final Identifier TADPOLE_BUCKET = Identifier.fromNamespaceAndPath("minecraft", "tadpole_bucket");

    public static void registerSoupBases() {
        SoupBaseManager.registerFluidSoupBase(WATER, Items.WATER_BUCKET, 0x3F76E4);
        SoupBaseManager.registerFluidSoupBase(LAVA, Items.LAVA_BUCKET, 0xFF9838);
        SoupBaseManager.registerFluidSoupBase(
                MILK,
                Items.MILK_BUCKET,
                BuiltInRegistries.FLUID.getValue(MILK),
                0xF4EEE1
        );

        SoupBaseManager.registerMobSoupBase(AXOLOTL_BUCKET, Items.AXOLOTL_BUCKET);
        SoupBaseManager.registerMobSoupBase(COD_BUCKET, Items.COD_BUCKET);
        SoupBaseManager.registerMobSoupBase(SALMON_BUCKET, Items.SALMON_BUCKET);
        SoupBaseManager.registerMobSoupBase(TROPICAL_FISH_BUCKET, Items.TROPICAL_FISH_BUCKET);
        SoupBaseManager.registerMobSoupBase(PUFFERFISH_BUCKET, Items.PUFFERFISH_BUCKET);
        SoupBaseManager.registerMobSoupBase(TADPOLE_BUCKET, Items.TADPOLE_BUCKET);

        if (FabricLoader.getInstance().isModLoaded("kaleidoscope_tavern"))
            ModJuiceSoupBases.registerSoupBases();
    }
}
