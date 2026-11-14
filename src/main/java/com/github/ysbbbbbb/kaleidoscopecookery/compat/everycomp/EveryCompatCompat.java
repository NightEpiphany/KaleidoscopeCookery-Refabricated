package com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp.module.KaleidoscopeCookeryModule;
import net.mehvahdjukaar.every_compat.api.ECPlugin;
import net.mehvahdjukaar.every_compat.api.EveryCompatPlugin;
import net.mehvahdjukaar.every_compat.api.PluginEvent;

@ECPlugin
public class EveryCompatCompat implements EveryCompatPlugin {

    @Override
    public void register(PluginEvent pluginEvent) {
        pluginEvent.addModule(KaleidoscopeCookery.MOD_ID, () -> KaleidoscopeCookeryModule.class);
    }
}
