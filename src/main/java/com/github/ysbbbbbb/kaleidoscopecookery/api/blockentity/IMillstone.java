package com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * 把石磨单独拆分出一个接口类，方便其他模组操纵，比如车万女仆
 */
public interface IMillstone extends IKaleidoscopeBlockEntity {
    /**
     * 放置物品到石磨上
     *
     * @param level     使用者所在的世界
     * @param user      使用者实体
     * @param putOnItem 打算放置的物品
     * @return 放置是否成功
     */
    boolean onPutItem(Level level, @Nullable final LivingEntity user, ItemStack putOnItem);

    /**
     * 取回石磨中全部尚未加工完成的原材料，并重置加工进度
     *
     * @param level 使用者所在的世界
     * @param user  使用者实体
     * @return 取回是否成功
     */
    default boolean onTakeOut(Level level, LivingEntity user) {
        return false;
    }
}
