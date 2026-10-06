package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IChoppingBoard;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEnchantments;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class ChoppingBoardBlockEntity extends BaseBlockEntity implements IChoppingBoard {
    private static final String MODEL_ID = "ModelId";
    private static final String CURRENT_CUT_STACK = "CurrentCutStack";
    private static final String RESULT_ITEM = "ResultItem";
    private static final String MAX_CUT_COUNT = "MaxCutCount";
    private static final String CURRENT_CUT_COUNT = "CurrentCutCount";
    /**
     * 仅用于客户端渲染
     */
    public @Nullable ResourceLocation[] cacheModels = null;
    public @Nullable ResourceLocation previousModel = null;
    /**
     * 服务端客户端共通数据
     */
    private @Nullable ResourceLocation modelId = null;
    private int maxCutCount = 0;
    private int currentCutCount = 0;
    private ItemStack currentCutStack = ItemStack.EMPTY;
    private List<ItemStack> results = List.of();

    public ChoppingBoardBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.CHOPPING_BOARD_BE, pos, blockState);
    }

    public static void popResource(Level level, BlockPos pos, ItemStack stack) {
        if (!level.isClientSide && !stack.isEmpty()) {
            ItemEntity entity = new ItemEntity(level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.25,
                    pos.getZ() + 0.5,
                    stack, 0, 0, 0);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    @Override
    public boolean onPutItem(Level level, @Nullable LivingEntity user, ItemStack putOnItem) {
        if (!this.currentCutStack.isEmpty()) {
            return false;
        }
        SingleRecipeInput container = new SingleRecipeInput(putOnItem);
        Optional<RecipeHolder<ChoppingBoardRecipe>> recipeOptional = level.getRecipeManager()
                .getRecipeFor(ModRecipes.CHOPPING_BOARD_RECIPE, container, level);
        if (recipeOptional.isPresent()) {
            ChoppingBoardRecipe recipe = recipeOptional.get().value();
            this.modelId = recipe.getModelId();
            this.maxCutCount = recipe.getCutCount();
            this.currentCutCount = 0;
            this.currentCutStack = putOnItem.split(1);
            this.results = recipe.getResults();
            this.refresh();
            level.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 1.2F);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCutItem(Level level, @Nullable LivingEntity user, ItemStack cutterItem) {
        if (this.currentCutStack.isEmpty()) {
            return false;
        }
        // 如果已经切完，执行取出逻辑
        if (this.currentCutCount >= this.maxCutCount) {
            for (ItemStack result : this.results) {
                popResource(level, worldPosition, result.copy());
            }
            this.resetBoardData();
            level.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 2 + level.random.nextFloat() * 0.2f);
            return true;
        } else if (cutterItem.is(TagMod.KITCHEN_KNIFE)) {
            // 否则，检测是否是刀具，进行切菜逻辑
            int enchantmentLevel = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                    .getHolder(ModEnchantments.QUICK_KNIFE)
                    .map(enchantment -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, cutterItem))
                    .orElse(0);
            this.currentCutCount = Math.min(this.maxCutCount,
                    this.currentCutCount + (1 << Mth.clamp(enchantmentLevel, 0, 2)));
            this.playParticlesSound();
            this.refresh();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTakeOut(Level level, LivingEntity user) {
        if (this.currentCutCount == 0 && !this.currentCutStack.isEmpty()) {
            if (user instanceof Player player) {
                ItemUtils.giveItemToPlayer(player, this.currentCutStack);
            } else {
                popResource(level, this.worldPosition, this.currentCutStack);
            }
            this.resetBoardData();
            level.playSound(null, this.worldPosition,
                    SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                    SoundSource.BLOCKS,
                    1, 1.2f + level.random.nextFloat() * 0.2f);
            return true;
        }
        return false;
    }

    @Override
    public void playParticlesSound() {
        if (this.level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    this.worldPosition.getX() + 0.25 + random.nextDouble() / 2,
                    this.worldPosition.getY() + 0.25,
                    this.worldPosition.getZ() + 0.25 + random.nextDouble() / 2,
                    2, 0, 0, 0, 0.1);
            serverLevel.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 1.5f + level.random.nextFloat() * 0.4f);
        }
    }

    @Override
    public boolean hasItemOnBoard() {
        return !this.currentCutStack.isEmpty();
    }

    private void resetBoardData() {
        this.modelId = null;
        this.results = List.of();
        this.currentCutStack = ItemStack.EMPTY;
        this.currentCutCount = 0;
        this.maxCutCount = 0;
        this.refresh();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.modelId != null) {
            tag.putString(MODEL_ID, this.modelId.toString());
        }
        tag.putInt(MAX_CUT_COUNT, this.maxCutCount);
        tag.putInt(CURRENT_CUT_COUNT, this.currentCutCount);
        tag.put(CURRENT_CUT_STACK, this.currentCutStack.saveOptional(registries));
        ListTag resultTag = new ListTag();
        for (ItemStack result : this.results) {
            resultTag.add(result.saveOptional(registries));
        }
        tag.put(RESULT_ITEM, resultTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(MODEL_ID)) {
            this.modelId = ResourceLocation.parse(tag.getString(MODEL_ID));
        } else {
            this.modelId = null;
        }
        this.maxCutCount = tag.getInt(MAX_CUT_COUNT);
        this.currentCutCount = tag.getInt(CURRENT_CUT_COUNT);
        this.currentCutStack = ItemStack.parseOptional(registries, tag.getCompound(CURRENT_CUT_STACK));
        this.results = List.of();
        if (tag.contains(RESULT_ITEM, Tag.TAG_LIST)) {
            ListTag resultTag = tag.getList(RESULT_ITEM, Tag.TAG_COMPOUND);
            List<ItemStack> loadedResults = new ArrayList<>(resultTag.size());
            for (int i = 0; i < resultTag.size(); i++) {
                ItemStack result = ItemStack.parseOptional(registries, resultTag.getCompound(i));
                if (!result.isEmpty()) {
                    loadedResults.add(result);
                }
            }
            this.results = List.copyOf(loadedResults);
        } else if (tag.contains(RESULT_ITEM, Tag.TAG_COMPOUND)) {
            ItemStack result = ItemStack.parseOptional(registries, tag.getCompound(RESULT_ITEM));
            this.results = result.isEmpty() ? List.of() : List.of(result);
        }
    }

    @Nullable
    public ResourceLocation getModelId() {
        return modelId;
    }

    public int getMaxCutCount() {
        return maxCutCount;
    }

    public int getCurrentCutCount() {
        return currentCutCount;
    }

    public ItemStack getCurrentCutStack() {
        return currentCutStack;
    }
}
