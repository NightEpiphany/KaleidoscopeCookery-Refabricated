package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery.MOD_ID;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    public abstract ItemModelShaper getItemModelShaper();

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    public BakedModel kaleidoscopeCookery$usePlateModel(
            BakedModel model, ItemStack stack,
            ItemDisplayContext renderMode,
            boolean leftHanded,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay
    ) {

        boolean bl = renderMode == ItemDisplayContext.GUI || renderMode == ItemDisplayContext.GROUND || renderMode == ItemDisplayContext.FIXED;
        if (bl) {
            if (stack.is(ModItems.COLD_CUT_HAM_SLICES)) {
                return getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "cold_cut_ham_slices_in_gui")));
            }
            if (stack.is(ModItems.TEAPOT)) {
                return getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "teapot_in_gui")));
            }
            if (stack.is(ModItems.FRUIT_BASKET)) {
                return getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "fruit_basket_in_gui")));
            }
        } else {
            if (stack.is(ModItems.COLD_CUT_HAM_SLICES)) {
                return getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "cold_cut_ham_slices_block")));
            }
            if (stack.is(ModItems.TEAPOT)) {
                return getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "teapot_3d")));
            }
            if (stack.is(ModItems.FRUIT_BASKET)) {
                return getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "fruit_basket_full")));
            }
        }
        return model;
    }
}
