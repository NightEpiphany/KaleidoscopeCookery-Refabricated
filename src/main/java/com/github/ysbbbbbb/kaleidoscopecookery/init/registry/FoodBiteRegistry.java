package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModConsumables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods.*;

public final class FoodBiteRegistry {
    public static final Map<Identifier, FoodData> FOOD_DATA_MAP = Maps.newLinkedHashMap();
    public static final Map<Identifier, Item> FOOD_ITEM_MAP = Maps.newLinkedHashMap();

    public static final FoodBiteRegistry INSTANCE = new FoodBiteRegistry();

    private FoodBiteRegistry() {
    }

    public static Identifier DARK_CUISINE;
    public static Identifier SUSPICIOUS_STIR_FRY;
    public static Identifier SLIME_BALL_MEAL;
    public static Identifier FONDANT_PIE;
    public static Identifier DONGPO_PORK;
    public static Identifier FONDANT_SPIDER_EYE;
    public static Identifier CHORUS_FRIED_EGG;
    public static Identifier BRAISED_FISH;
    public static Identifier GOLDEN_SALAD;
    public static Identifier SPICY_CHICKEN;
    public static Identifier YAKITORI;
    public static Identifier PAN_SEARED_KNIGHT_STEAK;
    public static Identifier STARGAZY_PIE;
    public static Identifier SWEET_AND_SOUR_ENDER_PEARLS;
    public static Identifier CRYSTAL_LAMB_CHOP;
    public static Identifier BLAZE_LAMB_CHOP;
    public static Identifier FROST_LAMB_CHOP;
    public static Identifier NETHER_STYLE_SASHIMI;
    public static Identifier END_STYLE_SASHIMI;
    public static Identifier DESERT_STYLE_SASHIMI;
    public static Identifier TUNDRA_STYLE_SASHIMI;
    public static Identifier COLD_STYLE_SASHIMI;
    public static Identifier CANDIED_POTATO;
    public static Identifier DOUGH_DROP_SOUP;
    public static Identifier STUFFED_TIGER_SKIN_PEPPER;
    public static Identifier SPICY_RABBIT_HEAD;
    public static Identifier FOUR_JOY_MEATBALL_SOUP;
    public static Identifier NUMBING_SPICY_CHICKEN;
    public static Identifier FRIED_CATERPILLAR;
    public static Identifier FRIED_SPRING_ROLL;
    public static Identifier SPICY_BLOOD_STEW;
    public static Identifier FRUIT_PLATTER;

    public static Identifier BRAISED_PORK_RIBS;
    public static Identifier COLD_ROASTED_MEAT;
    public static Identifier OIL_SPLASHED_FISH;

    public static Identifier BROWN_MUSHROOM_POT_SOUP;
    public static Identifier RED_MUSHROOM_POT_SOUP;
    public static Identifier WARPED_FUNGUS_POT_SOUP;
    public static Identifier CRIMSON_FUNGUS_POT_SOUP;
    public static Identifier BUDDHA_JUMPS_OVER_THE_WALL;

