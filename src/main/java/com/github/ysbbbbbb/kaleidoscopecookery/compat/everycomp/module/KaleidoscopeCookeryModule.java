package com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp.module;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.ChairBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.CookStoolBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.mehvahdjukaar.every_compat.api.PaletteStrategies;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.TabAddMode;
import net.mehvahdjukaar.every_compat.modules.EveryCompatModule;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;

public final class KaleidoscopeCookeryModule extends EveryCompatModule {
    public SimpleEntrySet<WoodType, CookStoolBlock> cookStools;
    public SimpleEntrySet<WoodType, ChairBlock> chairs;
    public SimpleEntrySet<WoodType, TableBlock> tables;

    public KaleidoscopeCookeryModule() {
        super(KaleidoscopeCookery.MOD_ID, "kalc", KaleidoscopeCookery.MOD_ID);

        cookStools = SimpleEntrySet.builder(WoodType.class, "", "cook_stool",
                        () -> (CookStoolBlock) ModBlocks.COOK_STOOL_OAK, () -> VanillaWoodTypes.OAK,
                        (w, p) -> new CookStoolBlock(p.sound(w.getSound())))
                .addCustomItem((_, block, p) -> new BlockItem(block, p))
                .includeModelsBlock(Identifier.tryParse("kaleidoscope_cookery:block/cook_stool/oak"))
                .addTexture(Identifier.tryParse("kaleidoscope_cookery:block/cook_stool/oak"), PaletteStrategies.STRIPPED_LOG_SIDE_STANDARD)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagMod.COOK_STOOL, Registries.BLOCK)
                .addTag(TagMod.COOKERY_MOD_ITEMS, Registries.ITEM)
                .setTab(getTab(modRes("cookery_main")))
                .setTabMode(TabAddMode.AFTER_SAME_TYPE)
                .defaultRecipe()
                .build();

        chairs = SimpleEntrySet.builder(WoodType.class, "", "chair",
                        () -> (ChairBlock) ModBlocks.CHAIR_OAK, () -> VanillaWoodTypes.OAK,
                        (w, p) -> new ChairBlock(p.sound(w.getSound())))
                .addCustomItem((_, block, p) -> new BlockItem(block, p))
                .addTile(() -> ModBlocks.CHAIR_BE)
                .includeModelsBlock(Identifier.tryParse("kaleidoscope_cookery:block/chair/oak"))
                .addTexture(Identifier.tryParse("kaleidoscope_cookery:block/chair/oak"), PaletteStrategies.STRIPPED_LOG_SIDE_STANDARD)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagMod.CHAIR, Registries.BLOCK)
                .addTag(TagMod.CAT_LIE_ON_BLOCKS, Registries.BLOCK)
                .addTag(TagMod.COOKERY_MOD_ITEMS, Registries.ITEM)
                .setTab(getTab(modRes("cookery_main")))
                .setTabMode(TabAddMode.AFTER_SAME_TYPE)
                .defaultRecipe()
                .build();

        tables = SimpleEntrySet.builder(WoodType.class, "", "table",
                        () -> (TableBlock) ModBlocks.TABLE_OAK, () -> VanillaWoodTypes.OAK,
                        (w, p) -> new TableBlock(p.sound(w.getSound())))
                .addCustomItem((_, block, p) -> new BlockItem(block, p))
                .addTile(() -> ModBlocks.TABLE_BE)
                .includeModelsBlock(Identifier.tryParse("kaleidoscope_cookery:block/table/oak"))
                .addTexture(Identifier.tryParse("kaleidoscope_cookery:block/table/oak"), PaletteStrategies.STRIPPED_LOG_SIDE_STANDARD)
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registries.BLOCK)
                .addTag(TagMod.TABLE, Registries.BLOCK)
                .addTag(TagMod.COOKERY_MOD_ITEMS, Registries.ITEM)
                .setTab(getTab(modRes("cookery_main")))
                .setTabMode(TabAddMode.AFTER_SAME_TYPE)
                .defaultRecipe()
                .build();

        this.addEntry(cookStools);
        this.addEntry(chairs);
        this.addEntry(tables);
    }
}
