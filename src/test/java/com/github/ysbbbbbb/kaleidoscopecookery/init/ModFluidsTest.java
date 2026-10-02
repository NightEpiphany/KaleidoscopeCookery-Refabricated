package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModFluidsTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void milkUsesTheModNamespaceAndKeepsLegacyIdRecognized() {
        assertEquals(KaleidoscopeCookery.MOD_ID, ModFluids.MILK_ID.getNamespace());
        assertTrue(ModFluids.isMilkId(ModFluids.MILK_ID));
        assertTrue(ModFluids.isMilkId(ModFluids.VANILLA_MILK_ID));
        assertFalse(ModFluids.isMilkId(ResourceLocation.withDefaultNamespace("water")));
    }
}
