package com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.ChiliRistraBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class BlockLootTables extends BlockLootSubProvider {
    public final Set<Block> knownBlocks = new HashSet<>();
    public final HolderLookup.RegistryLookup<Enchantment> enchantment;

    public BlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        this.enchantment = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.STOVE );
        dropSelf(ModBlocks.POT );
        dropSelf(ModBlocks.CHOPPING_BOARD );

        dropSelf(ModBlocks.OIL_POT );

        dropSelf(ModBlocks.COOK_STOOL_OAK );
        dropSelf(ModBlocks.COOK_STOOL_SPRUCE );
        dropSelf(ModBlocks.COOK_STOOL_ACACIA );
        dropSelf(ModBlocks.COOK_STOOL_BAMBOO );
        dropSelf(ModBlocks.COOK_STOOL_BIRCH );
        dropSelf(ModBlocks.COOK_STOOL_CHERRY );
        dropSelf(ModBlocks.COOK_STOOL_CRIMSON );
        dropSelf(ModBlocks.COOK_STOOL_DARK_OAK );
        dropSelf(ModBlocks.COOK_STOOL_JUNGLE );
        dropSelf(ModBlocks.COOK_STOOL_MANGROVE );
        dropSelf(ModBlocks.COOK_STOOL_WARPED );

        dropSelf(ModBlocks.CHAIR_OAK );
        dropSelf(ModBlocks.CHAIR_SPRUCE );
        dropSelf(ModBlocks.CHAIR_ACACIA );
        dropSelf(ModBlocks.CHAIR_BAMBOO );
        dropSelf(ModBlocks.CHAIR_BIRCH );
        dropSelf(ModBlocks.CHAIR_CHERRY );
        dropSelf(ModBlocks.CHAIR_CRIMSON );
        dropSelf(ModBlocks.CHAIR_DARK_OAK );
        dropSelf(ModBlocks.CHAIR_JUNGLE );
        dropSelf(ModBlocks.CHAIR_MANGROVE );
        dropSelf(ModBlocks.CHAIR_WARPED );

        dropSelf(ModBlocks.TABLE_OAK );
        dropSelf(ModBlocks.TABLE_SPRUCE );
        dropSelf(ModBlocks.TABLE_ACACIA );
        dropSelf(ModBlocks.TABLE_BAMBOO );
        dropSelf(ModBlocks.TABLE_BIRCH );
        dropSelf(ModBlocks.TABLE_CHERRY );
        dropSelf(ModBlocks.TABLE_CRIMSON );
        dropSelf(ModBlocks.TABLE_DARK_OAK );
        dropSelf(ModBlocks.TABLE_JUNGLE );
        dropSelf(ModBlocks.TABLE_MANGROVE );
        dropSelf(ModBlocks.TABLE_WARPED );

        dropSelf(ModBlocks.STOCKPOT );
        dropSelf(ModBlocks.FRUIT_BASKET );
        dropSelf(ModBlocks.KITCHENWARE_RACKS );
        dropSelf(ModBlocks.KITCHEN_SHELF );
        dropSelf(ModBlocks.STRAW_BLOCK );
        dropSelf(ModBlocks.SHAWARMA_SPIT );
        dropSelf(ModBlocks.OIL_BLOCK );
        dropSelf(ModBlocks.ENAMEL_BASIN );

        this.add(ModBlocks.TOMATO_CROP , createCropDrops(ModBlocks.TOMATO_CROP , ModItems.TOMATO ,
                ModItems.TOMATO_SEED , createCropBuilder(ModBlocks.TOMATO_CROP )));

        var chiliBuilder = createCropBuilder(ModBlocks.CHILI_CROP );
        LootPoolSingletonContainer.Builder<?> greenChili = LootItem.lootTableItem(ModItems.GREEN_CHILI )
                .when(LootItemRandomChanceCondition.randomChance(0.2F));
        this.add(ModBlocks.CHILI_CROP , createCropDrops(ModBlocks.CHILI_CROP , ModItems.RED_CHILI , ModItems.CHILI_SEED , chiliBuilder)
                .withPool(LootPool.lootPool().when(chiliBuilder).add(greenChili)));

        var lettuceBuilder = createCropBuilder(ModBlocks.LETTUCE_CROP );
        LootPoolSingletonContainer.Builder<?> caterpillar = LootItem.lootTableItem(ModItems.CATERPILLAR )
                .when(LootItemRandomChanceCondition.randomChance(0.1F));
        this.add(ModBlocks.LETTUCE_CROP , createCropDrops(ModBlocks.LETTUCE_CROP , ModItems.LETTUCE , ModItems.LETTUCE_SEED , lettuceBuilder)
                .withPool(LootPool.lootPool().when(lettuceBuilder).add(caterpillar)));

        Item riceSeed = ModItems.WILD_RICE_SEED ;
        LootItemCondition.Builder riceCropBuilder = createRiceCropBuilder();
        var countFunction = SetItemCountFunction.setCount(UniformGenerator.between(2, 4));
        LootPool.Builder ricePanicle = LootPool.lootPool().add(LootItem.lootTableItem(ModItems.RICE_PANICLE )
                .when(riceCropBuilder).apply(countFunction).otherwise(LootItem.lootTableItem(riceSeed)));
        LootPool.Builder extraRiceSeeds = LootPool.lootPool().add(LootItem.lootTableItem(riceSeed))
                .when(riceCropBuilder).apply(ApplyBonusCount.addBonusBinomialDistributionCount(enchantment.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3));

        this.add(ModBlocks.RICE_CROP , this.applyExplosionDecay(ModBlocks.RICE_CROP ,
                LootTable.lootTable().withPool(ricePanicle).withPool(extraRiceSeeds)));

        FoodBiteRegistry.FOOD_DATA_MAP.forEach(this::dropFoodBite);
        // 特殊的方块食物
        dropFoodBite(ModBlocks.COLD_CUT_HAM_SLICES, ModItems.COLD_CUT_HAM_SLICES, Items.BOWL);

        this.add(ModBlocks.CHILI_RISTRA , createChiliRistraLootTable());
    }

    private LootTable.Builder createChiliRistraLootTable() {
        LootPool.Builder builder = LootPool.lootPool();

        StatePropertiesPredicate.Builder isSheared = StatePropertiesPredicate.Builder.properties().hasProperty(ChiliRistraBlock.SHEARED, true);
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.CHILI_RISTRA ).setProperties(isSheared);

        LootItemConditionalFunction.Builder<?> normalDrop = SetItemCountFunction.setCount(ConstantValue.exactly(6));
        LootItemConditionalFunction.Builder<?> shearedDrop = SetItemCountFunction.setCount(ConstantValue.exactly(3));

        LootPoolSingletonContainer.Builder<?> normalLoot = LootItem.lootTableItem(ModItems.RED_CHILI ).apply(normalDrop);
        LootPoolSingletonContainer.Builder<?> shearedLoot = LootItem.lootTableItem(ModItems.RED_CHILI ).apply(shearedDrop);

        builder.add(shearedLoot.when(condition).otherwise(normalLoot));

        return LootTable.lootTable().withPool(builder.when(ExplosionCondition.survivesExplosion()));
    }

    private LootItemCondition.Builder createCropBuilder(Block cropBlock) {
        StatePropertiesPredicate.Builder property = StatePropertiesPredicate.Builder
                .properties().hasProperty(CropBlock.AGE, 7);
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock)
                .setProperties(property);
    }

    private LootItemCondition.Builder createRiceCropBuilder() {
        StatePropertiesPredicate.Builder property = StatePropertiesPredicate.Builder
                .properties().hasProperty(CropBlock.AGE, 7)
                .hasProperty(RiceCropBlock.LOCATION, RiceCropBlock.DOWN);
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.RICE_CROP )
                .setProperties(property);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        super.generate(output);

        // 穿戴草帽掉落番茄辣椒等种子
        var tomato = getSeed(ModItems.TOMATO_SEED );
        var chili = getSeed(ModItems.CHILI_SEED );
        var lettuce = getSeed(ModItems.LETTUCE_SEED );
        var rice = getSeed(ModItems.WILD_RICE_SEED );

        // 原版其他几个种子也掉，但是概率较低
        // 甜菜、南瓜、西瓜种子
        var beetRootSeed = getSeed(Items.BEETROOT_SEEDS, 0.02F);
        var pumpkinSeed = getSeed(Items.PUMPKIN_SEEDS, 0.02F);
        var melonSeed = getSeed(Items.MELON_SEEDS, 0.02F);

        LootTable.Builder dropSeed = LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(tomato).add(chili).add(lettuce).add(rice)
                .add(beetRootSeed).add(pumpkinSeed).add(melonSeed));
        ResourceKey<LootTable> id = ResourceKey.create(Registries.LOOT_TABLE, modLoc("straw_hat_seed_drop"));
        output.accept(id, dropSeed);
    }

    private LootPoolSingletonContainer.Builder<?> getSeed(ItemLike item) {
        return getSeed(item, 0.125F);
    }

    private LootPoolSingletonContainer.Builder<?> getSeed(ItemLike item, float probability) {
        ItemPredicate hasHat = ItemPredicate.Builder.item().of(TagMod.STRAW_HAT).build();
        LootItemCondition.Builder hatMatches = AdvanceBlockMatchTool.toolMatches(EquipmentSlot.HEAD, hasHat);
        return LootItem.lootTableItem(item)
                .when(LootItemRandomChanceCondition.randomChance(probability)).when(hatMatches)
                .apply(ApplyBonusCount.addUniformBonusCount(enchantment.getOrThrow(Enchantments.FORTUNE), 2));
    }

    private void dropFoodBite(Block block, Item food, ItemLike... lootItems) {
        if (!(block instanceof FoodBiteBlock foodBiteBlock)) {
            return;
        }
        ConstantValue exactly = ConstantValue.exactly(1);
        StatePropertiesPredicate.Builder notBite = StatePropertiesPredicate.Builder.properties().hasProperty(foodBiteBlock.getBites(), 0);
        LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(foodBiteBlock).setProperties(notBite);

        LootTable.Builder lootTable = LootTable.lootTable();
        for (int i = 0; i < lootItems.length; i++) {
            ItemLike itemLike = lootItems[i];
            LootPool.Builder rolls = LootPool.lootPool().setRolls(exactly).when(ExplosionCondition.survivesExplosion());
            if (i == 0) {
                rolls.add(LootItem.lootTableItem(food).when(builder).otherwise(LootItem.lootTableItem(itemLike)));
            } else {
                rolls.add(EmptyLootItem.emptyItem().when(builder).otherwise(LootItem.lootTableItem(itemLike)));
            }
            lootTable.withPool(rolls);
        }

        this.add(block, lootTable);
    }

    private void dropFoodBite(ResourceLocation id, FoodBiteRegistry.FoodData data) {
        Block block = BuiltInRegistries.BLOCK.get(id);
        Item food = BuiltInRegistries.ITEM.get(id);
        if (!(block instanceof FoodBiteBlock foodBiteBlock)) {
            return;
        }
        ConstantValue exactly = ConstantValue.exactly(1);
        StatePropertiesPredicate.Builder notBite = StatePropertiesPredicate.Builder.properties().hasProperty(foodBiteBlock.getBites(), 0);
        LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(foodBiteBlock).setProperties(notBite);

        LootTable.Builder lootTable = LootTable.lootTable();
        for (int i = 0; i < data.getLootItems().size(); i++) {
            ItemLike itemLike = data.getLootItems().get(i);
            LootPool.Builder rolls = LootPool.lootPool().setRolls(exactly).when(ExplosionCondition.survivesExplosion());
            if (i == 0) {
                rolls.add(LootItem.lootTableItem(food).when(builder).otherwise(LootItem.lootTableItem(itemLike)));
            } else {
                rolls.add(EmptyLootItem.emptyItem().when(builder).otherwise(LootItem.lootTableItem(itemLike)));
            }
            lootTable.withPool(rolls);
        }
        this.add(block, lootTable);
    }

    @Override
    public void add(Block block, LootTable.Builder builder) {
        this.knownBlocks.add(block);
        super.add(block, builder);
    }

    public ResourceLocation modLoc(String name) {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name);
    }
}
