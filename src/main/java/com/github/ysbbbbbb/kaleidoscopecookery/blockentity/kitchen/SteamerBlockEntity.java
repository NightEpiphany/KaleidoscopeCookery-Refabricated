package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ServerThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ISteamer;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class SteamerBlockEntity extends BaseBlockEntity implements ISteamer {
    // 最大火力等级为 4，往上继续叠蒸笼，火力等级逐步降低
    public static final int MAX_LIT_LEVEL = 4;
    public static final String COOKING_PROGRESS_TAG = "CookingProgress";
    public static final String COOKING_TIME_TAG = "CookingTime";
    public static final String ITEMS_TAG = "Items";

    // 合成表
    private final RecipeManager.CachedCheck<SingleRecipeInput, SteamerRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.STEAMER_RECIPE);

    // 0-3 是下层蒸笼，4-7 是上层蒸笼
    private final NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[8];
    private final int[] cookingTime = new int[8];

    private int litLevel = 0;

    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.STEAMER_BE, pos, state);
    }

    // 将蒸笼数据一分为二，分别保存到两个 tag 里
    public static void saveSplit(CompoundTag tag1, CompoundTag tag2,
                                 Level level,
                                 NonNullList<ItemStack> items,
                                 int[] cookingProgress,
                                 int[] cookingTime) {
        // 保存两部分
        NonNullList<ItemStack> first = NonNullList.withSize(4, ItemStack.EMPTY);
        NonNullList<ItemStack> second = NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            first.set(i, items.get(i));
            second.set(i, items.get(i + 4));
        }

        int[] firstCookingProgress = new int[4];
        int[] secondCookingProgress = new int[4];

        int[] firstCookingTime = new int[4];
        int[] secondCookingTime = new int[4];

        System.arraycopy(cookingProgress, 0, firstCookingProgress, 0, 4);
        System.arraycopy(cookingProgress, 4, secondCookingProgress, 0, 4);

        System.arraycopy(cookingTime, 0, firstCookingTime, 0, 4);
        System.arraycopy(cookingTime, 4, secondCookingTime, 0, 4);

        PortHelper.saveAllItems(tag1, first, false, level.registryAccess());
        if (!tag1.isEmpty()) {
            tag1.putIntArray(COOKING_PROGRESS_TAG, firstCookingProgress);
            tag1.putIntArray(COOKING_TIME_TAG, firstCookingTime);
        }

        PortHelper.saveAllItems(tag2, first, false, level.registryAccess());
        if (!tag2.isEmpty()) {
            tag2.putIntArray(COOKING_PROGRESS_TAG, secondCookingProgress);
            tag2.putIntArray(COOKING_TIME_TAG, secondCookingTime);
        }
    }

    public void tick(Level level) {
        // 蒸笼每火力每 5 tick 更新一次
        if (level.getGameTime() % 5 == 0) {
            updateLitLevel(level);

            // 如果有蒸熟的，且进度为 -1，那么释放蒸汽粒子
            for (int i = 0; i < this.items.size(); i++) {
                if (this.cookingTime[i] == -1) {
                    this.makeRipeParticles(level, worldPosition);
                    break;
                }
            }
        }

        if (this.litLevel > 0) {
            cookingTick(level, worldPosition, getBlockState(), this);
        } else {
            cooldownTick(level, worldPosition, getBlockState(), this);
        }
    }

    // 合并物品，仅在放置时调用
    public void mergeItem(ItemStack stack, Level level) {
        CompoundTag data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(ModBlocks.STEAMER_BE, new CompoundTag())).copyTagWithoutId();

        NonNullList<ItemStack> merge = NonNullList.withSize(8, ItemStack.EMPTY);
        int[] mergeCookingProgress = new int[8];
        int[] mergeCookingTime = new int[8];
        ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data);
        // 先尝试把物品里的数据 0-3 取出，放到 4-7
        if (data.getList(ITEMS_TAG).isPresent()) {
            NonNullList<ItemStack> itemsInStack = NonNullList.withSize(4, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(valueInput, itemsInStack);
            for (int i = 0; i < 4; i++) {
                merge.set(i + 4, itemsInStack.get(i));
            }
        }
        if (data.getList(COOKING_PROGRESS_TAG).isPresent()) {
            int[] times = data.getIntArray(COOKING_PROGRESS_TAG).orElse(new int[0]);
            int length = Math.min(mergeCookingProgress.length - 4, times.length);
            System.arraycopy(times, 0, mergeCookingProgress, 4, length);
        }
        if (data.getList(COOKING_TIME_TAG).isPresent()) {
            int[] times = data.getIntArray(COOKING_TIME_TAG).orElse(new int[0]);
            int length = Math.min(mergeCookingTime.length - 4, times.length);
            System.arraycopy(times, 0, mergeCookingTime, 4, length);
        }

        // 接着把自己的数据放到 0-3
        for (int i = 0; i < 4; i++) {
            merge.set(i, this.items.get(i));
            mergeCookingProgress[i] = this.cookingProgress[i];
            mergeCookingTime[i] = this.cookingTime[i];
        }

        // 写进 data
        PortHelper.saveAllItems(data, merge, false, level.registryAccess());
        data.putIntArray(COOKING_PROGRESS_TAG, mergeCookingProgress);
        data.putIntArray(COOKING_TIME_TAG, mergeCookingTime);

        // 写回物品
        PortHelper.setBlockEntityData(stack, this.getType(), data, level);
    }

    public List<ItemStack> dropAsItem(Level level) {
        List<ItemStack> drops = Lists.newArrayList();
        // 先看看是单层还是双层
        boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
        // 全为空？那么直接返回
        ItemStack first = ModItems.STEAMER.getDefaultInstance();
        if (this.items.stream().allMatch(ItemStack::isEmpty)) {
            drops.add(first);
            if (!half) {
                drops.add(ModItems.STEAMER.getDefaultInstance());
            }
            return drops;
        }

        // 只需要保存物品和进度即可
        CompoundTag tag1 = new CompoundTag();
        CompoundTag tag2 = new CompoundTag();
        saveSplit(tag1, tag2, level, this.items, this.cookingProgress, this.cookingTime);

        PortHelper.setBlockEntityData(first, this.getType(), tag1, level);
        drops.add(first);

        if (!half) {
            ItemStack second = ModItems.STEAMER.getDefaultInstance();
            PortHelper.setBlockEntityData(second, this.getType(), tag2, level);
            drops.add(second);
        }
        return drops;
    }

    @Override
    public void updateLitLevel(Level level) {
        BlockPos pos = this.getBlockPos();
        if (this.hasHeatSource(level)) {
            this.litLevel = MAX_LIT_LEVEL;
        } else if (level.getBlockEntity(pos.below()) instanceof SteamerBlockEntity steamer) {
            // 下层是蒸笼，且需要是双层的才能传递火力
            if (steamer.getBlockState().getValue(SteamerBlock.HALF)) {
                this.litLevel = 0;
            } else {
                this.litLevel = Math.max(steamer.litLevel - 1, 0);
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

    private void cookingTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        // 先检查上面是否是蒸笼
        BlockState aboveState = level.getBlockState(pos.above());
        boolean aboveIsSteamer = aboveState.is(ModBlocks.STEAMER);
        if (!aboveIsSteamer) {
            // 上面不是蒸笼，释放蒸汽粒子
            this.makeCookingParticles(level, pos);
            // 既没有上层蒸笼，也没有盖子，不能蒸
            if (!state.getValue(SteamerBlock.HAS_LID)) {
                return;
            }
        }

        // 开始蒸
        boolean hasCooking = false;
        for (int i = 0; i < steamer.items.size(); i++) {
            ItemStack stack = steamer.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            hasCooking = true;
            int progress = steamer.cookingProgress[i]++;
            if (progress < steamer.cookingTime[i]) {
                continue;
            }
            if (level instanceof ServerLevel serverLevel) {
                SingleRecipeInput container = new SingleRecipeInput(stack);
                ItemStack resultStack = steamer.quickCheck.getRecipeFor(container, serverLevel)
                        .map(r -> r.value().assemble(container))
                        .orElse(stack);
                if (!resultStack.isEmpty()) {
                    steamer.items.set(i, resultStack);
                    // 设置为 -1 代表已经蒸熟
                    steamer.cookingTime[i] = -1;
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                }
            }
        }
        if (hasCooking) {
            setChanged(level, pos, state);
        }
    }

    private void cooldownTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        boolean hasCooking = false;

        for (int i = 0; i < steamer.items.size(); i++) {
            if (steamer.cookingProgress[i] > 0) {
                hasCooking = true;
                steamer.cookingProgress[i] = Math.clamp(steamer.cookingProgress[i] - 2, 0, steamer.cookingTime[i]);
            }
        }

        if (hasCooking) {
            setChanged(level, pos, state);
        }
    }

    public void makeCookingParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && level.getRandom().nextFloat() < 0.1F) {
            RandomSource random = serverLevel.getRandom();
            boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
            double yOffset = half ? 0.5 : 1;
            serverLevel.sendParticles(ModParticles.COOKING,
                    pos.getX() + 0.5 + random.nextDouble() / 2 * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + yOffset + random.nextDouble() / 2,
                    pos.getZ() + 0.5 + random.nextDouble() / 2 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }
    }

    public void makeRipeParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && level.getRandom().nextFloat() < 0.5F) {
            RandomSource random = serverLevel.getRandom();
            boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
            double yOffset = half ? 0.25 : 0.75;
            serverLevel.sendParticles(ModParticles.COOKING,
                    pos.getX() + 0.5 + random.nextDouble() / 1.25 * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + yOffset + random.nextDouble() / 2,
                    pos.getZ() + 0.5 + random.nextDouble() / 1.25 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }
    }

    public Optional<RecipeHolder<SteamerRecipe>> getSteamerRecipe(Level level, ItemStack stack) {
        if (this.items.stream().noneMatch(ItemStack::isEmpty) || level.isClientSide()) {
            return Optional.empty();
        }
        return this.quickCheck.getRecipeFor(new SingleRecipeInput(stack), (ServerLevel) level);
    }

    @Override
    public boolean placeFood(Level level, LivingEntity user, ItemStack food) {
        // 先检查这层是否是能交互的
        // 上层必须不能阻拦
        BlockPos above = this.getBlockPos().above();
        if (level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) {
            return false;
        }

        // 然后检查配方
        Optional<RecipeHolder<SteamerRecipe>> steamerRecipe = getSteamerRecipe(level, food);
        if (steamerRecipe.isEmpty()) {
            return false;
        }

        int cookTime = steamerRecipe.get().value().getCookTick();
        if (cookTime <= 0) {
            return false;
        }

        boolean added = false;
        boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
        int endIndex = half ? 4 : 8;

        // 一次性放入
        for (int i = 0; i < endIndex && !food.isEmpty(); i++) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                this.cookingTime[i] = cookTime;
                this.cookingProgress[i] = 0;
                this.items.set(i, food.split(1));
                added = true;
            }
        }

        if (added) {
            this.refresh();
        }
        //只有成功放入才返回 true
        return added;
    }

    @Override
    public boolean takeFood(Level level, LivingEntity user, InteractionHand hand) {
        // 先检查这层是否是能交互的
        // 上层必须不能阻拦
        BlockPos above = this.getBlockPos().above();
        if (level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) {
            return false;
        }

        BlockState blockState = this.getBlockState();
        boolean isAllEmpty = true;

        if (blockState.getValue(SteamerBlock.HAS_LID)) return false;

        boolean half = blockState.getValue(SteamerBlock.HALF);
        int preferredSlot = user instanceof Player player ? player.getInventory().getSelectedSlot() : -1;
        int endIndex = half ? 4 : 8;

        // 一次性取出所有物品
        for (int i = 0; i < endIndex; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            isAllEmpty = false;
            ItemUtils.getItemToLivingEntity(user, stack, preferredSlot);
            this.items.set(i, ItemStack.EMPTY);
            this.cookingTime[i] = 0;
            this.cookingProgress[i] = 0;
        }

        boolean hasLid = blockState.getValue(SteamerBlock.HAS_LID);
        boolean isAboveSteamer = level.getBlockState(this.getBlockPos().above()).is(this.getBlockState().getBlock());

        // 全为空，未加盖且上层不是蒸笼，那么拆掉一层
        if (isAllEmpty && !hasLid && !isAboveSteamer) {
            ItemUtils.getItemToLivingEntity(user, ModItems.STEAMER.getDefaultInstance(), preferredSlot);
            // 把 对应槽位 全部清空
            for (int i = endIndex - 4; i < endIndex; i++) {
                this.items.set(i, ItemStack.EMPTY);
                this.cookingTime[i] = 0;
                this.cookingProgress[i] = 0;
            }
            // 释放粒子效果
            level.playSound(null, this.getBlockPos(), SoundEvents.WOOD_BREAK.value(), SoundSource.BLOCKS);
            if (half) {
                level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
            } else {
                setChanged();
                level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(SteamerBlock.HALF, true));
            }
            return true;
        } else {
            this.refresh();
        }

        // 只有成功取出或拆下蒸笼才返回 true
        return !isAllEmpty;
    }

    @ServerThreadSafe
    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.items.clear();
        ContainerHelper.loadAllItems(valueInput, this.items);
        valueInput.getIntArray(COOKING_PROGRESS_TAG).ifPresent(times -> {
            int length = Math.min(this.cookingProgress.length, times.length);
            System.arraycopy(times, 0, this.cookingProgress, 0, length);
        });
        valueInput.getIntArray(COOKING_TIME_TAG).ifPresent(times -> {
            int length = Math.min(this.cookingTime.length, times.length);
            System.arraycopy(times, 0, this.cookingTime, 0, length);
        });
    }

    @ServerThreadSafe
    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ContainerHelper.saveAllItems(valueOutput, this.items);
        valueOutput.putIntArray(COOKING_PROGRESS_TAG, this.cookingProgress);
        valueOutput.putIntArray(COOKING_TIME_TAG, this.cookingTime);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }



    public int[] getCookingProgress() {
        return cookingProgress;
    }

    public int[] getCookingTime() {
        return cookingTime;
    }
}
