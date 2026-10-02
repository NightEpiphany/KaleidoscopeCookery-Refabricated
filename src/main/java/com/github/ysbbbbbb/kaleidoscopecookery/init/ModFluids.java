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
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

@SuppressWarnings("all")
public final class ModFluids {
    public static final ResourceLocation MILK_ID = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "milk");
    public static final ResourceLocation VANILLA_MILK_ID = new ResourceLocation("minecraft", "milk");

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

        //倾倒逻辑兼容性注册
        FluidStorage.ITEM.registerFallback((stack, context) -> stack.is(Items.MILK_BUCKET)
                ? new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(milk), FluidConstants.BUCKET)
                : null);
        //装桶逻辑兼容性注册
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(Items.MILK_BUCKET), milk, FluidConstants.BUCKET));
        KaleidoscopeCookery.LOGGER.debug("Registered milk fluid and fallback bucket storage: {}", MILK_ID);
    }

    // 兼容其他模组的牛奶注册类型
    public static boolean matchesTeaFluid(ResourceLocation expected, ResourceLocation actual) {
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.equals(actual)) {
            return true;
        }
        if (!isMilkId(expected)) {
            return false;
        }
        Fluid fluid = BuiltInRegistries.FLUID.getOptional(actual).orElse(Fluids.EMPTY);
        return fluid != Fluids.EMPTY && fluid.getBucket() == Items.MILK_BUCKET;
    }

    public static boolean isMilkId(ResourceLocation id) {
        return MILK_ID.equals(id) || VANILLA_MILK_ID.equals(id);
    }

    public static boolean matchesFluidTag(ResourceLocation actual, TagKey<Fluid> tag) {
        return actual != null && BuiltInRegistries.FLUID.getOptional(actual)
                .map(fluid -> fluid.is(tag))
                .orElse(false);
    }

    @Environment(EnvType.CLIENT)
    public static void registerFluidRenderers() {
        Fluid milk = BuiltInRegistries.FLUID.getOptional(MILK_ID)
                .or(() -> BuiltInRegistries.FLUID.getOptional(VANILLA_MILK_ID))
                .orElseThrow(() ->
                        new IllegalStateException("Milk fluid was not initialized"));
        String texture = BuiltInRegistries.FLUID.getKey(milk).getNamespace().equals("minecraft")
                ? "block/milk_still" : "stockpot/milk";
        registerSingleStateRender(milk, texture, 0xFFFFFFFF);
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
        return new ResourceLocation(KaleidoscopeCookery.MOD_ID, path);
    }
}
