package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class SteamerItem extends WithTooltipsBlockItem {
    private static final int NONE = 0;
    private static final int HAS = 1;

    public SteamerItem(Block block, Properties properties) {
        super(block, properties, "steamer");
    }

    @Override
    protected boolean placeBlock(@NonNull BlockPlaceContext context, @NonNull BlockState state) {
        // 蒸笼只能从顶面放置，物品始终只代表一层。
        return context.getClickedFace() == Direction.UP && super.placeBlock(context, state);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @NonNull Optional<TooltipComponent> getTooltipImage(@NonNull ItemStack stack) {
        ClientLevel level = Minecraft.getInstance().level;
        TypedEntityData<?> blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (level == null || blockEntityData == null || !blockEntityData.contains(SteamerBlockEntity.ITEMS_TAG)) {
            return Optional.empty();
        }
        NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(),
                blockEntityData.copyTagWithoutId()), items);
        return Optional.of(new ItemContainerTooltip(new ItemStackHandler(items)));
    }

    @Override
    public int getDefaultMaxStackSize() {
        if (this.getDefaultInstance().has(DataComponents.BLOCK_ENTITY_DATA)) {
            return 1;
        }
        return super.getDefaultMaxStackSize();
    }

    @SuppressWarnings("unused")
    @Deprecated
    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        CompoundTag data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(ModBlocks.STEAMER_BE, new CompoundTag())).copyTagWithoutId();
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
            if (level != null) {
                ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data), items);
                if (items.stream().anyMatch(item -> !item.isEmpty())) {
                    return HAS;
                }
            }
        }
        return NONE;
    }
}
