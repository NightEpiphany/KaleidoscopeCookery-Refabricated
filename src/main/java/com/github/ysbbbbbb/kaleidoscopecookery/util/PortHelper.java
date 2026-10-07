package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueInputContextHelper;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

// 麻将看看你干的好事
public class PortHelper {
    public static Vec3 fromRGB24(int packed) {
        double d = (packed >> 16 & 0xFF) / 255.0;
        double e = (packed >> 8 & 0xFF) / 255.0;
        double f = (packed & 0xFF) / 255.0;
        return new Vec3(d, e, f);
    }

    public static final int DEFAULT_COLOR = -8355712;

    public static ResourceKey<Block> createBlockId(String name) {
       return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name));
    }

    public static ResourceKey<Item> createItemId(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name));
    }

    public static ResourceKey<PoiType> createPoiId(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name));
    }

    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, boolean alwaysPutTag, HolderLookup.Provider levelRegistry) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte)i);
                if (itemStack.isEmpty()) continue;
                listTag.add(ItemStack.CODEC.encode(itemStack, levelRegistry.createSerializationContext(NbtOps.INSTANCE), compoundTag).getOrThrow());
            }
        }

        if (!listTag.isEmpty() || alwaysPutTag) {
            tag.put("Items", listTag);
        }

        return tag;
    }

    public static void setBlockEntityData(ItemStack stack, BlockEntityType<?> blockEntityType, CompoundTag blockEntityData, Level level) {
        blockEntityData.remove("id");
        if (blockEntityData.isEmpty()) {
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        } else {
            BlockEntity.addEntityType(TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess()), blockEntityType);
            stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(blockEntityType, blockEntityData));
        }
    }

    public static ValueInput emptyStatic(Level level) {
        return new ValueInputContextHelper(level.registryAccess(), NbtOps.INSTANCE).empty();
    }

    public static CompoundTag encodeItem(ItemStack stack, Level level) {
        return ItemStack.CODEC.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), stack)
                .result()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .orElseGet(CompoundTag::new);
    }

    public static ItemStack decodeItem(CompoundTag tag, Level level) {
        if (tag.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ItemStack.CODEC.parse(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag)
                .result()
                .orElse(ItemStack.EMPTY);
    }

    public static ResourceKey<EntityType<?>> sign(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, id));
    }

    public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = new EntityDataSerializer<>() {
        @Override
        public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, CompoundTag> codec() {
            return ByteBufCodecs.TRUSTED_COMPOUND_TAG;
        }

        public @NonNull CompoundTag copy(CompoundTag compoundTag) {
            return compoundTag.copy();
        }
    };
}
