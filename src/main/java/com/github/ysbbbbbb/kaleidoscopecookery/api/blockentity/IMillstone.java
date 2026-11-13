package com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 把切菜板单独拆分出一个接口类，方便其他模组操纵，比如女仆
 */
public interface IMillstone {
    /**
     * 放置物品到石磨上
     *
     * @param level     使用者所在的世界
     * @param putOnItem 打算放置的物品
     * @return 放置是否成功
     */
    boolean onPutItem(Level level, ItemStack putOnItem);

    /**
     * 放置物品到石磨上，创造模式玩家不消耗物品；自动化可传入 null。
     */
    default boolean onPutItem(Level level, @Nullable LivingEntity user, ItemStack putOnItem) {
        return onPutItem(level, putOnItem);
    }
}
