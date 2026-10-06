package com.github.ysbbbbbb.kaleidoscopecookery.api.entity;

/**
 * 代表该方块可以被玩家坐
 */
public interface ISittable {
    /**
     * 返回实体落座高度
     * @return 座位高度偏移量
     */
    default float renderHeightOffset() {
        return 0.0F;
    }
}
