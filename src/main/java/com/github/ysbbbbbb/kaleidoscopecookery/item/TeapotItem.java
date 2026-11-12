package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.FluidUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer.EMPTY_TEA_FLUID;

public class TeapotItem extends WithTooltipsBlockItem {
    public TeapotItem() {
        super(ModBlocks.TEAPOT, new Properties().stacksTo(1), "teapot");
    }

    /**
     * 获取当前茶壶倾倒出的茶叶
     */
    public static ItemStack getPourOut(ItemStack stack, Level level) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return ItemStack.EMPTY;
        }
        CompoundTag tag = data.copyTag();

        // 先判断状态
        int status = tag.getInt(TeapotBlockEntity.STATUS);
        if (status != ITeapot.FINISHED) {
            return ItemStack.EMPTY;
        }

        // 还有茶水剩余么
        return ItemStack.parseOptional(level.registryAccess(), tag.getCompound(TeapotBlockEntity.RESULT));
    }

    /**
     * 执行倾倒，此时会扣除一数量成品
     */
    public static void pourOut(ItemStack stack, Level level) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return;
        }
        CompoundTag tag = data.copyTag();

        int status = tag.getInt(TeapotBlockEntity.STATUS);
        if (status != ITeapot.FINISHED) {
            return;
        }

        ItemStack result = ItemStack.parseOptional(level.registryAccess(), tag.getCompound(TeapotBlockEntity.RESULT));
        if (result.isEmpty()) {
            return;
        }

        result.shrink(1);
        // 如果倒完了，直接重置所有内容
        if (result.isEmpty()) {
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            return;
        }

        // 否则只更新数量
        tag.put(TeapotBlockEntity.RESULT, result.saveOptional(level.registryAccess()));
        BlockItem.setBlockEntityData(stack, ModBlocks.TEAPOT_BE, tag);
    }

    public static boolean fillFluid(ItemStack stack, Fluid fluid, LivingEntity user) {
        CustomData data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        // 先判断状态
        int status = tag.getInt(TeapotBlockEntity.STATUS);
        if (status != ITeapot.PUT_INGREDIENT) {
            return false;
        }
        // 再判断是否存在流体
        String fluidId = StringUtils.defaultIfBlank(tag.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
        if (!fluidId.equals(EMPTY_TEA_FLUID.toString())) {
            return false;
        }
        // 执行流体添加
        ResourceLocation key = BuiltInRegistries.FLUID.getKey((fluid));
        tag.putString(TeapotBlockEntity.TEA_FLUID_ID, key.toString());
        BlockItem.setBlockEntityData(stack, ModBlocks.TEAPOT_BE, tag);
        user.playSound(getFillSound(fluid), 1.0F, 1.0F);
        return true;
    }

    public static void clearAll(ItemStack stack, Player player) {
        stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        player.playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
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

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return InteractionResult.PASS;
        }
        CompoundTag tag = data.copyTag();
        Level level = player.level();

        // 先判断状态
        int status = tag.getInt(TeapotBlockEntity.STATUS);
        if (status == ITeapot.FINISHED) {
            pourOut(stack, level);
        } else if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(tag.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (!"minecraft:lava".equals(fluidId)) {
                // 仅岩浆能烫伤生物
                return InteractionResult.PASS;
            }
            // 概率消耗
            if (player.getRandom().nextFloat() < 0.3F) {
                clearAll(stack, player);
            }
        }


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

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        // 如果已经有流体了，返回
        CustomData data = itemInHand.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            String fluidId = StringUtils.defaultIfBlank(tag.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
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
        ItemStack pickup = bucketpickup.pickupBlock(player, level, pos, blockState);
        if (pickup.isEmpty()) {
            return InteractionResultHolder.fail(itemInHand);
        }

        var storage = FluidUtils.getItemStorage(pickup);
        if (storage == null) {
            return InteractionResultHolder.fail(itemInHand);
        }
        FluidVariant fluidVariant = FluidUtils.findFirstResource(storage);
        if (fluidVariant.isBlank()) {
            return InteractionResultHolder.fail(itemInHand);
        }
        Fluid fluid = fluidVariant.getFluid();
        boolean result = fillFluid(itemInHand, fluid, player);
        if (result) {
            return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide());
        }
        return InteractionResultHolder.fail(itemInHand);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // 两种情况显示进度条
        // 1 准备阶段，装了流体
        // 2 完成阶段，有产物
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return false;
        }
        CompoundTag tag = data.copyTag();

        int status = tag.getInt(TeapotBlockEntity.STATUS);

        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(tag.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            return !fluidId.equals(EMPTY_TEA_FLUID.toString());
        }

        return status == ITeapot.FINISHED;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getBarColor(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return 0x9df7ff;
        }
        CompoundTag tag = data.copyTag();
        int status = tag.getInt(TeapotBlockEntity.STATUS);
        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(tag.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
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
            return isMysteryTea(tag) ? 0xd536d8 : 0x89ee24;
        return 0x9df7ff;
    }

    private static boolean isMysteryTea(CompoundTag tag) {
        return tag.getCompound(TeapotBlockEntity.RESULT)
                .getString("id")
                .contains("mystery_tea");
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return 0;
        }
        CompoundTag tag = data.copyTag();

        int status = tag.getInt(TeapotBlockEntity.STATUS);

        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(tag.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return 0;
            }
            return 13;
        }

        if (status == ITeapot.FINISHED) {
            int count = tag.getCompound(TeapotBlockEntity.RESULT).getInt("count");
            if (count <= 0) {
                return 0;
            }
            // 进度条长度根据剩余产物数量占总量的比例来计算，满了是13格
            return Math.round(13.0F * count / TeapotRecipe.OUTPUT_COUNT);
        }

        return 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag pFlag) {
        super.appendHoverText(stack, context, list, pFlag);
        // 根据茶壶状态显示原料或成品信息
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return;
        }
        // 间隔一行显示茶壶具体的液体和茶叶存储信息
        list.add(Component.empty());
        CompoundTag tag = data.copyTag();
        HolderLookup.Provider registries = context.registries();

        int status = tag.getInt(TeapotBlockEntity.STATUS);
        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = StringUtils.defaultIfBlank(tag.getString(TeapotBlockEntity.TEA_FLUID_ID), EMPTY_TEA_FLUID.toString());
            if (!fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                ResourceLocation key = ResourceLocation.tryParse(fluidId);
                if (key != null) {
                    list.add(TeaFluidHelper.getDisplayName(key).copy().withStyle(ChatFormatting.WHITE));
                }
            }
            if (registries != null && tag.contains(TeapotBlockEntity.INPUT)) {
                ItemStack ingredient = ItemStack.parseOptional(registries, tag.getCompound(TeapotBlockEntity.INPUT));
                if (!ingredient.isEmpty()) {
                    list.add(ingredient.getHoverName().copy().withStyle(ChatFormatting.WHITE));
                }
            }
        }

        if (status == ITeapot.FINISHED && registries != null) {
            ItemStack result = ItemStack.parseOptional(registries, tag.getCompound(TeapotBlockEntity.RESULT));
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

    private static SoundEvent getFillSound(Fluid fluid) {
        return fluid == Fluids.LAVA ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
    }
}
