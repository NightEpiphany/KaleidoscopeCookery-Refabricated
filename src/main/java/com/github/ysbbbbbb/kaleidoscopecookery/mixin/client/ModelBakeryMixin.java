package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
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

    @Shadow
    protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation modelLocation);
    @Unique
    private static final ModelResourceLocation EGG = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "egg"));
    @Unique
    private static final ModelResourceLocation HONEY = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "honey"));
    @Unique
    private static final ModelResourceLocation TOMATO_SAUCE = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tomato_sauce_spray"));
    @Unique
    private static final ModelResourceLocation RAW_DOUGH_IN_MILLSTONE = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_dough_in_millstone"));
    @Unique
    private static final ModelResourceLocation OIL_IN_MILLSTONE = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "oil_in_millstone"));
    @Unique
    private static final ModelResourceLocation COLD_CUT_HAM_SLICES_GUI = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cold_cut_ham_slices_in_gui"));
    @Unique
    private static final ModelResourceLocation TEAPOT_GUI = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot_in_gui"));
    @Unique
    private static final ModelResourceLocation FRUIT_BASKET_GUI = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fruit_basket_in_gui"));
    @Unique
    private static final ModelResourceLocation COLD_CUT_HAM_SLICES = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cold_cut_ham_slices_block"));
    @Unique
    private static final ModelResourceLocation TEAPOT = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot_3d"));
    @Unique
    private static final ModelResourceLocation FRUIT_BASKET = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fruit_basket_full"));


    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", shift = At.Shift.AFTER, ordinal = 1))
    private void kaleidoscopeCookery$loadSpecialItemModelAndDependencies(BlockColors blockColors, ProfilerFiller profilerFiller, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources, CallbackInfo ci) {
        this.loadSpecialItemModelAndDependencies(HONEY);
        this.loadSpecialItemModelAndDependencies(EGG);
        this.loadSpecialItemModelAndDependencies(RAW_DOUGH_IN_MILLSTONE);
        this.loadSpecialItemModelAndDependencies(OIL_IN_MILLSTONE);
        this.loadSpecialItemModelAndDependencies(COLD_CUT_HAM_SLICES_GUI);
        this.loadSpecialItemModelAndDependencies(TOMATO_SAUCE);
        this.loadSpecialItemModelAndDependencies(TEAPOT_GUI);
        this.loadSpecialItemModelAndDependencies(FRUIT_BASKET_GUI);
        this.loadSpecialItemModelAndDependencies(COLD_CUT_HAM_SLICES);
        this.loadSpecialItemModelAndDependencies(TEAPOT);
        this.loadSpecialItemModelAndDependencies(FRUIT_BASKET);
    }
}
