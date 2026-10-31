package com.github.ysbbbbbb.kaleidoscopecookery.api.event.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.event.CameraEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.util.event.IKCEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public abstract class ViewportEvent implements IKCEvent {
    private final GameRenderer renderer;
    private final Camera camera;
    private final double partialTick;

    @ApiStatus.Internal
    public ViewportEvent(GameRenderer renderer, Camera camera, double partialTick)
    {
        this.renderer = renderer;
        this.camera = camera;
        this.partialTick = partialTick;
    }

    public GameRenderer getRenderer()
    {
        return renderer;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public double getPartialTick() {
        return partialTick;
    }

    public static void register() {
        CALLBACK.register(event -> {
            if (event instanceof ViewportEvent.ComputeCameraAngles computeCameraAngles)
                CameraEvent.onCameraTick(computeCameraAngles);
        });
    }

    public static class ComputeCameraAngles extends ViewportEvent
    {
        private float yaw;
        private float pitch;
        private float roll;

        @ApiStatus.Internal
        public ComputeCameraAngles(GameRenderer renderer, Camera camera, double renderPartialTicks, float yaw, float pitch, float roll) {
            super(renderer, camera, renderPartialTicks);
            this.setYaw(yaw);
            this.setPitch(pitch);
            this.setRoll(roll);
        }

        public float getYaw()
        {
            return yaw;
        }

        public void setYaw(float yaw)
        {
            this.yaw = yaw;
        }

        public float getPitch()
        {
            return pitch;
        }

        public void setPitch(float pitch)
        {
            this.pitch = pitch;
        }

        public float getRoll()
        {
            return roll;
        }

        public void setRoll(float roll)
        {
            this.roll = roll;
        }
    }
}
