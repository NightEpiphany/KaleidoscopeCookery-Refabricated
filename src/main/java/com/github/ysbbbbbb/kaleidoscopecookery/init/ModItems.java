package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "deprecation"})
public final class ModItems {
    // Block items
    public static final Item STOVE = registerItemViaBlock(ModBlocks.STOVE);
    public static final Item POT = registerItemViaBlock(ModBlocks.POT);
    public static final Item STOCKPOT = registerItemViaBlock(ModBlocks.STOCKPOT);
    public static final Item STOCKPOT_LID = registerItem("stockpot_lid", p -> new StockpotLidItem(
            p.durability(245)
            .repairable(Items.IRON_INGOT)
            .equippableUnswappable(EquipmentSlot.OFFHAND)
            .component(
                    DataComponents.BLOCKS_ATTACKS,
                    new BlocksAttacks(
                            0.15F,
                            1.23F,
                            List.of(new BlocksAttacks.DamageReduction(34.2F, Optional.empty(), 0.0F, 1.0F)),
                            new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                            Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                            Optional.of(SoundEvents.SHIELD_BLOCK),
                            Optional.of(SoundEvents.SHIELD_BREAK)
                    )
            )
            .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
    ));
    public static final Item OIL_BLOCK = registerItemViaBlock(ModBlocks.OIL_BLOCK);
    public static final Item CHOPPING_BOARD = registerItemViaBlock(ModBlocks.CHOPPING_BOARD);
    public static final Item ENAMEL_BASIN = registerItemViaBlock(ModBlocks.ENAMEL_BASIN);
    public static final Item KITCHENWARE_RACKS = registerItemViaBlock(ModBlocks.KITCHENWARE_RACKS, (block, properties) -> new WithTooltipsBlockItem(block, properties, "kitchenware_racks"));
    public static final Item CHILI_RISTRA = registerItemViaBlock(ModBlocks.CHILI_RISTRA);
    public static final Item STRUNG_MUSHROOMS = registerItemViaBlock(ModBlocks.STRUNG_MUSHROOMS);
    public static final Item STRAW_BLOCK = registerItemViaBlock(ModBlocks.STRAW_BLOCK);
    public static final Item SHAWARMA_SPIT = registerItemViaBlock(ModBlocks.SHAWARMA_SPIT);
    public static final Item MILLSTONE = registerItemViaBlock(ModBlocks.MILLSTONE);
    public static final Item STEAMER = registerItemViaBlock(ModBlocks.STEAMER, SteamerItem::new);
    public static final Item OIL_POT = registerItemViaBlock(ModBlocks.OIL_POT, OilPotItem::new, new Item.Properties().stacksTo(1));

    // Tea
    public static final Item TEAPOT = registerItemViaBlock(ModBlocks.TEAPOT, TeapotItem::new, new Item.Properties().stacksTo(1));
    public static final Item EMPTY_CUP = registerItemViaBlock(ModBlocks.EMPTY_CUP, EmptyCupItem::new);
    public static final Item BARLEY_TEA = registerItemViaBlock(ModBlocks.BARLEY_TEA, (block, properties) -> new TeacupItem(block, TeacupRegistry.TEACUP_DATA_MAP.get(TeacupRegistry.BARLEY_TEA).getEffects(), properties));
    public static final Item TIEGUANYIN = registerItemViaBlock(ModBlocks.TIEGUANYIN, (block, properties) -> new TeacupItem(block, TeacupRegistry.TEACUP_DATA_MAP.get(TeacupRegistry.TIEGUANYIN).getEffects(), properties));
    public static final Item BILUOCHUN = registerItemViaBlock(ModBlocks.BILUOCHUN, (block, properties) -> new TeacupItem(block, TeacupRegistry.TEACUP_DATA_MAP.get(TeacupRegistry.BILUOCHUN).getEffects(), properties));
    public static final Item OOLONG = registerItemViaBlock(ModBlocks.OOLONG, (block, properties) -> new TeacupItem(block, TeacupRegistry.TEACUP_DATA_MAP.get(TeacupRegistry.OOLONG).getEffects(), properties));
    public static final Item SAKURA_FUBUKI = registerItemViaBlock(ModBlocks.SAKURA_FUBUKI, (block, properties) -> new TeacupItem(block, TeacupRegistry.TEACUP_DATA_MAP.get(TeacupRegistry.SAKURA_FUBUKI).getEffects(), properties));
    public static final Item FLOWER_TEA = registerItemViaBlock(ModBlocks.FLOWER_TEA, (block, properties) -> new TeacupItem(block, TeacupRegistry.TEACUP_DATA_MAP.get(TeacupRegistry.FLOWER_TEA).getEffects(), properties));

