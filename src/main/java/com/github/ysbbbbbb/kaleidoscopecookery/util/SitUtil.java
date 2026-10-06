package com.github.ysbbbbbb.kaleidoscopecookery.util;

import java.util.HashMap;
import java.util.Map;

import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ServerThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 这个工具类用于正确处理座位和实体之间的交互逻辑
 */
public class SitUtil {
    /**
     * <维度类型ID, <位置, 实体>> 此映射关系仅针对服务端
     */
    @ServerThreadSafe
    private static final Map<Identifier, Map<BlockPos, SitEntity>> OCCUPIED = new HashMap<>();

    private SitUtil() {}

    /**
     * 在映射表内添加一个坐下实体并持续跟踪缓存，这不会生成实体本身
     *
     * @param level 世界盒子
     * @param blockPos 添加实体的方块位置
     * @param entity 待加入的实体
     * @return 添加成功则返回 true，注意仅服务端。客户端永远返回false
     */
    public static boolean addSitEntity(Level level, BlockPos blockPos, SitEntity entity) {
        if (!level.isClientSide()) {
            Identifier id = getDimensionTypeId(level);

            OCCUPIED.computeIfAbsent(id, _ -> new HashMap<>());
            OCCUPIED.get(id).put(blockPos, entity);
            return true;
        }

        return false;
    }

    /**
     * 从跟踪它们的映射表中移除一个坐下实体。这不会移除实体本身。
     *
     * @param level 要从中移除实体的世界
     * @param pos 要从中移除实体的位置
     * @return 如果实体被移除则返回 true，否则返回 false。在客户端上始终返回 false。
     */
    public static boolean removeSitEntity(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            Identifier id = getDimensionTypeId(level);

            if (OCCUPIED.containsKey(id)) {
                OCCUPIED.get(id).remove(pos);
                return true;
            }
        }

        return false;
    }

    /**
     * 获取位于给定世界中给定位置的坐下实体
     *
     * @param level 要从中获取实体的世界
     * @param pos 要从中获取实体的位置
     * @return 给定世界中给定位置的实体，如果没有则为 null。在客户端上始终为 null。
     */
    public static SitEntity getSitEntity(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            Identifier id = getDimensionTypeId(level);

            if (OCCUPIED.containsKey(id) && OCCUPIED.get(id).containsKey(pos))
                return OCCUPIED.get(id).get(pos);
        }

        return null;
    }

    /**
     * 检查在给定世界中给定方块位置是否有玩家坐着
     *
     * @param level 要检查的世界
     * @param pos 要检查的位置
     * @return 如果在给定世界中给定位置有玩家坐着则返回 true，否则返回 false。在客户端上始终返回 false。
     */
    public static boolean isOccupied(Level level, BlockPos pos) {
        Identifier id = getDimensionTypeId(level);

        return SitUtil.OCCUPIED.containsKey(id) && SitUtil.OCCUPIED.get(id).containsKey(pos);
    }

    /**
     * 检查是否有玩家坐在任意位置
     *
     * @param player 要检查的玩家
     * @return 如果给定玩家坐在任意位置则返回 true，否则返回 false
     */
    public static boolean isPlayerSitting(Player player) {
        for (var entry : OCCUPIED.entrySet()) {
            for (SitEntity entity : entry.getValue().values()) {
                if (entity.hasPassenger(player))
                    return true;
            }
        }

        return false;
    }

    private static Identifier getDimensionTypeId(Level level) {
        return level.dimension().identifier();
    }
}
