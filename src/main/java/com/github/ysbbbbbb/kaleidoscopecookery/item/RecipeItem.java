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
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.RecipeItemTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityEvaluator;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
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
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class RecipeItem extends WithTooltipsBlockItem {

    public static final Identifier POT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot");

    public static final Identifier STOCKPOT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot");

    public static final Identifier TEAPOT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot");

    private static final int NO_RECIPE = 0;
    private static final int HAS_RECIPE = 1;

    public RecipeItem(Block block, Properties properties) {
        super(block, properties, "recipe_item");
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

    public static boolean isAutoBind(ItemStack stack) {
        RecipeRecord record = getRecipe(stack);
        return record != null && record.autoBind();
    }

    public static void setAutoBind(ItemStack stack, boolean autoBind) {
        RecipeRecord record = getRecipe(stack);
        if (record != null) {
            setRecipe(stack, new RecipeRecord(record.input(), record.output(), record.type(), record.flexRecipe(), autoBind, record.base()));
        }
    }

    public static InteractionResult tryUseOnCookware(ItemStack stack, Level level, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof RecipeItem recipeItem)) {
            return InteractionResult.PASS;
        }
        if (isAutoBind(stack)) {
            return InteractionResult.PASS;
        }
        if (hasRecipe(stack)) {
            BlockEntity blockEntity = level.getBlockEntity(hitResult.getBlockPos());
            return tryPutRecipeOnCookware(stack, level, player, blockEntity);
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return recipeItem.useOn(new UseOnContext(player, hand, hitResult));
    }

    public static InteractionResult tryPutRecipeOnCookware(ItemStack stack, Level level, Player player, BlockEntity blockEntity) {
        if (!(stack.getItem() instanceof RecipeItem recipeItem)) {
            return InteractionResult.PASS;
        }
        if (isAutoBind(stack)) {
            return InteractionResult.PASS;
        }
        if (!(blockEntity instanceof PotBlockEntity) && !(blockEntity instanceof StockpotBlockEntity) && !(blockEntity instanceof TeapotBlockEntity)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return recipeItem.onPutRecipe(blockEntity, player, stack);
    }

    @SuppressWarnings("unused")
    @Deprecated
    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (hasRecipe(stack)) {
            return HAS_RECIPE;
        }
        return NO_RECIPE;
    }

    @Override
    public @NotNull Component getName(@NonNull ItemStack pStack) {
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

    private static @NonNull Component parseType(RecipeRecord recipe) {
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
        if (context.getLevel() instanceof ServerLevel serverLevel) {
            ItemStack itemInHand = context.getItemInHand();
            BlockPos clickedPos = context.getClickedPos();
            BlockEntity blockEntity = serverLevel.getLevel().getBlockEntity(clickedPos);
            RecipeManager recipeManager = serverLevel.getLevel().recipeAccess();
            if (blockEntity == null) {
                return super.useOn(context);
            }
            Player player = context.getPlayer();
            if (player == null) {
                return super.useOn(context);
            }
            if (hasRecipe(itemInHand)) {
                if (isAutoBind(itemInHand)) {
                    return InteractionResult.PASS;
                }
                return this.onPutRecipe(blockEntity, player, itemInHand);
            } else {
                InteractionHand hand = context.getHand();
                return this.onRecordRecipe(context.getLevel(), player, blockEntity, recipeManager, itemInHand, hand);
            }
        }
        return super.useOn(context);
    }

    private InteractionResult onPutRecipe(BlockEntity blockEntity, Player player, ItemStack itemInHand) {
        RecipeRecord record = getRecipe(itemInHand);
        if (record == null) {
            return InteractionResult.PASS;
        }

        Identifier cookwareType;
        switch (blockEntity) {
            case PotBlockEntity _ -> cookwareType = POT;
            case StockpotBlockEntity _ -> cookwareType = STOCKPOT;
            case TeapotBlockEntity _ -> cookwareType = TEAPOT;
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
                    slot -> pot.onPlaceOil(player.level(), player, player.getInventory().getNonEquipmentItems().get(slot)));
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
                    slot -> stockpot.addSoupBase(player.level(), player, player.getInventory().getNonEquipmentItems().get(slot)));
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
            return stack.is(bucket.getItem()) && fluid != null && ModFluids.matchesTeaFluid(record.base(), fluid.id());
        },
                slot -> teapot.addTeaFluid(player.level(), player, player.getInventory().getNonEquipmentItems().get(slot),
                        ContainerItemContext.ofPlayerSlot(player, PlayerInventoryStorage.of(player).getSlots().get(slot))));
        return handlePutRecipe(player, record, base, () -> teapot.addAllIngredients(record.input(), player));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasValidInputs(RecipeRecord record, int slots, boolean stacked) {
        return !record.input().isEmpty() && record.input().size() <= slots
                && record.input().stream().anyMatch(stack -> !stack.isEmpty())
                && (stacked || record.input().stream().allMatch(stack -> stack.isEmpty() || stack.getCount() == 1));
    }

    private static InteractionResult failRecipe(Player player, String reason, Object... args) {
        player.sendOverlayMessage(Component.translatable("tooltip.kaleidoscope_cookery.recipe_item." + reason, args));
        return InteractionResult.FAIL;
    }

    @NotNull
    private InteractionResult handlePutRecipe(Player player, RecipeRecord record, @Nullable BaseRequirement base, Runnable success) {
        if (base != null) {
            int baseSlot = -1;
            var inventory = player.getInventory().getNonEquipmentItems();
            for (int slot = 0; slot < inventory.size(); slot++) {
                if (!inventory.get(slot).isEmpty() && base.matches().test(inventory.get(slot))) {
                    baseSlot = slot;
                    break;
                }
            }
            if (baseSlot < 0) {
                player.sendOverlayMessage(Component.translatable("tooltip.kaleidoscope_cookery.recipe_item.missing")
                        .append(record.baseStack().getHoverName()).append("×1"));
                return InteractionResult.FAIL;
            }
            // 基底也可能是配方食材；一起预检，避免同一个桶被重复计数。
            List<ItemStack> required = new ArrayList<>(record.input());
            if (!player.isCreative()) {
                required.add(inventory.get(baseSlot).copyWithCount(1));
            }
            Reference2IntMap<Item> missing = RecipeIngredientStorage.check(player, required);
            if (!missing.isEmpty()) {
                return missingIngredients(player, missing);
            }
            // 预检事务会恢复槽位，加入基底时必须重新取得当前槽位中的物品。
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
        player.sendOverlayMessage(component);
        return InteractionResult.FAIL;
    }

    private record BaseRequirement(Predicate<ItemStack> matches, IntPredicate insert) {
    }

    private InteractionResult onRecordRecipe(Level level, Player player, BlockEntity blockEntity, RecipeManager recipeManager,
                                             ItemStack itemInHand, InteractionHand hand) {
        //2n-65 就很离谱
        if (blockEntity instanceof PotBlockEntity pot && pot.getStatus() == IPot.PUT_INGREDIENT) {
            List<ItemStack> inputs = pot.getInputs().stream().filter(s -> !s.isEmpty()).map(ItemStack::copy).toList();
            if (inputs.isEmpty()) {
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
            RecipeResult recipeResult = getPotRecipeResult(level, recipeManager, pot, inputs, recordStack);
            setRecipe(recordStack, new RecipeRecord(inputs, recipeResult.output(), POT, recipeResult.flexRecipe()));
            player.setItemInHand(hand, recordStack);
            return InteractionResult.SUCCESS;
        }

        if (blockEntity instanceof StockpotBlockEntity stockpot && stockpot.getStatus() == IStockpot.PUT_INGREDIENT) {
            List<ItemStack> inputs = stockpot.getInputs().stream().filter(s -> !s.isEmpty()).map(ItemStack::copy).toList();
            if (inputs.isEmpty()) {
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
            RecipeResult recipeResult = getStockpotRecipeResult(level, recipeManager, stockpot, inputs, recordStack);
            setRecipe(recordStack, new RecipeRecord(inputs, recipeResult.output(), STOCKPOT, recipeResult.flexRecipe(), stockpot.getSoupBaseId()));
            player.setItemInHand(hand, recordStack);
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
                ItemStack instance = BuiltInRegistries.ITEM.getValue(TeacupRegistry.MYSTERY_TEA).getDefaultInstance();
                setRecipe(recordStack, new RecipeRecord(Collections.singletonList(input.copy()), instance, TEAPOT, teapot.getTeaFluidId()));
            });
            // 与炒锅/煮锅一致：写回记录好的配方物品并返回成功
            player.setItemInHand(hand, recordStack);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private RecipeResult getPotRecipeResult(Level level, RecipeManager recipeManager, PotBlockEntity pot,
                                            List<ItemStack> inputs, ItemStack recordStack) {
        var container = pot.getInput();
        var potRecipe = recipeManager.getRecipeFor(ModRecipes.POT_RECIPE, container, level);
        if (potRecipe.isPresent()) {
            ItemStack resultItem = potRecipe.get().value().getResultItem(level.registryAccess());
            return new RecipeResult(resultItem, false);
        }

        var flexPotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_POT_RECIPE, container, level);
        if (flexPotRecipe.isPresent()) {
            var recipe = flexPotRecipe.get();
            ItemStack result = recipe.value().assemble(container, level.registryAccess());
            setQuality(level, inputs, recipe.value().ingredients(), recipe.id().identifier(), result, recordStack);
            return new RecipeResult(result, true);
        }

        ItemStack instance = FoodBiteRegistry.getItem(FoodBiteRegistry.SUSPICIOUS_STIR_FRY).getDefaultInstance();
        return new RecipeResult(instance, false);
    }

    private RecipeResult getStockpotRecipeResult(Level level, RecipeManager recipeManager, StockpotBlockEntity stockpot,
                                                 List<ItemStack> inputs, ItemStack recordStack) {
        var container = stockpot.getContainer();
        var stockpotRecipe = recipeManager.getRecipeFor(ModRecipes.STOCKPOT_RECIPE, container, level);
        if (stockpotRecipe.isPresent()) {
            ItemStack resultItem = stockpotRecipe.get().value().getResultItem(level.registryAccess());
            return new RecipeResult(resultItem, false);
        }

        var flexStockpotRecipe = recipeManager.getRecipeFor(ModRecipes.FLEX_STOCKPOT_RECIPE, container, level);
        if (flexStockpotRecipe.isPresent()) {
            var recipe = flexStockpotRecipe.get();
            ItemStack result = recipe.value().assemble(container, level.registryAccess());
            setQuality(level, inputs, recipe.value().ingredients(), recipe.id().identifier(), result, recordStack);
            return new RecipeResult(result, true);
        }

        return new RecipeResult(Items.SUSPICIOUS_STEW.getDefaultInstance(), false);
    }

    private void setQuality(Level level, List<ItemStack> inputs, List<Ingredient> ingredients,
                            Identifier recipeId, ItemStack result, ItemStack recordStack) {
        if (level instanceof ServerLevel serverLevel) {
            Quality quality = QualityEvaluator.evaluate(inputs, ingredients, recipeId, serverLevel.getSeed());
            QualityUtils.setQuality(result, quality);
            QualityUtils.setQuality(recordStack, quality);
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NonNull ItemStack stack) {
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

    public record RecipeRecord(List<ItemStack> input, ItemStack output, Identifier type, boolean flexRecipe, boolean autoBind, Identifier base) {
        public RecipeRecord(List<ItemStack> input, ItemStack output, Identifier type) {
            this(input, output, type, false, false);
        }

        public RecipeRecord(List<ItemStack> input, ItemStack output, Identifier type, boolean flexRecipe) {
            this(input, output, type, flexRecipe, false);
        }

        public RecipeRecord(List<ItemStack> input, ItemStack output, Identifier type, boolean flexRecipe, boolean autoBind) {
            this(input, output, type, flexRecipe, autoBind, ModSoupBases.WATER);
        }

        public RecipeRecord(List<ItemStack> input, ItemStack output, Identifier type, Identifier base) {
            this(input, output, type, false, false, base);
        }

        public RecipeRecord(List<ItemStack> input, ItemStack output, Identifier type, boolean flexRecipe, Identifier base) {
            this(input, output, type, flexRecipe, false, base);
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
                Identifier.CODEC.fieldOf("type").forGetter(RecipeRecord::type),
                Codec.BOOL.optionalFieldOf("flex_recipe", false).forGetter(RecipeRecord::flexRecipe),
                Codec.BOOL.optionalFieldOf("auto_bind", false).forGetter(RecipeRecord::autoBind),
                Identifier.CODEC.optionalFieldOf("base", ModSoupBases.WATER).forGetter(RecipeRecord::base)
        ).apply(instance, RecipeRecord::new));

        public static final RecipeRecord INSTANCE = new RecipeRecord(List.of(), ItemStack.EMPTY, POT, false, false);

        public static final StreamCodec<RegistryFriendlyByteBuf, RecipeRecord> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull RecipeRecord decode(RegistryFriendlyByteBuf buffer) {
                int size = buffer.readVarInt();
                List<ItemStack> inputs = Lists.newArrayList();
                for (int i = 0; i < size; i++) {
                    inputs.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
                }
                ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
                Identifier type = buffer.readIdentifier();
                boolean flexRecipe = buffer.readBoolean();
                boolean autoBind = buffer.readBoolean();
                Identifier base = buffer.readIdentifier();
                return new RecipeRecord(inputs, output, type, flexRecipe, autoBind, base);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, RecipeRecord value) {
                buffer.writeVarInt(value.input().size());
                for (ItemStack s : value.input()) {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, s);
                }
                ItemStack.STREAM_CODEC.encode(buffer, value.output());
                buffer.writeIdentifier(value.type());
                buffer.writeBoolean(value.flexRecipe());
                buffer.writeBoolean(value.autoBind());
                buffer.writeIdentifier(value.base());
            }
        };

        public static RecipeRecord pot(ItemLike output, ItemLike... input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), POT);
        }

        public static RecipeRecord pot(Item output, Item[] input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), POT);
        }

        public static RecipeRecord stockpot(ItemLike output, ItemLike... input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), STOCKPOT);
        }

        public static RecipeRecord stockpot(Item output, Item[] input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), STOCKPOT);
        }

        public static RecipeRecord teapot(ItemLike output, ItemLike... input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), TEAPOT);
        }

        public static RecipeRecord teapot(Item output, Item[] input) {
            List<ItemStack> inputList = Arrays.stream(input).map(ItemStack::new).toList();
            return new RecipeRecord(inputList, new ItemStack(output), TEAPOT);
        }
    }

    public record RecipeTemplate(List<Identifier> input, Identifier output, Identifier type, Identifier base) {
        public RecipeTemplate(List<Identifier> input, Identifier output, Identifier type) {
            this(input, output, type, ModSoupBases.WATER);
        }

        public static final Codec<RecipeTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.listOf().fieldOf("input").forGetter(RecipeTemplate::input),
                Identifier.CODEC.fieldOf("output").forGetter(RecipeTemplate::output),
                Identifier.CODEC.fieldOf("type").forGetter(RecipeTemplate::type),
                Identifier.CODEC.optionalFieldOf("base", ModSoupBases.WATER).forGetter(RecipeTemplate::base)
        ).apply(instance, RecipeTemplate::new));
        public static final RecipeTemplate EMPTY = new RecipeTemplate(List.of(), Identifier.fromNamespaceAndPath("minecraft", "air"), POT);

        public RecipeRecord toRecipeRecord() {
            List<ItemStack> inputs = this.input.stream()
                    .map(id -> BuiltInRegistries.ITEM.getValue(id).getDefaultInstance())
                    .toList();
            ItemStack outputStack = BuiltInRegistries.ITEM.getValue(this.output).getDefaultInstance();
            return new RecipeRecord(inputs, outputStack, this.type, this.base);
        }
    }
}
