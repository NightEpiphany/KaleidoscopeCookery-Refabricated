package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp.EveryCompatCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.event.ExtraLootTableDrop;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.CommonRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

public final class KaleidoscopeCookery implements ModInitializer {
    public static final String MOD_ID = "kaleidoscope_cookery";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        NeoForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, GeneralConfig.init());
        // 药水效果优先注册
        ModEffects.registerEffects();
        CommonRegistry.init();
        NetworkHandler.init();
        ModArmorMaterials.registerArmorMaterials();
        ModTrigger.init();
        ModFluids.registerFluids();
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ModEntities.registerEntities();
        ModPoi.registerPoiTypes();
        ModVillager.registerVillagerProfessions();
        ModCreativeTabs.registerTabs();
        ModSounds.registerSounds();
        ModParticles.registerParticles();
        ModRecipes.registerRecipes();
        ModLootModifier.registerLootModifiers();
        ModTrades.registerTrades();
        ModSoupBases.registerSoupBases();
        ModDataComponents.registerDataComponents();
        // 注册额外的战利品表事件
        ExtraLootTableDrop.register();
        // 木材兼容
        if (FabricLoader.getInstance().isModLoaded("everycomp")) {
            EveryCompatCompat.init();
        }
    }
}