    public static final Item TRASH_CAN = registerItemViaBlock(ModBlocks.TRASH_CAN);

    // Feast
    public static final Item COLD_CUT_HAM_SLICES = registerItemViaBlock(ModBlocks.COLD_CUT_HAM_SLICES, (block, properties) -> new LiftBlockItem(block, properties, "cold_cut_ham_slices"), new Item.Properties());

    // Tools
    public static final Item COPPER_KITCHEN_KNIFE = registerItem("copper_kitchen_knife", p -> new KitchenKnifeItem(p, ToolMaterial.COPPER, 2.5F, -2.4F), new Item.Properties());
    public static final Item IRON_KITCHEN_KNIFE = registerItem("iron_kitchen_knife", p -> new KitchenKnifeItem(p, ToolMaterial.IRON, 3.0F, -2.4F), new Item.Properties());
    public static final Item GOLD_KITCHEN_KNIFE = registerItem("gold_kitchen_knife", p -> new KitchenKnifeItem(p, ToolMaterial.GOLD, 3.0F, -2.4F), new Item.Properties());
    public static final Item DIAMOND_KITCHEN_KNIFE = registerItem("diamond_kitchen_knife", p -> new KitchenKnifeItem(p, ToolMaterial.DIAMOND, 3.0F, -2.4F), new Item.Properties());
    public static final Item NETHERITE_KITCHEN_KNIFE = registerItem("netherite_kitchen_knife", p -> new KitchenKnifeItem(p, ToolMaterial.NETHERITE, 3.0F, -2.4F), new Item.Properties());
    public static final Item SICKLE = registerItem("sickle", SickleItem::new, new Item.Properties());

