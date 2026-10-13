package com.github.ysbbbbbb.kaleidoscopecookery.client.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.*;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.model.ExtraLoadingItemModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.registry.ExtraModelLoadingProperties;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.FlatulenceEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay.PotOverlayEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay.TeapotOverlayEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay.TrashCanOverlay;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModClientTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModEntitiesRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModParticleFactoryRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.MillstoneModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TeapotModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TrashCanModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.LeftBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.NormalBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.PatternModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.*;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.item.StrawHatArmorRenderer;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.create.ponder.init.PonderCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.init.TrinketsCompactClient;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ConfigGetter;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Objects;

import static com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery.MOD_ID;

@Environment(EnvType.CLIENT)
public final class ClientRegistry {
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded(ConfigGetter.ID)
                && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ClientConfig.init();
        // 注册盔甲渲染器
        ArmorRenderer.register(new StrawHatArmorRenderer(), ModItems.STRAW_HAT.get(), ModItems.STRAW_HAT_FLOWER.get());

        registerItemProperties();
        registerClientEvents();
        registerBlockEntityRenderers();
        modCompatClient();

        ModClientTooltip.register();
        ModEntitiesRender.register();
        ModParticleFactoryRegistry.register();
        ModFluids.registerFluidRenderers();

        FabricLoader
                .getInstance()
                .getModContainer(MOD_ID)
                .ifPresent(container -> {
                            ResourceLoader.registerBuiltinPack(
                                    Identifier.withDefaultNamespace("kaleidoscope_eating_animation"),
                                    container,
                                    Component.translatable("resourcePack.kaleidoscope_eating_animation"),
                                    PackActivationType.NORMAL
                            );
                            ResourceLoader.registerBuiltinPack(
                                    Identifier.withDefaultNamespace("kaleidoscope_classic_texture"),
                                    container,
                                    Component.translatable("resourcePack.kaleidoscope_classic_texture"),
                                    PackActivationType.NORMAL
                            );
                        }
                );
    }

    private static void registerItemProperties() {
        ConditionalItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchen_shovel/has_oil"), KitchenShovelCondition.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot_lid/using"), StockpotLidCondition.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer/has_item"), SteamerCondition.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_item/has_recipe"), RecipeItemCondition.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "oil_pot/has_oil"), OilPotBlockCondition.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "transmutation_lunch_bag/has_food"), TransmutationLunchBagItemCondition.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "ingredient/special_render"), SpecialRenderCondition.MAP_CODEC);
        ItemModels.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "model_arguments"), ExtraLoadingItemModel.Unbaked.MAP_CODEC);
        ExtraModelLoadingProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "model_display/model"), ModelDisplayCondition.MAP_CODEC);
    }

    private static void registerClientEvents() {
        FlatulenceEvent.register();
        PotOverlayEvent.register();
        TeapotOverlayEvent.register();
        TrashCanOverlay.register();
    }

    private static void registerBlockEntityRenderers() {
        register(ModBlocks.POT_BE, PotBlockEntityRender::new);
        register(ModBlocks.FRUIT_BASKET_BE, FruitBasketBlockEntityRender::new);
        register(ModBlocks.CHOPPING_BOARD_BE, ChoppingBoardBlockEntityRender::new);
        register(ModBlocks.STOCKPOT_BE, StockpotBlockEntityRender::new);
        register(ModBlocks.KITCHENWARE_RACKS_BE, KitchenwareRacksBlockEntityRender::new);
        register(ModBlocks.CHAIR_BE, ChairBlockEntityRender::new);
        register(ModBlocks.TABLE_BE, TableBlockEntityRender::new);
        register(ModBlocks.SHAWARMA_SPIT_BE, ShawarmaSpitBlockEntityRender::new);
        register(ModBlocks.MILLSTONE_BE, MillstoneBlockEntityRender::new);
        register(ModBlocks.RECIPE_BLOCK_BE, RecipeBlockEntityRender::new);
        register(ModBlocks.STEAMER_BE, SteamerBlockEntityRender::new);
        register(ModBlocks.TRASH_CAN_BE, TrashCanBlockEntityRender::new);
        register(ModBlocks.TEAPOT_BE, TeapotBlockEntityRender::new);
        register(ModBlocks.FOOD_BITE_THREE_BY_THREE_BE, FoodBiteThreeByThreeBlockEntityRender::new);
        register(ModBlocks.BAMBOO_TRAY_BE, BambooTrayBlockEntityRender::new);
        register(ModBlocks.TEA_BANNER_BE, TeaBannerBlockEntityRenderer::new);

        ModelLayerRegistry.registerModelLayer(MillstoneModel.LAYER_LOCATION, MillstoneModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(TeapotModel.LAYER_LOCATION, TeapotModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(TrashCanModel.LAYER_LOCATION, TrashCanModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(NormalBannerModel.LAYER_LOCATION, NormalBannerModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(LeftBannerModel.LAYER_LOCATION, LeftBannerModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(PatternModel.LAYER_LOCATION, PatternModel::createBodyLayer);
    }

    private static <T extends BlockEntity, S extends BlockEntityRenderState> void register(final BlockEntityType<? extends T> type, final BlockEntityRendererProvider<T, S> renderer) {
        Registry.register(BuiltInClientRegistries.BLOCK_ENTITY_RENDERER,
                ResourceKey.create(ClientRegistries.BLOCK_ENTITY_RENDERER, Objects.requireNonNull(
                        BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type),
                        () -> "Unregistered block entity type: " + type)),
                renderer);
    }

    private static void modCompatClient() {
        PonderCompat.init();
        TrinketsCompactClient.init();
    }
}
