package com.helium.mixin.idle;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.idle.IdleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique
    private static boolean helium$callbacksRegistered = false;

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void helium$tickIdleCheck(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled) return;
        if (!IdleManager.isInitialized()) return;

        if (!helium$callbacksRegistered) {
            helium$callbacksRegistered = true;
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null && client.getWindow() != null) {
                    IdleManager.setWindow(client.getWindow().getHandle());
                }
            } catch (Throwable ignored) {}
        }

        IdleManager.tick();
    }
}
