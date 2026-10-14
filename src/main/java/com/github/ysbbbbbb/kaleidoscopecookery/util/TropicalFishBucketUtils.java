package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;

public final class TropicalFishBucketUtils {
    private static final TropicalFish.Pattern[] PATTERNS = TropicalFish.Pattern.values();
    private static final DyeColor[] COLORS = DyeColor.values();

    private TropicalFishBucketUtils() {
    }

    /**
     * 将无花色的热带鱼桶随机初始化一次，之后渲染、取出和释放实体都复用这份组件。
     * 已有组件（包括只设置部分花色的桶）保持原样。
     */
    public static boolean initializeVariant(ItemStack stack, RandomSource random) {
        if (!(stack.getItem() instanceof MobBucketItem bucket) || bucket.type != EntityTypes.TROPICAL_FISH
                || stack.has(DataComponents.TROPICAL_FISH_PATTERN)
                || stack.has(DataComponents.TROPICAL_FISH_BASE_COLOR)
                || stack.has(DataComponents.TROPICAL_FISH_PATTERN_COLOR)) {
            return false;
        }

        TropicalFish.Variant variant;
        if (random.nextFloat() < 0.9F) {
            variant = Util.getRandom(TropicalFish.COMMON_VARIANTS, random);
        } else {
            variant = new TropicalFish.Variant(
                    Util.getRandom(PATTERNS, random),
                    Util.getRandom(COLORS, random),
                    Util.getRandom(COLORS, random)
            );
        }
        stack.set(DataComponents.TROPICAL_FISH_PATTERN, variant.pattern());
        stack.set(DataComponents.TROPICAL_FISH_BASE_COLOR, variant.baseColor());
        stack.set(DataComponents.TROPICAL_FISH_PATTERN_COLOR, variant.patternColor());
        return true;
    }
}
