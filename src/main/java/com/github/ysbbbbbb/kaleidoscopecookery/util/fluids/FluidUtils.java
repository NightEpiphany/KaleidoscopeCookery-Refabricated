package com.github.ysbbbbbb.kaleidoscopecookery.util.fluids;

import net.minecraft.world.InteractionHand;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FluidUtils {
    /**
     * 类似于 forge 提供的 FluidUtil 里的方法，但不限于玩家实体
     * <p>
     * 该方法尝试将流体从物品容器转移到流体容器里，成功返回 true，失败返回 false
     *
     * @param user    任意实体，通常是玩家，但也可以是其他生物
     * @param bucket  物品形态的容器，一般是桶，但也可以是其他模组的容器
     * @param handler 流体容器的存储接口，一般是方块实体的流体槽
     * @param amount  要转移的流体量，单位为毫桶（mB）
     * @return 如果成功转移了流体，返回 true；如果没有转移任何流体，返回 false
     */
    public static boolean emptyItem(LivingEntity user, ItemStack bucket, Storage<FluidVariant> handler, int amount) {
        return emptyItem(user, bucket, handler, amount, getMutableContext(user, bucket));
    }

    public static boolean emptyItem(LivingEntity user, ItemStack bucket, Storage<FluidVariant> handler, int amount,
                                    @Nullable ContainerItemContext context) {
        if (bucket.isEmpty() || handler == null) {
            return false;
        }
        boolean creative = user instanceof Player player && player.isCreative();
        if (creative) {
            // Discard returned containers without exposing the player's inventory to extraction.
            context = ContainerItemContext.withConstant(bucket.copyWithCount(1));
        }
        if (context == null) {
            return false;
        }
        Storage<FluidVariant> itemStorage = context.find(FluidStorage.ITEM);
        if (itemStorage == null) {
            return false;
        }
        long maxTransfer = toTransferAmount(amount);
        if (maxTransfer <= 0) {
            return false;
        }
        FluidVariant resource = findFirstResource(itemStorage);
        if (resource.isBlank()) {
            return false;
        }

        try (Transaction transaction = Transaction.openOuter()) {
            long available = findFirstAmount(itemStorage);
            long moveAmount = Math.min(maxTransfer, available);
            if (moveAmount <= 0) {
                return false;
            }
            long inserted = handler.insert(resource, moveAmount, transaction);
            if (inserted <= 0) {
                return false;
            }
            try (Transaction extraction = transaction.openNested()) {
                long extracted = itemStorage.extract(resource, inserted, extraction);
                if (extracted != inserted) {
                    return false;
                }
                if (!creative) {
                    extraction.commit();
                }
            }
            transaction.commit();
        }

        SoundEvent sound = FluidVariantAttributes.getEmptySound(resource);
        if (sound != null) {
            user.playSound(sound);
        }
        return true;
    }

    /**
     * 类似于 forge 提供的 FluidUtil 里的方法，但不限于玩家实体
     * <p>
     * 该方法尝试将流体从流体容器转移到物品容器里，成功返回 true，失败返回 false
     *
     * @param user    任意实体，通常是玩家，但也可以是其他生物
     * @param bucket  物品形态的容器，一般是桶，但也可以是其他模组的容器
     * @param handler 流体容器的存储接口，一般是方块实体的流体槽
     * @param amount  要转移的流体量，单位为毫桶（mB）
     * @return 如果成功转移了流体，返回 true；如果没有转移任何流体，返回 false
     */
    public static boolean fillItem(LivingEntity user, ItemStack bucket, Storage<FluidVariant> handler, int amount) {
        if (bucket.isEmpty() || handler == null) {
            return false;
        }
        ContainerItemContext context = getMutableContext(user, bucket);
        if (context == null) {
            return false;
        }
        Storage<FluidVariant> itemStorage = context.find(FluidStorage.ITEM);
        if (itemStorage == null) {
            return false;
        }
        long maxTransfer = toTransferAmount(amount);
        if (maxTransfer <= 0) {
            return false;
        }
        FluidVariant resource = findFirstResource(handler);
        if (resource.isBlank()) {
            return false;
        }
        long available = findFirstAmount(handler);
        long moveAmount = Math.min(maxTransfer, available);
        if (moveAmount <= 0) {
            return false;
        }

        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = itemStorage.insert(resource, moveAmount, transaction);
            if (inserted <= 0) {
                return false;
            }
            long extracted = handler.extract(resource, inserted, transaction);
            if (extracted != inserted) {
                return false;
            }
            transaction.commit();
        }

        SoundEvent sound = FluidVariantAttributes.getFillSound(resource);
        if (sound != null) {
            user.playSound(sound);
        }
        return true;
    }

    public static boolean isFluidContainer(ItemStack stack) {
        return getItemStorage(stack) != null;
    }

    @Nullable
    public static Storage<FluidVariant> getItemStorage(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ContainerItemContext context = ContainerItemContext.withConstant(stack.copyWithCount(1));
        return context.find(FluidStorage.ITEM);
    }

    private static long toTransferAmount(int milliBuckets) {
        if (milliBuckets <= 0) {
            return 0;
        }
        return (long) milliBuckets * FluidConstants.BUCKET / 1000;
    }

    public static FluidVariant findFirstResource(Storage<FluidVariant> storage) {
        for (StorageView<FluidVariant> view : storage) {
            if (!view.isResourceBlank() && view.getAmount() > 0) {
                return view.getResource();
            }
        }
        return FluidVariant.blank();
    }

    public static long findFirstAmount(Storage<FluidVariant> storage) {
        for (StorageView<FluidVariant> view : storage) {
            if (!view.isResourceBlank() && view.getAmount() > 0) {
                return view.getAmount();
            }
        }
        return 0;
    }

    public static boolean hasFluid(ItemStack stack) {
        Storage<FluidVariant> itemStorage = FluidUtils.getItemStorage(stack);
        return itemStorage != null && !findFirstResource(itemStorage).isBlank();
    }

    @Nullable
    private static ContainerItemContext getMutableContext(LivingEntity user, ItemStack stack) {
        if (!(user instanceof Player player)) {
            return null;
        }
        if (player.getMainHandItem() == stack) {
            return ContainerItemContext.ofPlayerHand(player, InteractionHand.MAIN_HAND);
        }
        if (player.getOffhandItem() == stack) {
            return ContainerItemContext.ofPlayerHand(player, InteractionHand.OFF_HAND);
        }
        return null;
    }
}
