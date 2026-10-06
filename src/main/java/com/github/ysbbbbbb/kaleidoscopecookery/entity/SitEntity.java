package com.github.ysbbbbbb.kaleidoscopecookery.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.SitUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

public class SitEntity extends Entity {
    public static final int DEFAULT = 0;
    public static final int TRASH_CAN = 1;
    private static final Direction[] DISMOUNT_DIRECTIONS = {
            Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH
    };
    private static final EntityDataAccessor<Integer> SIT_TYPE = SynchedEntityData.defineId(SitEntity.class, EntityDataSerializers.INT);
    private int passengerTick = 0;

    public SitEntity(EntityType<? extends SitEntity> type, Level level) {
        super(type, level);
    }

    protected SitEntity(Level level) {
        this(ModEntities.SIT, level);
        this.noPhysics = true;
    }

    public SitEntity(Level level, BlockPos pos, double y) {
        this(level);
        this.setPos(pos.getX() + 0.5, pos.getY() + y, pos.getZ() + 0.5);
    }

    public SitEntity(Level level, BlockPos pos, double y, int sitType) {
        this(level, pos, y);
        this.setSitType(sitType);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.checkBelowWorld();
            this.checkPassengers();
            if (this.tickCount % 20 == 0) {
                BlockState blockState = this.level().getBlockState(this.blockPosition());
                if (!blockState.is(TagMod.SITTABLE)) {
                    this.discard();
                }
            }
        }
    }

    @Override
    protected void removePassenger(@NonNull Entity passenger) {
        if (this.getSitType() == TRASH_CAN && passenger instanceof Player player) {
            player.playSound(ModSounds.TRASH_CAN);
        }
        super.removePassenger(passenger);
    }

    private void checkPassengers() {
        if (this.getPassengers().isEmpty()) {
            this.passengerTick++;
        } else {
            this.passengerTick = 0;
        }
        if (this.passengerTick > 10) {
            this.discard();
        }
    }

    @Override
    public @NonNull Vec3 getDismountLocationForPassenger(@NonNull LivingEntity passenger) {
        BlockPos seatPos = this.blockPosition();
        Vec3 location = findCardinalDismountLocation(seatPos,
                candidate -> this.level().getBlockState(candidate).isAir());
        discard();
        return location != null ? location : Vec3.atBottomCenterOf(seatPos.above());
    }

    static @Nullable Vec3 findCardinalDismountLocation(BlockPos seatPos, Predicate<BlockPos> isAir) {
        for (Direction direction : DISMOUNT_DIRECTIONS) {
            BlockPos candidate = seatPos.relative(direction);
            if (isAir.test(candidate)) {
                return Vec3.atBottomCenterOf(candidate);
            }
        }
        return null;
    }

    @Override
    public void remove(@NonNull RemovalReason reason) {
        super.remove(reason);
        SitUtil.removeSitEntity(level(), blockPosition());
    }

    @Override
    protected void defineSynchedData(@NonNull Builder builder) {
        builder.define(SIT_TYPE, DEFAULT);
    }

    @Override
    public void readAdditionalSaveData(@NonNull ValueInput nbt) {}

    @Override
    public void addAdditionalSaveData(@NonNull ValueOutput nbt) {}

    @Override
    public @NonNull Packet<ClientGamePacketListener> getAddEntityPacket(@NonNull ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return false;
    }

    public int getSitType() {
        return this.entityData.get(SIT_TYPE);
    }

    public void setSitType(int sitType) {
        this.entityData.set(SIT_TYPE, sitType);
    }
}