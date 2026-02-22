package com.helium.mixin.idle;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.idle.IdleManager;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void helium$checkShouldRender(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.autoPauseOnIdle) return;
        if (!IdleManager.isInitialized()) return;

        IdleManager.tick();

        if (!IdleManager.checkForRender()) {
            ci.cancel();
        }
    }
}
