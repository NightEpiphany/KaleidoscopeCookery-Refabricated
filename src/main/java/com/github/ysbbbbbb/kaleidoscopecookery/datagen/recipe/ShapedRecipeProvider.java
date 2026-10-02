package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.CommonTags;


public class ShapedRecipeProvider extends ModRecipeProvider {
    public ShapedRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        this.shaped(RecipeCategory.REDSTONE, ModItems.STOVE)
                .pattern("###")
                .pattern("#F#")
                .pattern("###")
                .define('#', CommonTags.Items.COBBLESTONES)
                .define('F', Items.CAMPFIRE)
                .unlockedBy("has_campfire", has(Items.CAMPFIRE))
                .save(consumer, "kaleidoscope_cookery:stove_campfire");

        this.shaped(RecipeCategory.REDSTONE, ModItems.STOVE)
                .pattern("###")
                .pattern("#F#")
                .pattern("###")
                .define('#', CommonTags.Items.COBBLESTONES)
                .define('F', Items.SOUL_CAMPFIRE)
                .unlockedBy("has_soul_campfire", has(Items.SOUL_CAMPFIRE))
                .save(consumer, "kaleidoscope_cookery:stove_soul_campfire");

        this.shaped(RecipeCategory.DECORATIONS, ModItems.FRUIT_BASKET)
                .pattern(" S ")
                .pattern("#C#")
                .pattern("###")
                .define('S', Items.STICK)
                .define('#', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.SCARECROW)
                .pattern(" H ")
                .pattern("SPS")
                .pattern(" # ")
                .define('H', TagMod.STRAW_HAT)
                .define('S', Items.STICK)
                .define('P', Items.PUMPKIN)
                .define('#', TagMod.STRAW_BALE)
                .unlockedBy("has_pumpkin", has(Items.PUMPKIN))
                .save(consumer);

