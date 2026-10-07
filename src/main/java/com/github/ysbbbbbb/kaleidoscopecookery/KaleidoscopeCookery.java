package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.ModJeiPlugin;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ConfigGetter;
import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.CommonRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public final class KaleidoscopeCookery implements ModInitializer {

    public static final String MOD_ID = "kaleidoscope_cookery";

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded(ConfigGetter.ID))
            GeneralConfig.init();
        // 药水效果优先注册
        ModEffects.registerEffects();
        ModArmorMaterials.registerArmorMaterials();
        ModTrigger.init();
        TeacupRegistry.init();
        ModFluids.registerFluids();
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ModEntities.registerEntities();
        ModPoi.registerPoiTypes();
        ModVillager.registerVillagerProfessions();
        ModCreativeTabs.registerTabs();
        ModSounds.registerSounds();
        ModBlockSoundSets.init();
        ModParticles.registerParticles();
        ModRecipes.registerRecipes();
        ModLootModifier.registerLootModifiers();
        ModSoupBases.registerSoupBases();
        ModDataComponents.registerDataComponents();
        // 事件
        ModEvents.init();
        CommonRegistry.init();
        NetworkHandler.init();
        // 注册额外的战利品表事件
        if (FabricLoader.getInstance().isModLoaded("jei"))
            ModJeiPlugin.syncRecipes();
    }
}
