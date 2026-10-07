package com.github.ysbbbbbb.kaleidoscopecookery.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.PortHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ScarecrowEntity extends LivingEntity {

    protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_SHOULDER = SynchedEntityData.defineId(
            ScarecrowEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE
    );
    private static final Predicate<Entity> RIDEABLE_MINECARTS = e -> e instanceof AbstractMinecart minecart && minecart.isRideable();
    private static final Predicate<Entity> SHOULDER_RIDING_ENTITY = e -> e instanceof ShoulderRidingEntity entity && !entity.isOrderedToSit() && entity.canSitOnShoulder();
    private static final String HAND_ITEMS_TAG = "HandItems";
    private static final String ARMOR_ITEMS_TAG = "ArmorItems";
    private static final String SHOULDER_ENTITY_TAG = "ShoulderEntity";

    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);

    public long lastHit;
    private int cooldown;
    private long timeEntitySatOnShoulder;

    public ScarecrowEntity(EntityType<ScarecrowEntity> type, Level level) {
        super(type, level);
    }

    public ScarecrowEntity(Level level, double pX, double pY, double pZ) {
        this(ModEntities.SCARECROW, level);
        this.setPos(pX, pY, pZ);
    }

    public static AttributeSupplier createAttributes() {
        return createLivingAttributes().add(Attributes.STEP_HEIGHT, 0.0).build();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOULDER, Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.handItems.stream().anyMatch(i -> i.is(BlockItemTags.LANTERNS.item())))
            this.level().setBlock(this.blockPosition().above(), Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 12), Block.UPDATE_ALL);
        else this.level().setBlock(this.blockPosition().above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        if (this.cooldown > 0) {
            this.cooldown--;
        }
    }

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, @NonNull InteractionHand hand, @NonNull Vec3 location) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(Items.NAME_TAG)) {
            return InteractionResult.PASS;
        }
        if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.CONSUME;
        }
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
        if (this.cooldown > 0) {
            return InteractionResult.PASS;
        }
        if (isClickHand(location)) {
            return handleHandItems(player, itemInHand);
        }
        if (isClickHead(location)) {
            return handleHeadItems(player, itemInHand);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult handleHeadItems(Player player, ItemStack itemInHand) {
        this.cooldown = 5;
        ItemStack headItem = this.getItemBySlot(EquipmentSlot.HEAD);
        if (itemInHand.isEmpty() && !headItem.isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            ItemUtils.giveItemToPlayer(player, headItem);
            return InteractionResult.SUCCESS;
        }

        if (!(itemInHand.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof SkullBlock)) {
            return InteractionResult.PASS;
        }

        if (player.getAbilities().instabuild && headItem.isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, itemInHand.copyWithCount(1));
            this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_HEAD_ON_SCARECROW);
            return InteractionResult.SUCCESS;
        }
        if (!itemInHand.isEmpty() && itemInHand.getCount() > 1) {
            if (headItem.isEmpty()) {
                this.setItemSlot(EquipmentSlot.HEAD, itemInHand.split(1));
                this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
                ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_HEAD_ON_SCARECROW);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        this.setItemSlot(EquipmentSlot.HEAD, itemInHand);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
        player.setItemInHand(InteractionHand.MAIN_HAND, headItem);
        ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_HEAD_ON_SCARECROW);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleHandItems(Player player, ItemStack itemInHand) {
        this.cooldown = 5;
        if (itemInHand.isEmpty()) {
            ItemStack mainHand = this.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offhand = this.getItemInHand(InteractionHand.OFF_HAND);
            if (!mainHand.isEmpty()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                ItemUtils.giveItemToPlayer(player, mainHand);
                return InteractionResult.SUCCESS;
            }
            if (!offhand.isEmpty()) {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                ItemUtils.giveItemToPlayer(player, offhand);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (itemInHand.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LanternBlock) {
            if (swapHand(InteractionHand.OFF_HAND, player, itemInHand)) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.LANTERN_PLACE.value(), this.getSoundSource());
                return InteractionResult.SUCCESS;
            }
        }
        if (itemInHand.has(DataComponents.DAMAGE)) {
            if (swapHand(InteractionHand.MAIN_HAND, player, itemInHand)) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private boolean swapHand(InteractionHand hand, Player player, ItemStack itemInHand) {
        ItemStack scarecrowStack = this.getItemInHand(hand);
        if (player.getAbilities().instabuild && scarecrowStack.isEmpty() && !itemInHand.isEmpty()) {
            this.setItemInHand(hand, itemInHand.copyWithCount(1));
            return true;
        }
        if (!itemInHand.isEmpty() && itemInHand.getCount() > 1) {
            if (scarecrowStack.isEmpty()) {
                this.setItemInHand(hand, itemInHand.split(1));
                return true;
            }
            return false;
        }
        this.setItemInHand(hand, itemInHand);
        player.setItemInHand(InteractionHand.MAIN_HAND, scarecrowStack);
        return true;
    }

    private boolean isClickHand(Vec3 vector) {
        return 17 / 16.0 <= vector.y && vector.y <= 27 / 17.0;
    }


    private boolean isClickHead(Vec3 vector) {
        return 27 / 17.0 < vector.y;
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource source, float amount) {
        if (this.isRemoved()) {
            return false;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill(serverLevel);
            return false;
        }

        if (this.isInvulnerableTo(serverLevel, source)) {
            return false;
        }

        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByAnything(serverLevel, source);
            this.kill(serverLevel);
            return false;
        }

        Entity entity = source.getEntity();
        if (entity instanceof Player player) {
            if (!player.getAbilities().mayBuild) {
                return false;
            }
        }

        if (source.isCreativePlayer()) {
            this.playBrokenSound();
            this.showBreakingParticles();
            this.kill(serverLevel);
            return false;
        }

        long gameTime = this.level().getGameTime();
        if (gameTime - this.lastHit > 5) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F);
            this.level().broadcastEntityEvent(this, (byte) 32);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            this.lastHit = gameTime;
            if (getShoulderEntity() instanceof ShoulderRidingEntity) {
                this.removeEntitiesOnShoulder();
            }
        } else {
            this.brokenByPlayer(serverLevel, source);
            this.showBreakingParticles();
            this.kill(serverLevel);
        }

        return true;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 32) {
            if (this.level().isClientSide()) {
                this.lastHit = this.level().getGameTime();
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void brokenByPlayer(ServerLevel level, DamageSource damageSource) {
        ItemStack stack = new ItemStack(ModItems.SCARECROW);
        if (this.hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        }
        Block.popResource(this.level(), this.blockPosition(), stack);
        this.brokenByAnything(level, damageSource);
    }

    private void brokenByAnything(ServerLevel level, DamageSource damageSource) {
        this.playBrokenSound();
        this.dropAllDeathLoot(level, damageSource);
        for (int i = 0; i < this.handItems.size(); ++i) {
            ItemStack stack = this.handItems.get(i);
            if (!stack.isEmpty()) {
                Block.popResource(this.level(), this.blockPosition().above(), stack);
                this.handItems.set(i, ItemStack.EMPTY);
            }
        }

        for (int i = 0; i < this.armorItems.size(); ++i) {
            ItemStack stack = this.armorItems.get(i);
            if (!stack.isEmpty()) {
                Block.popResource(this.level(), this.blockPosition().above(), stack);
                this.armorItems.set(i, ItemStack.EMPTY);
            }
        }
    }

    private void playBrokenSound() {
        this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState());
            serverLevel.sendParticles(particleOption,
                    this.getX(), this.getY(2 / 3.0),
                    this.getZ(), 10,
                    this.getBbWidth() / 4f,
                    this.getBbHeight() / 4f,
                    this.getBbWidth() / 4f,
                    0.05);
        }
    }

    private boolean setEntityOnShoulder(LivingEntity entity) {
        if (this.canEntityOnShoulder()) {
            this.setShoulderEntity(entity);
            this.timeEntitySatOnShoulder = this.level().getGameTime();
            return true;
        }
        return false;
    }

    private void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20 < this.level().getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntity());
            this.setShoulderEntity(null);
        }
    }

    private void respawnEntityOnShoulder(LivingEntity entity) {
        if (this.level() instanceof ServerLevel serverLevel && entity != null) {
            EntityType.create(entity.getType(), PortHelper.emptyStatic(level()), this.level(), EntitySpawnReason.LOAD).ifPresent(entry -> {
                entry.setPos(this.getX(), this.getY() + 1.675, this.getZ());
                serverLevel.addWithUUID(entity);
            });
        }
    }


    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        for (int i = 0; i < this.handItems.size(); i++) {
            ItemStack itemStack = this.handItems.get(i);
            if (!itemStack.isEmpty()) {
                valueOutput.list(HAND_ITEMS_TAG, ItemStackWithSlot.CODEC).add(new ItemStackWithSlot(i, itemStack));
            }
        }
        for (int i = 0; i < this.armorItems.size(); i++) {
            ItemStack itemStack = this.armorItems.get(i);
            if (!itemStack.isEmpty()) {
                valueOutput.list(ARMOR_ITEMS_TAG, ItemStackWithSlot.CODEC).add(new ItemStackWithSlot(i, itemStack));
            }
        }
        if (this.getShoulderEntity() != null) {
            EntityReference.store(getShoulderEntityRef(), valueOutput, SHOULDER_ENTITY_TAG);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.handItems.clear();
        this.armorItems.clear();
        for (ItemStackWithSlot itemStackWithSlot : valueInput.listOrEmpty(HAND_ITEMS_TAG, ItemStackWithSlot.CODEC)) {
            if (itemStackWithSlot.isValidInContainer(this.handItems.size())) {
                this.handItems.set(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
        for (ItemStackWithSlot itemStackWithSlot : valueInput.listOrEmpty(ARMOR_ITEMS_TAG, ItemStackWithSlot.CODEC)) {
            if (itemStackWithSlot.isValidInContainer(this.armorItems.size())) {
                this.armorItems.set(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
        EntityReference<LivingEntity> ref = EntityReference.readWithOldOwnerConversion(valueInput, SHOULDER_ENTITY_TAG, this.level());
        if (ref != null) {
            this.entityData.set(DATA_SHOULDER, Optional.of(ref));
        }else {
            this.entityData.set(DATA_SHOULDER, Optional.empty());
        }
    }


    @Override
    protected void tickHeadTurn(float f) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
    }

    @Override
    public void setYBodyRot(float offset) {
        this.yBodyRotO = this.yRotO = offset;
        this.yHeadRotO = this.yHeadRot = offset;
    }

    @Override
    public void setYHeadRot(float rotation) {
        this.yBodyRotO = this.yRotO = rotation;
        this.yHeadRotO = this.yHeadRot = rotation;
    }

    @Override
    public void kill(@NonNull ServerLevel serverLevel) {
        if (this.getShoulderEntity() != null) {
            this.removeEntitiesOnShoulder();
        }
        if (this.level().getBlockState(this.blockPosition().above()).is(Blocks.LIGHT))
            this.level().setBlock(this.blockPosition().above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        this.remove(RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NonNull Entity entity) {
    }

    @Override
    protected void pushEntities() {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), RIDEABLE_MINECARTS);
        for (Entity entity : list) {
            if (this.distanceToSqr(entity) <= 0.2) {
                entity.push(this);
                return;
            }
        }

        if (this.canEntityOnShoulder()) {
            list = this.level().getEntities(this, this.getBoundingBox().inflate(2), SHOULDER_RIDING_ENTITY);
            for (Entity entity : list) {
                if (this.distanceToSqr(entity) <= 1.5 && entity instanceof ShoulderRidingEntity shoulderEntity
                    && setEntityOnShoulder(shoulderEntity)) {
                    return;
                }
            }
        }
    }

    private boolean canEntityOnShoulder() {
        return !this.isPassenger() && this.onGround() && !this.isInWater() && !this.isInPowderSnow && this.getShoulderEntity() == null;
    }


    public @NotNull Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }


    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot.getType()) {
            case HAND -> this.handItems.get(slot.getIndex());
            case HUMANOID_ARMOR -> this.armorItems.get(slot.getIndex());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, @NonNull ItemStack stack) {
        switch (slot.getType()) {
            case HAND:
                this.onEquipItem(slot, this.handItems.set(slot.getIndex(), stack), stack);
                break;
            case HUMANOID_ARMOR:
                this.onEquipItem(slot, this.armorItems.set(slot.getIndex(), stack), stack);
        }
    }

    @Override
    public boolean skipAttackInteraction(@NonNull Entity entity) {
        return entity instanceof Player player && !this.level().mayInteract(player, this.blockPosition());
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public @NotNull Fallsounds getFallSounds() {
        return new Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource damageSource) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public void thunderHit(@NonNull ServerLevel level, @NonNull LightningBolt lightningBolt) {
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.SCARECROW);
    }

    @Nullable
    public LivingEntity getShoulderEntity() {
        return EntityReference.getLivingEntity(getShoulderEntityRef(), this.level());
    }

    @Nullable
    public EntityReference<LivingEntity> getShoulderEntityRef() {
        return this.entityData.get(DATA_SHOULDER).orElse(null);
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    public void setShoulderEntity(@Nullable LivingEntity livingEntity) {
        this.entityData.set(DATA_SHOULDER, Optional.ofNullable(livingEntity).map(EntityReference::of));
    }

    public void setShoulderEntityRef(@Nullable EntityReference<LivingEntity> entityReference) {
        this.entityData.set(DATA_SHOULDER, Optional.ofNullable(entityReference));
    }
}
