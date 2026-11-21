package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SteamerItem extends WithTooltipsBlockItem {
    public static final ResourceLocation HAS_ITEMS = ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "has_items");
    private static final int NONE = 0;
    private static final int HAS = 1;

    public SteamerItem() {
        super(ModBlocks.STEAMER, new Properties(), "steamer");
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        BlockState target = context.getLevel().getBlockState(context.getClickedPos());
        // 可替换的非空气方块可以从任意一面点击，其余情况仍须从顶面放置。
        return (context.getClickedFace() == Direction.UP || (!target.isAir() && target.is(BlockTags.REPLACEABLE)))
                && super.placeBlock(context, state);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player,
                                                 ItemStack stack, BlockState state) {
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof SteamerBlockEntity steamer)) {
            return false;
        }
        steamer.loadItem(stack, level);
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ClientLevel level = Minecraft.getInstance().level;
        CompoundTag data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        if (level == null || !data.contains(SteamerBlockEntity.ITEMS_TAG)) {
            return Optional.empty();
        }
        NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(data, items, level.registryAccess());
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
    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        CompoundTag data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
            if (level != null) {
                ContainerHelper.loadAllItems(data, items, level.registryAccess());
                if (items.stream().anyMatch(item -> !item.isEmpty())) {
                    return HAS;
                }
            }
        }
        return NONE;
    }
}
