package com.github.ysbbbbbb.kaleidoscopecookery.util.fluids;

import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
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
        return emptyItem(user, bucket, handler, amount, getInteractionContext(user, bucket));
    }

    public static boolean emptyItem(LivingEntity user, ItemStack bucket, Storage<FluidVariant> handler, int amount,
                                    ContainerItemContext context) {
        if (bucket.isEmpty() || handler == null) {
            return false;
        }
        boolean creativePlayer = isCreativePlayer(user);
        if (creativePlayer) {
            context = ContainerItemContext.withConstant(bucket.copyWithCount(1));
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
            long extracted = itemStorage.extract(resource, inserted, transaction);
            if (extracted != inserted) {
                return false;
            }
            transaction.commit();
        }

        ItemVariant resultVariant = context.getItemVariant();
        ItemStack result = ItemUtils.getContainerItem(resultVariant.toStack((int) Math.min(Integer.MAX_VALUE, context.getAmount()))).getDefaultInstance();
        if (creativePlayer) {
            if (!result.isEmpty() && user instanceof Player player) {
                ItemUtils.giveItemToPlayer(player, result);
            }
        } else if (!isSurvivalPlayer(user)) {
            bucket.shrink(1);
            if (!result.isEmpty()) {
                ItemUtils.getItemToLivingEntity(user, result);
            }
        }
        playTransferSound(user, FluidVariantAttributes.getEmptySound(resource));
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
        boolean creativePlayer = isCreativePlayer(user);
        ContainerItemContext context = getInteractionContext(user, bucket);
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

        ItemVariant resultVariant = context.getItemVariant();
        ItemStack result = ItemUtils.getContainerItem(resultVariant.toStack((int) Math.min(Integer.MAX_VALUE, context.getAmount()))).getDefaultInstance();
        if (creativePlayer) {
            if (!result.isEmpty() && user instanceof Player player) {
                ItemUtils.giveItemToPlayer(player, result);
            }
        } else if (!isSurvivalPlayer(user)) {
            bucket.shrink(1);
            if (!result.isEmpty()) {
                ItemUtils.getItemToLivingEntity(user, result);
            }
        }
        playTransferSound(user, FluidVariantAttributes.getFillSound(resource));
        return true;
    }

    public static void playTransferSound(LivingEntity user, @Nullable SoundEvent sound) {
        if (sound != null && !user.level().isClientSide) {
            // 服务端运行
            user.level().playSound(null, user.getX(), user.getY(), user.getZ(),
                    sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static boolean isFluidContainer(ItemStack stack) {
        return getItemStorage(stack) != null;
    }

    public static boolean hasFluid(ItemStack stack) {
        Storage<FluidVariant> storage = getItemStorage(stack);
        return storage != null && findFirstAmount(storage) > 0;
    }

    @Nullable
    public static Storage<FluidVariant> getItemStorage(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ContainerItemContext context = ContainerItemContext.withConstant(stack.copyWithCount(1));
        return context.find(FluidStorage.ITEM);
    }

    private static boolean isSurvivalPlayer(LivingEntity user) {
        return user instanceof Player player && !player.isCreative();
    }

    private static boolean isCreativePlayer(LivingEntity user) {
        return user instanceof Player player && player.isCreative();
    }

    private static ContainerItemContext getInteractionContext(LivingEntity user, ItemStack stack) {
        if (isSurvivalPlayer(user) && user instanceof Player player) {
            return ContainerItemContext.ofPlayerHand(player, InteractionHand.MAIN_HAND);
        }
        return ContainerItemContext.withConstant(stack.copyWithCount(1));
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
}
