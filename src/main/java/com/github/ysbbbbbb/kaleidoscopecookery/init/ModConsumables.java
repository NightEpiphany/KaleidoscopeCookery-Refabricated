package com.github.ysbbbbbb.kaleidoscopecookery.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.*;
import static net.minecraft.world.effect.MobEffects.*;

public interface ModConsumables {
    Consumable TEA_EGG = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SULFUR, 60 * 20), 1.0F))
            .build();

    // 腊八粥
    Consumable LABA_CONGEE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1F))
            .build();

    // 粽子
    Consumable ZONGZI = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(REGENERATION, 20 * 20), 1F))
            .build();

    // 番茄
    Consumable TOMATO = Consumables.defaultFood()
            .build();

    // 辣椒
    Consumable CHILI = Consumables.defaultFood()
            .build();

    // 生菜
    Consumable LETTUCE = Consumables.defaultFood()
            .build();

    // 猪儿虫
    Consumable CATERPILLAR = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(NAUSEA, 200), 1F))
            .build();

    // 刺身
    Consumable SASHIMI = Consumables.defaultFood()
            .build();

    // 生羊排
    Consumable RAW_LAMB_CHOPS = Consumables.defaultFood()
            .build();

    // 生切制小肉
    Consumable RAW_CUT_SMALL_MEATS = Consumables.defaultFood()
            .build();

    // 生牛杂
    Consumable RAW_COW_OFFAL = Consumables.defaultFood()
            .build();

    // 生五花肉
    Consumable RAW_PORK_BELLY = Consumables.defaultFood()
            .build();

    // 生驴肉
    Consumable RAW_DONKEY_MEAT = Consumables.defaultFood()
            .build();

    // 生丸子
    Consumable RAW_MEATBALL = Consumables.defaultFood()
            .build();

    // 熟羊排
    Consumable COOKED_LAMB_CHOPS = Consumables.defaultFood()
            .build();

    // 熟切制小肉
    Consumable COOKED_CUT_SMALL_MEATS = Consumables.defaultFood()
            .build();

    // 熟丸子
    Consumable COOKED_MEATBALL = Consumables.defaultFood()
            .build();

    // 熟牛杂
    Consumable COOKED_COW_OFFAL = Consumables.defaultFood()
            .build();

    // 熟五花肉
    Consumable COOKED_PORK_BELLY = Consumables.defaultFood()
            .build();

    // 熟驴肉
    Consumable COOKED_DONKEY_MEAT = Consumables.defaultFood()
            .build();

    // 驴肉火烧
    Consumable DONKEY_BURGER = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SATIATED_SHIELD, 45 * 20), 1.0F))
            .build();

    // 包子
    Consumable BAOZI = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ABSORPTION, 80 * 20), 1.0F))
            .build();

    // 饺子
    Consumable DUMPLING = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 烤包子
    Consumable SAMSA = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(HASTE, 100 * 20, 1), 1.0F))
            .build();

    // 馒头
    Consumable MANTOU = Consumables.defaultFood()
            .consumeSeconds(0.8F)
            .build();

    // 馅饼
    Consumable MEAT_PIE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 牛肉面
    Consumable BEEF_NOODLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(VITALITY, 8 * 60 * 20), 1.0F))
            .build();

    // 烩面
    Consumable HUI_NOODLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(VITALITY, 8 * 60 * 20), 1.0F))
            .build();

    // 乌冬面
    Consumable UDON_NOODLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(VITALITY, 8 * 60 * 20), 1.0F))
            .build();

    // 热干面
    Consumable HOT_DRY_NOODLES = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(VITALITY, 8 * 60 * 20), 1F))
            .build();

    // 煎蛋
    Consumable FRIED_EGG = Consumables.defaultFood()
            .build();

    // 黑暗料理
    Consumable DARK_CUISINE_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(
                    List.of(
                        new MobEffectInstance(BLINDNESS, 300),
                        new MobEffectInstance(POISON, 100)), 0.33F)
            )
            .build();

    Consumable DARK_CUISINE_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(
                    List.of(
                            new MobEffectInstance(BLINDNESS, 300),
                            new MobEffectInstance(POISON, 100)), 0.33F)
            )
            .build();

    // 迷之炒菜
    Consumable SUSPICIOUS_STIR_FRY_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SPEED, 1200),
                                    new MobEffectInstance(JUMP_BOOST, 1200),
                                    new MobEffectInstance(HASTE, 1200),
                                    new MobEffectInstance(LUCK, 1200),
                                    new MobEffectInstance(MINING_FATIGUE, 1200),
                                    new MobEffectInstance(NAUSEA, 400)), 0.15F
                    )

            )
            .build();

    Consumable SUSPICIOUS_STIR_FRY_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SPEED, 1200),
                                    new MobEffectInstance(JUMP_BOOST, 1200),
                                    new MobEffectInstance(HASTE, 1200),
                                    new MobEffectInstance(LUCK, 1200),
                                    new MobEffectInstance(MINING_FATIGUE, 1200),
                                    new MobEffectInstance(NAUSEA, 400)), 0.15F
                    )

            )
            .build();

    // 粘液饭
    Consumable SLIME_BALL_MEAL_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(JUMP_BOOST, 1000), 1))
            .build();

    Consumable SLIME_BALL_MEAL_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(JUMP_BOOST, 1000), 1))
            .build();

    // 翻糖派
    Consumable FONDANT_PIE_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(LUCK, 2800),
                                    new MobEffectInstance(REGENERATION, 200)), 1.0F
                    )
            )
            .build();

    Consumable FONDANT_PIE_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(LUCK, 2800),
                                    new MobEffectInstance(REGENERATION, 200)), 1.0F
                    )
            )
            .build();

    // 东坡肉
    Consumable DONGPO_PORK_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WARMTH, 800)), 1.0F
                    )
            )
            .build();

    Consumable DONGPO_PORK_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WARMTH, 800)), 1.0F
                    )
            )
            .build();

    // 翻糖蛛眼
    Consumable FONDANT_SPIDER_EYE_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SULFUR, 1000), 1.0F))
            .build();

    Consumable FONDANT_SPIDER_EYE_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SULFUR, 1000), 1.0F))
            .build();

    // 荷包紫颂烧
    Consumable CHORUS_FRIED_EGG_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 400),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    Consumable CHORUS_FRIED_EGG_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 400),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    // 红烧鱼
    Consumable BRAISED_FISH_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WATER_BREATHING, 3600)), 1.0F
                    )
            )
            .build();

    Consumable BRAISED_FISH_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WATER_BREATHING, 3600)), 1.0F
                    )
            )
            .build();

    // 黄金沙拉
    Consumable GOLDEN_SALAD_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(RESISTANCE, 2000),
                                    new MobEffectInstance(REGENERATION, 200)), 1.0F
                    )
            )
            .build();

    Consumable GOLDEN_SALAD_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(RESISTANCE, 2000),
                                    new MobEffectInstance(REGENERATION, 200)), 1.0F
                    )
            )
            .build();

    // 辣子鸡
    Consumable SPICY_CHICKEN_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FIRE_RESISTANCE, 1600),
                                    new MobEffectInstance(REGENERATION, 2000)), 1.0F
                    )
            )
            .build();

    Consumable SPICY_CHICKEN_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FIRE_RESISTANCE, 1600),
                                    new MobEffectInstance(REGENERATION, 2000)), 1.0F
                    )
            )
            .build();

    // 烧鸟串
    Consumable YAKITORI_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WARMTH, 800)), 1.0F
                    )
            )
            .build();

    Consumable YAKITORI_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WARMTH, 800)), 1.0F
                    )
            )
            .build();

    // 水晶羊排
    Consumable CRYSTAL_LAMB_CHOP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(HASTE, 1200), 1.0F))
            .build();

    Consumable CRYSTAL_LAMB_CHOP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(HASTE, 1200), 1.0F))
            .build();

    // 下界风味刺身
    Consumable NETHER_STYLE_SASHIMI_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FIRE_RESISTANCE, 1600),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    Consumable NETHER_STYLE_SASHIMI_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FIRE_RESISTANCE, 1600),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    // 香煎骑士牛排
    Consumable PAN_SEARED_KNIGHT_STEAK_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WARMTH, 800)), 1.0F
                    )
            )
            .build();

    Consumable PAN_SEARED_KNIGHT_STEAK_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(WARMTH, 800)), 1.0F
                    )
            )
            .build();

    // 仰望星空派
    Consumable STARGAZY_PIE_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 600),
                                    new MobEffectInstance(UNLUCK, 3600)), 1.0F
                    )
            )
            .build();

    Consumable STARGAZY_PIE_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 600),
                                    new MobEffectInstance(UNLUCK, 3600)), 1.0F
                    )
            )
            .build();

    // 珍珠咕噜肉
    Consumable SWEET_AND_SOUR_ENDER_PEARLS_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 400),
                                    new MobEffectInstance(SLOW_FALLING, 600)), 1.0F
                    )
            )
            .build();

    Consumable SWEET_AND_SOUR_ENDER_PEARLS_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 400),
                                    new MobEffectInstance(SLOW_FALLING, 600)), 1.0F
                    )
            )
            .build();

    // 烈焰羊排
    Consumable BLAZE_LAMB_CHOP_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FIRE_RESISTANCE, 1600),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    Consumable BLAZE_LAMB_CHOP_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FIRE_RESISTANCE, 1600),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    // 凛冬羊排
    Consumable FROST_LAMB_CHOP_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(TUNDRA_STRIDER, 900),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    Consumable FROST_LAMB_CHOP_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(TUNDRA_STRIDER, 900),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    // 末地风味刺身
    Consumable END_STYLE_SASHIMI_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SLOW_FALLING, 600),
                                    new MobEffectInstance(ABSORPTION, 800)), 1.0F
                    )
            )
            .build();

    Consumable END_STYLE_SASHIMI_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SLOW_FALLING, 600),
                                    new MobEffectInstance(ABSORPTION, 800)), 1.0F
                    )
            )
            .build();

    // 沙漠风味刺身
    Consumable DESERT_STYLE_SASHIMI_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(WARMTH, 800),
                                    new MobEffectInstance(MUSTARD, 1600)), 1.0F
                    )
            )
            .build();

    Consumable DESERT_STYLE_SASHIMI_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(WARMTH, 800),
                                    new MobEffectInstance(MUSTARD, 1600)), 1.0F
                    )
            )
            .build();

    // 苔原风味刺身
    Consumable TUNDRA_STYLE_SASHIMI_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(VIGOR, 900),
                                    new MobEffectInstance(PRESERVATION, 2400)), 1.0F
                    )
            )
            .build();

    Consumable TUNDRA_STYLE_SASHIMI_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(VIGOR, 900),
                                    new MobEffectInstance(PRESERVATION, 2400)), 1.0F
                    )
            )
            .build();

    // 寒带风味刺身
    Consumable COLD_STYLE_SASHIMI_BLOCK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(TUNDRA_STRIDER, 900),
                                    new MobEffectInstance(VIGOR, 900)), 1.0F
                    )
            )
            .build();

    Consumable COLD_STYLE_SASHIMI_ITEM = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(TUNDRA_STRIDER, 900),
                                    new MobEffectInstance(VIGOR, 900)), 1.0F
                    )
            )
            .build();

    // 水煎包
    Consumable SHENGJIAN_MANTOU_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable SHENGJIAN_MANTOU_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 6000), 1.0F))
            .build();

    // 番茄炒蛋
    Consumable SCRAMBLE_EGG_WITH_TOMATOES = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(VIGOR, 1400),
                                    new MobEffectInstance(WARMTH, 900)), 1.0F
                    )
            )
            .build();

    // 爆炒牛杂
    Consumable STIR_FRIED_BEEF_OFFAL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 600),
                                    new MobEffectInstance(WARMTH, 1200)), 1.0F
                    )
            )
            .build();

    // 红烧牛肉
    Consumable BRAISED_BEEF = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 1000),
                                    new MobEffectInstance(SATIATED_SHIELD, 1600)), 1.0F
                    )
            )
            .build();

    // 青椒炒肉
    Consumable STIR_FRIED_PORK_WITH_PEPPERS = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(MUSTARD, 2400),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    // 糖醋里脊
    Consumable SWEET_AND_SOUR_PORK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(LUCK, 4200)), 1.0F
                    )
            )
            .build();

    // 鱼香肉丝
    Consumable FISH_FLAVORED_SHREDDED_PORK = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 2400),
                                    new MobEffectInstance(WARMTH, 900)), 1.0F
                    )
            )
            .build();

    // 田园杂蔬
    Consumable COUNTRY_STYLE_MIXED_VEGETABLES = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(PRESERVATION, 4400),
                                    new MobEffectInstance(VIGOR, 900)), 1.0F
                    )
            )
            .build();

    // 米饭
    Consumable COOKED_RICE = Consumables.defaultFood()
            .build();

    // 蛋炒饭
    Consumable EGG_FRIED_RICE = Consumables.defaultFood()
            .build();

    // 美味蛋炒饭
    Consumable DELICIOUS_EGG_FRIED_RICE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 900), 1.0F))
            .build();

    // 谜之炒菜盖饭
    Consumable SUSPICIOUS_STIR_FRY_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SPEED, 1200),
                                    new MobEffectInstance(HASTE, 1200),
                                    new MobEffectInstance(LUCK, 1200),
                                    new MobEffectInstance(MINING_FATIGUE, 1200),
                                    new MobEffectInstance(NAUSEA, 1200),
                                    new MobEffectInstance(JUMP_BOOST, 1200)), 0.15F
                    )
            )
            .build();

    // 番茄炒蛋盖饭
    Consumable SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(VIGOR, 1400),
                                    new MobEffectInstance(WARMTH, 900)), 1.0F
                    )
            )
            .build();

    // 爆炒牛杂盖饭
    Consumable STIR_FRIED_BEEF_OFFAL_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 600),
                                    new MobEffectInstance(WARMTH, 1200)), 1.0F
                    )
            )
            .build();

    // 红烧牛肉盖饭
    Consumable BRAISED_BEEF_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FLATULENCE, 1000),
                                    new MobEffectInstance(SATIATED_SHIELD, 1600)), 1.0F
                    )
            )
            .build();

    // 青椒炒肉盖饭
    Consumable STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(MUSTARD, 2400),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    // 糖醋里脊盖饭
    Consumable SWEET_AND_SOUR_PORK_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 1600),
                                    new MobEffectInstance(LUCK, 4200)), 1.0F
                    )
            )
            .build();

    // 鱼香肉丝盖饭
    Consumable FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SATIATED_SHIELD, 2400),
                                    new MobEffectInstance(WARMTH, 900)), 1.0F
                    )
            )
            .build();

    // 红烧鱼盖饭
    Consumable BRAISED_FISH_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(WATER_BREATHING, 3600),
                                    new MobEffectInstance(SATIATED_SHIELD, 1600)), 1.0F
                    )
            )
            .build();

    // 辣子鸡盖饭
    Consumable SPICY_CHICKEN_RICE_BOWL = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(FIRE_RESISTANCE, 1600),
                                    new MobEffectInstance(RESISTANCE, 2000)), 1.0F
                    )
            )
            .build();

    // 大骨汤
    Consumable PORK_BONE_SOUP = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(VIGOR, 3600), 1.0F))
            .build();

    // 海鲜味噌汤
    Consumable SEAFOOD_MISO_SOUP = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(WATER_BREATHING, 3600),
                                    new MobEffectInstance(DOLPHINS_GRACE, 800)), 1.0F
                    )
            )
            .build();

    // 恐惧浓汤
    Consumable FEARSOME_THICK_SOUP = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(SULFUR, 9600),
                                    new MobEffectInstance(MUSTARD, 1600)), 1.0F
                    )
            )
            .build();

    // 萝卜羊肉汤
    Consumable LAMB_AND_RADISH_SOUP = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(TUNDRA_STRIDER, 3200), 1.0F))
            .build();

    // 土豆炖牛肉
    Consumable BRAISED_BEEF_WITH_POTATOES = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5400), 1.0F))
            .build();

    // 野菌兔肉汤
    Consumable WILD_MUSHROOM_RABBIT_SOUP = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SPEED, 600), 1.0F))
            .build();

    // 番茄牛腩汤
    Consumable TOMATO_BEEF_BRISKET_SOUP = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SATIATED_SHIELD, 3600), 1.0F))
            .build();

    // 河豚汤
    Consumable PUFFERFISH_SOUP = Consumables.defaultFood()
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(POISON, 300),
                                    new MobEffectInstance(MUSTARD, 3600)), 1.0F
                    )
            )
            .build();

    // 罗宋汤
    Consumable BORSCHT = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FLATULENCE, 3000), 1.0F))
            .build();

    // 牛丸汤
    Consumable BEEF_MEATBALL_SOUP = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(PRESERVATION, 3600), 1.0F))
            .build();

    // 小鸡炖蘑菇
    Consumable CHICKEN_AND_MUSHROOM_STEW = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5400), 1.0F))
            .build();

    // 驴肉汤
    Consumable DONKEY_SOUP = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 8 * 60 * 20), 1.0F))
            .build();

    // 1.2.0 新增

    // 拔丝土豆
    Consumable CANDIED_POTATO_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable CANDIED_POTATO_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 疙瘩汤
    Consumable DOUGH_DROP_SOUP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable DOUGH_DROP_SOUP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 虎皮青椒酿肉
    Consumable STUFFED_TIGER_SKIN_PEPPER_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable STUFFED_TIGER_SKIN_PEPPER_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 麻辣兔头
    Consumable SPICY_RABBIT_HEAD_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable SPICY_RABBIT_HEAD_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 四喜丸子汤
    Consumable FOUR_JOY_MEATBALL_SOUP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable FOUR_JOY_MEATBALL_SOUP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 椒麻鸡
    Consumable NUMBING_SPICY_CHICKEN_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F))
            .build();

    Consumable NUMBING_SPICY_CHICKEN_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F))
            .build();

    // 油炸猪儿虫
    Consumable FRIED_CATERPILLAR_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FLATULENCE, 10 * 20), 1.0F))
            .build();

    Consumable FRIED_CATERPILLAR_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FLATULENCE, 10 * 20), 1.0F))
            .build();

    // 炸春卷
    Consumable FRIED_SPRING_ROLL_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable FRIED_SPRING_ROLL_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 毛血旺
    Consumable SPICY_BLOOD_STEW_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 80 * 20), 1.0F))
            .build();

    Consumable SPICY_BLOOD_STEW_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 80 * 20), 1.0F))
            .build();

    // 水果拼盘
    Consumable FRUIT_PLATTER_BLOCK = Consumables.defaultFood()
            .build();

    Consumable FRUIT_PLATTER_ITEM = Consumables.defaultFood()
            .build();

    // 棕色蘑菇瓦罐汤
    Consumable BROWN_MUSHROOM_POT_SOUP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F))
            .build();

    Consumable BROWN_MUSHROOM_POT_SOUP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F))
            .build();

    // 棕色蘑菇瓦罐汤
    Consumable SHELF_MUSHROOM_POT_SOUP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F))
            .build();

    Consumable SHELF_MUSHROOM_POT_SOUP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F))
            .build();

    // 红色蘑菇瓦罐汤
    Consumable RED_MUSHROOM_POT_SOUP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F))
            .build();

    Consumable RED_MUSHROOM_POT_SOUP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F))
            .build();

    // 诡异菌瓦罐汤
    Consumable WARPED_FUNGUS_POT_SOUP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F))
            .build();

    Consumable WARPED_FUNGUS_POT_SOUP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F))
            .build();

    // 绯红菌瓦罐汤
    Consumable CRIMSON_FUNGUS_POT_SOUP_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F))
            .build();

    Consumable CRIMSON_FUNGUS_POT_SOUP_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F))
            .build();

    // 佛跳墙
    Consumable BUDDHA_JUMPS_OVER_THE_WALL_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SATIATED_SHIELD, 3 * 60 * 20), 1.0F))
            .build();

    Consumable BUDDHA_JUMPS_OVER_THE_WALL_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SATIATED_SHIELD, 3 * 60 * 20), 1.0F))
            .build();

    // 红烧排骨
    Consumable BRAISED_PORK_RIBS_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    Consumable BRAISED_PORK_RIBS_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();


    // 冷肉炙
    Consumable COLD_ROASTED_MEAT_BLOCK = Consumables.defaultFood()
            .build();

    Consumable COLD_ROASTED_MEAT_ITEM = Consumables.defaultFood()
            .build();

    // 油泼鱼
    Consumable OIL_SPLASHED_FISH_BLOCK = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();


    Consumable OIL_SPLASHED_FISH_ITEM = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F))
            .build();

    // 冷切火腿片，只有方块形态才能进食
    Consumable COLD_CUT_HAM_SLICES_BLOCK = Consumables.defaultFood()
            .build();

    // 瓦罐奶茶，饮用音效与 1.21.1 保持一致
    Consumable CLAY_POT_MILK_TEA = Consumables.defaultDrink()
            .sound(SoundEvents.HONEY_DRINK)
            .build();
}
