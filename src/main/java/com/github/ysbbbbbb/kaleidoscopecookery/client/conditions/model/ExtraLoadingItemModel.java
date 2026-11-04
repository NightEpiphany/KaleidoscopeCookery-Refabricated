package com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.prop.ExtraModelLoadingProperty;
import com.github.ysbbbbbb.kaleidoscopecookery.client.conditions.registry.ExtraModelLoadingProperties;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ExtraLoadingItemModel implements ItemModel {
    private static final String MODELS = "models/";
    private static final String CHOPPING_BOARD = MODELS + "chopping_board";
    private static final String ITEM_CHOPPING_BOARD = MODELS + "item/chopping_board";
    private static final String ITEM_PREFIX = "item/";
    private static final String JSON = ".json";

    private final ExtraModelLoadingProperty property;
    private final Map<String, ItemModel> models;
    private final ItemModel fallback;

    public ExtraLoadingItemModel(ExtraModelLoadingProperty property, Map<String, ItemModel> models, ItemModel fallback) {
        this.property = property;
        this.models = models;
        this.fallback = fallback;
    }

    /**
     * 扫描所有命名空间下的砧板模型。
     * <p>
     * 26.x 的 model_display 条目是静态写死的，只有厨房本体的模型；插件模组（如国味）通过
     * model_id 注册的砧板模型不在条目表里，会回退成 item/model 的灰色占位模型。
     * 这里沿用 1.21.1 ModModelLoading 的动态扫描行为：把 models/chopping_board 与
     * models/item/chopping_board 下的所有模型补进条目表。
     */
    private static List<ExternalModel> getExternalModels() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager == null) {
            return List.of();
        }
        Map<Identifier, ExternalModel> models = new LinkedHashMap<>();
        collectModels(resourceManager, CHOPPING_BOARD, models);
        collectModels(resourceManager, ITEM_CHOPPING_BOARD, models);
        return List.copyOf(models.values());
    }

    @SuppressWarnings("all")
    private static void collectModels(ResourceManager resourceManager, String directory, Map<Identifier, ExternalModel> models) {
        resourceManager.listResources(directory, id -> id.getPath().endsWith(JSON)).keySet().forEach(resourceId -> {
            String path = resourceId.getPath();
            String modelPath = path.substring(MODELS.length(), path.length() - JSON.length());
            if (modelPath.startsWith(ITEM_PREFIX)) {
                modelPath = modelPath.substring(ITEM_PREFIX.length());
            }
            Identifier modelId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), path.substring(MODELS.length(), path.length() - JSON.length()));
            Identifier displayId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), modelPath);
            // models/chopping_board 优先；同名模型只保留先扫到的一份
            models.putIfAbsent(displayId, new ExternalModel(modelId, displayId));
        });
    }

    /**
     * @param modelId   实际模型文件 id（models/ 下）
     * @param displayId 渲染 key 使用的 id（去掉 item/ 前缀，和 model_display 条目名一致）
     */
    private record ExternalModel(Identifier modelId, Identifier displayId) {
    }

    @Override
    public void update(@NonNull ItemStackRenderState output, @NonNull ItemStack item, @NonNull ItemModelResolver resolver, @NonNull ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        output.appendModelIdentityElement(this);
        this.models.getOrDefault(this.property.get(item, level, owner == null ? null : owner.asLivingEntity(), seed, displayContext), this.fallback)
                .update(output, item, resolver, displayContext, level, owner, seed);
    }

    @Environment(EnvType.CLIENT)
    public record Entry(String name, ItemModel.Unbaked model) {
        public static final Codec<ExtraLoadingItemModel.Entry> CODEC = RecordCodecBuilder.create(
                i -> i.group(
                                Codec.STRING.fieldOf("name").forGetter(ExtraLoadingItemModel.Entry::name),
                                ItemModels.CODEC.fieldOf("model").forGetter(ExtraLoadingItemModel.Entry::model)
                        )
                        .apply(i, ExtraLoadingItemModel.Entry::new)
        );
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked(
            Optional<Transformation> transformation,
            ExtraModelLoadingProperty property,
            List<ExtraLoadingItemModel.Entry> entries,
            Optional<ItemModel.Unbaked> fallback
    ) implements ItemModel.Unbaked {
        public static final MapCodec<ExtraLoadingItemModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                                Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(ExtraLoadingItemModel.Unbaked::transformation),
                                ExtraModelLoadingProperties.MAP_CODEC.forGetter(ExtraLoadingItemModel.Unbaked::property),
                                ExtraLoadingItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(ExtraLoadingItemModel.Unbaked::entries),
                                ItemModels.CODEC.optionalFieldOf("fallback").forGetter(ExtraLoadingItemModel.Unbaked::fallback)
                        )
                        .apply(i, ExtraLoadingItemModel.Unbaked::new)
        );


        @Override
        public @NonNull MapCodec<? extends ItemModel.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public @NonNull ItemModel bake(@NonNull BakingContext context, @NonNull Matrix4fc transformation) {
            Matrix4fc childTransform = Transformation.compose(transformation, this.transformation);
            Map<String, ItemModel> modelMap = new HashMap<>();

            for (Entry entry : this.entries) {
                ItemModel baked = entry.model.bake(context, childTransform);
                // 同时登记带命名空间的 key，供按完整 id 查询（避免不同模组同名模型冲突）
                if (!entry.name.contains(":"))
                    modelMap.putIfAbsent(KaleidoscopeCookery.MOD_ID + ":" + entry.name, baked);
                else
                    modelMap.put(entry.name, baked);
            }

            for (ExternalModel external : getExternalModels()) {
                if (modelMap.containsKey(external.displayId().toString())) {
                    continue;
                }
                ItemModel baked = new CuboidItemModelWrapper.Unbaked(external.modelId(), Optional.empty(), List.of())
                        .bake(context, childTransform);
                modelMap.putIfAbsent(external.displayId().toString(), baked);
            }

            ItemModel bakedFallback = this.fallback.map(m -> m.bake(context, childTransform)).orElseGet(() -> context.missingItemModel(childTransform));
            return new ExtraLoadingItemModel(this.property, modelMap, bakedFallback);
        }

        @Override
        public void resolveDependencies(@NonNull Resolver resolver) {
            this.fallback.ifPresent(m -> m.resolveDependencies(resolver));
            this.entries.forEach(entry -> entry.model.resolveDependencies(resolver));
            // 动态补进来的模型必须提前声明依赖，否则会以「not discovered previously」回退到 missing model
            getExternalModels().forEach(external -> resolver.markDependency(external.modelId()));
        }
    }
}
