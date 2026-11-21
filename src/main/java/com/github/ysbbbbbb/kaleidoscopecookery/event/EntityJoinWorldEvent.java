package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.ai.CatLieOnBlockGoal;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Predicate;

public class EntityJoinWorldEvent {
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register(EntityJoinWorldEvent::onEntityJoinWorld);
    }

    private static void onEntityJoinWorld(Entity entity, ServerLevel level) {
        if (entity instanceof Cat cat) {
            cat.goalSelector.addGoal(5, new CatLieOnBlockGoal(cat, 1.1, 8));
        } else if (entity instanceof Creeper creeper) {
            creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, LivingEntity.class, 6,
                    1, 1.2, e -> e.hasEffect(ModEffects.MUSTARD)));
        } else if (entity instanceof Bee bee) {
            addSkewerGoal(bee, 1.25, ModItems.CANDIED_GLOW_BERRY_SKEWER, ModItems.CANDIED_APPLE_SKEWER);
        } else if (entity instanceof Fox fox) {
            addSkewerGoal(fox, 1.0, ModItems.CANDIED_SWEET_BERRY_SKEWER);
        } else if (entity instanceof Chicken chicken) {
            addSkewerGoal(chicken, 1.0, ModItems.CANDIED_CATERPILLAR_SKEWER);
        } else if (entity instanceof EnderMan enderMan) {
            addSkewerGoal(enderMan, 1.0, ModItems.CANDIED_CHORUS_FRUIT_SKEWER);
        }
    }

    private static void addSkewerGoal(PathfinderMob mob, double speed, ItemLike... skewers) {
        if (mob.goalSelector.getAvailableGoals().stream().noneMatch(goal -> goal.getGoal() instanceof SkewerTemptGoal)) {
            mob.goalSelector.addGoal(3, new SkewerTemptGoal(mob, speed, Ingredient.of(skewers)));
        }
    }

    private static class SkewerTemptGoal extends TemptGoal {
        private SkewerTemptGoal(PathfinderMob mob, double speed, Predicate<ItemStack> items) {
            super(mob, speed, items, false);
        }
    }
}
