package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IMillstone;
import com.github.ysbbbbbb.kaleidoscopecookery.api.annotations.ServerThreadSafe;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.MillstoneMatchRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.output.RandomOutput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.MillstoneRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.MillstoneBindableData;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.resources.MillstoneBindableDataReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.IItemHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.fabricmc.fabric.impl.serialization.SpecialCodecs;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MillstoneBlockEntity extends BaseBlockEntity implements IMillstone {
    public static final int MAX_INPUT_COUNT = 8;
    private static final int OUTPUT_SLOT_COUNT = 4;
    private static final String ENTITY_ID_KEY = "EntityId";
    private static final String ENTITY_KEY = "StoredEntity";
    private static final String CACHE_ROT_KEY = "CacheRot";
    private static final String ROT_SPEED_TICK_KEY = "RotSpeedTick";
    private static final String LIFT_ANGLE_KEY = "LiftAngle";
    private static final String INPUT_ITEM_KEY = "InputItem";
    private static final String OUTPUT_ITEM_KEY = "OutputItem";
    private static final String PROGRESS_KEY = "Progress";

    private final RecipeManager.CachedCheck<SimpleInput, MillstoneRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.MILLSTONE_RECIPE);
    private final ItemStackHandler outputs = new ItemStackHandler(OUTPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            refresh();
        }
    };

    private UUID entityId = Util.NIL_UUID;
    private float cacheRot = 0f;
    private float rotSpeedTick = 200f;
    private float liftAngle = 5f;
    @NotNull
    private ItemStack input = ItemStack.EMPTY;
    private int progress = 0;
    // 绑定的实体
    private @Nullable EntityReference<LivingEntity> bindRef;
    private Vec3 offset = Vec3.ZERO;

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.MILLSTONE_BE, pos, state);
    }

    public float getRotation(Level level, float partialTick) {
        float degPerTick = 360f / Math.max(this.rotSpeedTick, 1);
        float gameTime = level.getGameTime() + partialTick;
        return Mth.abs(getCacheRot() + gameTime * degPerTick) % 360;
    }

    public void tick(Level level) {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (Util.NIL_UUID.equals(entityId)) {
            return;
        }
        // 每三秒额外检查一次输出，9 是为了避免大家同时触发
        if (serverLevel.getGameTime() % 20 == 9 && !this.isOutputEmpty()) {
            Direction direction = this.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            BlockPos outputPos = this.worldPosition.relative(direction);
            for (int i = 0; i < this.outputs.getSlots(); i++) {
                // 直接生成掉落物
                ItemStack outputStack = this.outputs.getStackInSlot(i);
                if (outputStack.isEmpty()) {
                    continue;
                }
                ItemEntity entity = new ItemEntity(serverLevel,
                        outputPos.getX() + 0.5,
                        outputPos.getY(),
                        outputPos.getZ() + 0.5,
                        outputStack, 0, 0, 0);
                entity.setDefaultPickUpDelay();
                serverLevel.addFreshEntity(entity);
            }
            this.resetWhenTakeout();
        }
        // 旋转一圈的时间 (ticks)
        float rot = this.getRotation(level, 0);
        Vec3 center = Vec3.atBottomCenterOf(this.getBlockPos());
        double maxDistanceSqr = 5 * 5;
        LivingEntity bindEntity = getBindEntity(level);
        // 如果实体存在，检查是否需要更新位置
        if (bindEntity == null) {
            // 必须距离磨盘足够近才可以（5 格）
            if (serverLevel.getEntity(entityId) instanceof LivingEntity mob
                && mob.isAlive()
                && mob.distanceToSqr(center) < maxDistanceSqr
                && this.canBindEntity(mob)) {
                this.bindEntity(mob);
            } else {
                this.entityId = Util.NIL_UUID;
                this.cacheRot = 0f;
                this.liftAngle = 0f;
                this.refresh();
                return;
            }
        } else if (!bindEntity.isAlive()
                   || bindEntity.distanceToSqr(center) >= maxDistanceSqr
                   || bindEntity.isInWall()) {
            this.entityId = Util.NIL_UUID;
            this.bindRef = null;
            this.cacheRot = rot;
            this.liftAngle = 0f;
            this.refresh();
            return;
        }
        // 如果实体存在，检查是否需要更新位置
        Vec3 pos = new Vec3(0, 0, 2)
                .add(this.offset)
                .yRot(rot * Mth.DEG_TO_RAD)
                .add(center);
        if (bindEntity == null) return;
        bindEntity.setPosRaw(pos.x, pos.y, pos.z);
        bindEntity.setYRot(-rot - 90);
        bindEntity.setXRot(0);
        bindEntity.setOldPosAndRot();
        // 如果实体带有库存，那么可以尝试往磨盘里放物品
        if (bindEntity.tickCount % 10 == 0 && this.isOutputEmpty() && this.input.isEmpty() && this.progress <= 0) {
            // Fabric暂时只能支持驴和骡使用物品栏
            if (bindEntity instanceof AbstractChestedHorse chestedHorse) {
                ItemStackHandler handler = new ItemStackHandler(chestedHorse.inventory.items);
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stackInSlot = handler.getStackInSlot(i);
                    if (stackInSlot.isEmpty()) {
                        continue;
                    }
                    ItemStack stack = handler.extractItem(i, MAX_INPUT_COUNT, true);
                    if (this.onPutItem(level, stack)) {
                        handler.extractItem(i, MAX_INPUT_COUNT, false);
                        return;
                    }
                }
            }
            // 如果实体没能成功放入物品，那么此时检查磨盘上方 3x3x1 范围内的物品实体
            BlockPos above = this.worldPosition.above();
            Vec3 startPos = new Vec3(above.getX() - 0.3125, above.getY(), above.getZ() - 0.3125);
            Vec3 endPos = new Vec3(above.getX() + 1.3125, above.getY() + 0.5, above.getZ() + 1.3125);
            AABB aabb = new AABB(startPos, endPos);
            List<ItemEntity> entities = serverLevel.getEntitiesOfClass(ItemEntity.class, aabb);

            for (ItemEntity itemEntity : entities) {
                ItemStack stack = itemEntity.getItem();
                if (stack.isEmpty()) {
                    continue;
                }
                int countCanInsert = Math.min(stack.getCount(), MAX_INPUT_COUNT);
                ItemStack stackToInsert = stack.copyWithCount(countCanInsert);
                if (this.onPutItem(level, stackToInsert)) {
                    stack.shrink(countCanInsert);
                    if (stack.isEmpty()) {
                        itemEntity.discard();
                    } else {
                        itemEntity.setItem(stack);
                    }
                    break;
                }
            }
        }
        // 释放粒子效果
        if (serverLevel.getGameTime() % 5 == 2) {
            Item item = !this.isOutputEmpty() ? this.getOutput().getItem() : (!this.input.isEmpty() ? this.input.getItem() : Items.AIR);
            if (item != Items.AIR) {
                Vec3 particlePos = new Vec3(0, 1, 1)
                        .yRot(rot * Mth.DEG_TO_RAD)
                        .add(center);
                if (item instanceof BlockItem blockItem) {
                    BlockState block = blockItem.getBlock().defaultBlockState();
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, block);
                    serverLevel.sendParticles(option, particlePos.x, particlePos.y, particlePos.z, 5, 0.1, 0.1, 0.1, 0.05);
                } else {
                    ItemParticleOption option = new ItemParticleOption(ParticleTypes.ITEM, item.getDefaultInstance().getItem());
                    serverLevel.sendParticles(option, particlePos.x, particlePos.y, particlePos.z, 5, 0.1, 0.1, 0.1, 0.05);
                }
            }
        }
        // 播放音频
        if (serverLevel.getGameTime() % 25 == 0) {
            float pitch = level.getRandom().nextFloat() * 0.2f + 0.9f;
            serverLevel.playSound(null, this.worldPosition, ModSounds.BLOCK_MILLSTONE, SoundSource.BLOCKS, 0.5f, pitch);
        }
        // 输出栏为空才能进行研磨
        if (this.progress > 0 && this.isOutputEmpty()) {
            this.progress--;
            // 每 10 tick 保存一次
            if (this.progress % 10 == 0) {
                this.refresh();
            }
        }
        // 当进度为 0 时，检查输入输出
        if (this.progress <= 0 && !this.input.isEmpty() && this.isOutputEmpty()) {
            SimpleInput container = new SimpleInput(List.of(this.input));
            this.matchRecipe(container, serverLevel).ifPresentOrElse(recipe -> {
                for (int i = 0; i < this.input.getCount(); i++) {
                    // 依据输入数量决定输出数量
                    recipe.value().results().stream()
                            .filter(output -> !output.isEmpty())
                            .filter(output -> serverLevel.getRandom().nextFloat() < output.chance())
                            .map(RandomOutput::stack)
                            .forEach(stack -> ItemUtils.insertItemStacked(this.outputs, stack.create(), false));
                }
                this.input = ItemStack.EMPTY;
                this.refresh();
            }, () -> {
                // 几乎不太可能，但是此时把输入转向输出
                this.outputs.setStackInSlot(0, this.input.copyAndClear());
                this.input = ItemStack.EMPTY;
                this.refresh();
            });
        }
    }

    @Override
    public boolean onPutItem(Level level, ItemStack putOnItem) {
        // 先清空输出槽才可以
        if (!this.isOutputEmpty()) {
            return false;
        }
        // 正在工作中，不能放入
        if (this.progress > 0 && !this.input.isEmpty()) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            SimpleInput container = new SimpleInput(List.of(putOnItem));
            return this.matchRecipe(container, serverLevel).map(recipe -> {
                this.input = putOnItem.split(MAX_INPUT_COUNT);
                this.progress = Math.max(Math.round(this.rotSpeedTick), 1);
                this.refresh();
                level.playSound(null, this.worldPosition,
                        SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.8f,
                        level.getRandom().nextFloat() * 0.2f + 0.9f);
                return true;
            }).orElse(false);
        }
        return false;
    }

    public void onPutItemForPonder(ClientLevel level, ItemStack putOnItem) {
        this.input = putOnItem.split(MAX_INPUT_COUNT);
        this.progress = Math.max(Math.round(this.rotSpeedTick), 1);
        this.refresh();
    }

    public void resetWhenTakeout() {
        for (int i = 0; i < this.outputs.getSlots(); i++) {
            if (!this.outputs.getStackInSlot(i).isEmpty()) {
                this.outputs.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        this.progress = 0;
        this.refresh();
    }

    public Optional<RecipeHolder<MillstoneRecipe>> matchRecipe(SimpleInput input, ServerLevel level) {
        RecipeHolder<MillstoneRecipe> recipe = MillstoneRecipeSerializer.getEmptyRecipe();

        MillstoneMatchRecipeEvent.Pre preEvent = new MillstoneMatchRecipeEvent.Pre(level, this, input);
        ModEvents.MILLSTONE_RECIPE_PRE.invoker().onMillstoneMatchRecipePre(preEvent);
        if (preEvent.getOutput() != null) {
            recipe = preEvent.getOutput();
        } else {
            Optional<RecipeHolder<MillstoneRecipe>> opt = this.quickCheck.getRecipeFor(input, level);
            if (opt.isPresent()) {
                recipe = opt.orElseThrow();
            }
        }

        MillstoneMatchRecipeEvent.Post postEvent = new MillstoneMatchRecipeEvent.Post(level, this, input, recipe);
        ModEvents.MILLSTONE_RECIPE_POST.invoker().onMillstoneMatchRecipePost(postEvent);
        if (postEvent.getOutput() != null) {
            recipe = postEvent.getOutput();
        }
        return MillstoneRecipeSerializer.EMPTY_ID.equals(recipe.id().identifier()) ? Optional.empty() : Optional.of(recipe);
    }

    public boolean saddleEntityIsControlling(Mob mob) {
        if (!(mob instanceof AbstractHorse saddled)) {
            return false;
        }
        return saddled.isSaddled() && mob.getControllingPassenger() != null;
    }


    public boolean canBindEntity(Mob mob) {
        return canBindEntity((LivingEntity) mob);
    }

    @SuppressWarnings("deprecation")
    public boolean canBindEntity(LivingEntity entity) {
        if (entity.getBbHeight() < 1 || entity.getType().builtInRegistryHolder().is(TagMod.MILLSTONE_BIND_BLACKLIST)
                || entity.getVehicle() != null) {
            return false;
        }
        return !(entity instanceof Mob mob) || !this.saddleEntityIsControlling(mob);
    }

    public void bindEntity(Mob mob) {
        bindEntity((LivingEntity) mob);
    }

    public void bindEntity(LivingEntity entity) {
        if (this.level == null || this.level.isClientSide() || !entity.isAlive() || !this.canBindEntity(entity)) {
            return;
        }
        // Preserve the 26.2 handling of passengers when binding.
        if (entity.getControllingPassenger() != null) {
            entity.ejectPassengers();
        }
        this.entityId = entity.getUUID();
        this.bindRef = EntityReference.of(entity);
        float rot = this.getRotation(this.level, 0);
        this.cacheRot = fixRot(getCacheRot() - (rot - getCacheRot()));
        MillstoneBindableData data = MillstoneBindableDataReloadListener.INSTANCE.getOrDefault(entity.getType(), MillstoneBindableData.DEFAULT);
        this.rotSpeedTick = data.rotSpeedTick();
        this.liftAngle = data.liftAngle();
        this.offset = data.offset();
        this.refresh();
    }

    public boolean isBoundTo(LivingEntity entity) {
        return this.entityId.equals(entity.getUUID());
    }

    @Override
    public void fillCrashReportCategory(net.minecraft.CrashReportCategory category) {
        super.fillCrashReportCategory(category);
        category.setDetail("Cookery millstone bound entity", () -> entityId.toString());
        category.setDetail("Cookery millstone rotation", () -> "period=" + rotSpeedTick + ", offset=" + cacheRot);
        category.setDetail("Cookery millstone input", () -> input.toString());
    }

    public void unbindEntity() {
        this.entityId = Util.NIL_UUID;
        this.bindRef = null;
        this.liftAngle = 0f;
    }

    public void sendActionBarMessage(LivingEntity user, String key, Object... args) {
        if (user instanceof ServerPlayer serverPlayer) {
            MutableComponent message = Component.translatable(key, args);
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
    }

    @ServerThreadSafe
    @Override
    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        if (this.bindRef != null)
            EntityReference.store(this.bindRef, valueOutput, ENTITY_KEY);
        if (this.entityId != Util.NIL_UUID)
            valueOutput.putString(ENTITY_ID_KEY, this.entityId.toString());
        valueOutput.putFloat(CACHE_ROT_KEY, fixRot(getCacheRot()));
        valueOutput.putFloat(ROT_SPEED_TICK_KEY, rotSpeedTick);
        valueOutput.putFloat(LIFT_ANGLE_KEY, liftAngle);
        if (!input.isEmpty()) {
            valueOutput.store(INPUT_ITEM_KEY, ItemStack.CODEC, this.input);
        }
        if (!this.isOutputEmpty())
            this.outputs.serializeNBT(valueOutput.child(OUTPUT_ITEM_KEY));
        valueOutput.putInt(PROGRESS_KEY, this.progress);
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    @ServerThreadSafe
    @Override
    protected void loadAdditional(@NonNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.bindRef = EntityReference.read(valueInput, ENTITY_KEY);
        this.entityId = UUID.fromString(valueInput.getStringOr(ENTITY_ID_KEY, Util.NIL_UUID.toString()));
        this.cacheRot = valueInput.getFloatOr(CACHE_ROT_KEY, 0f);
        this.rotSpeedTick = valueInput.getFloatOr(ROT_SPEED_TICK_KEY, 200f);
        this.liftAngle = valueInput.getFloatOr(LIFT_ANGLE_KEY, 5f);
        this.input = valueInput.read(INPUT_ITEM_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.outputs.setSize(OUTPUT_SLOT_COUNT);
        if (valueInput.contains(OUTPUT_ITEM_KEY)) {
            valueInput.child(OUTPUT_ITEM_KEY).ifPresentOrElse(outputInput -> {
                if (outputInput.read(SpecialCodecs.contains("Items")).orElse(false) || outputInput.read(SpecialCodecs.contains("Size")).orElse(false)) {
                    this.outputs.deserializeNBT(outputInput);
                } else {
                    valueInput.read(OUTPUT_ITEM_KEY, ItemStack.CODEC)
                            .ifPresent(stack -> this.outputs.setStackInSlot(0, stack));
                }
            }, () -> {});
        }
        this.progress = valueInput.getIntOr(PROGRESS_KEY, 0);
    }

    public boolean hasEntity() {
        return !Util.NIL_UUID.equals(this.entityId);
    }

    public float getCacheRot() {
        return this.cacheRot;
    }

    public void setCacheRot(float cacheRot) {
        this.cacheRot = cacheRot;
    }

    public float getLiftAngle() {
        return this.liftAngle;
    }

    public @NonNull ItemStack getInput() {
        return this.input;
    }

    public @NonNull ItemStack getOutput() {
        for (int i = 0; i < this.outputs.getSlots(); i++) {
            ItemStack stack = this.outputs.getStackInSlot(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public IItemHandler getOutputs() {
        return this.outputs;
    }

    public boolean isOutputEmpty() {
        for (int i = 0; i < this.outputs.getSlots(); i++) {
            if (!this.outputs.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public float getProgressPercent() {
        float total = Math.max(this.rotSpeedTick, 1);
        return (total - this.progress) / total;
    }

    /**
     * 修正 cacheRot 的值，此值应该在 0-360 之间，过大或过小都会导致动画异常
     */
    private float fixRot(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return 0f;
        }
        return Math.abs(value) % 360;
    }

    public void setBindMob(@Nullable Mob mob) {
        this.bindRef = EntityReference.of(mob);
    }

    @Nullable
    public Mob getBindMob(Level level) {
        LivingEntity entity = getBindEntity(level);
        return entity instanceof Mob mob ? mob : null;
    }

    @Nullable
    public LivingEntity getBindEntity(Level level) {
        return EntityReference.get(this.bindRef, level, LivingEntity.class);
    }
}
