package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;


import static com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry.*;

public class ShapelessRecipeProvider extends ModRecipeProvider {
    public ShapelessRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        this.shapeless(RecipeCategory.DECORATIONS, ModItems.RAW_ZONGZI, 1)
                .requires(ModItems.RICE_SEED)
                .requires(Items.LILY_PAD)
                .unlockedBy("has_lily_pad", has(Items.LILY_PAD))
                .save(consumer);

        this.shapeless(RecipeCategory.FOOD, ModItems.RAW_BAMBOO_TUBE_RICE, 1)
                .requires(Items.BAMBOO)
                .requires(TagCommon.GRAIN_RICE)
                .requires(TagCommon.RAW_MEATS)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(consumer);
        this.shapeless(RecipeCategory.DECORATIONS, ModItems.RICE_PANICLE, 9)
                .requires(ModItems.STRAW_BLOCK)
                .unlockedBy("has_rice_panicle", has(ModItems.RICE_PANICLE))
                .save(consumer);

        this.shapeless(RecipeCategory.DECORATIONS, ModItems.OIL, 9)
                .requires(ModItems.OIL_BLOCK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        this.shapeless(RecipeCategory.FOOD, ModItems.CHILI_SEED, 1)
                .requires(ModItems.GREEN_CHILI)
                .unlockedBy("has_chili", has(ModItems.GREEN_CHILI))
                .save(consumer, "chili_seed_from_green_chili");

        this.shapeless(RecipeCategory.FOOD, ModItems.CHILI_SEED, 1)
                .requires(ModItems.RED_CHILI)
                .unlockedBy("has_chili", has(ModItems.RED_CHILI))
                .save(consumer, "chili_seed_from_red_chili");

        this.shapeless(RecipeCategory.FOOD, ModItems.TOMATO_SEED, 1)
                .requires(ModItems.TOMATO)
                .unlockedBy("has_tomato", has(ModItems.TOMATO))
                .save(consumer);

        this.shapeless(RecipeCategory.FOOD, ModItems.STUFFED_DOUGH_FOOD, 1)
                .requires(TagCommon.RAW_MEATS)
                .requires(TagCommon.VEGETABLES)
                .requires(TagCommon.DOUGH)
                .unlockedBy("has_dough", has(TagCommon.DOUGH))
                .save(consumer);

        this.shapeless(RecipeCategory.MISC, ModItems.RECIPE_ITEM, 1)
                .requires(ModItems.RECIPE_ITEM)
                .unlockedBy("has_recipe_item", has(ModItems.RECIPE_ITEM))
                .save(consumer, "reset_recipe_item");

        this.shapeless(RecipeCategory.FOOD, ModItems.RAW_MEATBALL, 1)
                .requires(TagCommon.RAW_MEATS)
                .requires(TagCommon.RAW_MEATS)
                .requires(TagCommon.VEGETABLES)
                .unlockedBy("has_raw_meats", has(TagCommon.RAW_MEATS))
                .save(consumer);

        this.shapeless(RecipeCategory.FOOD, ModItems.EMPTY_CUP, 1)
                .requires(Items.FLOWER_POT)
                .unlockedBy("has_flower_pot", has(Items.FLOWER_POT))
                .save(consumer);

//        this.shapeless(RecipeCategory.FOOD, PlateRegistry.getItem(PlateRegistry.BERRY_PLATTER), 1)
//                .requires(Items.SWEET_BERRIES, 4)
//                .requires(Items.GLOW_BERRIES, 4)
//                .requires(Items.BOWL)
//                .unlockedBy("has_berry_platter", has(Items.SWEET_BERRIES))
//                .save(consumer, modLoc("berry_platter"));
//
//        this.shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.GOLDEN_SALAD), 1)
//                .requires(Items.GOLDEN_APPLE, 2)
//                .requires(Items.GOLDEN_CARROT, 2)
//                .requires(Items.GLISTERING_MELON_SLICE, 2)
//                .requires(Items.BOWL)
//                .unlockedBy("has_golden_apple", has(Items.GOLDEN_APPLE))
//                .save(consumer, modLoc("golden_salad"));
//
//        this.shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.NETHER_STYLE_SASHIMI), 1)
//                .requires(Items.CRIMSON_FUNGUS)
//                .requires(Items.WARPED_FUNGUS)
//                .requires(ModItems.SASHIMI, 4)
//                .requires(Items.BOWL)
//                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
//                .save(consumer, modLoc("nether_style_sashimi"));
//
//        this.shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.DESERT_STYLE_SASHIMI), 1)
//                .requires(Items.CACTUS, 2)
//                .requires(ModItems.SASHIMI, 4)
//                .requires(Items.BOWL)
//                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
//                .save(consumer, modLoc("desert_style_sashimi"));
//
//        this.shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.COLD_STYLE_SASHIMI), 1)
//                .requires(Items.SNOWBALL, 3)
//                .requires(ModItems.SASHIMI, 4)
//                .requires(Items.BOWL)
//                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
//                .save(consumer, modLoc("cold_style_sashimi"));
//
//        this.shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.END_STYLE_SASHIMI), 1)
//                .requires(Items.CHORUS_FRUIT, 3)
//                .requires(ModItems.SASHIMI, 4)
//                .requires(Items.BOWL)
//                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
//                .save(consumer, modLoc("end_style_sashimi"));
//
//        this.shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.TUNDRA_STYLE_SASHIMI), 1)
//                .requires(Ingredient.of(), 2)
//                .requires(ModItems.SASHIMI, 4)
//                .requires(Items.BOWL)
//                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
//                .save(consumer, modLoc("tundra_style_sashimi"));
//
//        this.shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.COLD_ROASTED_MEAT), 1)
//                .requires(Items.COOKED_BEEF, 3)
//                .requires(Items.BOWL)
//                .unlockedBy("has_cooked_beef", has(Items.COOKED_BEEF))
//                .save(consumer, modLoc("cold_roasted_meat"));
//
//        this.shapeless(RecipeCategory.FOOD, ModItems.COLD_CUT_HAM_SLICES, 1)
//                .requires(ModItems.COOKED_PORK_BELLY, 8)
//                .requires(Items.BOWL)
//                .unlockedBy("has_cooked_pork_belly", has(ModItems.COOKED_PORK_BELLY))
//                .save(consumer, modLoc("cold_cut_ham_slices"));

        addRiceBowlRecipe(consumer,
                ModItems.SCRAMBLE_EGG_WITH_TOMATOES,
                ModItems.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL,
                "scramble_egg_with_tomatoes_rice_bowl"
        );

        addRiceBowlRecipe(consumer,
                ModItems.BRAISED_BEEF,
                ModItems.BRAISED_BEEF_RICE_BOWL,
                "braised_beef_rice_bowl"
        );

        addRiceBowlRecipe(consumer,
                ModItems.STIR_FRIED_PORK_WITH_PEPPERS,
                ModItems.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL,
                "stir_fried_pork_with_peppers_rice_bowl"
        );

        addRiceBowlRecipe(consumer,
                ModItems.SWEET_AND_SOUR_PORK,
                ModItems.SWEET_AND_SOUR_PORK_RICE_BOWL,
                "sweet_and_sour_pork_rice_bowl"
        );

        addRiceBowlRecipe(consumer,
                ModItems.FISH_FLAVORED_SHREDDED_PORK,
                ModItems.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL,
                "fish_flavored_shredded_pork_rice_bowl"
        );

        for (int i = 0; i < 8; i++) {
            int count = i + 1;
            String name = "flour_from_" + count + "_wheat";
            this.shapeless(RecipeCategory.FOOD, ModItems.RAW_DOUGH, count)
                    .requires(Items.WATER_BUCKET)
                    .requires(ModItems.FLOUR, count)
                    .unlockedBy("has_wheat", has(Items.WHEAT))
                    .save(consumer, name);
        }

        addPlateRecipe(consumer, ModItems.SHENGJIAN_MANTOU, SHENGJIAN_MANTOU_PLATE);
        addPlateRecipe(consumer, ModItems.BAOZI, BAOZI_PLATE);
        addPlateRecipe(consumer, ModItems.QINGTUAN, QINGTUAN_PLATE);
        addPlateRecipe(consumer, ModItems.STICKY_CANDY, STICKY_CANDY_PLATE);
        addPlateRecipe(consumer, ModItems.STICKY_RICE_CAKE, STICKY_RICE_CAKE_PLATE);
        addPlateRecipe(consumer, ModItems.ZONGZI, ZONGZI_PLATE);
        addPlateRecipe(consumer, ModItems.TOMATO, TOMATO_PLATTER);
        addPlateRecipe(consumer, Items.APPLE, APPLE_PLATTER);
        addPlateRecipe(consumer, Items.MELON_SLICE, WATERMELON_PLATTER);
        addPlateRecipe(consumer, Items.CHORUS_FRUIT, CHORUS_FRUIT_PLATTER);
    }

    private void addRiceBowlRecipe(RecipeOutput consumer, ItemLike dish, Item result, String id) {
        Ingredient ingredient = Ingredient.of(dish);
        //RiceBowlRecipe recipe = new RiceBowlRecipe(CraftingBookCategory.MISC, ingredient, result.getDefaultInstance());
        //consumer.accept(modLoc(id), recipe, null);
    }

    private void addPlateRecipe(RecipeOutput consumer, ItemLike ingredient, Identifier result) {
        Item resultItem = PlateRegistry.getItem(result);
        int count = PlateRegistry.getCount(result);
        this.shapeless(RecipeCategory.FOOD, resultItem, 1)
                .requires(ingredient, count)
                .requires(Items.BOWL)
                .unlockedBy("has_ingredient", has(ingredient))
                .save(consumer);
    }
}
