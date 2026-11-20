package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;


import java.util.List;

/**
 * 带有举起姿势的方块物品
 */
public class LiftBlockItem extends WithTooltipsBlockItem {
    public LiftBlockItem(Block block, Properties properties, String name) {
        super(block, properties, name);
    }

    public LiftBlockItem(Block block, String name) {
        super(block, name);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NonNull TooltipFlag flag) {
        tooltip.add(Component.translatable(getKey()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
