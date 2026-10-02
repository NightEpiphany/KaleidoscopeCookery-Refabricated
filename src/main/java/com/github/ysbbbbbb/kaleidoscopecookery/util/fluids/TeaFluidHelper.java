package com.github.ysbbbbbb.kaleidoscopecookery.util.fluids;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * 茶壶液体特判的工具类
 */
public final class TeaFluidHelper {
    private TeaFluidHelper() {
    }

    public record Reference(ResourceLocation id, boolean physicalFluid) {
    }

    /**
     * 从物品中获取液体引用的实体对象信息
     *
     * @param stack 物品
     * @return 液体信息
     */
    @Nullable
    public static Reference fromContainer(ItemStack stack) {
        Storage<FluidVariant> storage = FluidUtils.getItemStorage(stack);
        if (storage != null) {
            FluidVariant resource = FluidUtils.findFirstResource(storage);
            if (!resource.isBlank()) {
                return new Reference(BuiltInRegistries.FLUID.getKey(resource.getFluid()), true);
            }
        }

        ResourceLocation virtualId = getVirtualBucketFluidId(stack);
        if (virtualId != null) {
            return new Reference(virtualId, false);
        }
        return null;
    }

    /**
     * 是否是合法的液体生产者
     * @param stack 物品
     * @return 是否合法
     */
    public static boolean isSupportedFilledContainer(ItemStack stack) {
        return fromContainer(stack) != null;
    }

    /**
     * 是否是合法的液体消耗者
     * @param stack 物品
     * @return 是否合法
     */
    public static boolean isSupportedEmptyContainer(ItemStack stack) {
        return FluidUtils.isFluidContainer(stack) || stack.is(Items.BUCKET);
    }

    /**
     * 是否是可倒的物理液体
     */
    public static boolean isPhysicalFluid(ResourceLocation id) {
        if (id == null || id.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            return false;
        }
        Fluid fluid = BuiltInRegistries.FLUID.get(id);
        return fluid != Fluids.EMPTY;
    }

    /**
     * 获取物品存储的液体ID
     * @param id 物品ID
     * @return 液体ID
     */
    public static Component getDisplayName(ResourceLocation id) {
        if (id == null || id.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            return Component.translatable("mco.configure.world.slot.empty");
        }
        if (isPhysicalFluid(id)) {
            if (ModFluids.isMilkId(id)) {
                return Component.translatable("fluid.minecraft.milk");
            }
            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            return FluidVariantAttributes.getName(FluidVariant.of(fluid));
        }

        ItemStack filledContainer = getFilledContainer(id);
        if (!filledContainer.isEmpty()) {
            String displayName = filledContainer.getHoverName().getString();
            Item containerItem = ItemUtils.getContainerItem(filledContainer);
            if (containerItem != Items.AIR) {
                String containerName = containerItem.getDefaultInstance().getHoverName().getString();
                if (displayName.endsWith(containerName)) {
                    String stripped = displayName.substring(0, displayName.length() - containerName.length()).stripTrailing();
                    if (!stripped.isEmpty()) {
                        return Component.literal(stripped);
                    }
                }
            }
            return filledContainer.getHoverName();
        }

        return Component.literal(humanize(id.getPath()));
    }

    public static boolean consumeVirtualBucketContainer(LivingEntity user, ItemStack stack) {
        ResourceLocation fluidId = getVirtualBucketFluidId(stack);
        if (fluidId == null) {
            return false;
        }
        if (user instanceof Player player && player.isCreative()) {
            Item containerItem = ItemUtils.getContainerItem(stack);
            if (containerItem != Items.AIR) {
                ItemUtils.giveItemToPlayer(player, new ItemStack(containerItem));
            }
        } else {
            Item containerItem = ItemUtils.getContainerItem(stack);
            stack.shrink(1);
            if (containerItem != Items.AIR) {
                ItemUtils.getItemToLivingEntity(user, new ItemStack(containerItem));
            }
        }
        playEmptySound(user, fluidId);
        return true;
    }

    /**
     * 填装不可倾倒的液体
     * @param user 使用者
     * @param emptyContainer 空的容器
     * @param id 液体ID
     * @return 是否成功
     */
    public static boolean fillVirtualBucketContainer(LivingEntity user, ItemStack emptyContainer, ResourceLocation id) {
        ItemStack filledContainer = getFilledContainer(id);
        if (filledContainer.isEmpty()) {
            return false;
        }
        Item containerItem = ItemUtils.getContainerItem(filledContainer);
        if (containerItem == Items.AIR || !emptyContainer.is(containerItem)) {
            return false;
        }
        if (user instanceof Player player && player.isCreative()) {
            ItemUtils.giveItemToPlayer(player, filledContainer.copy());
        } else {
            emptyContainer.shrink(1);
            ItemUtils.getItemToLivingEntity(user, filledContainer.copy());
        }
        playFillSound(user, id);
        return true;
    }

    /**
     * 获取物品存储的液体ID
     * @param id 液体ID
     * @return filled container
     */
    public static ItemStack getFilledContainer(ResourceLocation id) {
        if (id == null || id.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            return ItemStack.EMPTY;
        }
        if (isPhysicalFluid(id)) {
            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            return fluid.getBucket().getDefaultInstance();
        }

        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_bucket");
        if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = BuiltInRegistries.ITEM.get(itemId).getDefaultInstance();
        return isVirtualBucketFilledContainer(stack) ? stack : ItemStack.EMPTY;
    }

    /**
     * 获取物品存储的液体ID
     * @param stack 物品
     * @return 液体ID
     */
    @Nullable
    private static ResourceLocation getVirtualBucketFluidId(ItemStack stack) {
        if (!isVirtualBucketFilledContainer(stack)) {
            return null;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = itemId.getPath();
        if (path.endsWith("_bucket")) {
            return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), path.substring(0, path.length() - "_bucket".length()));
        }
        return itemId;
    }

    private static boolean isVirtualBucketFilledContainer(ItemStack stack) {
        return stack.is(TagMod.BUCKET_CONTAINER)
                && !stack.has(DataComponents.BUCKET_ENTITY_DATA)
                && ItemUtils.getContainerItem(stack) == Items.BUCKET
                && FluidUtils.getItemStorage(stack) == null;
    }

    public static void playEmptySound(LivingEntity user, ResourceLocation id) {
        FluidUtils.playTransferSound(user, getEmptySound(id));
    }

    public static void playFillSound(LivingEntity user, ResourceLocation id) {
        FluidUtils.playTransferSound(user, getFillSound(id));
    }

    @Nullable
    private static SoundEvent getEmptySound(ResourceLocation id) {
        if (isPhysicalFluid(id)) {
            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            return FluidVariantAttributes.getEmptySound(FluidVariant.of(fluid));
        }
        return SoundEvents.BUCKET_EMPTY;
    }

    @Nullable
    private static SoundEvent getFillSound(ResourceLocation id) {
        if (isPhysicalFluid(id)) {
            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            return FluidVariantAttributes.getFillSound(FluidVariant.of(fluid));
        }
        return SoundEvents.BUCKET_FILL;
    }

    private static String humanize(String path) {
        String[] parts = path.split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
