package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererPipelineMixin {

    @Unique
    private static boolean helium$failed = false;

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void helium$frameStart(CallbackInfo ci) {
        if (helium$failed) return;
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.renderPipelining) return;
            if (!RenderPipeline.isInitialized()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            int maxFps = client.options.getMaxFps().getValue();
            if (maxFps > 0 && maxFps < 260) {
                RenderPipeline.setTargetFps(maxFps);
            }

            RenderPipeline.onFrameStart();
        } catch (Throwable t) {
            helium$failed = true;
            HeliumClient.LOGGER.warn("render pipeline hook disabled ({})", t.getClass().getSimpleName());
        }
    }

    @Inject(method = "render", at = @At("RETURN"), require = 0)
    private void helium$frameEnd(CallbackInfo ci) {
        if (helium$failed) return;
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.renderPipelining) return;
            if (!RenderPipeline.isInitialized()) return;

            RenderPipeline.onFrameEnd();
        } catch (Throwable ignored) {}
    }
}
