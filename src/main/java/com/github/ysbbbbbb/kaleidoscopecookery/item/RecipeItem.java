package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IPot;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.RecipeItemTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityEvaluator;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.RecipeIngredientStorage;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class RecipeItem extends WithTooltipsBlockItem {
    public static final ResourceLocation HAS_RECIPE_PROPERTY = ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "has_recipe");
    public static final ResourceLocation POT = ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot");
    public static final ResourceLocation STOCKPOT = ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot");
    public static final ResourceLocation TEAPOT = ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot");

    private static final int NO_RECIPE = 0;
    private static final int HAS_RECIPE = 1;

    public RecipeItem() {
        super(ModBlocks.RECIPE_BLOCK, new Properties(), "recipe_item");
    }

    public static void setRecipe(ItemStack stack, RecipeRecord record) {
        stack.set(ModDataComponents.RECIPE_RECORD, record);
    }

    @Nullable
    public static RecipeRecord getRecipe(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof RecipeItem)) {
            return null;
        }
        return stack.get(ModDataComponents.RECIPE_RECORD);
    }

    public static boolean hasRecipe(ItemStack stack) {
        return stack.has(ModDataComponents.RECIPE_RECORD);
    }

    @SuppressWarnings("unused")
    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (hasRecipe(stack)) {
            return HAS_RECIPE;
        }
        return NO_RECIPE;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        if (hasRecipe(pStack)) {
            RecipeRecord recipe = getRecipe(pStack);
            if (recipe != null) {
                Component result = recipe.output().getHoverName();
                Component type = parseType(recipe);
                return Component.translatable("block.kaleidoscope_cookery.recipe_block.has_record", result, type);
            }
        }
        return super.getName(pStack);
    }

    private static @NotNull Component parseType(RecipeRecord recipe) {
        Component type;
        if (recipe.type().equals(POT)) {
            type = Component.translatable("block.kaleidoscope_cookery.pot");
        } else if (recipe.type().equals(STOCKPOT)) {
            type = Component.translatable("block.kaleidoscope_cookery.stockpot");
        } else if (recipe.type().equals(TEAPOT)) {
            type = Component.translatable("block.kaleidoscope_cookery.teapot");
        } else {
            type = Component.empty();
        }
        return type;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemInHand = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        BlockEntity blockEntity = context.getLevel().getBlockEntity(clickedPos);
        RecipeManager recipeManager = context.getLevel().getRecipeManager();
        if (blockEntity == null) {
            return super.useOn(context);
        }
        Player player = context.getPlayer();
        if (player == null) {
            return super.useOn(context);
        }
        if (hasRecipe(itemInHand)) {
            return this.onPutRecipe(blockEntity, player, itemInHand);
        } else {
            return this.onRecordRecipe(context.getLevel(), player, blockEntity, recipeManager, itemInHand);
        }
    }

    private InteractionResult onPutRecipe(BlockEntity blockEntity, Player player, ItemStack itemInHand) {
        RecipeRecord record = getRecipe(itemInHand);
        if (record == null) {
            return InteractionResult.PASS;
        }

        ResourceLocation cookwareType;
        switch (blockEntity) {
            case PotBlockEntity potBlock -> {
                if (blockEntity.getLevel() != null && !potBlock.hasHeatSource(blockEntity.getLevel()))
                    return failRecipeOnHeatSource(player);
                cookwareType = POT;
            }
            case StockpotBlockEntity stockpotBlock -> {
                if (blockEntity.getLevel() != null && !stockpotBlock.hasHeatSource(blockEntity.getLevel()))
                    return failRecipeOnHeatSource(player);
                cookwareType = STOCKPOT;
            }
            case TeapotBlockEntity teapotBlock -> {
                if (blockEntity.getLevel() != null && !teapotBlock.hasHeatSource(blockEntity.getLevel()))
                    return failRecipeOnHeatSource(player);
                cookwareType = TEAPOT;
            }
            case null, default -> {
                return InteractionResult.PASS;
            }
        }
        if (!record.type().equals(cookwareType)) {
            return failRecipe(player, "wrong_container", parseType(record));
        }

        if (blockEntity instanceof PotBlockEntity pot) {
            if (pot.getStatus() != IPot.PUT_INGREDIENT || !pot.isEmpty()
                    || !hasValidInputs(record, pot.getInputs().size(), false)) {
                return failRecipe(player, "blocked");
            }
            BaseRequirement base = pot.getBlockState().getValue(PotBlock.HAS_OIL) ? null
                    : new BaseRequirement(stack -> stack.is(TagMod.OIL),
                    slot -> pot.onPlaceOil(player.level(), player, player.getInventory().getItem(slot)));
            return handlePutRecipe(player, record, base, () -> pot.addAllIngredients(record.input(), player));
        }

        if (blockEntity instanceof StockpotBlockEntity stockpot) {
            if ((stockpot.getStatus() != IStockpot.PUT_SOUP_BASE && stockpot.getStatus() != IStockpot.PUT_INGREDIENT)
                    || stockpot.hasLid() || !stockpot.isEmpty()
                    || !hasValidInputs(record, stockpot.getInputs().size(), false)) {
                return failRecipe(player, "blocked");
            }
            boolean needsBase = stockpot.getStatus() == IStockpot.PUT_SOUP_BASE;
            if (!needsBase && !stockpot.getSoupBaseId().equals(record.base())) {
                return failRecipe(player, "wrong_base", record.baseStack().getHoverName());
            }
            var soupBase = SoupBaseManager.getSoupBase(record.base());
            BaseRequirement base = !needsBase ? null : new BaseRequirement(
                    stack -> soupBase != null && soupBase.isSoupBase(stack),
                    slot -> stockpot.addSoupBase(player.level(), player, player.getInventory().getItem(slot)));
            return handlePutRecipe(player, record, base, () -> stockpot.addAllIngredients(record.input(), player));
        }

        TeapotBlockEntity teapot = (TeapotBlockEntity) blockEntity;
        if (teapot.getStatus() != ITeapot.PUT_INGREDIENT || !teapot.getInput().isEmpty()
                || !hasValidInputs(record, 1, true)) {
            return failRecipe(player, "blocked");
        }
        boolean needsBase = teapot.getTeaFluidId().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID);
        if (!needsBase && !ModFluids.matchesTeaFluid(record.base(), teapot.getTeaFluidId())) {
            return failRecipe(player, "wrong_base", record.baseStack().getHoverName());
        }
        ItemStack bucket = record.baseStack();
        BaseRequirement base = !needsBase ? null : new BaseRequirement(stack -> {
            var fluid = TeaFluidHelper.fromContainer(stack);
            return !bucket.isEmpty() && stack.is(bucket.getItem()) && fluid != null
                    && ModFluids.matchesTeaFluid(record.base(), fluid.id());
        }, slot -> teapot.addTeaFluid(player.level(), player, player.getInventory().getItem(slot),
                ContainerItemContext.ofPlayerSlot(player, PlayerInventoryStorage.of(player).getSlots().get(slot))));
        return handlePutRecipe(player, record, base, () -> teapot.addAllIngredients(record.input(), player));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasValidInputs(RecipeRecord record, int slots, boolean stacked) {
        return !record.input().isEmpty() && record.input().size() <= slots
                && record.input().stream().anyMatch(stack -> !stack.isEmpty())
                && (stacked || record.input().stream().allMatch(stack -> stack.isEmpty() || stack.getCount() == 1));
    }

    private static InteractionResult failRecipeOnHeatSource(Player player) {
        player.displayClientMessage(Component.translatable("tip.kaleidoscope_cookery.pot.need_lit_stove"), true);
        return InteractionResult.FAIL;
    }

    private static InteractionResult failRecipe(Player player, String reason, Object... args) {
        player.displayClientMessage(Component.translatable("tooltip.kaleidoscope_cookery.recipe_item." + reason, args), true);
        return InteractionResult.FAIL;
    }

    private InteractionResult handlePutRecipe(Player player, RecipeRecord record, @Nullable BaseRequirement base, Runnable success) {
        if (base != null) {
            int baseSlot = -1;
            var inventory = player.getInventory().items;
            for (int slot = 0; slot < inventory.size(); slot++) {
                if (!inventory.get(slot).isEmpty() && base.matches().test(inventory.get(slot))) {
                    baseSlot = slot;
                    break;
                }
            }
            if (baseSlot < 0) {
                player.displayClientMessage(Component.translatable("tooltip.kaleidoscope_cookery.recipe_item.missing")
                        .append(CommonComponents.SPACE).append(record.baseStack().getHoverName()).append("×1"), true);
                return InteractionResult.FAIL;
            }

            List<ItemStack> required = new ArrayList<>(record.input());
            if (!player.isCreative()) {
                required.add(inventory.get(baseSlot).copyWithCount(1));
            }
            Reference2IntMap<Item> missing = RecipeIngredientStorage.check(player, required);
            if (!missing.isEmpty()) {
                return missingIngredients(player, missing);
            }
            if (!base.insert().test(baseSlot)) {
                return failRecipe(player, "blocked");
            }
        }

        Reference2IntMap<Item> missing = RecipeIngredientStorage.extract(player, record.input());
        if (!missing.isEmpty()) {
            return missingIngredients(player, missing);
        }

        success.run();
        return InteractionResult.SUCCESS;
    }

    private InteractionResult missingIngredients(Player player, Reference2IntMap<Item> missing) {
        MutableComponent component = Component.translatable("tooltip.kaleidoscope_cookery.recipe_item.missing");
        for (Item item : missing.keySet()) {
            component.append(CommonComponents.SPACE).append(item.getDefaultInstance().getHoverName())
                    .append(Component.literal("×%d".formatted(missing.getInt(item))));
        }
        player.displayClientMessage(component, true);
        return InteractionResult.FAIL;
    }

    private record BaseRequirement(Predicate<ItemStack> matches, IntPredicate insert) {
    }

    private InteractionResult onRecordRecipe(Level level, Player player, BlockEntity blockEntity,
                                             RecipeManager recipeManager, ItemStack itemInHand) {
        if (blockEntity instanceof PotBlockEntity pot && pot.getStatus() == IPot.PUT_INGREDIENT) {
            List<ItemStack> inputs = pot.getInputs().stream().filter(s -> !s.isEmpty()).map(ItemStack::copy).toList();
            if (inputs.isEmpty()) {
                return InteractionResult.PASS;
            }
            ItemStack recordStack = itemInHand.split(1);
            RecipeResult recipeResult = getPotRecipeResult(level, recipeManager, pot, inputs, recordStack);
            setRecipe(recordStack, new RecipeRecord(inputs, recipeResult.output(), POT, recipeResult.flexRecipe(), ModSoupBases.OIL));
            ItemUtils.getItemToLivingEntity(player, recordStack);
            return InteractionResult.SUCCESS;
        }

        if (blockEntity instanceof StockpotBlockEntity stockpot && stockpot.getStatus() == IStockpot.PUT_INGREDIENT) {
            List<ItemStack> inputs = stockpot.getInputs().stream().filter(s -> !s.isEmpty()).map(ItemStack::copy).toList();
            if (inputs.isEmpty()) {
                return InteractionResult.PASS;
            }
            ItemStack recordStack = itemInHand.split(1);
            RecipeResult recipeResult = getStockpotRecipeResult(level, recipeManager, stockpot, inputs, recordStack);
            setRecipe(recordStack, new RecipeRecord(inputs, recipeResult.output(), STOCKPOT, recipeResult.flexRecipe(), stockpot.getSoupBaseId()));
            ItemUtils.getItemToLivingEntity(player, recordStack);
            return InteractionResult.SUCCESS;
        }
        if (blockEntity instanceof TeapotBlockEntity teapot && teapot.getStatus() == ITeapot.PUT_INGREDIENT) {
            ItemStack input = teapot.getInput();
            if (input.isEmpty()) {
                return InteractionResult.PASS;
            }
            // 如果数量大于 1，那么复制一个，其他的返回背包
            ItemStack recordStack = itemInHand.copyWithCount(1);
            int count = itemInHand.getCount();
            if (count > 1) {
                ItemEntity itemEntity = new ItemEntity(
                        level,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        new ItemStack(ModItems.RECIPE_ITEM, count - 1));
                itemEntity.setPickUpDelay(0);
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0.0F, 1.0F, 0.0F));
                level.addFreshEntity(
                        itemEntity
                );
            }
            TeapotInput container = new TeapotInput(teapot.getInput(), teapot.getTeaFluidId());
            recipeManager.getRecipeFor(ModRecipes.TEAPOT_RECIPE, container, level).ifPresentOrElse(recipe -> {
                ItemStack resultItem = recipe.value().getResultItem(level.registryAccess());
                setRecipe(recordStack, new RecipeRecord(Collections.singletonList(input.copy()), resultItem, TEAPOT, teapot.getTeaFluidId()));
            }, () -> {
                ItemStack instance = BuiltInRegistries.ITEM.get(TeacupRegistry.MYSTERY_TEA).getDefaultInstance();
                setRecipe(recordStack, new RecipeRecord(Collections.singletonList(input.copy()), instance, TEAPOT, teapot.getTeaFluidId()));
            });
            // 与炒锅/煮锅一致：写回记录好的配方物品并返回成功
            player.setItemInHand(player.swingingArm, recordStack);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private RecipeResult getPotRecipeResult(Level level, RecipeManager recipeManager, PotBlockEntity pot,
                                            List<ItemStack> inputs, ItemStack recordStack) {
        var container = pot.getContainer();
        var potRecipe = recipeManager.getRecipeFor(ModRecipes.POT_RECIPE, container, level);
        if (potRecipe.isPresent()) {
            ItemStack assemble = potRecipe.get().value()
                    .assemble(container, level.registryAccess());
            return new RecipeResult(assemble, false);
        }

        var flexPotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_POT_RECIPE, container, level);
        if (flexPotRecipe.isPresent()) {
            var recipe = flexPotRecipe.get();
            ItemStack result = recipe.value().assemble(container, level.registryAccess());
            setQuality(level, inputs, recipe.value().ingredients(), recipe.id(), result, recordStack);
            return new RecipeResult(result, true);
        }

        ItemStack instance = FoodBiteRegistry.getItem(FoodBiteRegistry.SUSPICIOUS_STIR_FRY).getDefaultInstance();
        return new RecipeResult(instance, false);
    }

    private RecipeResult getStockpotRecipeResult(Level level, RecipeManager recipeManager, StockpotBlockEntity stockpot,
                                                 List<ItemStack> inputs, ItemStack recordStack) {
        var container = stockpot.getInput();
        var stockpotRecipe = recipeManager.getRecipeFor(ModRecipes.STOCKPOT_RECIPE, container, level);
        if (stockpotRecipe.isPresent()) {
            ItemStack assemble = stockpotRecipe.get().value()
                    .assemble(container, level.registryAccess());
            return new RecipeResult(assemble, false);
        }

        var flexStockpotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_STOCKPOT_RECIPE, container, level);
        if (flexStockpotRecipe.isPresent()) {
            var recipe = flexStockpotRecipe.get();
            ItemStack result = recipe.value().assemble(container, level.registryAccess());
            setQuality(level, inputs, recipe.value().ingredients(), recipe.id(), result, recordStack);
            return new RecipeResult(result, true);
        }

        return new RecipeResult(BuiltInRegistries.ITEM.get(TeacupRegistry.MYSTERY_TEA).getDefaultInstance(), false);
    }

    private void setQuality(Level level, List<ItemStack> inputs, List<Ingredient> ingredients,
                            ResourceLocation recipeId, ItemStack result, ItemStack recordStack) {
        if (level instanceof ServerLevel serverLevel) {
            Quality quality = QualityEvaluator.evaluate(inputs, ingredients, recipeId, serverLevel.getSeed());
            QualityUtils.setQuality(result, quality);
            QualityUtils.setQuality(recordStack, quality);
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (hasRecipe(stack)) {
            RecipeRecord recipe = getRecipe(stack);
            if (recipe == null) {
                return Optional.empty();
            }
            Quality quality = null;
            if (QualityUtils.hasQuality(stack)) {
                quality = QualityUtils.getQuality(stack);
            }
            return Optional.of(new RecipeItemTooltip(recipe, quality));
        }
        return Optional.empty();
    }

    private record RecipeResult(ItemStack output, boolean flexRecipe) {
    }

    public record RecipeRecord(List<ItemStack> input, ItemStack output, ResourceLocation type, boolean flexRecipe,
                               ResourceLocation base) {
        public RecipeRecord(List<ItemStack> input, ItemStack output, ResourceLocation type) {
            this(input, output, type, false, ModSoupBases.WATER);
        }

        public RecipeRecord(List<ItemStack> input, ItemStack output, ResourceLocation type, boolean flexRecipe) {
            this(input, output, type, flexRecipe, ModSoupBases.WATER);
        }

        public RecipeRecord(List<ItemStack> input, ItemStack output, ResourceLocation type, ResourceLocation base) {
            this(input, output, type, false, base);
        }

        public ItemStack baseStack() {
            if (type.equals(POT)) {
                return ModItems.OIL.getDefaultInstance();
            }
            if (type.equals(STOCKPOT)) {
                var soupBase = SoupBaseManager.getSoupBase(base);
                return soupBase == null ? ItemStack.EMPTY : soupBase.getDisplayStack();
            }
            return type.equals(TEAPOT) ? TeaFluidHelper.getFilledContainer(base) : ItemStack.EMPTY;
        }

        public static final Codec<RecipeRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.OPTIONAL_CODEC.listOf().fieldOf("input").forGetter(RecipeRecord::input),
                ItemStack.CODEC.fieldOf("output").forGetter(RecipeRecord::output),
                ResourceLocation.CODEC.fieldOf("type").forGetter(RecipeRecord::type),
                Codec.BOOL.optionalFieldOf("flex_recipe", false).forGetter(RecipeRecord::flexRecipe),
                ResourceLocation.CODEC.optionalFieldOf("base", ModSoupBases.WATER).forGetter(RecipeRecord::base)
        ).apply(instance, RecipeRecord::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RecipeRecord> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull RecipeRecord decode(RegistryFriendlyByteBuf buffer) {
                int size = buffer.readVarInt();
                List<ItemStack> inputs = Lists.newArrayList();
                for (int i = 0; i < size; i++) {
                    inputs.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
                }
                ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
                ResourceLocation type = buffer.readResourceLocation();
                boolean flexRecipe = buffer.readBoolean();
                ResourceLocation base = buffer.readResourceLocation();
                return new RecipeRecord(inputs, output, type, flexRecipe, base);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, RecipeRecord value) {
                buffer.writeVarInt(value.input().size());
                for (ItemStack s : value.input()) {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, s);
                }
                ItemStack.STREAM_CODEC.encode(buffer, value.output());
                buffer.writeResourceLocation(value.type());
                buffer.writeBoolean(value.flexRecipe());
                buffer.writeResourceLocation(value.base());
            }
        };
        public static RecipeRecord pot(ItemLike output, ItemLike... input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), POT, false, ModSoupBases.OIL);
        }

        public static RecipeRecord pot(Item output, Item[] input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), POT, false, ModSoupBases.OIL);
        }

        public static RecipeRecord stockpot(ItemLike output, ItemLike... input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), STOCKPOT, false);
        }

        public static RecipeRecord stockpot(Item output, Item[] input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), STOCKPOT, false);
        }

        public static RecipeRecord teapot(ItemLike output, ItemLike... input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), TEAPOT, false);
        }

        public static RecipeRecord teapot(Item output, Item[] input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), TEAPOT, false);
        }
    }
}
