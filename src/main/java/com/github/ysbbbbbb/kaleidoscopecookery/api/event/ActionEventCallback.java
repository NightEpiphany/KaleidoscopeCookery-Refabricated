package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ActionEventCallback {

    @FunctionalInterface
    interface ChoppingBoardModify {
        void onChoppingBoardModify(ChoppingBoardEvent.Modify event);
    }

    @FunctionalInterface
    interface ChoppingBoardPost {
        void onChoppingBoardPost(ChoppingBoardEvent.Post event);
    }

    @FunctionalInterface
    interface ChoppingBoardReset {
        void onChoppingBoardReset(ChoppingBoardEvent.Reset event);
    }

    @FunctionalInterface
    interface MillstoneMatchRecipePre {
        void onMillstoneMatchRecipePre(MillstoneMatchRecipeEvent.Pre event);
    }

    @FunctionalInterface
    interface MillstoneMatchRecipePost {
        void onMillstoneMatchRecipePost(MillstoneMatchRecipeEvent.Post event);
    }

    @FunctionalInterface
    interface CheckSpecialItem{
        void onCheckItemEvent(RecipeItemEvent.CheckItem event);
    }

    @FunctionalInterface
    interface DeductSpecialItem{
        void onDeductItemEvent(RecipeItemEvent.DeductItem event);
    }

    @FunctionalInterface
    interface PlayerLeftClick {
        void onPlayerLeftClick(@Nullable Player player, InteractionHand hand);
    }

    @FunctionalInterface
    interface FarmlandTrample {
        void onFarmlandTrample(FarmlandTrampleEvent event);
    }

    @FunctionalInterface
    interface StockMatchRecipePre {
        void onStockMatchRecipePre(StockpotMatchRecipeEvent.Pre event);
    }

    @FunctionalInterface
    interface StockMatchRecipePost {
        void onStockMatchRecipePost(StockpotMatchRecipeEvent.Post event);
    }

    @FunctionalInterface
    interface SickleHarvest {
        void onSickleHarvest(SickleHarvestEvent event);
    }

    @FunctionalInterface
    interface ProjectileImpact {
        void onProjectileImpact(ProjectileImpactEvent event);
    }

    @FunctionalInterface
    interface EntityChangeTarget {
        void onEntityChangeTarget(LivingChangeTargetEvent event);
    }

    @FunctionalInterface
    interface CropGrowPre {
        void beforeGrow(CropGrowEvent.Pre event);
    }

    @FunctionalInterface
    interface CropGrowPost {
        void afterGrow(CropGrowEvent.Post event);
    }
}
