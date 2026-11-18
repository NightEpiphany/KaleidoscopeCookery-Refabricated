package com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TableBlockEntityRenderState extends BlockEntityRenderState {
    public List<TableItemStackRenderState> items = Collections.emptyList();
    public boolean hasCarpet = false;
    public Direction.Axis axis = Direction.Axis.X;
    public ItemStackRenderState carpetModel = new ItemStackRenderState();
}