        this.shaped(RecipeCategory.REDSTONE, ModItems.POT)
                .pattern("###")
                .pattern("###")
                .pattern(" # ")
                .define('#', CommonTags.Items.INGOTS_IRON)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.IRON_KITCHEN_KNIFE)
                .pattern("##")
                .pattern("#S")
                .define('#', CommonTags.Items.INGOTS_IRON)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.GOLD_KITCHEN_KNIFE)
                .pattern("##")
                .pattern("#S")
                .define('#', CommonTags.Items.INGOTS_GOLD)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.COPPER_KITCHEN_KNIFE)
                .pattern("##")
                .pattern("#S")
                .define('#', CommonTags.Items.INGOTS_COPPER)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.DIAMOND_KITCHEN_KNIFE)
                .pattern("##")
                .pattern("#S")
                .define('#', CommonTags.Items.GEMS_DIAMOND)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.KITCHEN_SHOVEL)
                .pattern("I  ")
                .pattern(" N ")
                .pattern("  S")
                .define('I', CommonTags.Items.INGOTS_IRON)
                .define('N', CommonTags.Items.NUGGETS_IRON)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.STRAW_HAT.get())
                .pattern(" W ")
                .pattern(" S ")
                .pattern("WWW")
                .define('W', Items.WHEAT)
                .define('S', Items.STRING)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.STRAW_HAT_FLOWER.get())
                .pattern("FFF")
                .pattern("FHF")
                .pattern("FFF")
                .define('F', ItemTags.FLOWERS)
                .define('H', ModItems.STRAW_HAT.get())
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.OIL_BLOCK)
                .pattern("OOO")
                .pattern("OOO")
                .pattern("OOO")
                .define('O', ModItems.OIL)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.CHOPPING_BOARD)
                .pattern("PPP")
                .pattern("PPP")
                .define('P', ItemTags.WOODEN_PRESSURE_PLATES)
                .unlockedBy("has_wood", has(Items.OAK_PLANKS))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.STOCKPOT)
                .pattern("B B")
                .pattern("I I")
                .pattern("III")
                .define('B', Items.BRICK)
                .define('I', CommonTags.Items.INGOTS_IRON)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.STOCKPOT_LID)
                .pattern(" B ")
                .pattern("III")
                .define('B', Items.BRICK)
                .define('I', CommonTags.Items.INGOTS_IRON)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.ENAMEL_BASIN)
                .pattern("O")
                .pattern("I")
                .pattern("B")
                .define('B', Items.BUCKET)
                .define('I', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .define('O', Items.STONE_BUTTON)
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.KITCHENWARE_RACKS)
                .pattern("SSS")
                .pattern("INI")
                .define('S', Items.STICK)
                .define('I', CommonTags.Items.INGOTS_IRON)
                .define('N', CommonTags.Items.NUGGETS_IRON)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.CHILI_RISTRA)
                .pattern("CC")
                .pattern("CC")
                .pattern("CC")
                .define('C', ModItems.RED_CHILI)
                .unlockedBy("has_red_chili", has(ModItems.RED_CHILI))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.STRUNG_MUSHROOMS)
                .pattern("MM")
                .pattern("MM")
                .pattern("MM")
                .define('M', Items.BROWN_MUSHROOM)
                .unlockedBy("has_brown_mushroom", has(Items.BROWN_MUSHROOM))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.STRAW_BLOCK)
                .pattern("RRR")
                .pattern("RRR")
                .pattern("RRR")
                .define('R', ModItems.RICE_PANICLE)
                .unlockedBy("has_rice_panicle", has(ModItems.RICE_PANICLE))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.FARMER_CHEST_PLATE)
                .pattern("I I")
                .pattern("LLL")
                .pattern("LLL")
                .define('I', CommonTags.Items.INGOTS_IRON)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.FARMER_LEGGINGS)
                .pattern("LIL")
                .pattern("L L")
                .pattern("L L")
                .define('I', CommonTags.Items.INGOTS_IRON)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.FARMER_BOOTS)
                .pattern("I I")
                .pattern("L L")
                .define('I', CommonTags.Items.INGOTS_IRON)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.SHAWARMA_SPIT)
                .pattern("ICI")
                .pattern("ICI")
                .define('I', Items.IRON_CHAIN)
                .define('C', Items.CAMPFIRE)
                .unlockedBy("has_campfire", has(Items.CAMPFIRE))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.MILLSTONE)
                .pattern(" F ")
                .pattern("SG ")
                .pattern("TTT")
                .define('F', CommonTags.Items.FENCES_WOODEN)
                .define('S', Items.STICK)
                .define('G', Items.GRINDSTONE)
                .define('T', Items.SMOOTH_STONE)
                .unlockedBy("has_smooth_stone", has(Items.SMOOTH_STONE))
                .save(consumer);

        this.shaped(RecipeCategory.DECORATIONS, ModItems.RECIPE_ITEM)
                .pattern("PP")
                .pattern("PP")
                .define('P', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.STEAMER)
                .pattern("TTT")
                .pattern("BBB")
                .define('T', Items.BAMBOO_TRAPDOOR)
                .define('B', Items.BAMBOO_BLOCK)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.TRANSMUTATION_LUNCH_BAG)
                .pattern(" L ")
                .pattern("LSL")
                .pattern("LLL")
                .define('L', Items.LEATHER)
                .define('S', Items.AMETHYST_SHARD)
                .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.OIL_POT)
                .pattern("P ")
                .pattern("BS")
                .define('P', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .define('B', Items.BUCKET)
                .define('S', Items.STICK)
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(consumer);

        this.shaped(RecipeCategory.TOOLS, ModItems.SICKLE)
                .pattern("AAB")
                .pattern(" CA")
                .pattern("C  ")
                .define('A', Items.FLINT)
                .define('B', Items.STRING)
                .define('C', Items.STICK)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(consumer);

        this.shaped(RecipeCategory.FOOD, ModItems.TEAPOT)
                .pattern(" I ")
                .pattern("IBI")
                .pattern("III")
                .define('I', CommonTags.Items.INGOTS_COPPER)
                .define('B', Items.BUCKET)
                .unlockedBy("has_ingot_copper", has(Items.COPPER_INGOT))
                .save(consumer);

        this.shaped(RecipeCategory.MISC, ModItems.TRASH_CAN)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('I', CommonTags.Items.INGOTS_IRON)
                .define('C', Items.COMPOSTER)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);
    }
}