    // Special items
    public static final Item OIL = registerItem("oil", p -> new WithTooltipsItem(p, "oil"));
    public static final Item RECIPE_ITEM = registerItem("recipe_item", p -> new RecipeItem(ModBlocks.RECIPE_BLOCK, p.useItemDescriptionPrefix()));
    public static final Item KITCHEN_SHOVEL = registerItem("kitchen_shovel", KitchenShovelItem::new);
    public static final Item FRUIT_BASKET = registerItemViaBlock(ModBlocks.FRUIT_BASKET, FruitBasketItem::new);
    public static final Item SCARECROW = registerItem("scarecrow", ScarecrowItem::new);
    public static final Item STRAW_HAT = registerItem("straw_hat", p -> new StrawHatItem(false, p));
    public static final Item STRAW_HAT_FLOWER = registerItem("straw_hat_flower", p -> new StrawHatItem(true, p));
    public static final Item FARMER_CHEST_PLATE = registerItem("farmer_chest_plate", Item::new, new Item.Properties().stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.CHESTPLATE));
    public static final Item FARMER_LEGGINGS = registerItem("farmer_leggings", Item::new, new Item.Properties().stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.LEGGINGS));
    public static final Item FARMER_BOOTS = registerItem("farmer_boots", Item::new, new Item.Properties().stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.BOOTS));
    public static final Item TRANSMUTATION_LUNCH_BAG = registerItem("transmutation_lunch_bag", TransmutationLunchBagItem::new);

    // Seeds
    public static final Item TOMATO_SEED = registerItem("tomato_seed", createBlockItemWithCustomItemName(ModBlocks.TOMATO_CROP));
    public static final Item CHILI_SEED = registerItem("chili_seed", createBlockItemWithCustomItemName(ModBlocks.CHILI_CROP));
    public static final Item LETTUCE_SEED = registerItem("lettuce_seed", createBlockItemWithCustomItemName(ModBlocks.LETTUCE_CROP));
    public static final Item RICE_SEED = registerItem("rice", p -> new RiceItem(p.useItemDescriptionPrefix()));
    public static final Item WILD_RICE_SEED = registerItem("wild_rice", p -> new RiceItem(p.useItemDescriptionPrefix()));

    // Cook stools
    public static final Item COOK_STOOL_OAK = registerItemViaBlock(ModBlocks.COOK_STOOL_OAK);
    public static final Item COOK_STOOL_PALE_OAK = registerItemViaBlock(ModBlocks.COOK_STOOL_PALE_OAK);
    public static final Item COOK_STOOL_SPRUCE = registerItemViaBlock(ModBlocks.COOK_STOOL_SPRUCE);
    public static final Item COOK_STOOL_ACACIA = registerItemViaBlock(ModBlocks.COOK_STOOL_ACACIA);
    public static final Item COOK_STOOL_BAMBOO = registerItemViaBlock(ModBlocks.COOK_STOOL_BAMBOO);
    public static final Item COOK_STOOL_BIRCH = registerItemViaBlock(ModBlocks.COOK_STOOL_BIRCH);
    public static final Item COOK_STOOL_CHERRY = registerItemViaBlock(ModBlocks.COOK_STOOL_CHERRY);
    public static final Item COOK_STOOL_CRIMSON = registerItemViaBlock(ModBlocks.COOK_STOOL_CRIMSON);
    public static final Item COOK_STOOL_DARK_OAK = registerItemViaBlock(ModBlocks.COOK_STOOL_DARK_OAK);
    public static final Item COOK_STOOL_JUNGLE = registerItemViaBlock(ModBlocks.COOK_STOOL_JUNGLE);
    public static final Item COOK_STOOL_MANGROVE = registerItemViaBlock(ModBlocks.COOK_STOOL_MANGROVE);
    public static final Item COOK_STOOL_WARPED = registerItemViaBlock(ModBlocks.COOK_STOOL_WARPED);

    // Chairs
    public static final Item CHAIR_OAK = registerItemViaBlock(ModBlocks.CHAIR_OAK);
    public static final Item CHAIR_PALE_OAK = registerItemViaBlock(ModBlocks.CHAIR_PALE_OAK);
    public static final Item CHAIR_SPRUCE = registerItemViaBlock(ModBlocks.CHAIR_SPRUCE);
    public static final Item CHAIR_ACACIA = registerItemViaBlock(ModBlocks.CHAIR_ACACIA);
    public static final Item CHAIR_BAMBOO = registerItemViaBlock(ModBlocks.CHAIR_BAMBOO);
    public static final Item CHAIR_BIRCH = registerItemViaBlock(ModBlocks.CHAIR_BIRCH);
    public static final Item CHAIR_CHERRY = registerItemViaBlock(ModBlocks.CHAIR_CHERRY);
    public static final Item CHAIR_CRIMSON = registerItemViaBlock(ModBlocks.CHAIR_CRIMSON);
    public static final Item CHAIR_DARK_OAK = registerItemViaBlock(ModBlocks.CHAIR_DARK_OAK);
    public static final Item CHAIR_JUNGLE = registerItemViaBlock(ModBlocks.CHAIR_JUNGLE);
    public static final Item CHAIR_MANGROVE = registerItemViaBlock(ModBlocks.CHAIR_MANGROVE);
    public static final Item CHAIR_WARPED = registerItemViaBlock(ModBlocks.CHAIR_WARPED);

    // Tables
    public static final Item TABLE_OAK = registerItemViaBlock(ModBlocks.TABLE_OAK);
    public static final Item TABLE_PALE_OAK = registerItemViaBlock(ModBlocks.TABLE_PALE_OAK);
    public static final Item TABLE_SPRUCE = registerItemViaBlock(ModBlocks.TABLE_SPRUCE);
    public static final Item TABLE_ACACIA = registerItemViaBlock(ModBlocks.TABLE_ACACIA);
    public static final Item TABLE_BAMBOO = registerItemViaBlock(ModBlocks.TABLE_BAMBOO);
    public static final Item TABLE_BIRCH = registerItemViaBlock(ModBlocks.TABLE_BIRCH);
    public static final Item TABLE_CHERRY = registerItemViaBlock(ModBlocks.TABLE_CHERRY);
    public static final Item TABLE_CRIMSON = registerItemViaBlock(ModBlocks.TABLE_CRIMSON);
    public static final Item TABLE_DARK_OAK = registerItemViaBlock(ModBlocks.TABLE_DARK_OAK);
    public static final Item TABLE_JUNGLE = registerItemViaBlock(ModBlocks.TABLE_JUNGLE);
    public static final Item TABLE_MANGROVE = registerItemViaBlock(ModBlocks.TABLE_MANGROVE);
    public static final Item TABLE_WARPED = registerItemViaBlock(ModBlocks.TABLE_WARPED);

    // Food items
    public static final Item TOMATO = registerItem("tomato", p -> new Item(p.food(ModFoods.TOMATO, ModConsumables.TOMATO)));
    public static final Item RED_CHILI = registerItem("red_chili", ChiliItem.RedChiliItem::new);
    public static final Item GREEN_CHILI = registerItem("green_chili", ChiliItem.GreenChiliItem::new);
    public static final Item LETTUCE = registerItem("lettuce", p -> new Item(p.food(ModFoods.LETTUCE, ModConsumables.LETTUCE)));
    public static final Item RICE_PANICLE = registerItem("rice_panicle");
    public static final Item CATERPILLAR = registerItem("caterpillar", p -> new WithTooltipsItem(p.food(ModFoods.CATERPILLAR, ModConsumables.CATERPILLAR), "caterpillar"));
    public static final Item FRIED_EGG = registerItem("fried_egg", p -> new Item(p.food(ModFoods.FRIED_EGG, ModConsumables.FRIED_EGG)));
    public static final Item DONKEY_BURGER = registerItem("donkey_burger", p -> new FoodWithEffectsItem(p, ModFoods.DONKEY_BURGER, ModConsumables.DONKEY_BURGER));
    public static final Item MANTOU = registerItem("mantou", p -> new FoodWithEffectsItem(p, ModFoods.MANTOU, ModConsumables.MANTOU));
    public static final Item BAOZI = registerItem("baozi", p -> new FoodWithEffectsItem(p, ModFoods.BAOZI, ModConsumables.BAOZI));
    public static final Item SAMSA = registerItem("samsa", p -> new FoodWithEffectsItem(p, ModFoods.SAMSA, ModConsumables.SAMSA));
    public static final Item MEAT_PIE = registerItem("meat_pie", p -> new FoodWithEffectsItem(p, ModFoods.MEAT_PIE, ModConsumables.MEAT_PIE));
    public static final Item DUMPLING = registerItem("dumpling", p -> new BowlFoodOnlyItem(p, ModFoods.DUMPLING, ModConsumables.DUMPLING));
    public static final Item RAW_DOUGH = registerItem("raw_dough", RawDoughItem::new);
    public static final Item FLOUR = registerItem("flour", FlourItem::new);
    public static final Item RAW_NOODLES = registerItem("raw_noodles");
    public static final Item STUFFED_DOUGH_FOOD = registerItem("stuffed_dough_food");

    // Bowl foods
    public static final Item COOKED_RICE = registerItem("cooked_rice", p -> new BowlFoodOnlyItem(p, ModFoods.COOKED_RICE, ModConsumables.COOKED_RICE));
    public static final Item SCRAMBLE_EGG_WITH_TOMATOES = registerItem("scramble_egg_with_tomatoes", p -> new BowlFoodOnlyItem(p, ModFoods.SCRAMBLE_EGG_WITH_TOMATOES, ModConsumables.SCRAMBLE_EGG_WITH_TOMATOES));
    public static final Item SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL = registerItem("scramble_egg_with_tomatoes_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL, ModConsumables.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL));
    public static final Item STIR_FRIED_BEEF_OFFAL = registerItem("stir_fried_beef_offal", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_BEEF_OFFAL, ModConsumables.STIR_FRIED_BEEF_OFFAL));
    public static final Item STIR_FRIED_BEEF_OFFAL_RICE_BOWL = registerItem("stir_fried_beef_offal_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_BEEF_OFFAL_RICE_BOWL, ModConsumables.STIR_FRIED_BEEF_OFFAL_RICE_BOWL));
    public static final Item BRAISED_BEEF = registerItem("braised_beef", p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_BEEF, ModConsumables.BRAISED_BEEF));
    public static final Item BRAISED_BEEF_RICE_BOWL = registerItem("braised_beef_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_BEEF_RICE_BOWL, ModConsumables.BRAISED_BEEF_RICE_BOWL));
    public static final Item STIR_FRIED_PORK_WITH_PEPPERS = registerItem("stir_fried_pork_with_peppers", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_PORK_WITH_PEPPERS, ModConsumables.STIR_FRIED_PORK_WITH_PEPPERS));
    public static final Item STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL = registerItem("stir_fried_pork_with_peppers_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL, ModConsumables.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL));
    public static final Item SWEET_AND_SOUR_PORK = registerItem("sweet_and_sour_pork", p -> new BowlFoodOnlyItem(p, ModFoods.SWEET_AND_SOUR_PORK, ModConsumables.SWEET_AND_SOUR_PORK));
    public static final Item SWEET_AND_SOUR_PORK_RICE_BOWL = registerItem("sweet_and_sour_pork_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.SWEET_AND_SOUR_PORK_RICE_BOWL, ModConsumables.SWEET_AND_SOUR_PORK_RICE_BOWL));
    public static final Item COUNTRY_STYLE_MIXED_VEGETABLES = registerItem("country_style_mixed_vegetables", p -> new BowlFoodOnlyItem(p, ModFoods.COUNTRY_STYLE_MIXED_VEGETABLES, ModConsumables.COUNTRY_STYLE_MIXED_VEGETABLES));
    public static final Item FISH_FLAVORED_SHREDDED_PORK = registerItem("fish_flavored_shredded_pork", p -> new BowlFoodOnlyItem(p, ModFoods.FISH_FLAVORED_SHREDDED_PORK, ModConsumables.FISH_FLAVORED_SHREDDED_PORK));
    public static final Item FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL = registerItem("fish_flavored_shredded_pork_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL, ModConsumables.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL));
    public static final Item BRAISED_FISH_RICE_BOWL = registerItem("braised_fish_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_FISH_RICE_BOWL, ModConsumables.BRAISED_FISH_RICE_BOWL));
    public static final Item SPICY_CHICKEN_RICE_BOWL = registerItem("spicy_chicken_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.SPICY_CHICKEN_RICE_BOWL, ModConsumables.SPICY_CHICKEN_RICE_BOWL));
    public static final Item SUSPICIOUS_STIR_FRY_RICE_BOWL = registerItem("suspicious_stir_fry_rice_bowl", p -> new BowlFoodOnlyItem(p, ModFoods.SUSPICIOUS_STIR_FRY_RICE_BOWL, ModConsumables.SUSPICIOUS_STIR_FRY_RICE_BOWL));
    public static final Item EGG_FRIED_RICE = registerItem("egg_fried_rice", p -> new BowlFoodOnlyItem(p, ModFoods.EGG_FRIED_RICE, ModConsumables.EGG_FRIED_RICE));
    public static final Item DELICIOUS_EGG_FRIED_RICE = registerItem("delicious_egg_fried_rice", p -> new BowlFoodOnlyItem(p, ModFoods.DELICIOUS_EGG_FRIED_RICE, ModConsumables.DELICIOUS_EGG_FRIED_RICE));
    public static final Item PORK_BONE_SOUP = registerItem("pork_bone_soup", p -> new BowlFoodOnlyItem(p, ModFoods.PORK_BONE_SOUP, ModConsumables.PORK_BONE_SOUP));
    public static final Item SEAFOOD_MISO_SOUP = registerItem("seafood_miso_soup", p -> new BowlFoodOnlyItem(p, ModFoods.SEAFOOD_MISO_SOUP, ModConsumables.SEAFOOD_MISO_SOUP));
    public static final Item FEARSOME_THICK_SOUP = registerItem("fearsome_thick_soup", p -> new BowlFoodOnlyItem(p, ModFoods.FEARSOME_THICK_SOUP, ModConsumables.FEARSOME_THICK_SOUP));
    public static final Item LAMB_AND_RADISH_SOUP = registerItem("lamb_and_radish_soup", p -> new BowlFoodOnlyItem(p, ModFoods.LAMB_AND_RADISH_SOUP, ModConsumables.LAMB_AND_RADISH_SOUP));
    public static final Item BRAISED_BEEF_WITH_POTATOES = registerItem("braised_beef_with_potatoes", p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_BEEF_WITH_POTATOES, ModConsumables.BRAISED_BEEF_WITH_POTATOES));
    public static final Item WILD_MUSHROOM_RABBIT_SOUP = registerItem("wild_mushroom_rabbit_soup", p -> new BowlFoodOnlyItem(p, ModFoods.WILD_MUSHROOM_RABBIT_SOUP, ModConsumables.WILD_MUSHROOM_RABBIT_SOUP));
    public static final Item TOMATO_BEEF_BRISKET_SOUP = registerItem("tomato_beef_brisket_soup", p -> new BowlFoodOnlyItem(p, ModFoods.TOMATO_BEEF_BRISKET_SOUP, ModConsumables.TOMATO_BEEF_BRISKET_SOUP));
    public static final Item PUFFERFISH_SOUP = registerItem("pufferfish_soup", p -> new BowlFoodOnlyItem(p, ModFoods.PUFFERFISH_SOUP, ModConsumables.PUFFERFISH_SOUP));
    public static final Item BORSCHT = registerItem("borscht", p -> new BowlFoodOnlyItem(p, ModFoods.BORSCHT, ModConsumables.BORSCHT));
    public static final Item BEEF_MEATBALL_SOUP = registerItem("beef_meatball_soup", p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_MEATBALL_SOUP, ModConsumables.BEEF_MEATBALL_SOUP));
    public static final Item CHICKEN_AND_MUSHROOM_STEW = registerItem("chicken_and_mushroom_stew", p -> new BowlFoodOnlyItem(p, ModFoods.CHICKEN_AND_MUSHROOM_STEW, ModConsumables.CHICKEN_AND_MUSHROOM_STEW));
    public static final Item DONKEY_SOUP = registerItem("donkey_soup", p -> new BowlFoodOnlyItem(p, ModFoods.DONKEY_SOUP, ModConsumables.DONKEY_SOUP));
    public static final Item BEEF_NOODLE = registerItem("beef_noodle", p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_NOODLE, ModConsumables.BEEF_NOODLE));
    public static final Item HUI_NOODLE = registerItem("hui_noodle", p -> new BowlFoodOnlyItem(p, ModFoods.HUI_NOODLE, ModConsumables.HUI_NOODLE));
    public static final Item UDON_NOODLE = registerItem("udon_noodle", p -> new BowlFoodOnlyItem(p, ModFoods.UDON_NOODLE, ModConsumables.UDON_NOODLE));

    // Raw and cooked foods
    public static final Item SASHIMI = registerItem("sashimi", p -> new Item(p.food(ModFoods.SASHIMI, ModConsumables.SASHIMI)));
    public static final Item RAW_LAMB_CHOPS = registerItem("raw_lamb_chops", p -> new Item(p.food(ModFoods.RAW_LAMB_CHOPS, ModConsumables.RAW_LAMB_CHOPS)));
    public static final Item RAW_COW_OFFAL = registerItem("raw_cow_offal", p -> new Item(p.food(ModFoods.RAW_COW_OFFAL, ModConsumables.RAW_COW_OFFAL)));
    public static final Item RAW_PORK_BELLY = registerItem("raw_pork_belly", p -> new Item(p.food(ModFoods.RAW_PORK_BELLY, ModConsumables.RAW_PORK_BELLY)));
    public static final Item RAW_DONKEY_MEAT = registerItem("raw_donkey_meat", p -> new Item(p.food(ModFoods.RAW_DONKEY_MEAT, ModConsumables.RAW_DONKEY_MEAT)));
    public static final Item RAW_CUT_SMALL_MEATS = registerItem("raw_cut_small_meats", p -> new Item(p.food(ModFoods.RAW_CUT_SMALL_MEATS, ModConsumables.RAW_CUT_SMALL_MEATS)));
    public static final Item RAW_MEATBALL  = registerItem("raw_meatball", p -> new Item(p.food(ModFoods.RAW_MEATBALL, ModConsumables.RAW_MEATBALL)));
    public static final Item COOKED_LAMB_CHOPS = registerItem("cooked_lamb_chops", p -> new Item(p.food(ModFoods.COOKED_LAMB_CHOPS, ModConsumables.COOKED_LAMB_CHOPS)));
    public static final Item COOKED_COW_OFFAL = registerItem("cooked_cow_offal", p -> new Item(p.food(ModFoods.COOKED_COW_OFFAL, ModConsumables.COOKED_COW_OFFAL)));
    public static final Item COOKED_PORK_BELLY = registerItem("cooked_pork_belly", p -> new Item(p.food(ModFoods.COOKED_PORK_BELLY, ModConsumables.COOKED_PORK_BELLY)));
    public static final Item COOKED_DONKEY_MEAT = registerItem("cooked_donkey_meat", p -> new Item(p.food(ModFoods.COOKED_DONKEY_MEAT, ModConsumables.COOKED_DONKEY_MEAT)));
    public static final Item COOKED_CUT_SMALL_MEATS = registerItem("cooked_cut_small_meats", p -> new Item(p.food(ModFoods.COOKED_CUT_SMALL_MEATS, ModConsumables.COOKED_CUT_SMALL_MEATS)));
    public static final Item COOKED_MEATBALL  = registerItem("cooked_meatball", p -> new Item(p.food(ModFoods.COOKED_MEATBALL, ModConsumables.COOKED_MEATBALL)));

    public static void registerItems() {

    }

    public static Item registerItemViaBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction, Item.Properties properties) {
        return registerItem(
                blockIdToItemId(block.builtInRegistryHolder().key()), properties2 -> biFunction.apply(block, properties2), properties.useBlockDescriptionPrefix()
        );
    }

    private static ResourceKey<Item> blockIdToItemId(ResourceKey<Block> resourceKey) {
        return ResourceKey.create(Registries.ITEM, resourceKey.identifier());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function, Item.Properties properties) {
        Item item = function.apply(properties.setId(resourceKey));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
    }

    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block block) {
        return properties -> new BlockItem(block, properties.useItemDescriptionPrefix());
    }

    public static Item registerItem(String string) {
        return registerItem(PortHelper.createItemId(string), Item::new, new Item.Properties());
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function) {
        return registerItem(PortHelper.createItemId(string), function, new Item.Properties());
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function, Item.Properties properties) {
        return registerItem(PortHelper.createItemId(string), function, properties);
    }


    public static Item registerItemViaBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction) {
        return registerItemViaBlock(block, biFunction, new Item.Properties());
    }

    public static Item registerItemViaBlock(Block block) {
        return registerItemViaBlock(block, BlockItem::new);
    }
}

