package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.ids.ModBlockItemIds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class BambooTubeRiceBlockItem extends BlockItem implements IHasContainer {
    public BambooTubeRiceBlockItem(Item.Properties p, Block block, FoodProperties properties) {
        super(block, p.food(properties).setId(ModBlockItemIds.BAMBOO_TUBE_RICE.item()).useBlockDescriptionPrefix());
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        return this.returnContainerToEntity(itemStack, level, entity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        MutableComponent full = Component.translatable("tooltip.kaleidoscope_cookery.bamboo_tube_rice.maxim")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);

        // 先拿到纯文本，再按 \n 切
        String text = full.getString();
        for (String line : text.split("\n")) {
            if (!line.isEmpty()) {
                builder.accept(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }
    }

    @Override
    public Item getContainerItem() {
        return Items.BAMBOO;
    }
}
