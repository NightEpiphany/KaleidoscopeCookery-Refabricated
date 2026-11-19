package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.forge.ItemStackHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class SteamerItem extends WithTooltipsBlockItem {
    public static final ResourceLocation HAS_ITEMS = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "has_items");
    private static final int NONE = 0;
    private static final int HAS = 1;

    public SteamerItem() {
        super(ModBlocks.STEAMER, new Item.Properties(), "steamer");
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, @NotNull BlockState state) {
        // 蒸笼只能从顶面放置，物品始终只代表一层。
        return context.getClickedFace() == Direction.UP && super.placeBlock(context, state);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NonNull BlockPos pos, Level level, @Nullable Player player,
                                                 @NonNull ItemStack stack, @NonNull BlockState state) {
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof SteamerBlockEntity steamer)) {
            return false;
        }
        steamer.loadItem(stack);
        return true;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data == null || !data.contains(SteamerBlockEntity.ITEMS_TAG)) {
            return Optional.empty();
        }
        NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(data, items);
        return Optional.of(new ItemContainerTooltip(new ItemStackHandler(items)));
    }

    @SuppressWarnings("unused")
    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        CompoundTag data = BlockItem.getBlockEntityData(stack);
        if (data != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(data, items);
            if (items.stream().anyMatch(item -> !item.isEmpty())) {
                return HAS;
            }
        }
        return NONE;
    }
}
