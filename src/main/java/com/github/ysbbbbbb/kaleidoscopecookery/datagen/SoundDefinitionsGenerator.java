package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class SoundDefinitionsGenerator extends FabricSoundsProvider {
    public SoundDefinitionsGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(HolderLookup.@NonNull Provider registries, @NonNull SoundExporter exporter) {
        exporter.add(ModSounds.BLOCK_KITCHEN_SHELF, definition(ModSounds.BLOCK_KITCHEN_SHELF, "block.kitchen_shelf")
                .sound(sound("block/kitchen_shelf")));

        SoundTypeBuilder stockpotSound = definition(ModSounds.BLOCK_STOCKPOT, "block.stockpot");
        for (int i = 0; i < 7; i++) {
            stockpotSound.sound(sound("block/stockpot/stockpot_" + i));
        }
        exporter.add(ModSounds.BLOCK_STOCKPOT, stockpotSound);

        exporter.add(ModSounds.BLOCK_PADDY, definition(ModSounds.BLOCK_PADDY, "block.paddy")
                .sound(sound("block/paddy/paddy_0"))
                .sound(sound("block/paddy/paddy_1")));

        exporter.add(ModSounds.ENTITY_FART, definition(ModSounds.ENTITY_FART, "entity.fart")
                .sound(sound("entity/fart/fart_0"))
                .sound(sound("entity/fart/fart_1"))
                .sound(sound("entity/fart/fart_2")));

        exporter.add(ModSounds.BLOCK_MILLSTONE, definition(ModSounds.BLOCK_MILLSTONE, "block.millstone")
                .sound(sound("block/millstone/millstone_0"))
                .sound(sound("block/millstone/millstone_1"))
                .sound(sound("block/millstone/millstone_2"))
                .sound(sound("block/millstone/millstone_3"))
                .sound(sound("block/millstone/millstone_4"))
                .sound(sound("block/millstone/millstone_5")));

        exporter.add(ModSounds.BLOCK_TEAPOT_PROCESSING, definition(ModSounds.BLOCK_TEAPOT_PROCESSING, "block.teapot")
                .sound(sound("block/teapot/processing/teapot_0"))
                .sound(sound("block/teapot/processing/teapot_1"))
                .sound(sound("block/teapot/processing/teapot_2"))
                .sound(sound("block/teapot/processing/teapot_3"))
                .sound(sound("block/teapot/processing/teapot_4")));

        exporter.add(ModSounds.TRASH_CAN, definition(ModSounds.TRASH_CAN, "block.trash_can")
                .sound(sound("block/trash_can")));

        exporter.add(ModSounds.BLOCK_RECIPE_BLOCK, definition(ModSounds.BLOCK_RECIPE_BLOCK, "block.recipe_block")
                .sound(sound("block/recipe_block/recipe_block_0"))
                .sound(sound("block/recipe_block/recipe_block_1"))
                .sound(sound("block/recipe_block/recipe_block_2")));

        exporter.add(ModSounds.ITEM_DOUGH_TRANSFORM, definition(ModSounds.ITEM_DOUGH_TRANSFORM, "item.dough_transform")
                .sound(sound("item/dough_transform")));
    }

    private static SoundTypeBuilder definition(SoundEvent soundEvent, String subtitle) {
        return SoundTypeBuilder.of(soundEvent).subtitle("subtitles.%s.%s".formatted(KaleidoscopeCookery.MOD_ID, subtitle));
    }

    private static SoundTypeBuilder.RegistrationBuilder sound(String name) {
        return SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name));
    }

    @Override
    public @NonNull String getName() {
        return "sound";
    }
}
