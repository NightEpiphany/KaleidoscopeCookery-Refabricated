package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock.HALF;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void kaleidoscopeCookery$onSpawnAtLocation(CallbackInfo ci, @Local(ordinal = 0) BlockPos pos) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        BlockState blockState = self.getBlockState();
        // 如果是蒸笼
        if (blockState.is(ModBlocks.STEAMER)) {
            if (this.level().getBlockState(pos).is(blockState.getBlock()) && this.level().getBlockState(pos).getValue(HALF)) {
                ci.cancel();
                handleFallingSteamer(this.level(), pos, blockState, self.blockData);
                return;
            }
            ci.cancel();
            // 换成自己的掉落物
            List<ItemStack> drops = dropAsItem(blockState, self.blockData);
            for (ItemStack drop : drops) {
                self.spawnAtLocation(drop);
            }
        }
    }

    @Unique
    public void handleFallingSteamer(Level level, BlockPos pos, BlockState fallState, @Nullable CompoundTag steamerTag) {
        NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
        int[] cookingProgress = new int[8];
        int[] cookingTime = new int[8];
        if (steamerTag != null) {
            if (steamerTag.contains(SteamerBlockEntity.ITEMS_TAG, Tag.TAG_LIST)) {
                ContainerHelper.loadAllItems(steamerTag, items);
            }
            if (steamerTag.contains(SteamerBlockEntity.COOKING_PROGRESS_TAG, Tag.TAG_INT_ARRAY)) {
                cookingProgress = steamerTag.getIntArray(SteamerBlockEntity.COOKING_PROGRESS_TAG);
            }
            if (steamerTag.contains(SteamerBlockEntity.COOKING_TIME_TAG, Tag.TAG_INT_ARRAY)) {
                cookingTime = steamerTag.getIntArray(SteamerBlockEntity.COOKING_TIME_TAG);
            }
        }
        level.setBlock(pos, level.getBlockState(pos).setValue(HALF, false), Block.UPDATE_ALL);
        if (level.getBlockEntity(pos) instanceof SteamerBlockEntity steamerBlockEntity)
            if (!fallState.getValue(HALF)) {
                level.setBlock(pos.above(), fallState.setValue(HALF, true), Block.UPDATE_ALL);
                var steamerBlockEntity2 = (SteamerBlockEntity) level.getBlockEntity(pos.above());
                for (var i = 0; i < 4; i++) {
                    steamerBlockEntity.getItems().set(i + 4, items.get(i));
                    assert steamerBlockEntity2 != null;
                    steamerBlockEntity2.getItems().set(i, items.get(i + 4));
                    steamerBlockEntity2.getCookingProgress()[i] = cookingProgress[i + 4];
                    steamerBlockEntity2.getCookingTime()[i] = cookingTime[i + 4];
                }
            } else {
                for (var i = 0; i < 4; i++) {
                    steamerBlockEntity.getItems().set(i + 4, items.get(i));
                    steamerBlockEntity.getCookingProgress()[i + 4] = cookingProgress[i];
                    steamerBlockEntity.getCookingTime()[i + 4] = cookingTime[i];
                }
            }
    }

    @Unique
    public List<ItemStack> dropAsItem(BlockState blockState, @Nullable CompoundTag steamerTag) {
        NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
        int[] cookingProgress = new int[8];
        int[] cookingTime = new int[8];
        if (steamerTag != null) {
            if (steamerTag.contains(SteamerBlockEntity.ITEMS_TAG, Tag.TAG_LIST)) {
                ContainerHelper.loadAllItems(steamerTag, items);
            }
            if (steamerTag.contains(SteamerBlockEntity.COOKING_PROGRESS_TAG, Tag.TAG_INT_ARRAY)) {
                cookingProgress = steamerTag.getIntArray(SteamerBlockEntity.COOKING_PROGRESS_TAG);
            }
            if (steamerTag.contains(SteamerBlockEntity.COOKING_TIME_TAG, Tag.TAG_INT_ARRAY)) {
                cookingTime = steamerTag.getIntArray(SteamerBlockEntity.COOKING_TIME_TAG);
            }
        }

        List<ItemStack> drops = Lists.newArrayList();
        // 先看看是单层还是双层
        boolean half = blockState.getValue(HALF);
        // 全为空？那么直接返回
        ItemStack first = ModItems.STEAMER.getDefaultInstance();
        if (items.stream().allMatch(ItemStack::isEmpty)) {
            drops.add(first);
            if (!half) {
                drops.add(ModItems.STEAMER.getDefaultInstance());
            }
            return drops;
        }

        // 只需要保存物品和进度即可
        CompoundTag tag1 = new CompoundTag();
        CompoundTag tag2 = new CompoundTag();
        SteamerBlockEntity.saveSplit(tag1, tag2, items, cookingProgress, cookingTime);

        BlockItem.setBlockEntityData(first, ModBlocks.STEAMER_BE, tag1);
        drops.add(first);

        if (!half) {
            ItemStack second = ModItems.STEAMER.getDefaultInstance();
            BlockItem.setBlockEntityData(second, ModBlocks.STEAMER_BE, tag2);
            drops.add(second);
        }
        return drops;
    }
}
