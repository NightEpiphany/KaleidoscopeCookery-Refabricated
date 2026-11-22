package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public final class ModEffects {
    public static final Holder<MobEffect> FLATULENCE = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flatulence"), new FlatulenceEffect(0xFFC6C6));
    public static final Holder<MobEffect> TUNDRA_STRIDER = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tundra_strider"), new BaseEffect(0xA1F8FC));
    public static final Holder<MobEffect> WARMTH = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "warmth"), new WarmthEffect(0xFF5F0E));
    public static final Holder<MobEffect> SATIATED_SHIELD = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "satiated_shield"), new BaseEffect(0xFF1313));
    public static final Holder<MobEffect> VIGOR = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "vigor"), new VigorEffect(0x84C322));
    public static final Holder<MobEffect> SULFUR = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "sulfur"), new SulfurEffect(0xE8B75E));
    public static final Holder<MobEffect> MUSTARD = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "mustard"), new BaseEffect(0x5A6D09));
    public static final Holder<MobEffect> PRESERVATION = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "preservation"), new BaseEffect(0xAEC639));
    public static final Holder<MobEffect> HINDER = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "hinder"), new BaseEffect(0x9E7E5A));
    public static final Holder<MobEffect> PROJECTILE_DODGE = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "projectile_dodge"), new BaseEffect(0x8E27F7));
    public static final Holder<MobEffect> INSTANT_SMELTING = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "instant_smelting"), new BaseEffect(0xF07C1C));
    public static final Holder<MobEffect> VITALITY = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "vitality"), new BaseEffect(0x6A9E4E));
    public static final Holder<MobEffect> END_SOUL = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "end_soul"), new EndSoulEffect(0xB867DE));

    public static void registerEffects() {
        // 效果初始化只在类初始化时加载一次
    }
}
