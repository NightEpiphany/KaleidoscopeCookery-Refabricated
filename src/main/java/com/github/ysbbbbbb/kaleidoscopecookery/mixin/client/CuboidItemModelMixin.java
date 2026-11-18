package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.model.GeneratedItemModel;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(CuboidItemModelWrapper.Unbaked.class)
public class CuboidItemModelMixin {
    @ModifyReturnValue(method = "bake", at = @At("RETURN"))
    private ItemModel kaleidoscopeCookery$trackGeneratedModel(ItemModel original, @Local(name = "resolvedModel") ResolvedModel resolvedModel) {
        for (ResolvedModel model = resolvedModel; model != null; model = model.parent()) {
            UnbakedModel source = model.wrapped();
            if (source.geometry() != null) {
                return source instanceof ItemModelGenerator ? new GeneratedItemModel(original) : original;
            }
        }
        return original;
    }
}
