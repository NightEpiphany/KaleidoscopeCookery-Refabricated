package com.github.ysbbbbbb.kaleidoscopecookery.client.render;

import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.EndSoulBeamRenderState;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModAttachmentType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class EndSoulBeamRenderer {
    private static final RenderStateDataKey<List<EndSoulBeamRenderState>> BEAMS =
            RenderStateDataKey.create(() -> "kaleidoscope_cookery:end_soul_beams");

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(EndSoulBeamRenderer::extract);
        LevelRenderEvents.COLLECT_SUBMITS.register(EndSoulBeamRenderer::submit);
    }

    private static void extract(LevelExtractionContext context) {
        ClientLevel level = context.level();
        LevelRenderState levelState = context.levelState();
        CameraRenderState camera = levelState.cameraRenderState;
        float partialTicks = levelState.worldPartialTicks;
        List<EndSoulBeamRenderState> beams = null;

        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive()) {
                continue;
            }
            Integer crystalId = living.getAttached(ModAttachmentType.END_SOUL_CRYSTAL_ID);
            if (crystalId == null || !(level.getEntity(crystalId) instanceof EndCrystal crystal)
                    || !crystal.isAlive()) {
                continue;
            }

            Vec3 start = living.getPosition(partialTicks).add(0, living.getBbHeight() * 0.5, 0);
            float age = crystal.time + partialTicks;
            Vec3 end = crystal.getPosition(partialTicks).add(0, EndCrystalRenderer.getY(age) + 2, 0);
            if (!camera.cullFrustum.isVisible(new AABB(start, end).inflate(1))) {
                continue;
            }

            if (beams == null) {
                beams = new ArrayList<>(level.getEntityCount());
            }
            // 绘制光柱原点
            Vec3 origin = start.subtract(camera.pos).add(0, -2, 0);
            int lightCoords = context.levelRenderer().entityRenderDispatcher().getPackedLightCoords(living, partialTicks);
            beams.add(new EndSoulBeamRenderState(origin, end.subtract(start), age, lightCoords));
        }

        levelState.setData(BEAMS, beams == null ? List.of() : List.copyOf(beams));
    }

    private static void submit(LevelRenderContext context) {
        List<EndSoulBeamRenderState> beams = context.levelState().getDataOrDefault(BEAMS, List.of());
        if (beams.isEmpty()) {
            return;
        }
        PoseStack poseStack = context.poseStack();
        SubmitNodeCollector collector = context.submitNodeCollector();
        for (EndSoulBeamRenderState beam : beams) {
            poseStack.pushPose();
            try {
                poseStack.translate(beam.origin());
                Vec3 offset = beam.offset();
                EnderDragonRenderer.submitCrystalBeams((float) offset.x, (float) offset.y, (float) offset.z,
                        beam.age(), poseStack, collector, beam.lightCoords());
            } finally {
                poseStack.popPose();
            }
        }
    }
}
