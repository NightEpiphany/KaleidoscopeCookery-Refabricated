package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids.ModBlockItemIds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids.ModItemIds;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModItems {
    // Block items
    public static final Item STOVE = registerItemViaBlockWithDesc(ModBlockItemIds.STOVE, ModBlocks.STOVE, "stove");
    public static final Item POT = registerItemViaBlockWithDesc(ModBlockItemIds.POT, ModBlocks.POT, "pot");
    public static final Item STOCKPOT = registerItemViaBlockWithDesc(ModBlockItemIds.STOCKPOT, ModBlocks.STOCKPOT, "stockpot");
    public static final Item STOCKPOT_LID = registerItem(ModItemIds.STOCKPOT_LID, p -> new StockpotLidItem(
            p.durability(245)
            .repairable(Items.IRON_INGOT)
            .equippableUnswappable(EquipmentSlot.OFFHAND)
            .delayedComponent(
                    DataComponents.BLOCKS_ATTACKS,
                    ctx -> new BlocksAttacks(
                            0.15F,
                            1.23F,
                            List.of(new BlocksAttacks.DamageReduction(34.2F, Optional.empty(), 0.0F, 1.0F)),
                            new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                            Optional.of(ctx.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                            Optional.of(SoundEvents.SHIELD_BLOCK),
                            Optional.of(SoundEvents.SHIELD_BREAK)
                    )
            )
            .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
    ));
    public static final Item OIL_BLOCK = registerItemViaBlock(ModBlockItemIds.OIL_BLOCK, ModBlocks.OIL_BLOCK, new Item.Properties().cookingFuel(ContextIntProviders.COOKING_TIME_COAL_BLOCK));
    public static final Item CHOPPING_BOARD = registerItemViaBlockWithDesc(ModBlockItemIds.CHOPPING_BOARD, ModBlocks.CHOPPING_BOARD, "chopping_board");
    public static final Item ENAMEL_BASIN = registerItemViaBlockWithDesc(ModBlockItemIds.ENAMEL_BASIN, ModBlocks.ENAMEL_BASIN, "enamel_basin");
    public static final Item KITCHENWARE_RACKS = registerItemViaBlockWithDesc(ModBlockItemIds.KITCHENWARE_RACKS, ModBlocks.KITCHENWARE_RACKS, "kitchenware_racks");
    public static final Item CHILI_RISTRA = registerItemViaBlock(ModBlockItemIds.CHILI_RISTRA, ModBlocks.CHILI_RISTRA);
    public static final Item STRUNG_MUSHROOMS = registerItemViaBlock(ModBlockItemIds.STRUNG_MUSHROOMS, ModBlocks.STRUNG_MUSHROOMS);
    public static final Item STRAW_BLOCK = registerItemViaBlock(ModBlockItemIds.STRAW_BLOCK, ModBlocks.STRAW_BLOCK);
    public static final Item SHAWARMA_SPIT = registerItemViaBlockWithDesc(ModBlockItemIds.SHAWARMA_SPIT, ModBlocks.SHAWARMA_SPIT, "shawarma_spit");
    public static final Item MILLSTONE = registerItemViaBlockWithDesc(ModBlockItemIds.MILLSTONE, ModBlocks.MILLSTONE, "millstone");
    public static final Item STEAMER = registerItemViaBlock(ModBlockItemIds.STEAMER, ModBlocks.STEAMER, SteamerItem::new);
    public static final Item OIL_POT = registerItemViaBlock(ModBlockItemIds.OIL_POT, ModBlocks.OIL_POT, OilPotItem::new);
    public static final Item TRASH_CAN = registerItemViaBlockWithDesc(ModBlockItemIds.TRASH_CAN, ModBlocks.TRASH_CAN, "trash_can");
    public static final Item TEAPOT = registerItemViaBlock(ModBlockItemIds.TEAPOT, ModBlocks.TEAPOT, TeapotItem::new);
    public static final Item EMPTY_CUP = registerItemViaBlock(ModBlockItemIds.EMPTY_CUP, ModBlocks.EMPTY_CUP, EmptyCupItem::new);
    public static final Item BAMBOO_TRAY = registerItemViaBlockWithDesc(ModBlockItemIds.BAMBOO_TRAY, ModBlocks.BAMBOO_TRAY, "bamboo_tray");
    public static final Item TEA_BANNER = registerItemViaBlock(ModBlockItemIds.TEA_BANNER, ModBlocks.TEA_BANNER, TeaBannerItem::new);
    public static final Item LONG_BENCH = registerItemViaBlock(ModBlockItemIds.LONG_BENCH, ModBlocks.LONG_BENCH);
    public static final Item RED_LANTERN = registerItemViaBlock(ModBlockItemIds.RED_LANTERN, ModBlocks.RED_LANTERN);
    public static final Item EIGHT_IMMORTALS_TABLE = registerItemViaBlock(ModBlockItemIds.EIGHT_IMMORTALS_TABLE, ModBlocks.EIGHT_IMMORTALS_TABLE);
    public static final Item CLAY_POT_MILK_TEA = registerItemViaBlock(ModBlockItemIds.CLAY_POT_MILK_TEA, ModBlocks.CLAY_POT_MILK_TEA, ClayPotMilkTeaItem::new);

    // Feast
    public static final Item COLD_CUT_HAM_SLICES = registerItemViaBlock(ModBlockItemIds.COLD_CUT_HAM_SLICES, ModBlocks.COLD_CUT_HAM_SLICES, (block, properties) -> new LiftBlockItem(block, properties, "cold_cut_ham_slices"), new Item.Properties());

    // Tools
    public static final Item COPPER_KITCHEN_KNIFE = registerItem(ModItemIds.COPPER_KITCHEN_KNIFE, p -> new KitchenKnifeItem(p, ToolMaterial.COPPER, 2.5F, -2.4F), new Item.Properties());
    public static final Item IRON_KITCHEN_KNIFE = registerItem(ModItemIds.IRON_KITCHEN_KNIFE, p -> new KitchenKnifeItem(p, ToolMaterial.IRON, 3.0F, -2.4F), new Item.Properties());
    public static final Item GOLD_KITCHEN_KNIFE = registerItem(ModItemIds.GOLD_KITCHEN_KNIFE, p -> new KitchenKnifeItem(p, ToolMaterial.GOLD, 3.0F, -2.3F), new Item.Properties());
    public static final Item DIAMOND_KITCHEN_KNIFE = registerItem(ModItemIds.DIAMOND_KITCHEN_KNIFE, p -> new KitchenKnifeItem(p, ToolMaterial.DIAMOND, 3.0F, -2.5F), new Item.Properties());
    public static final Item NETHERITE_KITCHEN_KNIFE = registerItem(ModItemIds.NETHERITE_KITCHEN_KNIFE, p -> new KitchenKnifeItem(p, ToolMaterial.NETHERITE, 3.0F, -2.5F), new Item.Properties());
    public static final Item SICKLE = registerItem(ModItemIds.SICKLE, SickleItem::new, new Item.Properties());

    // Special items
    public static final Item OIL = registerItem(ModItemIds.OIL, p -> new WithTooltipsItem(p.cookingFuel(ContextIntProviders.COOKING_TIME_COAL), "oil"));
    public static final Item RECIPE_ITEM = registerItem(ModBlockItemIds.RECIPE_ITEM.item(), p -> new RecipeItem(ModBlocks.RECIPE_BLOCK, p.useItemDescriptionPrefix()));
    public static final Item KITCHEN_SHOVEL = registerItem(ModItemIds.KITCHEN_SHOVEL, KitchenShovelItem::new);
    public static final Item FRUIT_BASKET = registerItemViaBlock(ModBlockItemIds.FRUIT_BASKET, ModBlocks.FRUIT_BASKET, FruitBasketItem::new);
    public static final Item SCARECROW = registerItem(ModItemIds.SCARECROW, ScarecrowItem::new);
    public static final Item FARMER_CHEST_PLATE = registerItem(ModItemIds.FARMER_CHEST_PLATE, Item::new, new Item.Properties().stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.CHESTPLATE));
    public static final Item FARMER_LEGGINGS = registerItem(ModItemIds.FARMER_LEGGINGS, Item::new, new Item.Properties().stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.LEGGINGS));
    public static final Item FARMER_BOOTS = registerItem(ModItemIds.FARMER_BOOTS, Item::new, new Item.Properties().stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.BOOTS));
    public static final Item TRANSMUTATION_LUNCH_BAG = registerItem(ModItemIds.TRANSMUTATION_LUNCH_BAG, TransmutationLunchBagItem::new);

    // Hats (mutable)
    public static Supplier<Item> STRAW_HAT = registerStrawHats(ModItemIds.STRAW_HAT, p -> new StrawHatItem(false, p));
    public static Supplier<Item> STRAW_HAT_FLOWER = registerStrawHats(ModItemIds.STRAW_HAT_FLOWER, p -> new StrawHatItem(true, p));

    // Seeds
    public static final Item TOMATO_SEED = registerItem(ModBlockItemIds.TOMATO_SEED.item(), wrapSeedItem(ModBlocks.TOMATO_CROP));
    public static final Item CHILI_SEED = registerItem(ModBlockItemIds.CHILI_SEED.item(), wrapSeedItem(ModBlocks.CHILI_CROP));
    public static final Item LETTUCE_SEED = registerItem(ModBlockItemIds.LETTUCE_SEED.item(), wrapSeedItem(ModBlocks.LETTUCE_CROP));
    public static final Item RICE_SEED = registerItem(ModBlockItemIds.RICE_SEED.item(), p -> new RiceItem(p.useItemDescriptionPrefix().compostable(ContextIntProviders.COMPOSTABLE_LOW)));
    public static final Item WILD_RICE_SEED = registerItem(ModBlockItemIds.WILD_RICE_SEED.item(), wrapSeedItem(ModBlocks.RICE_CROP));
    public static final Item TEA_SEED = registerItem(ModBlockItemIds.TEA_SEED.item(), wrapSeedItem(ModBlocks.TEA_TREE));

    // Tea-stuff
    public static final Item FRESH_TEA_LEAVES = registerItem(ModItemIds.FRESH_TEA_LEAVES, p -> new Item(p.compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM)));
    public static final Item DRIED_TEA_LEAVES = registerItem(ModItemIds.DRIED_TEA_LEAVES, p -> new Item(p.compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM)));
    public static final Item TIEGUANYIN_TEA_BAG = registerItem(ModItemIds.TIEGUANYIN_TEA_BAG);
    public static final Item BILUOCHUN_TEA_BAG = registerItem(ModItemIds.BILUOCHUN_TEA_BAG);
    public static final Item OOLONG_TEA_BAG = registerItem(ModItemIds.OOLONG_TEA_BAG);
    public static final Item SAKURA_FUBUKI_TEA_BAG = registerItem(ModItemIds.SAKURA_FUBUKI_TEA_BAG);
    public static final Item BARLEY_TEA_BAG = registerItem(ModItemIds.BARLEY_TEA_BAG);
    public static final Item BUTTER_TEA_BAG = registerItem(ModItemIds.BUTTER_TEA_BAG);

    // Cook stools
    public static final Item COOK_STOOL_OAK = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_OAK, ModBlocks.COOK_STOOL_OAK);
    public static final Item COOK_STOOL_POPLAR = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_POPLAR, ModBlocks.COOK_STOOL_POPLAR);
    public static final Item COOK_STOOL_PALE_OAK = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_PALE_OAK, ModBlocks.COOK_STOOL_PALE_OAK);
    public static final Item COOK_STOOL_SPRUCE = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_SPRUCE, ModBlocks.COOK_STOOL_SPRUCE);
    public static final Item COOK_STOOL_ACACIA = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_ACACIA, ModBlocks.COOK_STOOL_ACACIA);
    public static final Item COOK_STOOL_BAMBOO = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_BAMBOO, ModBlocks.COOK_STOOL_BAMBOO);
    public static final Item COOK_STOOL_BIRCH = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_BIRCH, ModBlocks.COOK_STOOL_BIRCH);
    public static final Item COOK_STOOL_CHERRY = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_CHERRY, ModBlocks.COOK_STOOL_CHERRY);
    public static final Item COOK_STOOL_CRIMSON = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_CRIMSON, ModBlocks.COOK_STOOL_CRIMSON);
    public static final Item COOK_STOOL_DARK_OAK = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_DARK_OAK, ModBlocks.COOK_STOOL_DARK_OAK);
    public static final Item COOK_STOOL_JUNGLE = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_JUNGLE, ModBlocks.COOK_STOOL_JUNGLE);
    public static final Item COOK_STOOL_MANGROVE = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_MANGROVE, ModBlocks.COOK_STOOL_MANGROVE);
    public static final Item COOK_STOOL_WARPED = registerItemViaBlock(ModBlockItemIds.COOK_STOOL_WARPED, ModBlocks.COOK_STOOL_WARPED);

    // Chairs
    public static final Item CHAIR_OAK = registerItemViaBlock(ModBlockItemIds.CHAIR_OAK, ModBlocks.CHAIR_OAK);
    public static final Item CHAIR_POPLAR = registerItemViaBlock(ModBlockItemIds.CHAIR_POPLAR, ModBlocks.CHAIR_POPLAR);
    public static final Item CHAIR_PALE_OAK = registerItemViaBlock(ModBlockItemIds.CHAIR_PALE_OAK, ModBlocks.CHAIR_PALE_OAK);
    public static final Item CHAIR_SPRUCE = registerItemViaBlock(ModBlockItemIds.CHAIR_SPRUCE, ModBlocks.CHAIR_SPRUCE);
    public static final Item CHAIR_ACACIA = registerItemViaBlock(ModBlockItemIds.CHAIR_ACACIA, ModBlocks.CHAIR_ACACIA);
    public static final Item CHAIR_BAMBOO = registerItemViaBlock(ModBlockItemIds.CHAIR_BAMBOO, ModBlocks.CHAIR_BAMBOO);
    public static final Item CHAIR_BIRCH = registerItemViaBlock(ModBlockItemIds.CHAIR_BIRCH, ModBlocks.CHAIR_BIRCH);
    public static final Item CHAIR_CHERRY = registerItemViaBlock(ModBlockItemIds.CHAIR_CHERRY, ModBlocks.CHAIR_CHERRY);
    public static final Item CHAIR_CRIMSON = registerItemViaBlock(ModBlockItemIds.CHAIR_CRIMSON, ModBlocks.CHAIR_CRIMSON);
    public static final Item CHAIR_DARK_OAK = registerItemViaBlock(ModBlockItemIds.CHAIR_DARK_OAK, ModBlocks.CHAIR_DARK_OAK);
    public static final Item CHAIR_JUNGLE = registerItemViaBlock(ModBlockItemIds.CHAIR_JUNGLE, ModBlocks.CHAIR_JUNGLE);
    public static final Item CHAIR_MANGROVE = registerItemViaBlock(ModBlockItemIds.CHAIR_MANGROVE, ModBlocks.CHAIR_MANGROVE);
    public static final Item CHAIR_WARPED = registerItemViaBlock(ModBlockItemIds.CHAIR_WARPED, ModBlocks.CHAIR_WARPED);

    // Tables
    public static final Item TABLE_OAK = registerItemViaBlock(ModBlockItemIds.TABLE_OAK, ModBlocks.TABLE_OAK);
    public static final Item TABLE_POPLAR = registerItemViaBlock(ModBlockItemIds.TABLE_POPLAR, ModBlocks.TABLE_POPLAR);
    public static final Item TABLE_PALE_OAK = registerItemViaBlock(ModBlockItemIds.TABLE_PALE_OAK, ModBlocks.TABLE_PALE_OAK);
    public static final Item TABLE_SPRUCE = registerItemViaBlock(ModBlockItemIds.TABLE_SPRUCE, ModBlocks.TABLE_SPRUCE);
    public static final Item TABLE_ACACIA = registerItemViaBlock(ModBlockItemIds.TABLE_ACACIA, ModBlocks.TABLE_ACACIA);
    public static final Item TABLE_BAMBOO = registerItemViaBlock(ModBlockItemIds.TABLE_BAMBOO, ModBlocks.TABLE_BAMBOO);
    public static final Item TABLE_BIRCH = registerItemViaBlock(ModBlockItemIds.TABLE_BIRCH, ModBlocks.TABLE_BIRCH);
    public static final Item TABLE_CHERRY = registerItemViaBlock(ModBlockItemIds.TABLE_CHERRY, ModBlocks.TABLE_CHERRY);
    public static final Item TABLE_CRIMSON = registerItemViaBlock(ModBlockItemIds.TABLE_CRIMSON, ModBlocks.TABLE_CRIMSON);
    public static final Item TABLE_DARK_OAK = registerItemViaBlock(ModBlockItemIds.TABLE_DARK_OAK, ModBlocks.TABLE_DARK_OAK);
    public static final Item TABLE_JUNGLE = registerItemViaBlock(ModBlockItemIds.TABLE_JUNGLE, ModBlocks.TABLE_JUNGLE);
    public static final Item TABLE_MANGROVE = registerItemViaBlock(ModBlockItemIds.TABLE_MANGROVE, ModBlocks.TABLE_MANGROVE);
    public static final Item TABLE_WARPED = registerItemViaBlock(ModBlockItemIds.TABLE_WARPED, ModBlocks.TABLE_WARPED);

    // Food items
    public static final Item TOMATO = registerItem(ModItemIds.TOMATO, p -> new Item(p.food(ModFoods.TOMATO, ModConsumables.TOMATO).compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM)));
    public static final Item RED_CHILI = registerItem(ModItemIds.RED_CHILI, ChiliItem.RedChiliItem::new);
    public static final Item GREEN_CHILI = registerItem(ModItemIds.GREEN_CHILI, ChiliItem.GreenChiliItem::new);
    public static final Item LETTUCE = registerItem(ModItemIds.LETTUCE, p -> new Item(p.food(ModFoods.LETTUCE, ModConsumables.LETTUCE).compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM)));
    public static final Item RICE_PANICLE = registerItem(ModItemIds.RICE_PANICLE, p -> new Item(p.compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM)));
    public static final Item CATERPILLAR = registerItem(ModItemIds.CATERPILLAR, p -> new WithTooltipsItem(p.food(ModFoods.CATERPILLAR, ModConsumables.CATERPILLAR).compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH), "caterpillar"));
    public static final Item FRIED_EGG = registerItem(ModItemIds.FRIED_EGG, p -> new Item(p.food(ModFoods.FRIED_EGG, ModConsumables.FRIED_EGG)));
    public static final Item TEA_EGG = registerItem(ModItemIds.TEA_EGG, p -> new FoodWithEffectsItem(p, ModFoods.TEA_EGG, ModConsumables.TEA_EGG));
    public static final Item DONKEY_BURGER = registerItem(ModItemIds.DONKEY_BURGER, p -> new FoodWithEffectsItem(p, ModFoods.DONKEY_BURGER, ModConsumables.DONKEY_BURGER));
    public static final Item MANTOU = registerItem(ModItemIds.MANTOU, p -> new FoodWithEffectsItem(p, ModFoods.MANTOU, ModConsumables.MANTOU));
    public static final Item BAOZI = registerItem(ModItemIds.BAOZI, p -> new FoodWithEffectsItem(p, ModFoods.BAOZI, ModConsumables.BAOZI));
    public static final Item SHENGJIAN_MANTOU = registerItem(ModItemIds.SHENGJIAN_MANTOU, p -> new FoodWithEffectsItem(p, ModFoods.SHENGJIAN_MANTOU_ITEM, ModConsumables.SHENGJIAN_MANTOU_ITEM));
    public static final Item QINGTUAN = registerItem(ModItemIds.QINGTUAN, p -> new FoodWithEffectsItem(p, ModFoods.QINGTUAN));
    public static final Item STICKY_CANDY = registerItem(ModItemIds.STICKY_CANDY, p -> new FoodWithEffectsItem(p, ModFoods.STICKY_CANDY));
    public static final Item STICKY_RICE_CAKE = registerItem(ModItemIds.STICKY_RICE_CAKE, p -> new FoodWithEffectsItem(p, ModFoods.STICKY_RICE_CAKE));
    public static final Item ZONGZI = registerItem(ModItemIds.ZONGZI, p -> new FoodWithEffectsItem(p, ModFoods.ZONGZI, ModConsumables.ZONGZI));
    public static final Item BAMBOO_TUBE_RICE = registerItem(ModBlockItemIds.BAMBOO_TUBE_RICE.item(), p -> new BambooTubeRiceBlockItem(p, ModBlocks.BAMBOO_TUBE_RICE, ModFoods.BAMBOO_TUBE_RICE));
    public static final Item SAMSA = registerItem(ModItemIds.SAMSA, p -> new FoodWithEffectsItem(p, ModFoods.SAMSA, ModConsumables.SAMSA));
    public static final Item MEAT_PIE = registerItem(ModItemIds.MEAT_PIE, p -> new FoodWithEffectsItem(p, ModFoods.MEAT_PIE, ModConsumables.MEAT_PIE));
    public static final Item DUMPLING = registerItem(ModItemIds.DUMPLING, p -> new Item(p.food(ModFoods.DUMPLING, ModConsumables.DUMPLING)));
    public static final Item RAW_DOUGH = registerItem(ModItemIds.RAW_DOUGH, RawDoughItem::new);
    public static final Item FLOUR = registerItem(ModItemIds.FLOUR, FlourItem::new);
    public static final Item RAW_NOODLES = registerItem(ModItemIds.RAW_NOODLES);
    public static final Item STUFFED_DOUGH_FOOD = registerItem(ModItemIds.STUFFED_DOUGH_FOOD);
    public static final Item RAW_ZONGZI = registerItem(ModItemIds.RAW_ZONGZI);
    public static final Item RAW_BAMBOO_TUBE_RICE = registerItem(ModItemIds.RAW_BAMBOO_TUBE_RICE);

    // Bowl foods
    public static final Item COOKED_RICE = registerItem(ModItemIds.COOKED_RICE, p -> new BowlFoodOnlyItem(p, ModFoods.COOKED_RICE, ModConsumables.COOKED_RICE, Items.BOWL));
    public static final Item SCRAMBLE_EGG_WITH_TOMATOES = registerItem(ModItemIds.SCRAMBLE_EGG_WITH_TOMATOES, p -> new BowlFoodOnlyItem(p, ModFoods.SCRAMBLE_EGG_WITH_TOMATOES, ModConsumables.SCRAMBLE_EGG_WITH_TOMATOES));
    public static final Item SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL = registerItem(ModItemIds.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL, ModConsumables.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL));
    public static final Item STIR_FRIED_BEEF_OFFAL = registerItem(ModItemIds.STIR_FRIED_BEEF_OFFAL, p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_BEEF_OFFAL, ModConsumables.STIR_FRIED_BEEF_OFFAL));
    public static final Item STIR_FRIED_BEEF_OFFAL_RICE_BOWL = registerItem(ModItemIds.STIR_FRIED_BEEF_OFFAL_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_BEEF_OFFAL_RICE_BOWL, ModConsumables.STIR_FRIED_BEEF_OFFAL_RICE_BOWL));
    public static final Item BRAISED_BEEF = registerItem(ModItemIds.BRAISED_BEEF, p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_BEEF, ModConsumables.BRAISED_BEEF));
    public static final Item BRAISED_BEEF_RICE_BOWL = registerItem(ModItemIds.BRAISED_BEEF_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_BEEF_RICE_BOWL, ModConsumables.BRAISED_BEEF_RICE_BOWL));
    public static final Item STIR_FRIED_PORK_WITH_PEPPERS = registerItem(ModItemIds.STIR_FRIED_PORK_WITH_PEPPERS, p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_PORK_WITH_PEPPERS, ModConsumables.STIR_FRIED_PORK_WITH_PEPPERS));
    public static final Item STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL = registerItem(ModItemIds.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL, ModConsumables.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL));
    public static final Item SWEET_AND_SOUR_PORK = registerItem(ModItemIds.SWEET_AND_SOUR_PORK, p -> new BowlFoodOnlyItem(p, ModFoods.SWEET_AND_SOUR_PORK, ModConsumables.SWEET_AND_SOUR_PORK));
    public static final Item SWEET_AND_SOUR_PORK_RICE_BOWL = registerItem(ModItemIds.SWEET_AND_SOUR_PORK_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.SWEET_AND_SOUR_PORK_RICE_BOWL, ModConsumables.SWEET_AND_SOUR_PORK_RICE_BOWL));
    public static final Item COUNTRY_STYLE_MIXED_VEGETABLES = registerItem(ModItemIds.COUNTRY_STYLE_MIXED_VEGETABLES, p -> new BowlFoodOnlyItem(p, ModFoods.COUNTRY_STYLE_MIXED_VEGETABLES, ModConsumables.COUNTRY_STYLE_MIXED_VEGETABLES));
    public static final Item FISH_FLAVORED_SHREDDED_PORK = registerItem(ModItemIds.FISH_FLAVORED_SHREDDED_PORK, p -> new BowlFoodOnlyItem(p, ModFoods.FISH_FLAVORED_SHREDDED_PORK, ModConsumables.FISH_FLAVORED_SHREDDED_PORK));
    public static final Item FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL = registerItem(ModItemIds.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL, ModConsumables.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL));
    public static final Item BRAISED_FISH_RICE_BOWL = registerItem(ModItemIds.BRAISED_FISH_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_FISH_RICE_BOWL, ModConsumables.BRAISED_FISH_RICE_BOWL));
    public static final Item SPICY_CHICKEN_RICE_BOWL = registerItem(ModItemIds.SPICY_CHICKEN_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.SPICY_CHICKEN_RICE_BOWL, ModConsumables.SPICY_CHICKEN_RICE_BOWL));
    public static final Item SUSPICIOUS_STIR_FRY_RICE_BOWL = registerItem(ModItemIds.SUSPICIOUS_STIR_FRY_RICE_BOWL, p -> new BowlFoodOnlyItem(p, ModFoods.SUSPICIOUS_STIR_FRY_RICE_BOWL, ModConsumables.SUSPICIOUS_STIR_FRY_RICE_BOWL));
    public static final Item EGG_FRIED_RICE = registerItem(ModItemIds.EGG_FRIED_RICE, p -> new BowlFoodOnlyItem(p, ModFoods.EGG_FRIED_RICE, ModConsumables.EGG_FRIED_RICE));
    public static final Item DELICIOUS_EGG_FRIED_RICE = registerItem(ModItemIds.DELICIOUS_EGG_FRIED_RICE, p -> new BowlFoodOnlyItem(p, ModFoods.DELICIOUS_EGG_FRIED_RICE, ModConsumables.DELICIOUS_EGG_FRIED_RICE));
    public static final Item PORK_BONE_SOUP = registerItem(ModItemIds.PORK_BONE_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.PORK_BONE_SOUP, ModConsumables.PORK_BONE_SOUP));
    public static final Item SEAFOOD_MISO_SOUP = registerItem(ModItemIds.SEAFOOD_MISO_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.SEAFOOD_MISO_SOUP, ModConsumables.SEAFOOD_MISO_SOUP));
    public static final Item FEARSOME_THICK_SOUP = registerItem(ModItemIds.FEARSOME_THICK_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.FEARSOME_THICK_SOUP, ModConsumables.FEARSOME_THICK_SOUP));
    public static final Item LAMB_AND_RADISH_SOUP = registerItem(ModItemIds.LAMB_AND_RADISH_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.LAMB_AND_RADISH_SOUP, ModConsumables.LAMB_AND_RADISH_SOUP));
    public static final Item BRAISED_BEEF_WITH_POTATOES = registerItem(ModItemIds.BRAISED_BEEF_WITH_POTATOES, p -> new BowlFoodOnlyItem(p, ModFoods.BRAISED_BEEF_WITH_POTATOES, ModConsumables.BRAISED_BEEF_WITH_POTATOES));
    public static final Item WILD_MUSHROOM_RABBIT_SOUP = registerItem(ModItemIds.WILD_MUSHROOM_RABBIT_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.WILD_MUSHROOM_RABBIT_SOUP, ModConsumables.WILD_MUSHROOM_RABBIT_SOUP));
    public static final Item TOMATO_BEEF_BRISKET_SOUP = registerItem(ModItemIds.TOMATO_BEEF_BRISKET_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.TOMATO_BEEF_BRISKET_SOUP, ModConsumables.TOMATO_BEEF_BRISKET_SOUP));
    public static final Item PUFFERFISH_SOUP = registerItem(ModItemIds.PUFFERFISH_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.PUFFERFISH_SOUP, ModConsumables.PUFFERFISH_SOUP));
    public static final Item BORSCHT = registerItem(ModItemIds.BORSCHT, p -> new BowlFoodOnlyItem(p, ModFoods.BORSCHT, ModConsumables.BORSCHT));
    public static final Item BEEF_MEATBALL_SOUP = registerItem(ModItemIds.BEEF_MEATBALL_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_MEATBALL_SOUP, ModConsumables.BEEF_MEATBALL_SOUP));
    public static final Item CHICKEN_AND_MUSHROOM_STEW = registerItem(ModItemIds.CHICKEN_AND_MUSHROOM_STEW, p -> new BowlFoodOnlyItem(p, ModFoods.CHICKEN_AND_MUSHROOM_STEW, ModConsumables.CHICKEN_AND_MUSHROOM_STEW));
    public static final Item DONKEY_SOUP = registerItem(ModItemIds.DONKEY_SOUP, p -> new BowlFoodOnlyItem(p, ModFoods.DONKEY_SOUP, ModConsumables.DONKEY_SOUP));
    public static final Item LABA_CONGEE = registerItem(ModItemIds.LABA_CONGEE, p -> new BowlFoodOnlyItem(p, ModFoods.LABA_CONGEE, ModConsumables.LABA_CONGEE));
    public static final Item BEEF_NOODLE = registerItem(ModItemIds.BEEF_NOODLE, p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_NOODLE, ModConsumables.BEEF_NOODLE));
    public static final Item HUI_NOODLE = registerItem(ModItemIds.HUI_NOODLE, p -> new BowlFoodOnlyItem(p, ModFoods.HUI_NOODLE, ModConsumables.HUI_NOODLE));
    public static final Item UDON_NOODLE = registerItem(ModItemIds.UDON_NOODLE, p -> new BowlFoodOnlyItem(p, ModFoods.UDON_NOODLE, ModConsumables.UDON_NOODLE));
    public static final Item HOT_DRY_NOODLES = registerItem(ModItemIds.HOT_DRY_NOODLES, p -> new BowlFoodOnlyItem(p, ModFoods.HOT_DRY_NOODLES, ModConsumables.HOT_DRY_NOODLES));

    // Raw and cooked foods
    public static final Item SASHIMI = registerItem(ModItemIds.SASHIMI, p -> new Item(p.food(ModFoods.SASHIMI, ModConsumables.SASHIMI)));
    public static final Item RAW_LAMB_CHOPS = registerItem(ModItemIds.RAW_LAMB_CHOPS, p -> new Item(p.food(ModFoods.RAW_LAMB_CHOPS, ModConsumables.RAW_LAMB_CHOPS)));
    public static final Item RAW_COW_OFFAL = registerItem(ModItemIds.RAW_COW_OFFAL, p -> new Item(p.food(ModFoods.RAW_COW_OFFAL, ModConsumables.RAW_COW_OFFAL)));
    public static final Item RAW_PORK_BELLY = registerItem(ModItemIds.RAW_PORK_BELLY, p -> new Item(p.food(ModFoods.RAW_PORK_BELLY, ModConsumables.RAW_PORK_BELLY)));
    public static final Item RAW_DONKEY_MEAT = registerItem(ModItemIds.RAW_DONKEY_MEAT, p -> new Item(p.food(ModFoods.RAW_DONKEY_MEAT, ModConsumables.RAW_DONKEY_MEAT)));
    public static final Item RAW_CUT_SMALL_MEATS = registerItem(ModItemIds.RAW_CUT_SMALL_MEATS, p -> new Item(p.food(ModFoods.RAW_CUT_SMALL_MEATS, ModConsumables.RAW_CUT_SMALL_MEATS)));
    public static final Item RAW_MEATBALL  = registerItem(ModItemIds.RAW_MEATBALL, p -> new Item(p.food(ModFoods.RAW_MEATBALL, ModConsumables.RAW_MEATBALL)));
    public static final Item COOKED_LAMB_CHOPS = registerItem(ModItemIds.COOKED_LAMB_CHOPS, p -> new Item(p.food(ModFoods.COOKED_LAMB_CHOPS, ModConsumables.COOKED_LAMB_CHOPS)));
    public static final Item COOKED_COW_OFFAL = registerItem(ModItemIds.COOKED_COW_OFFAL, p -> new Item(p.food(ModFoods.COOKED_COW_OFFAL, ModConsumables.COOKED_COW_OFFAL)));
    public static final Item COOKED_PORK_BELLY = registerItem(ModItemIds.COOKED_PORK_BELLY, p -> new Item(p.food(ModFoods.COOKED_PORK_BELLY, ModConsumables.COOKED_PORK_BELLY)));
    public static final Item COOKED_DONKEY_MEAT = registerItem(ModItemIds.COOKED_DONKEY_MEAT, p -> new Item(p.food(ModFoods.COOKED_DONKEY_MEAT, ModConsumables.COOKED_DONKEY_MEAT)));
    public static final Item COOKED_CUT_SMALL_MEATS = registerItem(ModItemIds.COOKED_CUT_SMALL_MEATS, p -> new Item(p.food(ModFoods.COOKED_CUT_SMALL_MEATS, ModConsumables.COOKED_CUT_SMALL_MEATS)));
    public static final Item COOKED_MEATBALL  = registerItem(ModItemIds.COOKED_MEATBALL, p -> new Item(p.food(ModFoods.COOKED_MEATBALL, ModConsumables.COOKED_MEATBALL)));

    // Special
    public static final Item MODEL_DISPLAY = registerItem(ModItemIds.MODEL_DISPLAY, p -> new ModelDisplayItem(p.stacksTo(1).rarity(Rarity.EPIC).component(ModDataComponents.MODEL_DISPLAY_MODEL, "")));
    public static final Item STRAW_BED = registerItem(ModBlockItemIds.STRAW_BED.item(), p -> new BlockItem(ModBlocks.STRAW_BED, p.stacksTo(16).compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));

    public static void registerItems() {

    }

    public static Item registerItemViaBlock(BlockItemId id, Block block, BiFunction<Block, Item.Properties, Item> biFunction, Item.Properties properties) {
        return registerItem(id.item(), properties2 -> biFunction.apply(block, properties2), properties.useBlockDescriptionPrefix());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function, Item.Properties properties) {
        Item item = function.apply(properties.setId(resourceKey));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function) {
        return registerItem(resourceKey, function, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey) {
        return registerItem(resourceKey, Item::new, new Item.Properties());
    }

    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block block) {
        return properties -> new BlockItem(block, properties.useItemDescriptionPrefix());
    }

    private static Function<Item.Properties, Item> wrapSeedItem(Block block) {
        return properties -> new WithTooltipsBlockItem(block, properties.useItemDescriptionPrefix()
                .compostable(ContextIntProviders.COMPOSTABLE_LOW), "crop_seed");
    }

    public static Supplier<Item> registerStrawHats(ResourceKey<Item> itemId, Function<Item.Properties, Item> function) {
        return () -> {
            if (!FabricLoader.getInstance().isModLoaded("trinkets_updated")) {
                return BuiltInRegistries.ITEM.getOptional(itemId).orElseGet(() -> {
                    Item.Properties properties = new Item.Properties();
                    Item item = function.apply(properties.setId(itemId));
                    return Registry.register(BuiltInRegistries.ITEM, itemId, item);
                });
            } else {
                // 这里的注册占位是给饰品栏准备的
                return function.apply(new Item.Properties());
            }
        };
    }


    public static Item registerItemViaBlock(BlockItemId id, Block block, BiFunction<Block, Item.Properties, Item> biFunction) {
        return registerItemViaBlock(id, block, biFunction, new Item.Properties());
    }

    public static Item registerItemViaBlock(BlockItemId id, Block block, Item.Properties properties) {
        return registerItemViaBlock(id, block, BlockItem::new, properties);
    }

    public static Item registerItemViaBlock(BlockItemId id, Block block) {
        return registerItemViaBlock(id, block, BlockItem::new);
    }

    public static Item registerItemViaBlockWithDesc(BlockItemId id, Block block, String descId) {
        return registerItemViaBlock(id, block, (blocks, properties) -> new WithTooltipsBlockItem(blocks, properties, descId));
    }
}
