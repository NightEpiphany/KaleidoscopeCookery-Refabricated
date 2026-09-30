package com.github.ysbbbbbb.kaleidoscopecookery.compat.create.ponder.init;

import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.MarkedNonStable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@MarkedNonStable
public class PonderCompat {
    public static final String ID = "create";

    public static boolean PONDER_LOADED = false;

    @Environment(EnvType.CLIENT)
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded(ID)) {
            PONDER_LOADED = true;
            KitchenPonderPlugin.init();
        }
    }
}
