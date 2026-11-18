package com.github.ysbbbbbb.kaleidoscopecookery.client.model;

import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.TableItemStackRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record GeneratedItemModel(ItemModel delegate) implements ItemModel {
    @Override
    public void update(@NonNull ItemStackRenderState output, @NonNull ItemStack item,
                       @NonNull ItemModelResolver resolver, @NonNull ItemDisplayContext displayContext,
                       @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        TableItemStackRenderState tableState = output instanceof TableItemStackRenderState state ? state : null;
        int firstLayer = tableState == null ? 0 : tableState.getLayerCount();
        this.delegate.update(output, item, resolver, displayContext, level, owner, seed);
        if (tableState != null) {
            tableState.markGeneratedLayersSince(firstLayer);
        }
    }
}
