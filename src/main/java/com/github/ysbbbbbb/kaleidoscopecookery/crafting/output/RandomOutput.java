package com.github.ysbbbbbb.kaleidoscopecookery.crafting.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public record RandomOutput(ItemStackTemplate stack, float chance) {
    public static final RandomOutput EMPTY = new RandomOutput(new ItemStackTemplate(Items.BARRIER), 1.0F);

    public static final Codec<RandomOutput> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            Item.CODEC.fieldOf("id").forGetter(output -> output.stack.typeHolder()),
            ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(output -> output.stack().count()),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(output -> output.stack().components()),
            Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(RandomOutput::chance)
    ).apply(instance, RandomOutput::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, RandomOutput> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, RandomOutput::stack,
            ByteBufCodecs.FLOAT, RandomOutput::chance,
            RandomOutput::new);

    public RandomOutput(Holder<Item> item, int count, DataComponentPatch components, float chance) {
        this(new ItemStackTemplate(item, count, components), Math.clamp(chance, 0.0F, 1.0F));
    }

    public boolean isEmpty() {
        return this.stack().create().isEmpty();
    }
}
