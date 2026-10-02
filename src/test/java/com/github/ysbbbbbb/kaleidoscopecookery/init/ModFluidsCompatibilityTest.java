package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.SharedConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModFluidsCompatibilityTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void milkUsesTheModNamespaceAndRecognizesTheLegacyKey() {
        assertEquals(KaleidoscopeCookery.MOD_ID, ModFluids.MILK_ID.getNamespace());
        assertTrue(ModFluids.isMilkId(ModFluids.MILK_ID));
        assertTrue(ModFluids.isMilkId(ModFluids.VANILLA_MILK_ID));
        assertFalse(ModFluids.isMilkId(Identifier.withDefaultNamespace("water")));
    }
}
