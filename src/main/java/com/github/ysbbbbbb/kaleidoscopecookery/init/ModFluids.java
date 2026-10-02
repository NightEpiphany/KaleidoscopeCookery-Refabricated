package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.MilkFluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
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
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@SuppressWarnings("all")
public final class ModFluids {

    /** 模组默认牛奶液体注册键 */
    public static final Identifier MILK_ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "milk");
    /** 兼容性键位，不可注册 */
    public static final Identifier VANILLA_MILK_ID = Identifier.withDefaultNamespace("milk");

    public static void registerFluids() {
        Fluid milk = BuiltInRegistries.FLUID.getOptional(MILK_ID)
                .orElseGet(() ->
                        Registry.register(
                                BuiltInRegistries.FLUID, MILK_ID, new MilkFluid()));
        FluidVariantAttributes.register(milk, new FluidVariantAttributeHandler() {
            @Override
            public @NonNull Component getName(@NonNull FluidVariant variant) {
                return Component.translatable("fluid.minecraft.milk");
            }

            @Override
            public @NonNull Optional<SoundEvent> getFillSound(@NonNull FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_FILL);
            }

            @Override
            public @NonNull Optional<SoundEvent> getEmptySound(@NonNull FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_EMPTY);
            }
        });

        // 桶物品液体倾倒逻辑
        FluidStorage.ITEM.registerFallback((stack, context) -> stack.is(Items.MILK_BUCKET)
                ? new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(milk), FluidConstants.BUCKET)
                : null);
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(Items.MILK_BUCKET), milk, FluidConstants.BUCKET));
    }

    public static boolean matchesTeaFluid(Identifier expected, Identifier actual) {
        if (expected.equals(actual)) {
            return true;
        }
        if (!isMilkId(expected) || actual == null) {
            return false;
        }
        Fluid fluid = BuiltInRegistries.FLUID.getOptional(actual).orElse(Fluids.EMPTY);
        return fluid != Fluids.EMPTY && fluid.getBucket() == Items.MILK_BUCKET;
    }

    public static boolean isMilkId(Identifier id) {
        return MILK_ID.equals(id) || VANILLA_MILK_ID.equals(id);
    }

    public static boolean matchesFluidTag(Identifier actual, TagKey<Fluid> tag) {
        return actual != null && BuiltInRegistries.FLUID.getOptional(actual)
                .map(fluid -> fluid.is(tag))
                .orElse(false);
    }

    @Environment(EnvType.CLIENT)
    public static void registerFluidRenderers() {
        Fluid milk = BuiltInRegistries.FLUID.getOptional(MILK_ID)
                .or(/*基本上不可能，但是以防万一*/() -> BuiltInRegistries.FLUID.getOptional(VANILLA_MILK_ID))
                .orElseThrow(() ->
                        new IllegalStateException("Milk fluid was not initialized"));
        registerSingleStateRender(
                milk,
                BuiltInRegistries.FLUID.getKey(milk).getNamespace().equals("minecraft") ?
                        "block/milk_still" :
                        "stockpot/milk",
                0xFFFFFFFF
                );
    }

    @Environment(EnvType.CLIENT)
    private static void registerSingleStateRender(Fluid still, String stillTexture, int color) {
        Identifier stillId = id(stillTexture);
        Material stillMaterial = new Material(stillId);
        FluidRenderingRegistry.register(still, still, new FluidModel.Unbaked(stillMaterial, stillMaterial, null, null), FluidRenderingRegistry.get(still));
        registration(still, color);
    }

    @Environment(EnvType.CLIENT)
    private static void registration(Fluid fluid, int color) {
        FluidVariantRendering.register(fluid, new FluidVariantRenderHandler() {
            @Override
            public int getColor(@NonNull FluidVariant fluidVariant, BlockAndTintGetter view, BlockPos pos) {
                return color;
            }
        });
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }
}
