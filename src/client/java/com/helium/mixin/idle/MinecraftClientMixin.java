package com.helium.mixin.idle;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.idle.IdleManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Unique
    private long helium$lastFrameTime = 0;

    @Inject(method = "render", at = @At("RETURN"))
    private void helium$throttleIdleFps(boolean tick, CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.autoPauseOnIdle) return;
        if (!IdleManager.isInitialized() || !IdleManager.isIdle()) {
            helium$lastFrameTime = 0;
            return;
        }

        long now = System.nanoTime();
        long frameIntervalNs = 1_000_000_000L / Math.max(1, IdleManager.getIdleFpsLimit());

        if (helium$lastFrameTime > 0) {
            long elapsed = now - helium$lastFrameTime;
            long remaining = frameIntervalNs - elapsed;
            if (remaining > 1_000_000L) {
                try {
                    Thread.sleep(remaining / 1_000_000L, (int) (remaining % 1_000_000L));
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        helium$lastFrameTime = System.nanoTime();
    }
}
