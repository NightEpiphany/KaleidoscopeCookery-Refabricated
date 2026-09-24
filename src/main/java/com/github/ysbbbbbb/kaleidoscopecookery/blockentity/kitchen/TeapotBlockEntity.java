package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ServerThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.TeapotIngredientStorage;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.CustomFluidTank;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.FluidUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class TeapotBlockEntity extends BaseBlockEntity implements ITeapot {
    /**
     * 前十秒可以取回原料，超过此时间，则无法取回，同时进入 PROCESSING 状态
     */
    public static final int INGREDIENT_TIME = 200;

    public static final String TEA_FLUID_ID = "TeaFluidId";
    public static final String RESULT = "Result";
    public static final String STATUS = "Status";

    private static final String INPUT = "Input";
    private static final String CURRENT_TICK = "CurrentTick";

    private final RecipeManager.CachedCheck<TeapotInput, TeapotRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.TEAPOT_RECIPE);

    private ItemStack input = ItemStack.EMPTY;
    private final Storage<ItemVariant> ingredientStorage = new TeapotIngredientStorage(this);
    private Identifier teaFluidId = TeapotRecipeSerializer.EMPTY_TEA_FLUID;
    private ItemStack result = ItemStack.EMPTY;

    private int status = PUT_INGREDIENT;
    private int currentTick = -1;

    public AnimationState boilingState = new AnimationState();

    public TeapotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TEAPOT_BE, pos, state);
    }

    public void tick(Level level) {
        // 客户端只根据服务端同步的状态播放动画，不预测倒计时或完成状态。
        if (level.isClientSide()) {
            if (status != FINISHED) {
                this.boilingState.stop();
            } else if (Math.floorMod(level.getGameTime() + worldPosition.hashCode(), 11) == 0) {
                if (hasHeatSource(level)) {
                    this.boilingState.start((int) level.getGameTime());
                } else {
                    this.boilingState.stop();
                }
            }
            return;
        }

        // 如果现在处于 PUT_INGREDIENT 阶段
        if (status == ITeapot.PUT_INGREDIENT) {
            // 每 23 tick 检查一次
            long offset = level.getGameTime() + worldPosition.hashCode();
            if (Math.floorMod(offset, 23) == 0) {
                // 茶壶为空
                if (teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
                    return;
                }
                // 没有热源，啥进度也不进行
                if (!hasHeatSource(level)) {
                    return;
                }

                // 此时播放音效和粒子
                this.onProcessingEffects(level);

                // 输入原料是空的
                if (input.isEmpty()) {
                    return;
                }
                // 材料放入冷却阶段
                if (this.currentTick > 0) {
                    this.currentTick = Math.max(-1, this.currentTick - 23);
                    this.refresh();
                    return;
                }
                // 时间到，开始进入 PROCESSING 状态
                TeapotInput container = new TeapotInput(this.input, this.teaFluidId);
                if (level instanceof ServerLevel serverLevel) {
                    Optional<RecipeHolder<TeapotRecipe>> recipeOpt = this.quickCheck.getRecipeFor(container, serverLevel);
                    if (recipeOpt.isPresent()) {
                        TeapotRecipe teapotRecipe = recipeOpt.get().value();
                        this.result = teapotRecipe.assemble(container);
                        this.currentTick = teapotRecipe.time();
                    } else {
                        this.result = new ItemStack(BuiltInRegistries.ITEM.getValue(TeacupRegistry.MYSTERY_TEA),
                                TeapotRecipe.MYSTERY_OUTPUT_COUNT);
                        this.currentTick = TeapotRecipeSerializer.DEFAULT_TIME;
                    }
                    this.status = PROCESSING;
                    this.refresh();
                }
            }
            return;
        }

        // 如果处于 PROCESSING 阶段，只需要检查热源并计数即可
        if (status == PROCESSING) {
            // 每 23 tick 检查一次
            long offset = level.getGameTime() + worldPosition.hashCode();
            if (Math.floorMod(offset, 23) == 0) {
                if (!hasHeatSource(level)) {
                    return;
                }

                // 此时播放音效和粒子
                this.onProcessingEffects(level);

                if (currentTick > 0) {
                    this.currentTick = Math.max(-1, this.currentTick - 23);
                    this.refresh();
                    return;
                }
                this.status = FINISHED;
                this.currentTick = -1;
                this.refresh();
            }
        }

        // 完成状态
        if (status == FINISHED) {
            // 每 11 tick 检查一次
            long offset = level.getGameTime() + worldPosition.hashCode();
            if (Math.floorMod(offset, 11) == 0) {
                // 没有热源，停止播放沸腾动画
                if (!hasHeatSource(level)) {
                    this.boilingState.stop();
                    this.onFinishEffects(level);
                } else {
                    this.boilingState.start((int) level.getGameTime());
                    this.onBoilingEffects(level);
                }
            }
        }
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
    public boolean addTeaFluid(Level level, LivingEntity user, ItemStack itemStack) {
        if (itemStack.is(ModItems.RECIPE_ITEM)) {
            return false;
        }

        // 当前状态正确
        if (this.status != PUT_INGREDIENT) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_tea_fluid.state_incorrect", this.getStatusText());
            return false;
        }

        // 流体已满
        if (!this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_tea_fluid.has_fluid");
            return false;
        }

        TeaFluidHelper.Reference fluidRef = TeaFluidHelper.fromContainer(itemStack);
        if (fluidRef == null) {
            return false;
        }

        if (!fluidRef.physicalFluid()) {
            if (!TeaFluidHelper.consumeVirtualBucketContainer(user, itemStack)) {
                return false;
            }
            this.teaFluidId = fluidRef.id();
            this.refresh();
            return true;
        }

        // 手持物品必须拥有流体能力
        Storage<FluidVariant> cap = FluidUtils.getItemStorage(itemStack);
        if (cap == null) {
            return false;
        }

        // 茶壶有流体
        // 检查流体是否相同

        FluidVariant fluidVariant = FluidUtils.findFirstResource(cap);
        if (fluidVariant.isBlank()) {
            return false;
        }
        long amount = FluidUtils.findFirstAmount(cap);
        if (amount < FluidConstants.BUCKET) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_tea_fluid.fluid_not_enough");
            return false;
        }

        CustomFluidTank needFluidHandler = new CustomFluidTank(FluidConstants.BUCKET, null);
        if (!FluidUtils.emptyItem(user, itemStack, needFluidHandler, CustomFluidTank.MB_PER_BUCKET)) {
            return false;
        }

        this.teaFluidId = fluidRef.id();
        this.refresh();
        return true;
    }

    @Override
    public boolean removeTeaFluid(Level level, LivingEntity user, ItemStack itemStack) {
        if (this.status != PUT_INGREDIENT || this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID) || !this.input.isEmpty()) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.take_tea_fluid.blocked");
            return false;
        }

        if (!TeaFluidHelper.isPhysicalFluid(this.teaFluidId)) {
            if (!TeaFluidHelper.fillVirtualBucketContainer(user, itemStack, this.teaFluidId)) {
                return false;
            }
            this.teaFluidId = TeapotRecipeSerializer.EMPTY_TEA_FLUID;
            this.currentTick = -1;
            this.refresh();
            return true;
        }

        Storage<FluidVariant> cap = FluidUtils.getItemStorage(itemStack);
        if (cap == null) {
            return false;
        }
        Fluid fluid = BuiltInRegistries.FLUID.getValue(this.teaFluidId);
        if (fluid == Fluids.EMPTY) {
            return false;
        }

        CustomFluidTank sourceFluidHandler = new CustomFluidTank(FluidConstants.BUCKET, null);
        sourceFluidHandler.fill(FluidVariant.of(fluid), FluidConstants.BUCKET, CustomFluidTank.FluidAction.EXECUTE);
        if (!FluidUtils.fillItem(user, itemStack, sourceFluidHandler, CustomFluidTank.MB_PER_BUCKET)) {
            return false;
        }

        this.teaFluidId = TeapotRecipeSerializer.EMPTY_TEA_FLUID;
        this.currentTick = -1;
        this.refresh();
        return true;
    }

    public static boolean isSupportedFluidContainer(ItemStack stack) {
        return TeaFluidHelper.isSupportedFilledContainer(stack) || TeaFluidHelper.isSupportedEmptyContainer(stack);
    }

    public static boolean hasSupportedFluid(ItemStack stack) {
        TeaFluidHelper.Reference fluidRef = TeaFluidHelper.fromContainer(stack);
        return fluidRef != null && fluidRef.id() != null;
    }

    @Override
    public boolean addIngredient(Level level, LivingEntity user, ItemStack itemStack) {
        if (itemStack.isEmpty() || itemStack.is(TagMod.INGREDIENT_BLOCKLIST)) {
            return false;
        }

        if (this.status != PUT_INGREDIENT) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_ingredient.state_incorrect", this.getStatusText());
            return false;
        }

        // 茶壶没有流体
        if (this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_ingredient.no_fluid");
            return false;
        }

        // 里面有物品，失败
        if (!this.input.isEmpty()) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_ingredient.has_ingredient");
            return false;
        }
        if (level instanceof ServerLevel) {
            int count = Math.min(itemStack.getCount(), getIngredientCapacity(itemStack));
            this.input = itemStack.copyWithCount(count);
            this.currentTick = INGREDIENT_TIME;
            this.refresh();
            itemStack.shrink(count);
            return true;
        }

        return level.isClientSide();
    }

    public Storage<ItemVariant> getIngredientStorage() {
        return this.ingredientStorage;
    }

    public boolean canInsertIngredient(ItemStack stack) {
        return !isRemoved() && this.status == PUT_INGREDIENT
                && !this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)
                && !stack.isEmpty() && !stack.is(TagMod.INGREDIENT_BLOCKLIST)
                && !isSupportedFluidContainer(stack)
                && (this.input.isEmpty() || ItemStack.isSameItemSameComponents(this.input, stack));
    }

    public int getIngredientCapacity(ItemStack stack) {
        if (this.level instanceof ServerLevel serverLevel && !stack.isEmpty()) {
            TeapotInput container = new TeapotInput(stack.copyWithCount(stack.getMaxStackSize()), this.teaFluidId);
            return this.quickCheck.getRecipeFor(container, serverLevel)
                    .map(holder -> Math.clamp(holder.value().ingredientCount(), 1, stack.getMaxStackSize()))
                    .orElse(1);
        }
        return 1;
    }

    // Transfer snapshots only mutate the stack; timers and notifications change on commit.
    public void setTransferInput(ItemStack stack) {
        this.input = stack;
    }

    public void onIngredientTransferCommitted() {
        this.currentTick = INGREDIENT_TIME;
        this.refresh();
    }

    @Override
    public boolean removeIngredient(Level level, LivingEntity user) {
        if (status != PUT_INGREDIENT) {
            return false;
        }

        if (this.input.isEmpty()) {
            return false;
        }

        ItemUtils.getItemToLivingEntity(user, this.input.copyAndClear());
        this.refresh();
        return true;
    }

    @ServerThreadSafe
    @Override
    public boolean takeTeapot(Level level, LivingEntity user) {
        // 同时保护直接调用接口的路径，以及已取下/已替换的旧方块实体引用。
        if (!(level instanceof ServerLevel) || this.level != level || isRemoved()
                || level.getBlockEntity(worldPosition) != this) {
            return false;
        }

        if (status == PROCESSING) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.take_teapot.state_incorrect");
            return false;
        }

        // 先保存数据并确认方块移除成功，再发放物品；不能忽略 setBlock 的失败。
        List<ItemStack> drops = getDrops();
        if (!level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL)) {
            KaleidoscopeCookery.LOGGER.warn("Cannot take teapot at {} in {} (status={}): block removal failed",
                    worldPosition, level.dimension().identifier(), status);
            return false;
        }
        for (ItemStack drop : drops) {
            ItemUtils.getItemToLivingEntity(user, drop);
        }

        level.playSound(null, worldPosition, SoundEvents.LANTERN_BREAK, SoundSource.BLOCKS, 0.6f,
                0.8f + level.getRandom().nextFloat() * 0.2F);

        return true;
    }

    public void addAllIngredients(List<ItemStack> ingredients, LivingEntity user) {
        if (this.level == null) {
            return;
        }
        if (ingredients == null) {
            return;
        }
        if (this.status != PUT_INGREDIENT) {
            return;
        }
        if (ingredients.size() > 1) return;
        ItemStack stack = ingredients.getFirst();
        if (stack.isEmpty()) {
            return;
        }
        // 如果带有容器，此时返还容器
        Item containerItem = ItemUtils.getContainerItem(stack);
        if (containerItem != Items.AIR) {
            ItemUtils.getItemToLivingEntity(user, containerItem.getDefaultInstance());
        }
        // 茶壶配方需要 ingredientCount 个原料，保留记录中的实际数量以便匹配配方
        this.input = stack.copy();
        level.playSound(null, this.worldPosition,
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        this.refresh();
    }

    /**
     * 茶壶变成物品时执行的逻辑
     */
    public List<ItemStack> getDrops() {
        List<ItemStack> drops = Lists.newArrayList();

        ItemStack teapot = ModItems.TEAPOT.getDefaultInstance();

        // 如果当前是准备状态
        if (this.status == PUT_INGREDIENT) {
            // 如果有原料，掉落原料
            if (!this.input.isEmpty()) {
                drops.add(this.input.copy());
            }

            TagValueOutput tag = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            tag.putString(TEA_FLUID_ID, this.teaFluidId.toString());
            BlockItem.setBlockEntityData(teapot, ModBlocks.TEAPOT_BE, tag);
            drops.add(teapot);

            return drops;
        }

        // 如果是进行中，那么直接空
        if (this.status == PROCESSING) {
            drops.add(teapot);
            return drops;
        }

        // 完成，那么返回部分数值
        if (this.level != null && this.level instanceof ServerLevel serverLevel) {
            TagValueOutput tag = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            tag.store(RESULT, ItemStack.CODEC, this.result);
            tag.putInt(STATUS, this.status);
            BlockItem.setBlockEntityData(teapot, ModBlocks.TEAPOT_BE, tag);
        }
        drops.add(teapot);

        return drops;
    }

    private void onProcessingEffects(Level level) {
        RandomSource random = level.getRandom();
        level.playSound(null, worldPosition, ModSounds.BLOCK_TEAPOT_PROCESSING, SoundSource.BLOCKS, 0.6f,
                0.8f + random.nextFloat() * 0.2F);
        this.onFinishEffects(level);
    }

    private void onBoilingEffects(Level level) {
        RandomSource random = level.getRandom();
        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4f,
                0.8f + random.nextFloat() * 0.2F);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.COOKING,
                    worldPosition.getX() + 0.5 + (level.getRandom().nextFloat() - 0.5F),
                    worldPosition.getY() + 0.6 + level.getRandom().nextDouble() / 5,
                    worldPosition.getZ() + 0.5 + (level.getRandom().nextFloat() - 0.5F),
                    3,
                    (level.getRandom().nextFloat() - 0.5F) * 0.05F,
                    0.1,
                    (level.getRandom().nextFloat() - 0.5F) * 0.05F,
                    0.02);
        }
    }

    private void onFinishEffects(Level level) {
        RandomSource random = level.getRandom();

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.COOKING,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 4 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.8 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 4 * (random.nextBoolean() ? 1 : -1),
                    1,
                    (level.getRandom().nextFloat() - 0.5F) * 0.05F,
                    0.1,
                    (level.getRandom().nextFloat() - 0.5F) * 0.05F,
                    0.02);
        }
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
        if (!this.input.isEmpty()) {
            valueOutput.store(INPUT, ItemStack.CODEC, this.input);
        }
        valueOutput.putString(TEA_FLUID_ID, this.teaFluidId.toString());
        if (!this.result.isEmpty()) {
            valueOutput.store(RESULT, ItemStack.CODEC, this.result);
        }
        valueOutput.putInt(STATUS, this.status);
        valueOutput.putInt(CURRENT_TICK, this.currentTick);
    }

    @ServerThreadSafe
    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.input = valueInput.read(INPUT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.teaFluidId = Identifier.tryParse(
                valueInput.getString(TEA_FLUID_ID).orElse(TeapotRecipeSerializer.EMPTY_TEA_FLUID.toString())
        );
        if (this.teaFluidId == null) {
            this.teaFluidId = TeapotRecipeSerializer.EMPTY_TEA_FLUID;
        }
        this.result = valueInput.read(RESULT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.status = valueInput.getIntOr(STATUS, PUT_INGREDIENT);
        this.currentTick = valueInput.getIntOr(CURRENT_TICK, -1);
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    public Component getStatusText() {
        if (status == PUT_INGREDIENT) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.put_ingredient");
        }
        if (status == PROCESSING) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.processing");
        }
        if (status == FINISHED) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.finished");
        }
        return Component.empty();
    }

    public ItemStack getInput() {
        return input;
    }

    public Identifier getTeaFluidId() {
        return teaFluidId;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getCurrentTick() {
        return currentTick;
    }
}
