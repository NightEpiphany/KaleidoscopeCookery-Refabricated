package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.util.event.IKCEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 砧板事件，仅在服务端触发。
 */
public abstract class ChoppingBoardEvent implements IKCEvent {
    private final ChoppingBoardBlockEntity blockEntity;

    protected ChoppingBoardEvent(ChoppingBoardBlockEntity blockEntity) {
        this.blockEntity = Objects.requireNonNull(blockEntity, "blockEntity");
    }

    public ChoppingBoardBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    private static List<ItemStack> copyResults(List<ItemStack> results) {
        Objects.requireNonNull(results, "results");
        List<ItemStack> copy = new ArrayList<>(results.size());
        for (ItemStack result : results) {
            copy.add(Objects.requireNonNull(result, "results cannot contain null").copy());
        }
        if (copy.isEmpty() || copy.stream().allMatch(ItemStack::isEmpty)) {
            throw new IllegalArgumentException("results must contain a non-empty item");
        }
        return List.copyOf(copy);
    }

    public abstract static class Cut extends ChoppingBoardEvent {
        private final ItemStack cutterItem;

        protected Cut(ChoppingBoardBlockEntity blockEntity, ItemStack cutterItem) {
            super(blockEntity);
            this.cutterItem = Objects.requireNonNull(cutterItem, "cutterItem");
        }

        public ItemStack getCutterItem() {
            return this.cutterItem.copy();
        }
    }

    /**
     * 每次成功刀切更新进度后、同步砧板前触发，可替换产物列表和模型。
     * 获取产物时返回不可变列表和物品副本，修改后需调用 setResults 写回。
     */
    public static final class Modify extends Cut {
        private List<ItemStack> results;
        private @Nullable ResourceLocation modelId;

        public Modify(ChoppingBoardBlockEntity blockEntity, ItemStack cutterItem,
                      List<ItemStack> results, @Nullable ResourceLocation modelId) {
            super(blockEntity, cutterItem);
            this.results = results;
            this.modelId = modelId;
        }

        public List<ItemStack> getResults() {
            return copyResults(this.results);
        }

        public void setResults(List<ItemStack> results) {
            this.results = results;
        }

        @Nullable
        public ResourceLocation getModelId() {
            return this.modelId;
        }

        public void setModelId(@Nullable ResourceLocation modelId) {
            this.modelId = modelId;
        }
    }

    /**
     * 每次成功刀切应用 Modify 的结果并同步砧板后触发，不代表已经切完。
     */
    public static final class Post extends Cut {
        private final @Nullable LivingEntity user;
        private final List<ItemStack> results;

        public Post(ChoppingBoardBlockEntity blockEntity, @Nullable LivingEntity user,
                    ItemStack cutterItem, List<ItemStack> results) {
            super(blockEntity, cutterItem);
            this.user = user;
            this.results = results;
        }

        /**
         * 发射器等自动化切割时为 null。
         */
        @Nullable
        public LivingEntity getUser() {
            return this.user;
        }

        public List<ItemStack> getResults() {
            return copyResults(this.results);
        }
    }

    /**
     * 取出成品或撤回未切食材时，在砧板数据已清空、同步前触发。
     */
    public static final class Reset extends ChoppingBoardEvent {
        public Reset(ChoppingBoardBlockEntity blockEntity) {
            super(blockEntity);
        }
    }
}
