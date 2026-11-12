package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer.EMPTY_TEA_FLUID;

@SuppressWarnings("UnstableApiUsage")
public class TeapotItem extends WithTooltipsBlockItem {
    public TeapotItem() {
        super(ModBlocks.TEAPOT, new Properties().stacksTo(1), "teapot");
    }

    /**
     * 获取当前茶壶倾倒出的茶叶
     */
    public static ItemStack getPourOut(ItemStack stack) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null) {
            return ItemStack.EMPTY;
        }

        // 先判断状态
        int status = data.getInt(TeapotBlockEntity.STATUS);
        if (status != ITeapot.FINISHED) {
            return ItemStack.EMPTY;
        }

        // 还有茶水剩余么
        return ItemStack.of(data.getCompound(TeapotBlockEntity.RESULT));
    }

    /**
     * 执行倾倒，此时会扣除一数量成品
     */
    public static void pourOut(ItemStack stack) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null) {
            return;
        }

        int status = data.getInt(TeapotBlockEntity.STATUS);
        if (status != ITeapot.FINISHED) {
            return;
        }

        ItemStack result = ItemStack.of(data.getCompound(TeapotBlockEntity.RESULT));
        if (result.isEmpty()) {
            return;
        }

        result.shrink(1);
        // 如果倒完了，直接重置所有内容
        if (result.isEmpty()) {
            stack.removeTagKey(BlockItem.BLOCK_ENTITY_TAG);
            return;
        }

        // 否则只更新数量
        data.put(TeapotBlockEntity.RESULT, result.serializeNBT());
        BlockItem.setBlockEntityData(stack, ModBlocks.TEAPOT_BE, data);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null) {
            return InteractionResult.PASS;
        }

        // 先判断状态
        int status = data.getInt(TeapotBlockEntity.STATUS);
        if (status == ITeapot.FINISHED) {
            pourOut(stack);
        } else if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(data.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (!fluidId.contains("lava")) {
                // 仅岩浆能烫伤生物
                return InteractionResult.PASS;
            }
            // 概率消耗
            if (player.getRandom().nextFloat() < 0.3F) {
                clearAll(stack, player);
            }
        }

        Level level = player.level();
        RandomSource random = level.random;
        target.hurt(level.damageSources().inFire(), 3);

        double x = target.getX();
        double y = target.getY() + target.getEyeHeight() + 0.25;
        double z = target.getZ();

        player.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);

        for (int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.LAVA,
                    x + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    y + random.nextDouble() / 3,
                    z + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    0.3, 0.1, 0.3);
        }

        return InteractionResult.SUCCESS;
    }

    public static void clearAll(ItemStack stack, Player player) {
        stack.removeTagKey(BlockItem.BLOCK_ENTITY_TAG);
        player.playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);


    }

    public static boolean fillFluid(ItemStack stack, Fluid fluid, LivingEntity user) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null) {
            data = new CompoundTag();
        }
        // 先判断状态
        int status = data.getInt(TeapotBlockEntity.STATUS);
        if (status != ITeapot.PUT_INGREDIENT) {
            return false;
        }
        // 再判断是否存在流体
        String fluidId = StringUtils.defaultIfBlank(data.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
        if (!fluidId.equals(EMPTY_TEA_FLUID.toString())) {
            return false;
        }
        // 执行流体添加
        ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid);
        data.putInt(TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);
        data.putString(TeapotBlockEntity.TEA_FLUID_ID, key.toString());
        CompoundTag tankTag = new CompoundTag();
        tankTag.putLong("amount", FluidConstants.BUCKET);
        tankTag.putString("fluid", key.toString());
        data.put(TeapotBlockEntity.TANK, tankTag);
        BlockItem.setBlockEntityData(stack, ModBlocks.TEAPOT_BE, data);

        var sound = FluidVariantAttributes.getFillSound(FluidVariant.of(fluid));
        if (sound != null) {
            user.playSound(sound);
        }
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        // 潜行时只放置方块
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    private static boolean isMysteryTea(CompoundTag tag) {
        return tag.getCompound(TeapotBlockEntity.RESULT)
                .getString("id")
                .contains("mystery_tea");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        // 如果已经有流体了，返回
        CompoundTag data = BlockItem.getBlockEntityData(itemInHand);
        if (data != null) {
            String fluidId = StringUtils.defaultIfBlank(data.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (!fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return InteractionResultHolder.fail(itemInHand);
            }
        }

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemInHand);
        }
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemInHand);
        }

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos relative = pos.relative(direction);

        // 权限检查
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(relative, direction, itemInHand)) {
            return InteractionResultHolder.fail(itemInHand);
        }

        BlockState blockState = level.getBlockState(pos);
        // 必须是可以用桶取流体的方块
        if (!(blockState.getBlock() instanceof BucketPickup bucketpickup)) {
            return InteractionResultHolder.fail(itemInHand);
        }

        // 执行取流体操作
        ItemStack pickup = bucketpickup.pickupBlock(level, pos, blockState);
        if (pickup.isEmpty()) {
            return InteractionResultHolder.fail(itemInHand);
        }

        Storage<FluidVariant> storage = FluidUtils.getItemStorage(pickup);
        if (storage == null) {
            return InteractionResultHolder.fail(itemInHand);
        }
        FluidVariant resource = FluidUtils.findFirstResource(storage);
        if (resource.isBlank()) {
            return InteractionResultHolder.fail(itemInHand);
        }
        boolean result = fillFluid(itemInHand, resource.getFluid(), player);
        if (result) {
            return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide());
        }
        return InteractionResultHolder.fail(itemInHand);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        // 两种情况显示进度条
        // 1 准备阶段，装了流体
        // 2 完成阶段，有产物
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null) {
            return false;
        }

        int status = data.getInt(TeapotBlockEntity.STATUS);

        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(data.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            return !fluidId.equals(EMPTY_TEA_FLUID.toString());
        }

        if (status == ITeapot.FINISHED) {
            ItemStack result = ItemStack.of(data.getCompound(TeapotBlockEntity.RESULT));
            return !result.isEmpty();
        }

        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null) {
            return 0x9df7ff;
        }
        int status = data.getInt(TeapotBlockEntity.STATUS);
        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(data.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return 0x9df7ff;
            }
            if (fluidId.contains("milk")) {
                return 0xf4eee1;
            }
            if (fluidId.contains("honey")) {
                return 0xedce52;
            }
            if (fluidId.contains("lava")) {
                return 0xe28120;
            }
            if (fluidId.contains("chocolate")) {
                return 0x4c2807;
            }
        }
        if (status == ITeapot.FINISHED)
            return isMysteryTea(data) ? 0xd536d8 : 0x89ee24;
        return 0x9df7ff;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null) {
            return 0;
        }

        int status = data.getInt(TeapotBlockEntity.STATUS);

        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(data.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return 0;
            }
            return 13;
        }

        if (status == ITeapot.FINISHED) {
            ItemStack result = ItemStack.of(data.getCompound(TeapotBlockEntity.RESULT));
            if (result.isEmpty()) {
                return 0;
            }
            // 进度条长度根据剩余产物数量占总量的比例来计算，满了是13格
            int count = result.getCount();
            return Math.round(13.0F * count / TeapotRecipe.OUTPUT_COUNT);
        }

        return 0;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> list, @NotNull TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, list, pFlag);
        // 根据茶壶状态显示原料或成品信息。
        CompoundTag data = BlockItem.getBlockEntityData(pStack);
        if (data == null) {
            return;
        }
        // 间隔一行显示茶壶具体的液体和茶叶存储信息。
        list.add(Component.empty());

        int status = data.getInt(TeapotBlockEntity.STATUS);
        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(data.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (!fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                ResourceLocation key = ResourceLocation.tryParse(fluidId);
                if (key != null) {
                    Fluid fluid = BuiltInRegistries.FLUID.get(key);
                    list.add(FluidVariantAttributes.getName(FluidVariant.of(fluid)).copy().withStyle(ChatFormatting.WHITE));
                }
            }
            if (data.contains(TeapotBlockEntity.INPUT)) {
                ItemStack ingredient = ItemStack.of(data.getCompound(TeapotBlockEntity.INPUT));
                if (!ingredient.isEmpty()) {
                    list.add(ingredient.getHoverName().copy().withStyle(ChatFormatting.WHITE));
                }
            }
        }

        if (status == ITeapot.FINISHED) {
            ItemStack result = ItemStack.of(data.getCompound(TeapotBlockEntity.RESULT));
            if (result.isEmpty()) {
                return;
            }
            Component resultComponent = ComponentUtils.formatList(Arrays.asList(
                    result.getHoverName(),
                    Component.literal("x%d".formatted(result.getCount()))
            ), CommonComponents.space(), Function.identity()).withStyle(ChatFormatting.GRAY);
            list.add(resultComponent);
        }
    }
}
