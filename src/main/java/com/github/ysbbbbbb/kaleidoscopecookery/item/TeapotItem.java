package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.FluidUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer.EMPTY_TEA_FLUID;

public class TeapotItem extends BlockItem {
    private static final String MYSTERY_TEA_ID = TeacupRegistry.MYSTERY_TEA.toString();

    public TeapotItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    /**
     * 获取当前茶壶倾倒出的茶叶
     */
    public static ItemStack getPourOut(ItemStack stack, Level level) {
        if (!(level instanceof ServerLevel serverLevel) || !stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return ItemStack.EMPTY;
        }
        CompoundTag tag = getBlockEntityData(stack);

        // 先判断状态
        if (getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT) != ITeapot.FINISHED) {
            return ItemStack.EMPTY;
        }
        return PortHelper.decodeItem(getCompound(tag, TeapotBlockEntity.RESULT), serverLevel);
    }

    /**
     * 执行倾倒，此时会扣除一数量成品
     */
    public static void pourOut(ItemStack stack, Level level) {
        if (!(level instanceof ServerLevel serverLevel) || !stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return;
        }
        CompoundTag tag = getBlockEntityData(stack);

        if (getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT) != ITeapot.FINISHED) {
            return;
        }

        ItemStack result = PortHelper.decodeItem(getCompound(tag, TeapotBlockEntity.RESULT), serverLevel);
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
        tag.put(TeapotBlockEntity.RESULT, PortHelper.encodeItem(result, serverLevel));
        PortHelper.setBlockEntityData(stack, ModBlocks.TEAPOT_BE, tag, level);
    }

    public static boolean fillFluid(ItemStack stack, Fluid fluid, LivingEntity user) {
        CompoundTag tag = getBlockEntityData(stack);
        // 先判断状态
        int status = getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);
        if (status != ITeapot.PUT_INGREDIENT) {
            return false;
        }
        // 再判断是否存在流体
        String fluidId = getString(tag, TeapotBlockEntity.TEA_FLUID_ID, EMPTY_TEA_FLUID.toString());
        if (!fluidId.equals(EMPTY_TEA_FLUID.toString())) {
            return false;
        }
        // 执行流体添加
        Identifier key = BuiltInRegistries.FLUID.getKey(fluid);
        tag.putString(TeapotBlockEntity.TEA_FLUID_ID, key.toString());
        PortHelper.setBlockEntityData(stack, ModBlocks.TEAPOT_BE, tag, user.level());
        user.playSound(getFillSound(fluid), 1.0F, 1.0F);
        return true;
    }

    public static void clearAll(ItemStack stack, Player player) {
        stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        player.playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        // 潜行时只放置方块
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NonNull InteractionResult interactLivingEntity(ItemStack stack, @NonNull Player player, @NonNull LivingEntity target, @NonNull InteractionHand hand) {
        if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return InteractionResult.PASS;
        }
        CompoundTag tag = getBlockEntityData(stack);
        Level level = player.level();

        // 先判断状态
        int status = getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);
        if (status == ITeapot.FINISHED) {
            pourOut(stack, level);
        } else if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = getString(tag, TeapotBlockEntity.TEA_FLUID_ID, EMPTY_TEA_FLUID.toString());
            if (!"minecraft:lava".equals(fluidId)) {
                // 仅岩浆能烫伤生物
                return InteractionResult.PASS;
            }
            // 概率消耗
            if (player.getRandom().nextFloat() < 0.3F) {
                clearAll(stack, player);
            }
        }


        RandomSource random = level.getRandom();
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
    public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        // 如果已经有流体了，返回
        if (itemInHand.has(DataComponents.BLOCK_ENTITY_DATA)) {
            CompoundTag tag = getBlockEntityData(itemInHand);
            String fluidId = getString(tag, TeapotBlockEntity.TEA_FLUID_ID, EMPTY_TEA_FLUID.toString());
            if (!fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return InteractionResult.FAIL;
            }
        }

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos relative = pos.relative(direction);

        // 权限检查
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(relative, direction, itemInHand)) {
            return InteractionResult.FAIL;
        }

        BlockState blockState = level.getBlockState(pos);
        // 必须是可以用桶取流体的方块
        if (!(blockState.getBlock() instanceof BucketPickup bucketpickup)) {
            return InteractionResult.FAIL;
        }

        // 执行取流体操作
        ItemStack pickup = bucketpickup.pickupBlock(player, level, pos, blockState);
        if (pickup.isEmpty()) {
            return InteractionResult.FAIL;
        }

        var storage = FluidUtils.getItemStorage(pickup);
        if (storage == null) {
            return InteractionResult.FAIL;
        }
        FluidVariant fluidVariant = FluidUtils.findFirstResource(storage);
        if (fluidVariant.isBlank()) {
            return InteractionResult.FAIL;
        }
        Fluid fluid = fluidVariant.getFluid();
        boolean result = fillFluid(itemInHand, fluid, player);
        if (result) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // 两种情况显示进度条
        // 1 准备阶段，装了流体
        // 2 完成阶段，有产物
        if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return false;
        }
        CompoundTag tag = quickFetchBlockEntityData(stack);

        int status = getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);

        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = getString(tag, TeapotBlockEntity.TEA_FLUID_ID, EMPTY_TEA_FLUID.toString());
            return !fluidId.equals(EMPTY_TEA_FLUID.toString());
        }

        return status == ITeapot.FINISHED;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getBarColor(ItemStack stack) {
        if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return 0x9df7ff;
        }
        CompoundTag tag = quickFetchBlockEntityData(stack);
        int status = getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);
        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = getString(tag, TeapotBlockEntity.TEA_FLUID_ID, EMPTY_TEA_FLUID.toString());
            if (fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return 0x9df7ff;
            }
            if (ModFluids.isMilkId(Identifier.tryParse(fluidId))) {
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

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return 0;
        }
        CompoundTag tag = quickFetchBlockEntityData(stack);

        int status = getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);

        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = getString(tag, TeapotBlockEntity.TEA_FLUID_ID, EMPTY_TEA_FLUID.toString());
            if (fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return 0;
            }
            return 13;
        }

        if (status == ITeapot.FINISHED) {
            int count = getResultCount(tag);
            if (count <= 0) {
                return 0;
            }
            // 进度条长度根据剩余产物数量占总量的比例来计算，满了是13格
            int capacity = isMysteryTea(tag) ? TeapotRecipe.MYSTERY_OUTPUT_COUNT : TeapotRecipe.OUTPUT_COUNT;
            return Math.clamp(Math.round(13.0F * count / capacity), 0, 13);
        }

        return 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag pFlag) {
        // 如果是成品阶段，那么显示成品信息
        if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return;
        }
        CompoundTag tag = getBlockEntityData(stack);

        int status = getInt(tag, TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);
        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = getString(tag, TeapotBlockEntity.TEA_FLUID_ID, EMPTY_TEA_FLUID.toString());
            if (fluidId.equals(EMPTY_TEA_FLUID.toString())) {
                return;
            }
            Identifier key = Identifier.tryParse(fluidId);
            if (key == null) {
                return;
            }
            consumer.accept(TeaFluidHelper.getDisplayName(key).copy().withStyle(ChatFormatting.GRAY));
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (status == ITeapot.FINISHED && level != null) {
            ItemStack result = decodeResult(tag, level);
            if (result.isEmpty()) {
                return;
            }
            Component resultComponent = ComponentUtils.formatList(Arrays.asList(
                    result.getHoverName(),
                    Component.literal("x%d".formatted(result.getCount()))
            ), CommonComponents.space(), Function.identity()).withStyle(ChatFormatting.GRAY);
            consumer.accept(resultComponent);
        }
    }

    private static SoundEvent getFillSound(Fluid fluid) {
        return fluid == Fluids.LAVA ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
    }

    private static CompoundTag getBlockEntityData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(ModBlocks.TEAPOT_BE, new CompoundTag()))
                .copyTagWithoutId();
    }

    @SuppressWarnings("deprecation")
    private static CompoundTag quickFetchBlockEntityData(ItemStack stack) {
        if (stack.get(DataComponents.BLOCK_ENTITY_DATA) == null) return new CompoundTag();
        return Objects.requireNonNull(stack.get(DataComponents.BLOCK_ENTITY_DATA)).getUnsafe();
    }

    @SuppressWarnings("all")
    private static int getInt(CompoundTag tag, String key, int defaultValue) {
        return tag.getInt(key).orElse(defaultValue);
    }

    @SuppressWarnings("all")
    private static String getString(CompoundTag tag, String key, String defaultValue) {
        return StringUtils.defaultIfBlank(tag.getString(key).orElse(defaultValue), defaultValue);
    }

    @SuppressWarnings("all")
    private static CompoundTag getCompound(CompoundTag tag, String key) {
        return tag.getCompound(key).orElse(new CompoundTag());
    }

    private static int getResultCount(CompoundTag tag) {
        return tag.getCompound(TeapotBlockEntity.RESULT)
                .map(result -> result.getInt("count").orElse(1))
                .orElse(0);
    }

    private static boolean isMysteryTea(CompoundTag tag) {
        return tag.getCompound(TeapotBlockEntity.RESULT)
                .flatMap(result -> result.getString("id"))
                .filter(MYSTERY_TEA_ID::equals)
                .isPresent();
    }

    private static ItemStack decodeResult(CompoundTag tag, Level level) {
        CompoundTag resultTag = getCompound(tag, TeapotBlockEntity.RESULT);
        if (resultTag.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (level instanceof ServerLevel serverLevel) {
            return PortHelper.decodeItem(resultTag, serverLevel);
        }
        if (level instanceof ClientLevel clientLevel) {
            return ItemStack.CODEC.parse(clientLevel.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), resultTag)
                    .result()
                    .orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
