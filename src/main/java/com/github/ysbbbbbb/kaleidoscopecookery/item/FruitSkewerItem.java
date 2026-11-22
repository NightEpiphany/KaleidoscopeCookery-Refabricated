package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FruitSkewerItem extends Item implements IHasContainer {

    private final boolean randomTeleport;

    public FruitSkewerItem(FoodProperties food) {
        this(food, false);
    }

    public FruitSkewerItem(FoodProperties food, boolean randomTeleport) {
        super(new Properties().food(food));
        this.randomTeleport = randomTeleport;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && this.randomTeleport) {
            for(int i = 0; i < 16; ++i) {
                double d = entity.getX() + (entity.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                double e = Mth.clamp(entity.getY() + (double)(entity.getRandom().nextInt(16) - 8), level.getMinBuildHeight(), level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1);
                double f = entity.getZ() + (entity.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }

                Vec3 vec3 = entity.position();
                if (entity.randomTeleport(d, e, f, true)) {
                    level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
                    SoundSource soundSource;
                    SoundEvent soundEvent;
                    if (entity instanceof Fox) {
                        soundEvent = SoundEvents.FOX_TELEPORT;
                        soundSource = SoundSource.NEUTRAL;
                    } else {
                        soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                        soundSource = SoundSource.PLAYERS;
                    }

                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, soundSource);
                    entity.resetFallDistance();
                    break;
                }
            }

            if (entity instanceof Player player) {
                player.resetCurrentImpulseContext();
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return level.isClientSide || entity.hasInfiniteMaterials() ? remaining : this.returnContainerToEntity(remaining, level, entity);
    }

    @Override
    public Item getContainerItem() {
        return Items.STICK;
    }
}
