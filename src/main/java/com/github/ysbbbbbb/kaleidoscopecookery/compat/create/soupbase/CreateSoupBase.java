package com.github.ysbbbbbb.kaleidoscopecookery.compat.create.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.simibubi.create.api.registrate.CreateRegistrateRegistrationCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class CreateSoupBase {
    public static final String ID = "create";
    public static final ResourceLocation CHOCOLATE = id("chocolate");
    public static final ResourceLocation HONEY = id("honey");

    private static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }

    public static void registerSoupBases() {
        registerFluidSoupBase(CHOCOLATE, id("chocolate_bucket"), 0xA04A3C);
        registerFluidSoupBase(HONEY, id("honey_bucket"), 0xEAAE30);
    }

    private static void registerFluidSoupBase(ResourceLocation name, ResourceLocation bucketId, int bubbleColor) {
        BuiltInRegistries.ITEM.getOptional(bucketId).ifPresentOrElse(
                bucket -> SoupBaseManager.registerFluidSoupBase(name, bucket, bubbleColor),
                () -> CreateRegistrateRegistrationCallback.register(Registries.ITEM, bucketId,
                        bucket -> SoupBaseManager.registerFluidSoupBase(name, bucket, bubbleColor)));
    }
}
