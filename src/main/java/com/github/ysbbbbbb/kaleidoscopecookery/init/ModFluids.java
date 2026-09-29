package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.MilkFluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

@SuppressWarnings("all")
public final class ModFluids {
    public static final ResourceLocation MILK_ID = ResourceLocation.withDefaultNamespace("milk");

    public static void registerFluids() {
        Fluid milk = BuiltInRegistries.FLUID.getOptional(MILK_ID).orElseGet(() ->
                Registry.register(BuiltInRegistries.FLUID, MILK_ID, new MilkFluid()));
        FluidVariantAttributes.register(milk, new FluidVariantAttributeHandler() {
            @Override
            public Component getName(FluidVariant variant) {
                return Component.translatable("fluid.minecraft.milk");
            }

            @Override
            public Optional<SoundEvent> getFillSound(FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_FILL);
            }

            @Override
            public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_EMPTY);
            }
        });

        // Let a mod's dedicated milk bucket provider take precedence.
        FluidStorage.ITEM.registerFallback((stack, context) -> stack.is(Items.MILK_BUCKET)
                ? new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(milk), FluidConstants.BUCKET)
                : null);
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(Items.MILK_BUCKET), milk, FluidConstants.BUCKET));
    }

    public static boolean matchesTeaFluid(ResourceLocation expected, ResourceLocation actual) {
        return expected.equals(actual) || (MILK_ID.equals(expected)
                && BuiltInRegistries.FLUID.get(actual).getBucket() == Items.MILK_BUCKET);
    }

    @Environment(EnvType.CLIENT)
    public static void registerFluidRenderers() {
        Fluid milk = BuiltInRegistries.FLUID.getOptional(MILK_ID)
                .orElseThrow(() ->
                        new IllegalStateException("Milk fluid was not initialized"));
        registerSingleStateRender(milk, "block/milk_still", 0xFFFFFFFF);
    }

    @Environment(EnvType.CLIENT)
    private static void registerSingleStateRender(Fluid still, String stillTexture, int color) {
        ResourceLocation stillId = id(stillTexture);
        FluidRenderHandlerRegistry.INSTANCE.register(still, still, new SimpleFluidRenderHandler(stillId, stillId, stillId, color));
        registration(still, color, stillId);
    }

    @Environment(EnvType.CLIENT)
    private static void registration(Fluid fluid, int color, ResourceLocation stillId) {
        FluidVariantRendering.register(fluid, new FluidVariantRenderHandler() {
            @Override
            public TextureAtlasSprite[] getSprites(FluidVariant fluidVariant) {
                TextureAtlasSprite stillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillId);
                return new TextureAtlasSprite[]{stillSprite, stillSprite};
            }

            @Override
            public int getColor(FluidVariant fluidVariant, BlockAndTintGetter view, BlockPos pos) {
                return color;
            }
        });
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }
}
