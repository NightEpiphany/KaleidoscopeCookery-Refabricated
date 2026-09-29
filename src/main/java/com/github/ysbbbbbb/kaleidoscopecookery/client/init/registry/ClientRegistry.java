package com.github.ysbbbbbb.kaleidoscopecookery.client.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.client.event.FlatulenceEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay.PotOverlayEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.gui.overlay.TrashCanOverlay;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.MillstoneModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TeapotModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TrashCanModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.LeftBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.NormalBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.PatternModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay.TeapotOverlay;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.*;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.item.StrawHatArmorRenderer;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.ItemRenderReplacerReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.init.TrinketsCompactClient;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.config.ModConfig;

import static com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery.MOD_ID;

@Environment(EnvType.CLIENT)
public final class ClientRegistry {
    public static void init() {
        NeoForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, ClientConfig.init());
        // 注册盔甲渲染器
        ArmorRenderer.register(new StrawHatArmorRenderer(), ModItems.STRAW_HAT, ModItems.STRAW_HAT_FLOWER);

        registerItemProperties();
        registerClientEvents();
        registerBlockEntityRenderers();
        registerResourceReloadListeners();
        modCompatClient();

        ModModelLoading.register();
        ModClientTooltip.register();
        ModEntitiesRender.register();
        ModFluidRenderers.register();
        ModParticleFactoryRegistry.register();
        ModBlockRenderLayerMap.register();
        ModFluids.registerFluidRenderers();

        FabricLoader
                .getInstance()
                .getModContainer(MOD_ID)
                .ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.withDefaultNamespace("kaleidoscope_classic_texture"),
                        container,
                        ResourcePackActivationType.NORMAL
                ));
    }

    private static void registerItemProperties() {
        ItemProperties.register(ModItems.KITCHEN_SHOVEL, KitchenShovelItem.HAS_OIL_PROPERTY, KitchenShovelItem::getTexture);
        ItemProperties.register(ModItems.STOCKPOT_LID, StockpotLidItem.USING_PROPERTY, StockpotLidItem::getTexture);
        ItemProperties.register(ModItems.STEAMER, SteamerItem.HAS_ITEMS, SteamerItem::getTexture);
        ItemProperties.register(ModItems.RECIPE_ITEM, RecipeItem.HAS_RECIPE_PROPERTY, RecipeItem::getTexture);
        ItemProperties.register(ModItems.OIL_POT, OilPotItem.HAS_OIL_PROPERTY, OilPotItem::getTexture);
        ItemProperties.register(ModItems.TRANSMUTATION_LUNCH_BAG, TransmutationLunchBagItem.HAS_ITEMS_PROPERTY, TransmutationLunchBagItem::getTexture);
        ItemProperties.register(ModItems.RAW_DOUGH, RawDoughItem.PULL_PROPERTY, RawDoughItem::getTexture);
    }

    private static void registerClientEvents() {
        FlatulenceEvent.register();
        PotOverlayEvent.register();
        TrashCanOverlay.register();
        HudRenderCallback.EVENT.register(new TeapotOverlay()::render);
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ModBlocks.POT_BE, PotBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.FRUIT_BASKET_BE, FruitBasketBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.CHOPPING_BOARD_BE, ChoppingBoardBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.STOCKPOT_BE, StockpotBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.KITCHENWARE_RACKS_BE, KitchenwareRacksBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.CHAIR_BE, ChairBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.TABLE_BE, TableBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.SHAWARMA_SPIT_BE, ShawarmaSpitBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.MILLSTONE_BE, MillstoneBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.RECIPE_BLOCK_BE, RecipeBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.STEAMER_BE, SteamerBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.TEAPOT_BE, TeapotBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.BAMBOO_TRAY_BE, BambooTrayBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.TEA_BANNER_BE, TeaBannerBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlocks.TRASH_CAN_BE, TrashCanBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.FOOD_BITE_THREE_BY_THREE_BE, FoodBiteThreeByThreeBlockEntityRender::new);

        EntityModelLayerRegistry.registerModelLayer(MillstoneModel.LAYER_LOCATION, MillstoneModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(TeapotModel.LAYER_LOCATION, TeapotModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(TrashCanModel.LAYER_LOCATION, TrashCanModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(NormalBannerModel.LAYER_LOCATION, NormalBannerModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(LeftBannerModel.LAYER_LOCATION, LeftBannerModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(PatternModel.LAYER_LOCATION, PatternModel::createBodyLayer);
    }

    private static void modCompatClient() {
        TrinketsCompactClient.init();
    }

    private static void registerResourceReloadListeners() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ItemRenderReplacerReloadListener());
    }
}
