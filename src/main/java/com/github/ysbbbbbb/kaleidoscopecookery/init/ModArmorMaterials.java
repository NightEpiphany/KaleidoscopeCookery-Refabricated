package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.ArmorItem.Type.*;

public final class ModArmorMaterials {
    public static final Holder<ArmorMaterial> FARMER = Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL,
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cookery_farmer"),
            new ArmorMaterial(
                    Map.of(HELMET, 3, CHESTPLATE, 4, LEGGINGS, 5, BOOTS, 2),
                    12,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(Items.LEATHER),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("cookery_farmer"))),
                    0, 0
            )
    );

    public static void registerArmorMaterials() {
        // 注册方法，用于确保类被加载
    }
}
