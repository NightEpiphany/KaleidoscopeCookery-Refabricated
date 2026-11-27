package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.KitchenShelfBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.KitchenShelfItemHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.KitchenShelfItemStorage;
import com.github.ysbbbbbb.kaleidoscopecookery.util.forge.ItemHandlerHelper;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.KitchenShelfItemHandler.CAPACITY;

@SuppressWarnings("UnstableApiUsage")
public class KitchenShelfBlockEntity extends BaseBlockEntity {
    private static final int RESTOCK_INTERVAL = 83;
    private static final int BULK_TAKE_LIMIT = 64;
    private final KitchenShelfItemHandler inventory = new KitchenShelfItemHandler(this);
    private final KitchenShelfItemStorage itemStorage = new KitchenShelfItemStorage(inventory, this::refresh);
    private boolean wasPowered;

    public KitchenShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.KITCHEN_SHELF_BE, pos, state);
    }

    public ItemStack getStoredStack() {
        return inventory.getStackInSlot(0);
    }

    public Storage<ItemVariant> getItemStorage() {
        return itemStorage;
    }

    public boolean interact(LivingEntity living, boolean bulk) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ItemStack held = living.getMainHandItem();
        ItemStack stored = getStoredStack();

        // 空手取出
        if (held.isEmpty()) {
            if (stored.isEmpty()) {
                return false;
            }
            ItemStack taken = takeStored(bulk ? BULK_TAKE_LIMIT : 1);
            dropToFront(serverLevel, getBlockState(), taken, false);
            return true;
        }

        // 存入
        if (!stored.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stored, held)) {
            return false;
        }
        int amount = bulk ? Math.min(held.getCount(), CAPACITY - stored.getCount()) : 1;
        if (amount <= 0) {
            return false;
        }
        ItemStack inserted = held.copyWithCount(amount);
        int remainder = inventory.insertItem(0, inserted, false).getCount();
        if (!(living instanceof Player player) || !player.isCreative()) {
            held.shrink(amount - remainder);
        }
        return remainder < amount;
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, KitchenShelfBlockEntity shelf) {
        long offsetTime = level.getGameTime() + pos.hashCode();
        if (Math.floorMod(offsetTime, RESTOCK_INTERVAL) == 0) {
            shelf.restock(state);
        }
    }

    public boolean updatePower(boolean powered) {
        if (powered == wasPowered) {
            return false;
        }
        wasPowered = powered;
        setChanged();
        return powered;
    }

    private void restock(BlockState state) {
        if (level == null || getStoredStack().getCount() >= CAPACITY) {
            return;
        }
        Direction front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        for (Direction direction : Direction.values()) {
            if (direction == front) {
                continue;
            }
            BlockPos sourcePos = worldPosition.relative(direction);
            BlockEntity source = level.getBlockEntity(sourcePos);
            if (source == null || source instanceof KitchenShelfBlockEntity) {
                continue;
            }
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, sourcePos, direction.getOpposite());
            if (storage != null) {
                restockFromStorage(storage);
            }
            if (getStoredStack().getCount() >= CAPACITY) {
                break;
            }
        }
    }

    private void restockFromStorage(Storage<ItemVariant> storage) {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
                ItemVariant candidate = view.getResource();
                ItemStack stored = getStoredStack();
                if (candidate.isBlank()
                        || (!stored.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stored, candidate.toStack()))) {
                    continue;
                }
                int needed = Math.min(16, CAPACITY - stored.getCount());
                try (Transaction transfer = transaction.openNested()) {
                    long extracted = view.extract(candidate, needed, transfer);
                    if (extracted > 0 && itemStorage.insert(candidate, extracted, transfer) == extracted) {
                        transfer.commit();
                    }
                }
                if (getStoredStack().getCount() >= CAPACITY) {
                    break;
                }
            }
            transaction.commit();
        }
    }

    public void dispense(BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            dropToFront(serverLevel, state, takeStored(1), true);
        }
    }

    private ItemStack takeStored(int amount) {
        ItemStack stored = getStoredStack();
        int taken = Math.min(amount, stored.getCount());
        if (taken == 0) {
            return ItemStack.EMPTY;
        }
        inventory.setStackInSlot(0, stored.copyWithCount(stored.getCount() - taken));
        return stored.copyWithCount(taken);
    }

    public boolean dispenseMatching(List<ItemStack> ingredients, boolean useRecipeCounts) {
        if (level == null) {
            return false;
        }

        Reference2IntMap<Item> remaining = new Reference2IntOpenHashMap<>(ingredients.size());
        for (ItemStack ingredient : ingredients) {
            if (ingredient.isEmpty()) {
                continue;
            }
            if (useRecipeCounts) {
                remaining.mergeInt(ingredient.getItem(), ingredient.getCount(), Integer::sum);
            } else {
                remaining.put(ingredient.getItem(), 1);
            }
        }
        if (remaining.isEmpty()) {
            return false;
        }

        boolean dispensed = false;
        for (BlockPos shelfPos : KitchenShelfBlock.getConnectedShelves(level, worldPosition)) {
            if (!(level.getBlockEntity(shelfPos) instanceof KitchenShelfBlockEntity shelf)) {
                continue;
            }
            Item item = shelf.getStoredStack().getItem();
            int needed = remaining.getInt(item);
            if (needed == 0) {
                continue;
            }
            if (!(level instanceof ServerLevel serverLevel)) {
                return level.isClientSide;
            }

            ItemStack taken = shelf.takeStored(needed);
            int takenCount = taken.getCount();
            if (takenCount == 0) {
                continue;
            }
            dropToFront(serverLevel, getBlockState(), taken, true);
            dispensed = true;
            if (takenCount == needed) {
                remaining.removeInt(item);
                if (remaining.isEmpty()) {
                    break;
                }
            } else {
                remaining.put(item, needed - takenCount);
            }
        }
        return dispensed;
    }

    private void dropToFront(ServerLevel level, BlockState state, ItemStack stack, boolean dispenseEffects) {
        if (stack.isEmpty()) {
            return;
        }

        Direction front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        double x = worldPosition.getX() + 0.5 + front.getStepX() * 0.65;
        double y = worldPosition.getY() + 0.45;
        double z = worldPosition.getZ() + 0.5 + front.getStepZ() * 0.65;

        int maxStackSize = stack.getMaxStackSize();
        while (!stack.isEmpty()) {
            ItemEntity entity = new ItemEntity(level, x, y, z, stack.split(maxStackSize));
            entity.setDeltaMovement(front.getStepX() * 0.2, 0.15, front.getStepZ() * 0.2);
            level.addFreshEntity(entity);
        }
        if (dispenseEffects) {
            level.playSound(null, worldPosition, ModSounds.BLOCK_KITCHEN_SHELF, SoundSource.BLOCKS, 1, 1);
            level.sendParticles(ModParticles.COOKING, x, y, z, 6, 0.12, 0.08, 0.12, 0.01);
        }
    }

    @Override
    protected void saveAdditional(@NonNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putBoolean("WasPowered", wasPowered);
    }

    @Override
    public void load(@NonNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        wasPowered = tag.getBoolean("WasPowered");
    }
}
