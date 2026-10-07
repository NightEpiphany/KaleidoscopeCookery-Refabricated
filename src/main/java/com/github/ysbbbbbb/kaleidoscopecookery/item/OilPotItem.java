package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents.OIL_POT_OIL_COUNT;

public class OilPotItem extends WithTooltipsBlockItem {
    private static final int NO_OIL = 0;
    private static final int HAS_OIL = 1;

    public OilPotItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1), "oil_pot");
    }

    public static void setOilCount(ItemStack stack, int count) {
        count = Math.clamp(count, 0, OilPotBlockEntity.MAX_OIL_COUNT);
        stack.set(OIL_POT_OIL_COUNT, count);
    }

    public static int getOilCount(ItemStack stack) {
        return stack.getOrDefault(OIL_POT_OIL_COUNT, 0);
    }

    public static boolean hasOil(ItemStack stack) {
        return getOilCount(stack) > 0;
    }

    public static void shrinkOilCount(ItemStack stack) {
        int currentCount = getOilCount(stack);
        if (currentCount > 0) {
            setOilCount(stack, currentCount - 1);
        }
    }

    public static ItemStack getFullOilPot() {
        ItemStack stack = new ItemStack(ModBlocks.OIL_POT);
        setOilCount(stack, OilPotBlockEntity.MAX_OIL_COUNT);
        return stack;
    }

    @SuppressWarnings("unused")
    @Deprecated
    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (hasOil(stack)) {
            return HAS_OIL;
        }
        return NO_OIL;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext tooltip, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltip, tooltipDisplay, consumer, tooltipFlag);
        int oilCount = getOilCount(stack);
        if (oilCount > 0) {
            consumer.accept(Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.count", oilCount)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            consumer.accept(Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.empty")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
