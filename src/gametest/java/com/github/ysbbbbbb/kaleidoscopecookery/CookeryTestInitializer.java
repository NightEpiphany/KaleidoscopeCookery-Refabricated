package com.github.ysbbbbbb.kaleidoscopecookery;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.gametest.framework.GameTestRegistry;

/** Fabric API 1.20.1 has no test filter; keep this test mod's headless suite scoped to this project. */
public final class CookeryTestInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        if (System.getProperty("fabric-api.gametest") != null) {
            GameTestRegistry.getAllTestFunctions().removeIf(test -> !test.getTestName().startsWith("cookerygametests.")
                    && !test.getTestName().startsWith("cookingpotcompatgametests."));
        }
    }
}
