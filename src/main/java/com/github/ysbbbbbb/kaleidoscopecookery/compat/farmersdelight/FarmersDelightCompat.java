package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.Level;

import java.util.List;

public class FarmersDelightCompat {
    public static final String ID = "farmersdelight";
    public static boolean IS_LOADED = false;

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded(ID)) {
            IS_LOADED = true;
            // 注册事件监听器
            CookingPotCompat.registerEvents();
        }
    }

    public static void getTransformRecipeForJei(Level level, List<StockpotRecipe> recipes) {
        if (IS_LOADED) {
            CookingPotCompat.getTransformRecipeForJei(level, recipes);
        }
    }
}
