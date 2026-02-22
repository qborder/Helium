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
    private long helium$lastIdleSleep = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void helium$tickIdleManager(boolean tick, CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.autoPauseOnIdle) return;
        if (!IdleManager.isInitialized()) return;

        IdleManager.tick();

        if (IdleManager.isIdle()) {
            long now = System.currentTimeMillis();
            long sleepMs = 1000L / Math.max(1, IdleManager.getIdleFpsLimit());
            long elapsed = now - helium$lastIdleSleep;
            if (elapsed < sleepMs) {
                try {
                    Thread.sleep(sleepMs - elapsed);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
            helium$lastIdleSleep = System.currentTimeMillis();
        }
    }
}
