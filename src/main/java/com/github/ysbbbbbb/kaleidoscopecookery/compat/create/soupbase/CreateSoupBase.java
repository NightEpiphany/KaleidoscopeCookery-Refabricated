package com.github.ysbbbbbb.kaleidoscopecookery.compat.create.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.zurrtum.create.AllFluids;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.Create;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

public final class CreateSoupBase {

    public static final Identifier CHOCOLATE = Identifier.fromNamespaceAndPath(Create.MOD_ID, "chocolate");
    public static final Identifier HONEY = Identifier.fromNamespaceAndPath(Create.MOD_ID, "honey");

    public static void registerSoupBases() {
        SoupBaseManager.registerFluidSoupBase(CHOCOLATE, AllItems.CHOCOLATE_BUCKET, AllFluids.CHOCOLATE, 0xA04A3C);
        SoupBaseManager.registerFluidSoupBase(HONEY, AllItems.HONEY_BUCKET, AllFluids.HONEY, 0xEAAE30);
    }

    @SuppressWarnings("deprecation")
    @Environment(EnvType.CLIENT)
    public static class ClientPatch {
        public static TextureAtlasSprite getStillFluidSprite(Fluid fluid) {
            if (fluid == AllFluids.CHOCOLATE) {
                return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(Create.MOD_ID, "block/chocolate_still")));
            }
            if (fluid == AllFluids.HONEY) {
                return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(Create.MOD_ID, "block/honey_still")));
            }
            // 如果没有找到渲染处理器，使用默认水纹理作为后备
            return Minecraft.getInstance().getModelManager().atlasManager.get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath("minecraft", "block/water_still")));
        }
    }
}