    public static void init() {

        DARK_CUISINE = INSTANCE.registerFoodData("dark_cuisine", FoodData
                .create(3, DARK_CUISINE_BLOCK, DARK_CUISINE_ITEM, ModConsumables.DARK_CUISINE_BLOCK, ModConsumables.DARK_CUISINE_ITEM)
                .setAnimateTick(FoodBiteAnimateTicks.DARK_CUISINE_ANIMATE_TICK));

        SUSPICIOUS_STIR_FRY = INSTANCE.registerFoodData("suspicious_stir_fry", FoodData
                .create(1, SUSPICIOUS_STIR_FRY_BLOCK, SUSPICIOUS_STIR_FRY_ITEM, ModConsumables.SUSPICIOUS_STIR_FRY_BLOCK, ModConsumables.SUSPICIOUS_STIR_FRY_ITEM)
                .setAnimateTick(FoodBiteAnimateTicks.SUSPICIOUS_STIR_FRY_ANIMATE_TICK));

        SLIME_BALL_MEAL = INSTANCE.registerFoodData("slime_ball_meal", FoodData
                .create(3, SLIME_BALL_MEAL_BLOCK, SLIME_BALL_MEAL_ITEM, ModConsumables.SLIME_BALL_MEAL_BLOCK, ModConsumables.SLIME_BALL_MEAL_ITEM));

        FONDANT_PIE = INSTANCE.registerFoodData("fondant_pie", FoodData
                .create(4, FONDANT_PIE_BLOCK, FONDANT_PIE_ITEM, ModConsumables.FONDANT_PIE_BLOCK, ModConsumables.FONDANT_PIE_ITEM));

        DONGPO_PORK = INSTANCE.registerFoodData("dongpo_pork", FoodData
                .create(3, DONGPO_PORK_BLOCK, DONGPO_PORK_ITEM, ModConsumables.DONGPO_PORK_BLOCK, ModConsumables.DONGPO_PORK_ITEM)
                .addLootItems(Items.BAMBOO));

        FONDANT_SPIDER_EYE = INSTANCE.registerFoodData("fondant_spider_eye", FoodData
                .create(4, FONDANT_SPIDER_EYE_BLOCK, FONDANT_SPIDER_EYE_ITEM, ModConsumables.FONDANT_SPIDER_EYE_BLOCK, ModConsumables.FONDANT_SPIDER_EYE_ITEM));

        CHORUS_FRIED_EGG = INSTANCE.registerFoodData("chorus_fried_egg", FoodData
                .create(3, CHORUS_FRIED_EGG_BLOCK, CHORUS_FRIED_EGG_ITEM, ModConsumables.CHORUS_FRIED_EGG_BLOCK, ModConsumables.CHORUS_FRIED_EGG_ITEM));

        BRAISED_FISH = INSTANCE.registerFoodData("braised_fish", FoodData
                .create(4, BRAISED_FISH_BLOCK, BRAISED_FISH_ITEM, ModConsumables.BRAISED_FISH_BLOCK, ModConsumables.BRAISED_FISH_ITEM)
                .addLootItems(Items.BONE, Items.BONE_MEAL));

        SPICY_CHICKEN = INSTANCE.registerFoodData("spicy_chicken", FoodData
                .create(4, SPICY_CHICKEN_BLOCK, SPICY_CHICKEN_ITEM, ModConsumables.SPICY_CHICKEN_BLOCK, ModConsumables.SPICY_CHICKEN_ITEM));

        YAKITORI = INSTANCE.registerFoodData("yakitori", FoodData
                .create(4, YAKITORI_BLOCK, YAKITORI_ITEM, ModConsumables.YAKITORI_BLOCK, ModConsumables.YAKITORI_ITEM));

        PAN_SEARED_KNIGHT_STEAK = INSTANCE.registerFoodData("pan_seared_knight_steak", FoodData
                .create(4, PAN_SEARED_KNIGHT_STEAK_BLOCK, PAN_SEARED_KNIGHT_STEAK_ITEM, ModConsumables.PAN_SEARED_KNIGHT_STEAK_BLOCK, ModConsumables.PAN_SEARED_KNIGHT_STEAK_ITEM)
                .addLootItems(Items.BONE, Items.BONE_MEAL));

        STARGAZY_PIE = INSTANCE.registerFoodData("stargazy_pie", FoodData
                .create(4, STARGAZY_PIE_BLOCK, STARGAZY_PIE_ITEM, ModConsumables.STARGAZY_PIE_BLOCK, ModConsumables.STARGAZY_PIE_ITEM));

        SWEET_AND_SOUR_ENDER_PEARLS = INSTANCE.registerFoodData("sweet_and_sour_ender_pearls", FoodData
                .create(3, SWEET_AND_SOUR_ENDER_PEARLS_BLOCK, SWEET_AND_SOUR_ENDER_PEARLS_ITEM, ModConsumables.SWEET_AND_SOUR_ENDER_PEARLS_BLOCK, ModConsumables.SWEET_AND_SOUR_ENDER_PEARLS_ITEM));

        CRYSTAL_LAMB_CHOP = INSTANCE.registerFoodData("crystal_lamb_chop", FoodData
                .create(3, CRYSTAL_LAMB_CHOP_BLOCK, CRYSTAL_LAMB_CHOP_ITEM, ModConsumables.CRYSTAL_LAMB_CHOP_BLOCK, ModConsumables.CRYSTAL_LAMB_CHOP_ITEM)
                .addLootItems(Items.AMETHYST_SHARD));

        BLAZE_LAMB_CHOP = INSTANCE.registerFoodData("blaze_lamb_chop", FoodData
                .create(3, BLAZE_LAMB_CHOP_BLOCK, BLAZE_LAMB_CHOP_ITEM, ModConsumables.BLAZE_LAMB_CHOP_BLOCK, ModConsumables.BLAZE_LAMB_CHOP_ITEM)
                .addLootItems(Items.BLAZE_ROD));

        FROST_LAMB_CHOP = INSTANCE.registerFoodData("frost_lamb_chop", FoodData
                .create(3, FROST_LAMB_CHOP_BLOCK, FROST_LAMB_CHOP_ITEM, ModConsumables.FROST_LAMB_CHOP_BLOCK, ModConsumables.FROST_LAMB_CHOP_ITEM)
                .addLootItems(Items.BLUE_ICE));

        NETHER_STYLE_SASHIMI = INSTANCE.registerFoodData("nether_style_sashimi", FoodData
                .create(4, NETHER_STYLE_SASHIMI_BLOCK, NETHER_STYLE_SASHIMI_ITEM, ModConsumables.NETHER_STYLE_SASHIMI_BLOCK, ModConsumables.NETHER_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS));

        END_STYLE_SASHIMI = INSTANCE.registerFoodData("end_style_sashimi", FoodData
                .create(4, END_STYLE_SASHIMI_BLOCK, END_STYLE_SASHIMI_ITEM, ModConsumables.END_STYLE_SASHIMI_BLOCK, ModConsumables.END_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.CHORUS_FRUIT));

        DESERT_STYLE_SASHIMI = INSTANCE.registerFoodData("desert_style_sashimi", FoodData
                .create(4, DESERT_STYLE_SASHIMI_BLOCK, DESERT_STYLE_SASHIMI_ITEM, ModConsumables.DESERT_STYLE_SASHIMI_BLOCK, ModConsumables.DESERT_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.CACTUS));

        TUNDRA_STYLE_SASHIMI = INSTANCE.registerFoodData("tundra_style_sashimi", FoodData
                .create(4, TUNDRA_STYLE_SASHIMI_BLOCK, TUNDRA_STYLE_SASHIMI_ITEM, ModConsumables.TUNDRA_STYLE_SASHIMI_BLOCK, ModConsumables.TUNDRA_STYLE_SASHIMI_ITEM));

        COLD_STYLE_SASHIMI = INSTANCE.registerFoodData("cold_style_sashimi", FoodData
                .create(4, COLD_STYLE_SASHIMI_BLOCK, COLD_STYLE_SASHIMI_ITEM, ModConsumables.COLD_STYLE_SASHIMI_BLOCK, ModConsumables.COLD_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.SNOWBALL, Items.SNOWBALL));

        CANDIED_POTATO = INSTANCE.registerFoodData("candied_potato", FoodData
                .create(3, CANDIED_POTATO_BLOCK, CANDIED_POTATO_ITEM, ModConsumables.CANDIED_POTATO_BLOCK, ModConsumables.CANDIED_POTATO_ITEM));

        STUFFED_TIGER_SKIN_PEPPER = INSTANCE.registerFoodData("stuffed_tiger_skin_pepper", FoodData
                .create(5, STUFFED_TIGER_SKIN_PEPPER_BLOCK, STUFFED_TIGER_SKIN_PEPPER_ITEM, ModConsumables.STUFFED_TIGER_SKIN_PEPPER_BLOCK, ModConsumables.STUFFED_TIGER_SKIN_PEPPER_ITEM));

        SPICY_RABBIT_HEAD = INSTANCE.registerFoodData("spicy_rabbit_head", FoodData
                .create(3, SPICY_RABBIT_HEAD_BLOCK, SPICY_RABBIT_HEAD_ITEM, ModConsumables.SPICY_RABBIT_HEAD_BLOCK, ModConsumables.SPICY_RABBIT_HEAD_ITEM));

        FRIED_CATERPILLAR = INSTANCE.registerFoodData("fried_caterpillar", FoodData
                .create(3, FRIED_CATERPILLAR_BLOCK, FRIED_CATERPILLAR_ITEM, ModConsumables.FRIED_CATERPILLAR_BLOCK, ModConsumables.FRIED_CATERPILLAR_ITEM)
                .setAABB(Block.box(1, 0, 3, 15, 4, 13)));

        FRIED_SPRING_ROLL = INSTANCE.registerFoodData("fried_spring_roll", FoodData
                .create(3, FRIED_SPRING_ROLL_BLOCK, FRIED_SPRING_ROLL_ITEM, ModConsumables.FRIED_SPRING_ROLL_BLOCK, ModConsumables.FRIED_SPRING_ROLL_ITEM));

        FRUIT_PLATTER = INSTANCE.registerFoodData("fruit_platter", FoodData
                .create(4, FRUIT_PLATTER_BLOCK, FRUIT_PLATTER_ITEM, ModConsumables.FRUIT_PLATTER_BLOCK, ModConsumables.FRUIT_PLATTER_ITEM));

        // ========================== 1x2 食物 ==========================

        BRAISED_PORK_RIBS = INSTANCE.registerFoodData("braised_pork_ribs", FoodData
                .createOneByTwo(4, BRAISED_PORK_RIBS_BLOCK, BRAISED_PORK_RIBS_ITEM, ModConsumables.BRAISED_PORK_RIBS_BLOCK, ModConsumables.BRAISED_PORK_RIBS_ITEM)
                .addLootItems(Items.BONE));

        COLD_ROASTED_MEAT = INSTANCE.registerFoodData("cold_roasted_meat", FoodData
                .createOneByTwo(3, COLD_ROASTED_MEAT_BLOCK, COLD_ROASTED_MEAT_ITEM, ModConsumables.COLD_ROASTED_MEAT_BLOCK, ModConsumables.COLD_ROASTED_MEAT_ITEM));

        OIL_SPLASHED_FISH = INSTANCE.registerFoodData("oil_splashed_fish", FoodData
                .createOneByTwo(5, OIL_SPLASHED_FISH_BLOCK, OIL_SPLASHED_FISH_ITEM, ModConsumables.OIL_SPLASHED_FISH_BLOCK, ModConsumables.OIL_SPLASHED_FISH_ITEM)
                .addLootItems(Items.BONE_MEAL));

        // ========================== 汤食类 ==========================

        DOUGH_DROP_SOUP = INSTANCE.registerFoodData("dough_drop_soup", FoodData
                .create(3, DOUGH_DROP_SOUP_BLOCK, DOUGH_DROP_SOUP_ITEM, ModConsumables.DOUGH_DROP_SOUP_BLOCK, ModConsumables.DOUGH_DROP_SOUP_ITEM)
                .bowlAABB());

        FOUR_JOY_MEATBALL_SOUP = INSTANCE.registerFoodData("four_joy_meatball_soup", FoodData
                .create(4, FOUR_JOY_MEATBALL_SOUP_BLOCK, FOUR_JOY_MEATBALL_SOUP_ITEM, ModConsumables.FOUR_JOY_MEATBALL_SOUP_BLOCK, ModConsumables.FOUR_JOY_MEATBALL_SOUP_ITEM)
                .bowlAABB());

        NUMBING_SPICY_CHICKEN = INSTANCE.registerFoodData("numbing_spicy_chicken", FoodData
                .create(3, NUMBING_SPICY_CHICKEN_BLOCK, NUMBING_SPICY_CHICKEN_ITEM, ModConsumables.NUMBING_SPICY_CHICKEN_BLOCK, ModConsumables.NUMBING_SPICY_CHICKEN_ITEM)
                .bowlAABB());

        SPICY_BLOOD_STEW = INSTANCE.registerFoodData("spicy_blood_stew", FoodData
                .create(3, SPICY_BLOOD_STEW_BLOCK, SPICY_BLOOD_STEW_ITEM, ModConsumables.SPICY_BLOOD_STEW_BLOCK, ModConsumables.SPICY_BLOOD_STEW_ITEM)
                .bowlAABB());

        // ========================== 瓦罐汤 ==========================

        BROWN_MUSHROOM_POT_SOUP = INSTANCE.registerFoodData("brown_mushroom_pot_soup", FoodData
                .create(2, BROWN_MUSHROOM_POT_SOUP_BLOCK, BROWN_MUSHROOM_POT_SOUP_ITEM, ModConsumables.BROWN_MUSHROOM_POT_SOUP_BLOCK, ModConsumables.BROWN_MUSHROOM_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        RED_MUSHROOM_POT_SOUP = INSTANCE.registerFoodData("red_mushroom_pot_soup", FoodData
                .create(2, RED_MUSHROOM_POT_SOUP_BLOCK, RED_MUSHROOM_POT_SOUP_ITEM, ModConsumables.RED_MUSHROOM_POT_SOUP_BLOCK, ModConsumables.RED_MUSHROOM_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        WARPED_FUNGUS_POT_SOUP = INSTANCE.registerFoodData("warped_fungus_pot_soup", FoodData
                .create(2, WARPED_FUNGUS_POT_SOUP_BLOCK, WARPED_FUNGUS_POT_SOUP_ITEM, ModConsumables.WARPED_FUNGUS_POT_SOUP_BLOCK, ModConsumables.WARPED_FUNGUS_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        CRIMSON_FUNGUS_POT_SOUP = INSTANCE.registerFoodData("crimson_fungus_pot_soup", FoodData
                .create(2, CRIMSON_FUNGUS_POT_SOUP_BLOCK, CRIMSON_FUNGUS_POT_SOUP_ITEM, ModConsumables.CRIMSON_FUNGUS_POT_SOUP_BLOCK, ModConsumables.CRIMSON_FUNGUS_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        BUDDHA_JUMPS_OVER_THE_WALL = INSTANCE.registerFoodData("buddha_jumps_over_the_wall", FoodData
                .create(2, BUDDHA_JUMPS_OVER_THE_WALL_BLOCK, BUDDHA_JUMPS_OVER_THE_WALL_ITEM, ModConsumables.BUDDHA_JUMPS_OVER_THE_WALL_BLOCK, ModConsumables.BUDDHA_JUMPS_OVER_THE_WALL_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        //  ========================== 其他 ==========================
        GOLDEN_SALAD = INSTANCE.registerFoodData("golden_salad", FoodData
                .create(6, GOLDEN_SALAD_BLOCK, GOLDEN_SALAD_ITEM, ModConsumables.GOLDEN_SALAD_BLOCK, ModConsumables.GOLDEN_SALAD_ITEM));
    }

    @SuppressWarnings("unused")
    public Identifier registerFoodData(Identifier foodName, FoodData data) {
        FOOD_DATA_MAP.put(foodName, data);
        return foodName;
    }

    public Identifier registerFoodData(String foodName, FoodData data) {
        Identifier id = mcLoc(foodName);
        FOOD_DATA_MAP.put(id, data);
        return id;
    }

    public static Identifier mcLoc(String name) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name);
    }

    @NotNull
    public static Item getItem(Identifier name) {
        if (FOOD_ITEM_MAP.containsKey(name)) return FOOD_ITEM_MAP.get(name);
        // fallback方法
        return BuiltInRegistries.ITEM.getValue(name);
    }

    public static Block getBlock(Identifier name) {
        return BuiltInRegistries.BLOCK.getValue(name);
    }

    public static final class FoodData {
        private final BlockType blockType;
        private final int maxBites;
        private final List<ItemLike> lootItems = Lists.newArrayList();
        private final FoodProperties blockFood;
        private final FoodProperties itemFood;
        private final Consumable blockConsumable;
        private final Consumable itemConsumable;
        private @Nullable FoodBiteAnimateTicks.AnimateTick animateTick = null;
        private @Nullable VoxelShape aabb = null;

        private FoodData(BlockType blockType, int maxBites, FoodProperties blockFood, FoodProperties itemFood, Consumable blockConsumable, Consumable itemConsumable) {
            this.blockType = blockType;
            this.maxBites = maxBites;
            this.lootItems.add(Items.BOWL);
            this.blockFood = blockFood;
            this.itemFood = itemFood;
            this.blockConsumable = blockConsumable;
            this.itemConsumable = itemConsumable;
        }

        public static FoodData create(int maxBites, FoodProperties blockFood, FoodProperties itemFood, Consumable blockConsumable, Consumable itemConsumable) {
            return new FoodData(BlockType.SINGLE, maxBites, blockFood, itemFood, blockConsumable, itemConsumable);
        }

        public static FoodData createOneByTwo(int maxBites, FoodProperties blockFood, FoodProperties itemFood, Consumable blockConsumable, Consumable itemConsumable) {
            return new FoodData(BlockType.ONE_BY_TWO, maxBites, blockFood, itemFood, blockConsumable, itemConsumable);
        }

        public FoodData setAnimateTick(FoodBiteAnimateTicks.AnimateTick animateTick) {
            this.animateTick = animateTick;
            return this;
        }

        public FoodData potSoupAnimateTick() {
            this.animateTick = FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK;
            return this;
        }

        public FoodData addLootItems(ItemLike... lootItems) {
            this.lootItems.addAll(Arrays.stream(lootItems).toList());
            return this;
        }

        public FoodData setLootItem(ItemLike lootItem) {
            this.lootItems.clear();
            this.lootItems.add(lootItem);
            return this;
        }

        public FoodData setAABB(VoxelShape aabb) {
            this.aabb = aabb;
            return this;
        }

        public @Nullable VoxelShape getAabb() {
            return aabb;
        }

        public FoodData bowlAABB() {
            this.aabb = Block.box(2, 0, 2, 14, 6, 14);
            return this;
        }

        public FoodData soupPotAABB() {
            this.aabb = Shapes.or(
                    Block.box(1, 0, 1, 15, 1, 15),
                    Block.box(4, 1, 4, 12, 7, 12)
            );
            return this;
        }

        public BlockType blockType() {
            return blockType;
        }

        public int maxBites() {
            return maxBites;
        }

        @Nullable
        public VoxelShape getAABB() {
            return aabb;
        }

        @Nullable
        public FoodBiteAnimateTicks.AnimateTick animateTick() {
            return animateTick;
        }

        public List<ItemLike> getLootItems() {
            return lootItems;
        }

        public FoodProperties blockFood() {
            return blockFood;
        }

        public FoodProperties itemFood() {
            return itemFood;
        }

        public Consumable blockConsumable() {
            return blockConsumable;
        }

        public Consumable itemConsumable() {
            return itemConsumable;
        }
    }

    public enum BlockType {
        SINGLE,
        ONE_BY_TWO
    }
}
