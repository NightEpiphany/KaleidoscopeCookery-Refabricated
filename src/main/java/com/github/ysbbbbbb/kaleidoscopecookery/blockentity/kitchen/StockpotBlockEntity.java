package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ServerThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.StockpotMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.FluidSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityEvaluator;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.particle.StockpotParticleOptions;
import com.github.ysbbbbbb.kaleidoscopecookery.util.BlockDrop;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class StockpotBlockEntity extends BaseBlockEntity implements IStockpot {
    public static final int MAX_TAKEOUT_COUNT = 9;

    private static final String RECIPE_ID = "RecipeId";
    private static final String SOUP_BASE_ID = "SoupBaseId";
    private static final String SOUP_BASE_ITEM = "SoupBaseItem";
    private static final String RESULT = "Result";
    private static final String STATUS = "Status";
    private static final String CURRENT_TICK = "CurrentTick";
    private static final String TAKEOUT_COUNT = "TakeoutCount";
    private static final String LID_ITEM = "LidItem";
    private static final String COOKING_TEXTURE = "CookingTexture";
    private static final String FINISHED_TEXTURE = "FinishedTexture";
    private static final String CARRIER = "Carrier";
    private static final String COOKING_BUBBLE_COLOR = "CookingBubbleColor";
    private static final String FINISHED_BUBBLE_COLOR = "FinishedBubbleColor";
    private static final String FLEX_RECIPE = "FlexRecipe";
    private static final String AUTOMATION_RECIPE_ID = "AutomationRecipeId";

    private final RecipeManager.CachedCheck<StockpotInput, StockpotRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.STOCKPOT_RECIPE);
    private final RecipeManager.CachedCheck<StockpotInput, FlexStockpotRecipe> flexQuickCheck = RecipeManager.createCheck(ModRecipes.FLEX_STOCKPOT_RECIPE);

    private NonNullList<ItemStack> inputs = NonNullList.withSize(StockpotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
    private Identifier recipeId = StockpotRecipeSerializer.EMPTY_ID;
    private Identifier soupBaseId = ModSoupBases.WATER;
    private ItemStack soupBaseItem = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;
    private int status = PUT_SOUP_BASE;
    private int currentTick = -1;
    private int takeoutCount = 0;
    private Ingredient carrier = StockpotRecipeSerializer.DEFAULT_CARRIER;
    private Identifier cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
    private Identifier finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
    private int cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
    private int finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;
    private boolean flexRecipe = false;
    private @Nullable Identifier automationRecipeId;

    // 强制刷新到服务器主线程，用于区块序列化存储
    private volatile boolean hasLidCached = false;
    /**
     * 盖子，因为盖子可以当做盾牌，所以会记录很多额外内容，需要专门保存
     */
    private ItemStack lidItem = ItemStack.EMPTY;

    /**
     * 主要用于客户端渲染的字段，recipe 里缓存了数据包中定义的部分客户端渲染需要的东西
     */
    public RecipeHolder<StockpotRecipe> recipe = StockpotRecipeSerializer.getEmptyRecipe();
    public @Nullable Entity renderEntity = null;

    public StockpotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.STOCKPOT_BE, pPos, pBlockState);
    }

    public void clientTick() {
        if (this.renderEntity != null) {
            this.renderEntity.tickCount++;
        }
    }

    public StockpotInput getInput() {
        return new StockpotInput(this.inputs, this.soupBaseId);
    }

    @Override
    public boolean hasHeatSource(Level level) {
        BlockState belowState = level.getBlockState(worldPosition.below());
        if (belowState.hasProperty(BlockStateProperties.LIT)) {
            return belowState.getValue(BlockStateProperties.LIT);
        }
        return belowState.is(TagMod.HEAT_SOURCE_BLOCKS_WITHOUT_LIT);
    }

    @Override
    public boolean hasLid() {
        if (level == null) {
            return false;
        }
        BlockState blockState = level.getBlockState(worldPosition);
        return level != null && blockState.hasProperty(StockpotBlock.HAS_LID)
               && blockState.getValue(StockpotBlock.HAS_LID);
    }

    public void tick(Level level) {
        // 没放入汤底时，不进行任何 tick
        if (this.status == PUT_SOUP_BASE) {
            return;
        }
        // 下方没有火源
        if (!this.hasHeatSource(level)) {
            return;
        }

        boolean hasLid = this.hasLid();
        this.hasLidCached = hasLid;
        // 音效播放
        if (level.getGameTime() % 15 == 0) {
            float volume = hasLid ? 0.075f : 0.2f;
            float pitch = hasLid ? 0.1f + level.getRandom().nextFloat() * 0.05f : 1f + level.getRandom().nextFloat() * 0.1f;
            level.playSound(null,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    ModSounds.BLOCK_STOCKPOT, SoundSource.BLOCKS, volume, pitch);
        }
        // 没有盖子时，不进行任何 tick，只生成粒子
        if (!hasLid) {
            this.spawnParticleWithoutLid(level);
            // 不在进行后续逻辑计算
            return;
        } else {
            // 有盖子时，生成白色粒子
            this.spawnParticleWithLid(level);
        }

        // 如果当前状态是放入素材，且素材不为空
        // 因为 isEmpty() 可能耗时，所以每隔 5 tick 检查一次
        if (status == PUT_INGREDIENT && level.getGameTime() % 5 == 0 && !this.isEmpty() && level instanceof ServerLevel serverLevel) {
            this.setRecipe(serverLevel);
            status = COOKING;
            this.refresh();
            return;
        }

        // 如果当前状态是烹饪中，递减当前 tick
        if (status == COOKING) {
            if (currentTick > 0) {
                currentTick--;
                return;
            }
            status = FINISHED;
            currentTick = -1;
            this.inputs.clear();
            this.refresh();
        }
    }

    public void addAllIngredients(List<ItemStack> ingredients, LivingEntity user) {
        if (this.level == null) {
            return;
        }
        if (this.hasLid()) {
            return;
        }
        if (this.status != PUT_INGREDIENT) {
            return;
        }
        for (int i = 0; i < Math.min(ingredients.size(), this.inputs.size()); i++) {
            ItemStack stack = ingredients.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            // 如果带有容器，此时返还容器
            Item containerItem = ItemUtils.getContainerItem(stack);
            if (containerItem != Items.AIR) {
                ItemUtils.getItemToLivingEntity(user, containerItem.getDefaultInstance());
            }
            this.inputs.set(i, stack.copyWithCount(1));
        }
        level.playSound(null, this.worldPosition,
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        this.refresh();
    }

    private void spawnParticleWithLid(Level level) {
        if (level instanceof ServerLevel serverLevel && level.getRandom().nextFloat() < 0.05F) {
            RandomSource random = serverLevel.getRandom();
            serverLevel.sendParticles(ModParticles.COOKING,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.375 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }
    }

    private void spawnParticleWithoutLid(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getRandom().nextFloat() < 0.25F) {
            int color = this.getBubbleColor();
            serverLevel.sendParticles(new StockpotParticleOptions(PortHelper.fromRGB24(color).toVector3f(), 1f),
                    worldPosition.getX() + 0.25 + (level.getRandom().nextFloat() * 0.5F),
                    worldPosition.getY() + 0.375,
                    worldPosition.getZ() + 0.25 + (level.getRandom().nextFloat() * 0.5F),
                    2,
                    (level.getRandom().nextFloat() - 0.5) * 0.1F,
                    0,
                    (level.getRandom().nextFloat() - 0.5) * 0.1F,
                    0);
        }
    }

    private int getBubbleColor() {
        // 需要检查下 recipe 是否更新
        if (this.level instanceof ServerLevel serverLevel
            && !StockpotRecipeSerializer.EMPTY_ID.equals(this.recipeId)
            && !this.flexRecipe
            && StockpotRecipeSerializer.EMPTY_ID.equals(this.recipe.id().identifier())) {
            ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, this.recipeId);
            RecipeHolder<StockpotRecipe> stockpotRecipe = serverLevel.recipeAccess().byKeyTyped(ModRecipes.STOCKPOT_RECIPE, recipeKey);
            this.recipe = Objects.requireNonNullElseGet(stockpotRecipe, StockpotRecipeSerializer::getEmptyRecipe);
        }
        if (status == COOKING) {
            return this.cookingBubbleColor;
        }
        if (status == FINISHED) {
            return this.finishedBubbleColor;
        }
        ISoupBase soup = this.getSoupBase();
        if (soup != null) {
            return soup.getBubbleColor();
        }
        return 0xffffff;
    }

    @Override
    public boolean onLidClick(Level level, LivingEntity user, ItemStack stack) {
        BlockState blockState = level.getBlockState(worldPosition);
        boolean hasLid = this.hasLid();
        this.hasLidCached = hasLid;

        // 第一种情况，放上盖子
        if (!hasLid && stack.is(ModItems.STOCKPOT_LID)) {
            this.setLidItem(stack.split(1));
            this.setChanged();
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, true));
            user.playSound(SoundEvents.LANTERN_PLACE, 0.5F, 0.5F);
            ModTrigger.EVENT.trigger(user, ModEventTriggerType.USE_LID_ON_STOCKPOT);
            return true;
        }

        // 第二种情况，取下盖子
        if (hasLid) {
            ItemStack lid = this.getLidItem().isEmpty() ? ModItems.STOCKPOT_LID.getDefaultInstance() : this.getLidItem().copy();
            this.setLidItem(ItemStack.EMPTY);
            if (stack.isEmpty()) {
                user.setItemInHand(InteractionHand.MAIN_HAND, lid);
            } else {
                BlockDrop.popResource(level, worldPosition, 0.5, lid);
            }
            this.setChanged();
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, false));
            user.playSound(SoundEvents.LANTERN_BREAK, 0.5F, 0.5F);
            return true;
        }

        return false;
    }

    public StockpotInput getContainer() {
        return new StockpotInput(this.inputs, this.soupBaseId);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRecipe(ServerLevel levelIn) {
        StockpotInput container = this.getContainer();

        StockpotMatchRecipeEvent.Pre preEvent = new StockpotMatchRecipeEvent.Pre(levelIn, this, container);
        ModEvents.STOCKPOT_RECIPE_PRE.invoker().onStockMatchRecipePre(preEvent);
        if (preEvent.getOutput() != null) {
            this.applyRecipe(levelIn, container, preEvent.getOutput());
        }

        this.quickCheck.getRecipeFor(container, levelIn).ifPresentOrElse(recipe -> this.applyRecipe(levelIn, container, recipe), () -> this.flexQuickCheck.getRecipeFor(container, levelIn).ifPresentOrElse(recipe -> this.applyFlexRecipe(levelIn, container, recipe), () -> {
            this.recipeId = StockpotRecipeSerializer.EMPTY_ID;
            this.recipe = StockpotRecipeSerializer.getEmptyRecipe();
            this.flexRecipe = false;
            this.result = Items.SUSPICIOUS_STEW.getDefaultInstance();
            this.currentTick = StockpotRecipeSerializer.DEFAULT_TIME;
            this.takeoutCount = 1;
            this.carrier = StockpotRecipeSerializer.DEFAULT_CARRIER;
            this.cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
            this.finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
            this.cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
            this.finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;
        }));

        // 触发事件，允许其他 mod 在配方匹配后进行操作
        StockpotMatchRecipeEvent.Post postEvent = new StockpotMatchRecipeEvent.Post(levelIn, this, container, this.recipeId);
        ModEvents.STOCKPOT_RECIPE_POST.invoker().onStockMatchRecipePost(postEvent);
        if (postEvent.getOutput() != null) {
            this.applyRecipe(levelIn, container, postEvent.getOutput());
        }
    }

    private void applyRecipe(Level level, StockpotInput container, RecipeHolder<StockpotRecipe> recipe) {
        this.recipeId = recipe.id().identifier();
        this.recipe = recipe;
        this.flexRecipe = false;
        this.result = recipe.value().assemble(container, level.registryAccess());
        this.currentTick = recipe.value().time();
        this.takeoutCount = Math.min(this.result.getCount(), MAX_TAKEOUT_COUNT);
        this.carrier = recipe.value().carrier();
        this.cookingTexture = recipe.value().cookingTexture();
        this.finishedTexture = recipe.value().finishedTexture();
        this.cookingBubbleColor = recipe.value().cookingBubbleColor();
        this.finishedBubbleColor = recipe.value().finishedBubbleColor();
    }

    private static boolean isFilledFluidContainer(ItemStack stack) {
        if (stack.getItem() instanceof BucketItem bucketItem && bucketItem.content != Fluids.EMPTY) {
            return true;
        }
        return FluidUtils.hasFluid(stack);
    }

    private void applyFlexRecipe(ServerLevel level, StockpotInput container, RecipeHolder<FlexStockpotRecipe> recipe) {
        FlexStockpotRecipe value = recipe.value();
        this.recipeId = recipe.id().identifier();
        this.recipe = StockpotRecipeSerializer.getEmptyRecipe();
        this.flexRecipe = true;
        this.result = value.assemble(container, level.registryAccess());
        this.currentTick = value.time();
        this.takeoutCount = Math.min(this.result.getCount(), MAX_TAKEOUT_COUNT);
        this.carrier = value.carrier();
        this.cookingTexture = value.cookingTexture();
        this.finishedTexture = value.finishedTexture();
        this.cookingBubbleColor = value.cookingBubbleColor();
        this.finishedBubbleColor = value.finishedBubbleColor();

        List<ItemStack> nonEmptyInputs = this.inputs.stream().filter(stack -> !stack.isEmpty()).toList();
        Quality quality = QualityEvaluator.evaluate(nonEmptyInputs, value.ingredients(), recipe.id().identifier(), level.getSeed());
        QualityUtils.setQuality(this.result, quality);
    }

    @Override
    public boolean addSoupBase(Level level, LivingEntity user, ItemStack bucket) {
        // 必须打开盖子才能放入汤底
        if (this.hasLid()) {
            return false;
        }
        // 当前状态是放入汤底
        if (this.status != PUT_SOUP_BASE) {
            return false;
        }
        for (var entry : SoupBaseManager.getAllSoupBases().entrySet()) {
            Identifier key = entry.getKey();
            ISoupBase soupBase = entry.getValue();
            if (soupBase.isSoupBase(bucket)) {
                this.soupBaseId = key;
                this.soupBaseItem = bucket.copyWithCount(1);
                this.renderEntity = null;
                this.status = PUT_INGREDIENT;
                this.refresh();

                ItemStack container = soupBase.getReturnContainer(level, user, bucket);
                if (user instanceof Player player && !player.isCreative())
                    bucket.shrink(1);
                ItemUtils.getItemToLivingEntity(user, container);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeSoupBase(Level level, LivingEntity user, ItemStack bucket) {
        // 当前是放入食材，但是还没放
        if (this.status == PUT_INGREDIENT && this.isEmpty() && SoupBaseManager.containsSoupBase(this.soupBaseId)) {
            ISoupBase soupBase = this.getSoupBase();
            if (soupBase == null || !soupBase.isContainer(bucket)) {
                return false;
            }
            this.renderEntity = null;
            this.soupBaseId = ModSoupBases.WATER;
            this.soupBaseItem = ItemStack.EMPTY;
            this.status = PUT_SOUP_BASE;
            this.refresh();

            ItemStack container = soupBase.getReturnSoupBase(level, user, bucket);
            if (user instanceof Player player && !player.isCreative())
                bucket.shrink(1);
            ItemUtils.getItemToLivingEntity(user, container);
            return true;
        }
        return false;
    }

    @Override
    public boolean addIngredient(Level level, LivingEntity user, ItemStack itemStack) {
        if (this.hasLid()) {
            return false;
        }
        if (this.status != PUT_INGREDIENT) {
            return false;
        }
        if (itemStack.is(TagMod.INGREDIENT_BLOCKLIST)) {
            return false;
        }
        // 容器只有对应上已注册的汤底才被视为合法交互
        if (isFilledFluidContainer(itemStack)) {
            return false;
        }
        // 检查是否有足够的空间放入食材
        for (int i = 0; i < this.inputs.size(); i++) {
            if (!this.inputs.get(i).isEmpty()) {
                continue;
            }
            // 如果带有容器，此时返还容器
            Item containerItem = ItemUtils.getContainerItem(itemStack);
            if (!containerItem.getDefaultInstance().isEmpty()) {
                ItemUtils.getItemToLivingEntity(user, containerItem.getDefaultInstance());
            }
            this.inputs.set(i, itemStack.split(1));
            level.playSound(null, user.getX(), user.getY() + 0.5, user.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            this.refresh();
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean removeIngredient(Level level, LivingEntity user) {
        if (hasLid()) {
            return false;
        }
        if (status != PUT_INGREDIENT) {
            return false;
        }
        for (int i = this.inputs.size() - 1; i >= 0; i--) {
            ItemStack stack = this.inputs.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            // 检查容器是否符合取出条件
            if (!containerIsMatch(user, stack)) {
                return false;
            }
            if (stack.has(ModDataComponents.SPECIAL_RENDER))
                stack.remove(ModDataComponents.SPECIAL_RENDER);
            ItemUtils.getItemToLivingEntity(user, stack.copy());
            this.inputs.set(i, ItemStack.EMPTY);
            // 如果是流体汤底，且温度过高，玩家会受到伤害
            ISoupBase soupBase = this.getSoupBase();
            if (soupBase instanceof FluidSoupBase fluidSoupBase && fluidSoupBase.getFluid().is(FluidTags.LAVA)) {
                user.hurt(level.damageSources().inFire(), 1);
                ModTrigger.EVENT.trigger(user, ModEventTriggerType.HURT_WHEN_TAKEOUT_FROM_STOCKPOT);
            }
            this.refresh();
            return true;
        }
        return false;
    }

    private boolean containerIsMatch(LivingEntity user, ItemStack stack) {
        Item containerItem = ItemUtils.getContainerItem(stack);
        if (containerItem == Items.AIR) {
            return true;
        }
        if (user.getMainHandItem().is(containerItem)) {
            user.getMainHandItem().shrink(1);
            return true;
        }
        sendActionBarMessage(user, "tip.kaleidoscope_cookery.kitchen.remove_ingredient.need_container",
                containerItem.getDefaultInstance().getHoverName());
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean takeOutProduct(Level level, LivingEntity user, ItemStack stack) {
        if (level.isClientSide()) return false;
        if (this.hasLid()) {
            return false;
        }
        // 如果当前状态是烹饪完成
        if (status != FINISHED || this.result.isEmpty() || this.takeoutCount <= 0) {
            return false;
        }
        // 兼容容器是否正确
        Ingredient carrier = this.carrier;
        if (!carrier.isEmpty() && !carrier.test(stack)) {
            Component carrierName = carrier.items()
                    .findFirst()
                    .map(holder -> holder.value().getDefaultInstance().getHoverName())
                    .orElse(ItemStack.EMPTY.getHoverName());
            this.sendActionBarMessage(user, "tip.kaleidoscope_cookery.pot.need_carrier", carrierName);
            return true;
        }
        if (!carrier.isEmpty()) {
            stack.shrink(1);
        }

        ItemStack resultCopy = this.result.copyWithCount(1);
        ItemUtils.getItemToLivingEntity(user, resultCopy);
        this.takeoutCount--;
        if (this.takeoutCount <= 0) {
            this.status = PUT_SOUP_BASE;
            this.inputs.clear();
            this.recipeId = StockpotRecipeSerializer.EMPTY_ID;
            this.soupBaseId = ModSoupBases.WATER;
            this.soupBaseItem = ItemStack.EMPTY;
            this.result = ItemStack.EMPTY;
            this.currentTick = -1;
            this.renderEntity = null;
            this.flexRecipe = false;
            this.carrier = StockpotRecipeSerializer.DEFAULT_CARRIER;
            this.cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
            this.finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
            this.cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
            this.finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;
        }
        this.refresh();
        return true;
    }

    private void sendActionBarMessage(LivingEntity user, String key, Object... args) {
        if (user instanceof ServerPlayer serverPlayer) {
            MutableComponent message = Component.translatable(key, args);
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
    }

    @ServerThreadSafe
    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ContainerHelper.saveAllItems(valueOutput, this.inputs);
        valueOutput.putString(RECIPE_ID, this.recipeId.toString());
        valueOutput.putString(SOUP_BASE_ID, this.soupBaseId.toString());
        valueOutput.storeNullable(SOUP_BASE_ITEM, ItemStack.CODEC, this.soupBaseItem.isEmpty() ? null : this.soupBaseItem);
        if (!this.result.isEmpty())
            valueOutput.storeNullable(RESULT, ItemStack.CODEC, this.result);
        valueOutput.putInt(STATUS, this.status);
        valueOutput.putInt(CURRENT_TICK, this.currentTick);
        valueOutput.putInt(TAKEOUT_COUNT, this.takeoutCount);
        valueOutput.putBoolean(FLEX_RECIPE, this.flexRecipe);
        valueOutput.store(CARRIER, Ingredient.CODEC, this.carrier);
        valueOutput.putString(COOKING_TEXTURE, this.cookingTexture.toString());
        valueOutput.putString(FINISHED_TEXTURE, this.finishedTexture.toString());
        valueOutput.putInt(COOKING_BUBBLE_COLOR, this.cookingBubbleColor);
        valueOutput.putInt(FINISHED_BUBBLE_COLOR, this.finishedBubbleColor);
        if (this.hasLidCached && !this.lidItem.isEmpty())
            valueOutput.storeNullable(LID_ITEM, ItemStack.CODEC, this.lidItem);
        if (this.automationRecipeId != null) {
            valueOutput.putString(AUTOMATION_RECIPE_ID, this.automationRecipeId.toString());
        }
    }

    @ServerThreadSafe
    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.inputs = NonNullList.withSize(StockpotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
        this.soupBaseItem = ItemStack.EMPTY;
        ContainerHelper.loadAllItems(valueInput, this.inputs);
        if (valueInput.contains(RECIPE_ID)) {
            this.recipeId = Objects.requireNonNullElse(
                    Identifier.tryParse(valueInput.getString(RECIPE_ID).orElse(StockpotRecipeSerializer.EMPTY_ID.toString())),
                    StockpotRecipeSerializer.EMPTY_ID
            );
            if (this.level != null && this.level instanceof ServerLevel serverLevel) {
                ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, this.recipeId);
                RecipeHolder<StockpotRecipe> stockpotRecipe = serverLevel.recipeAccess().byKeyTyped(ModRecipes.STOCKPOT_RECIPE, recipeKey);
                this.recipe = Objects.requireNonNullElseGet(stockpotRecipe, StockpotRecipeSerializer::getEmptyRecipe);
            }
            if (valueInput.contains(SOUP_BASE_ID)) {
                this.soupBaseId = Identifier.tryParse(valueInput.getString(SOUP_BASE_ID).orElse(ModSoupBases.WATER.toString()));
                assert this.soupBaseId != null;
                if (ModFluids.VANILLA_MILK_ID.equals(this.soupBaseId)) {
                    this.soupBaseId = ModFluids.MILK_ID;
                }
            }
            if (valueInput.contains(SOUP_BASE_ITEM)) {
                this.soupBaseItem = valueInput.read(SOUP_BASE_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            }
            if (valueInput.contains(RESULT)) this.result = valueInput.read(RESULT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            this.status = valueInput.getIntOr(STATUS, PUT_SOUP_BASE);
            this.currentTick = valueInput.getIntOr(CURRENT_TICK, 0);
            this.takeoutCount = valueInput.getIntOr(TAKEOUT_COUNT, 0);
            this.flexRecipe = valueInput.getBooleanOr(FLEX_RECIPE, false);
            this.carrier = valueInput.read(CARRIER, Ingredient.CODEC).orElse(StockpotRecipeSerializer.DEFAULT_CARRIER);
            this.cookingTexture = Objects.requireNonNullElse(
                    Identifier.tryParse(valueInput.getString(COOKING_TEXTURE).orElse(StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE.toString())),
                    StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE
            );
            this.finishedTexture = Objects.requireNonNullElse(
                    Identifier.tryParse(valueInput.getString(FINISHED_TEXTURE).orElse(StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE.toString())),
                    StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE
            );
            this.cookingBubbleColor = valueInput.getIntOr(COOKING_BUBBLE_COLOR, StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR);
            this.finishedBubbleColor = valueInput.getIntOr(FINISHED_BUBBLE_COLOR, StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR);
            if (valueInput.contains(LID_ITEM)) this.lidItem = valueInput.read(LID_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        }
        this.automationRecipeId = valueInput.contains(AUTOMATION_RECIPE_ID)
                ? Identifier.tryParse(valueInput.getString(AUTOMATION_RECIPE_ID).orElse(""))
                : null;
    }

    public boolean liquidMerged() {
        for (ItemStack stack : this.inputs.stream().filter(s -> !s.is(TagMod.SPECIAL)).toList()) {
            if (!ItemUtils.getContainerItem(stack).getDefaultInstance().isEmpty())
                return true;
        }
        return false;
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.inputs) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public NonNullList<ItemStack> getInputs() {
        return inputs;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public int getTakeoutCount() {
        return takeoutCount;
    }

    public ItemStack getResult() {
        return result;
    }

    public Identifier getSoupBaseId() {
        return soupBaseId;
    }

    @Nullable
    public ISoupBase getSoupBase() {
        return SoupBaseManager.getSoupBase(this.soupBaseId);
    }

    public ItemStack getLidItem() {
        return lidItem;
    }

    public void setLidItem(ItemStack lidItem) {
        this.lidItem = lidItem;
    }

    public ItemStack getSoupBaseItem() {
        return soupBaseItem;
    }

    public void setAutomationRecipeId(@Nullable Identifier automationRecipeId) {
        this.automationRecipeId = automationRecipeId;
        this.setChanged();
    }

    @Nullable
    public RecipeHolder<StockpotRecipe> getAutomationRecipe(ServerLevel level) {
        if (this.automationRecipeId == null) {
            return null;
        }
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, this.automationRecipeId);
        return level.recipeAccess().byKeyTyped(ModRecipes.STOCKPOT_RECIPE, recipeKey);
    }

    @Nullable
    public RecipeHolder<FlexStockpotRecipe> getAutomationFlexRecipe(ServerLevel level) {
        if (this.automationRecipeId == null) {
            return null;
        }
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, this.automationRecipeId);
        return level.recipeAccess().byKeyTyped(ModRecipes.FLEX_STOCKPOT_RECIPE, recipeKey);
    }

    public Identifier getCookingTexture() {
        return this.cookingTexture;
    }

    public Identifier getFinishedTexture() {
        return this.finishedTexture;
    }
}
