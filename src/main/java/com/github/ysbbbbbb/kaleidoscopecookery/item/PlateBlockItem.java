package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class PlateBlockItem extends WithTooltipsBlockItem {
    @SuppressWarnings("unused")
    public PlateBlockItem(Block block, Properties properties, String name, ResourceKey<Item> itemKey) {
        super(block, properties.setId(itemKey).useBlockDescriptionPrefix(), name);
    }

    public PlateBlockItem(Block block, String name, ResourceKey<Item> itemKey) {
        this(block, new Properties(), name, itemKey);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext tooltip, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        MutableComponent full = Component.translatable(getKey()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);

        // 先拿到纯文本，再按 \n 切
        String text = full.getString();
        for (String line : text.split("\n")) {
            if (!line.isEmpty()) {
                consumer.accept(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }
    }
}
