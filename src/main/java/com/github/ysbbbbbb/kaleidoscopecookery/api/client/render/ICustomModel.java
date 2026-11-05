package com.github.ysbbbbbb.kaleidoscopecookery.api.client.render;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.world.item.ItemStack;

public interface ICustomModel {
    default ItemStack getBaseModelDisplay(String key) {
        // 一定要是有效的Identifier字符串
        if (!key.contains(":"))
            key = KaleidoscopeCookery.MOD_ID + ":" + key;
        var model = ModItems.MODEL_DISPLAY.getDefaultInstance();
        model.set(ModDataComponents.MODEL_DISPLAY_MODEL, key);
        return model;
    }
}

