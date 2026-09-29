package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.client.init.registry.ClientRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class KaleidoscopeCookeryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientRegistry.init();
    }
}
