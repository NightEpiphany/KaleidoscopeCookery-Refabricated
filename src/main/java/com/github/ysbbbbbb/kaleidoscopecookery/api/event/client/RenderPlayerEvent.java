package com.github.ysbbbbbb.kaleidoscopecookery.api.event.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.event.PlayerRenderEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.util.event.CancellableKCEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public abstract class RenderPlayerEvent extends CancellableKCEvent {
    private final Player player;
    private final PlayerRenderer renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    @ApiStatus.Internal
    protected RenderPlayerEvent(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
    {
        this.player = player;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    public static void register() {
        CALLBACK.register(event -> {
            if (event instanceof RenderPlayerEvent.Pre pre)
                PlayerRenderEvent.onPlayerRender(pre);
        });
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerRenderer getRenderer()
    {
        return renderer;
    }

    public float getPartialTick()
    {
        return partialTick;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource()
    {
        return multiBufferSource;
    }

    public int getPackedLight()
    {
        return packedLight;
    }
    public static class Pre extends RenderPlayerEvent
    {
        @ApiStatus.Internal
        public Pre(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }

    public static class Post extends RenderPlayerEvent
    {
        @ApiStatus.Internal
        public Post(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }
}
