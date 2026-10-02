package com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

@Deprecated
@Environment(EnvType.CLIENT)
public class SitRenderer extends EntityRenderer<SitEntity, EntityRenderState> {
    private static final Identifier EMPTY = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/entity/empty.png");

    public SitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return EMPTY;
    }
    @Override
    public @NonNull EntityRenderState createRenderState() {
        return new EntityRenderState();
    }


}
