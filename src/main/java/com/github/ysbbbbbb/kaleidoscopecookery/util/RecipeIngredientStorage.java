package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.RecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.yyzsbackpack.YyzsBackpackCompat;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public final class RecipeIngredientStorage {

    private static final List<Item> DYED_BUNDLES = Items.DYED_BUNDLE.asList();

    /**
     * 尝试一次性取齐食材；返回缺少的数量，缺料时回滚所有扣除。
     */
    public static Reference2IntMap<Item> extract(Player player, List<ItemStack> ingredients) {
        Reference2IntMap<Item> remaining = new Reference2IntOpenHashMap<>(ingredients.size());
        for (ItemStack ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                remaining.mergeInt(ingredient.getItem(), ingredient.getCount(), Integer::sum);
            }
        }
        if (remaining.isEmpty()) {
            return remaining;
        }

        PlayerInventoryStorage inventory = PlayerInventoryStorage.of(player);
        int mainSlots = player.getInventory().getNonEquipmentItems().size();
        boolean hasYyzBackpack = FabricLoader.getInstance().isModLoaded(YyzsBackpackCompat.ID);
        int capacity = mainSlots + (hasYyzBackpack ? YyzsBackpackCompat.MAX_EXTRA_SLOTS : 0);
        List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>(capacity);
        slots.addAll(inventory.getSlots().subList(0, mainSlots));
        if (hasYyzBackpack) {
            YyzsBackpackCompat.addSlots(player.getInventory(), slots);
        }

        try (Transaction transaction = Transaction.openOuter()) {
            for (SingleSlotStorage<ItemVariant> slot : slots) {
                extractSpecialItems(slot, remaining, transaction);
                extractView(slot, remaining, transaction);
                if (remaining.isEmpty()) {
                    break;
                }
            }

            if (!remaining.isEmpty()) {
                for (SingleSlotStorage<ItemVariant> slot : slots) {
                    Storage<ItemVariant> contents = ContainerItemContext.ofPlayerSlot(player, slot).find(ItemStorage.ITEM);
                    if (contents == null || !contents.supportsExtraction()) {
                        continue;
                    }
                    Item containerItem = slot.getResource().getItem();
                    if (containerItem == Items.BUNDLE || DYED_BUNDLES.contains(containerItem)) {
                        extractBundle(slot, remaining, transaction);
                    } else {
                        // 容器移除堆叠后可能压缩索引，倒序提取以免跳过后续食材。
                        List<StorageView<ItemVariant>> views = StreamSupport.stream(contents.nonEmptyViews().spliterator(), false).toList();
                        for (StorageView<ItemVariant> view : views.reversed()) {
                            extractView(view, remaining, transaction);
                            if (remaining.isEmpty()) {
                                break;
                            }
                        }
                    }
                    if (remaining.isEmpty()) {
                        break;
                    }
                }
            }

            if (remaining.isEmpty()) {
                transaction.commit();
            }
        }
        return remaining;
    }

    private static void extractView(StorageView<ItemVariant> view, Reference2IntMap<Item> remaining,
                                    TransactionContext transaction) {
        if (view.isResourceBlank()) {
            return;
        }
        ItemVariant resource = view.getResource();
        Item item = resource.getItem();
        int needed = remaining.getInt(item);
        if (needed > 0) {
            int extracted = (int) view.extract(resource, needed, transaction);
            updateRemaining(remaining, item, needed - extracted);
        }
    }

    private static void extractSpecialItems(SingleSlotStorage<ItemVariant> slot, Reference2IntMap<Item> remaining,
                                            TransactionContext transaction) {
        if (slot.isResourceBlank()) {
            return;
        }
        ItemVariant original = slot.getResource();
        int count = (int) slot.getAmount();
        ItemStack stack = original.toStack(count);
        Reference2IntMap<Item> supply = new Reference2IntOpenHashMap<>(remaining.size());
        new RecipeItemEvent.CheckItem(stack, supply).post();
        if (supply.isEmpty()) {
            return;
        }

        Reference2IntMap<Item> deductions = new Reference2IntOpenHashMap<>(Math.min(supply.size(), remaining.size()));
        for (Item item : supply.keySet()) {
            int needed = remaining.getInt(item);
            if (needed <= 0) {
                continue;
            }
            var event = new RecipeItemEvent.DeductItem(stack, item, new int[]{needed});
            event.post();
            int deducted = needed - event.getNeedCount();
            if (deducted > 0) {
                deductions.put(item, deducted);
            }
        }
        if (deductions.isEmpty()) {
            return;
        }

        // 特殊容器事件修改的是副本，通过真实槽位事务性写回，确保缺料时可以回滚。
        if (replaceStack(slot, original, stack, transaction)) {
            applyDeductions(remaining, deductions);
        }
    }

    private static void extractBundle(SingleSlotStorage<ItemVariant> slot, Reference2IntMap<Item> remaining,
                                      TransactionContext transaction) {
        ItemVariant original = slot.getResource();
        BundleContents contents = original.getComponents().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        List<ItemStackTemplate> stacks = new ArrayList<>(contents.size());
        Reference2IntMap<Item> deductions = new Reference2IntOpenHashMap<>(remaining.size());
        for (ItemStackTemplate stack : contents.items()) {
            Item item = stack.item().value();
            int needed = remaining.getInt(item) - deductions.getInt(item);
            int extracted = Math.min(stack.count(), Math.max(needed, 0));
            if (extracted > 0) {
                deductions.mergeInt(item, extracted, Integer::sum);
            }
            int left = stack.count() - extracted;
            if (left > 0) {
                stacks.add(stack.withCount(left));
            }
        }
        if (deductions.isEmpty()) {
            return;
        }

        // Fabric Transfer API 8.0.25 的收纳袋提取会误删剩余 1 个的堆叠，直接事务性替换组件。
        ItemStack updated = original.toStack((int) slot.getAmount());
        updated.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(List.copyOf(stacks)));
        if (replaceStack(slot, original, updated, transaction)) {
            applyDeductions(remaining, deductions);
        }
    }

    private static boolean replaceStack(SingleSlotStorage<ItemVariant> slot, ItemVariant original, ItemStack stack,
                                        TransactionContext transaction) {
        long count = slot.getAmount();
        try (Transaction nested = transaction.openNested()) {
            if (slot.extract(original, count, nested) != count) {
                return false;
            }
            if (!stack.isEmpty() && slot.insert(ItemVariant.of(stack), stack.getCount(), nested) != stack.getCount()) {
                return false;
            }
            nested.commit();
        }
        return true;
    }

    private static void applyDeductions(Reference2IntMap<Item> remaining, Reference2IntMap<Item> deductions) {
        for (Item item : deductions.keySet()) {
            updateRemaining(remaining, item, remaining.getInt(item) - deductions.getInt(item));
        }
    }

    private static void updateRemaining(Reference2IntMap<Item> remaining, Item item, int count) {
        if (count > 0) {
            remaining.put(item, count);
        } else {
            remaining.removeInt(item);
        }
    }
}
