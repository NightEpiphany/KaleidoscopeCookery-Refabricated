package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.BambooTrayDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.ChoppingBoardDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.OilPotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.TeapotDispenseBehavior;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.DispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Set;

public final class ModDispenseBehaviors {
    public static final ResourceKey<List<DispenseBehavior>> OIL_POT = key("oil_pot");
    public static final ResourceKey<List<DispenseBehavior>> BAMBOO_TRAY = key("bamboo_tray");
    public static final ResourceKey<List<DispenseBehavior>> TEAPOT = key("teapot");
    public static final ResourceKey<List<DispenseBehavior>> CHOPPING_BOARD_KNIFE = key("chopping_board_knife");
    public static final ResourceKey<List<DispenseBehavior>> CHOPPING_BOARD_INGREDIENT = key("chopping_board_ingredient");

    public static void registerDispenseBehaviors() {
        register("oil_pot", OilPotDispenseBehavior.CODEC);
        register("bamboo_tray", BambooTrayDispenseBehavior.CODEC);
        register("teapot", TeapotDispenseBehavior.CODEC);
        register("chopping_board", ChoppingBoardDispenseBehavior.CODEC);

        DefaultItemComponentEvents.MODIFY.register(context -> context.modify(Set.of(
                Items.TROPICAL_FISH, Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON,
                Items.MUTTON, Items.COOKED_MUTTON, Items.BEEF, Items.COOKED_BEEF,
                Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.CHICKEN, Items.COOKED_CHICKEN,
                Items.RABBIT, Items.COOKED_RABBIT
        ), (builder, registries, _) -> builder.set(
                DataComponents.DISPENSE_BEHAVIOR, registries.getOrThrow(CHOPPING_BOARD_INGREDIENT))));
    }

    private static ResourceKey<List<DispenseBehavior>> key(String name) {
        return ResourceKey.create(Registries.DISPENSE_BEHAVIOR,
                Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name));
    }

    private static void register(String name, MapCodec<? extends DispenseBehavior> codec) {
        Registry.register(BuiltInRegistries.DISPENSE_BEHAVIOR_TYPE,
                Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name), codec);
    }
}
