package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class FruitBasketItem extends WithTooltipsBlockItem {

    private static final int MAX_SLOTS = 8;

    public FruitBasketItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1), "fruit_basket");
    }

    public static ItemStackHandler getItems(ItemStack stack) {
        ItemContainer container = stack.get(ModDataComponents.FRUIT_BASKET_ITEMS);
        ItemStackHandler items = new ItemStackHandler(MAX_SLOTS);
        if (container != null) {
            // 组件随 ItemStack 浅复制，取料时不能直接修改组件中的共享列表。
            for (int slot = 0; slot < Math.min(MAX_SLOTS, container.items().size()); slot++) {
                items.setStackInSlot(slot, container.items().get(slot).copy());
            }
        }
        return items;
    }

    public static void saveItems(ItemStack stack, ItemStackHandler items) {
        stack.set(ModDataComponents.FRUIT_BASKET_ITEMS, new ItemContainer(items.getStacks()));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.has(ModDataComponents.FRUIT_BASKET_ITEMS)) {
            ItemContainer handler = stack.get(ModDataComponents.FRUIT_BASKET_ITEMS);
            assert handler != null;
            return Optional.of(new ItemContainerTooltip(new ItemStackHandler(handler.items)));
        }
        return Optional.empty();
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    public record ItemContainer(NonNullList<ItemStack> items) {
        public static final Codec<ItemContainer> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(
                list -> {
                    NonNullList<ItemStack> handler = NonNullList.withSize(8, ItemStack.EMPTY);
                    for (int i = 0; i < Math.min(list.size(), handler.size()); i++) {
                        handler.set(i, list.get(i));
                    }
                    return new ItemContainer(handler);
                },
                ItemContainer::items
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainer> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NonNull ItemContainer decode(RegistryFriendlyByteBuf buffer) {
                CompoundTag compoundTag = buffer.readNbt();
                NonNullList<ItemStack> handler = NonNullList.withSize(8, ItemStack.EMPTY);
                if (compoundTag != null) {
                    ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, buffer.registryAccess(), compoundTag);
                    ContainerHelper.loadAllItems(valueInput, handler);
                }
                return new ItemContainer(handler);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, ItemContainer value) {
                CompoundTag compoundTag = PortHelper.saveAllItems(new CompoundTag(), value.items, true, buffer.registryAccess());
                buffer.writeNbt(compoundTag);
            }
        };
    }
}
