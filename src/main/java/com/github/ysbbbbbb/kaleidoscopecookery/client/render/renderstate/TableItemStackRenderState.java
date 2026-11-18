package com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class TableItemStackRenderState extends ItemStackRenderState {
    private int layerCount;
    private int generatedLayerCount;

    @Override
    public @NonNull LayerRenderState newLayer() {
        LayerRenderState layer = super.newLayer();
        this.layerCount++;
        return layer;
    }

    @Override
    public void clear() {
        super.clear();
        this.layerCount = 0;
        this.generatedLayerCount = 0;
    }

    public int getLayerCount() {
        return this.layerCount;
    }

    public void markGeneratedLayersSince(int firstLayer) {
        this.generatedLayerCount += this.layerCount - firstLayer;
    }

    public boolean hasThreeDimensionalModel() {
        return this.layerCount > this.generatedLayerCount;
    }
}
