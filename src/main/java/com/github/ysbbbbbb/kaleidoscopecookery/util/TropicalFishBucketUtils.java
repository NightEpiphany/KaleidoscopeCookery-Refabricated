package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.CustomData;

public final class TropicalFishBucketUtils {
    private static final TropicalFish.Pattern[] PATTERNS = TropicalFish.Pattern.values();
    private static final DyeColor[] COLORS = DyeColor.values();

    private TropicalFishBucketUtils() {
    }

    public static void initializeVariant(ItemStack stack, RandomSource random) {
        if (!(stack.getItem() instanceof MobBucketItem bucket)
                || bucket.type != EntityType.TROPICAL_FISH) {
            return;
        }

        CustomData existingData = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag bucketData = existingData.copyTag();
        if (bucketData.contains(TropicalFish.BUCKET_VARIANT_TAG)) {
            return;
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
        bucketData.putInt(TropicalFish.BUCKET_VARIANT_TAG, variant.getPackedId());
        stack.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(bucketData));
    }
}
