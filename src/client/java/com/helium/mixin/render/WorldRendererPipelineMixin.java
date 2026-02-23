package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererPipelineMixin {

    @Unique
    private static boolean helium$failed = false;

    @Unique
    private static Method helium$cameraPositionMethod = null;

    @Unique
    private static boolean helium$methodResolved = false;

    @Unique
    private static Vec3d helium$getCameraPosition(Camera camera) {
        if (!helium$methodResolved) {
            helium$methodResolved = true;
            try {
                helium$cameraPositionMethod = Camera.class.getMethod("getCameraPos");
            } catch (NoSuchMethodException e) {
                try {
                    helium$cameraPositionMethod = Camera.class.getMethod("getPos");
                } catch (NoSuchMethodException e2) {
                    HeliumClient.LOGGER.warn("could not find camera position method");
                }
            }
        }
        if (helium$cameraPositionMethod != null) {
            try {
                return (Vec3d) helium$cameraPositionMethod.invoke(camera);
            } catch (Throwable ignored) {}
        }
        return Vec3d.ZERO;
    }

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void helium$pipelineSwapAndSubmit(CallbackInfo ci) {
        if (helium$failed) return;
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.renderPipelining) return;
            if (!RenderPipeline.isInitialized()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;

            Camera camera = client.gameRenderer.getCamera();
            Vec3d pos = helium$getCameraPosition(camera);
            double cx = pos.x;
            double cy = pos.y;
            double cz = pos.z;
            float yaw = camera.getYaw();
            float pitch = camera.getPitch();
            int renderDist = client.options.getViewDistance().getValue();

            RenderPipeline.swapBuffers();
            RenderPipeline.setCameraData(cx, cy, cz, yaw, pitch, renderDist);

            RenderPipeline.submitCullingWork(() -> {
                int[] visible = computeVisibleEntities(cx, cy, cz, yaw, pitch, renderDist);
                return new RenderPipeline.CullingResult(visible, new int[0], System.nanoTime());
            });
        } catch (Throwable t) {
            helium$failed = true;
            HeliumClient.LOGGER.warn("render pipeline hook disabled ({})", t.getClass().getSimpleName());
        }
    }

    @Unique
    private static int[] computeVisibleEntities(double cx, double cy, double cz, float yaw, float pitch, int renderDist) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return new int[0];

        double maxDistSq = (renderDist * 16.0) * (renderDist * 16.0);
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);
        double forwardX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double forwardZ = Math.cos(yawRad) * Math.cos(pitchRad);

        java.util.List<Integer> ids = new java.util.ArrayList<>();
        try {
            for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                double dx = entity.getX() - cx;
                double dy = entity.getY() - cy;
                double dz = entity.getZ() - cz;
                double distSq = dx * dx + dy * dy + dz * dz;
                if (distSq > maxDistSq) continue;

                double dot = dx * forwardX + dz * forwardZ;
                if (dot < -16.0 && distSq > 256.0) continue;

                ids.add(entity.getId());
            }
        } catch (Throwable ignored) {}

        int[] result = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) result[i] = ids.get(i);
        return result;
    }
}
