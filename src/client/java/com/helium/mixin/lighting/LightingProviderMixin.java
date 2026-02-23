package com.helium.mixin.lighting;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.lighting.AsyncLightEngine;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightingProvider.class)
public abstract class LightingProviderMixin {

    @Unique
    private static boolean helium$failed = false;

    @Inject(method = "doLightUpdates", at = @At("HEAD"), cancellable = false, require = 0)
    private void helium$trackLightUpdates(CallbackInfoReturnable<Integer> cir) {
        if (helium$failed) return;
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.asyncLightUpdates) return;
            if (!AsyncLightEngine.isInitialized()) return;

            AsyncLightEngine.onLightUpdateBatch();
        } catch (Throwable t) {
            helium$failed = true;
            HeliumClient.LOGGER.warn("async light tracking disabled ({})", t.getClass().getSimpleName());
        }
    }
}
