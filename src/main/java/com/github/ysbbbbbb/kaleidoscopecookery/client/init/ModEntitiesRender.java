package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ColdCutHamSlicesModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ScarecrowModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.StrawHatModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.ScarecrowRender;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@Environment(EnvType.CLIENT)
public final class ModEntitiesRender {
    public static void register() {
        // 注册实体渲染器
        register(ModEntities.SIT, NoopRenderer::new);
        register(ModEntities.SCARECROW, ScarecrowRender::new);
        register(ModEntities.THROWABLE_BAOZI, ThrownItemRenderer::new);

        // 注册模型层定义
        ModelLayerRegistry.registerModelLayer(ScarecrowModel.LAYER_LOCATION, ScarecrowModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(StrawHatModel.LAYER_LOCATION, StrawHatModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ColdCutHamSlicesModel.LAYER_LOCATION, ColdCutHamSlicesModel::createBodyLayer);
    }

    private static <T extends Entity> void register(final EntityType<? extends T> type, final EntityRendererProvider<T> renderer) {
        Registry.register(BuiltInClientRegistries.ENTITY_RENDERER, ResourceKey.create(
                ClientRegistries.ENTITY_RENDERER,
                BuiltInRegistries.ENTITY_TYPE.getKey(type)
        ), renderer);
    }
}
