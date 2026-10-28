package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Unique
    private static final ModelResourceLocation COLD_CUT_HAM_SLICES_GUI = new ModelResourceLocation(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "cold_cut_ham_slices_in_gui"), "inventory");
    @Unique
    private static final ModelResourceLocation TEAPOT_GUI = new ModelResourceLocation(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "teapot_in_gui"), "inventory");
    @Unique
    private static final ModelResourceLocation FRUIT_BASKET_GUI = new ModelResourceLocation(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "fruit_basket_in_gui"), "inventory");
    @Unique
    private static final ModelResourceLocation COLD_CUT_HAM_SLICES = new ModelResourceLocation(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "cold_cut_ham_slices_block"), "inventory");
    @Unique
    private static final ModelResourceLocation TEAPOT = new ModelResourceLocation(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "teapot_3d"), "inventory");
    @Unique
    private static final ModelResourceLocation FRUIT_BASKET = new ModelResourceLocation(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "fruit_basket_full"), "inventory");

    @Shadow
    protected abstract void loadTopLevel(ModelResourceLocation modelLocation);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadTopLevel(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", shift = At.Shift.AFTER, ordinal = 3))
    private void kaleidoscopeCookery$loadSpecialItemModelAndDependencies(BlockColors blockColors, ProfilerFiller profilerFiller, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources, CallbackInfo ci) {
        this.loadTopLevel(COLD_CUT_HAM_SLICES_GUI);
        this.loadTopLevel(TEAPOT_GUI);
        this.loadTopLevel(FRUIT_BASKET_GUI);
        this.loadTopLevel(COLD_CUT_HAM_SLICES);
        this.loadTopLevel(TEAPOT);
        this.loadTopLevel(FRUIT_BASKET);
    }
}
