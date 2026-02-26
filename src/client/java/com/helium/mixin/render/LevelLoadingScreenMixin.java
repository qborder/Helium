package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.FastWorldLoadingOptimizer;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin {

    @Unique
    private static boolean helium$failed = false;
    @Unique
    private boolean helium$tracked = false;

    @Inject(method = "init", at = @At("HEAD"), require = 0)
    private void helium$onLoadStart(CallbackInfo ci) {
        if (helium$failed) return;
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastWorldLoading) return;
            if (!FastWorldLoadingOptimizer.isInitialized()) return;

            if (!helium$tracked) {
                helium$tracked = true;
                FastWorldLoadingOptimizer.onWorldLoadStart();
            }
        } catch (Throwable t) {
            helium$failed = true;
        }
    }

    @Inject(method = "shouldCloseOnEsc", at = @At("HEAD"), cancellable = true, require = 0)
    private void helium$allowEscClose(CallbackInfoReturnable<Boolean> cir) {
        if (helium$failed) return;
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastWorldLoading) return;

            cir.setReturnValue(true);
        } catch (Throwable t) {
            helium$failed = true;
        }
    }

    @Inject(method = "close", at = @At("HEAD"), require = 0)
    private void helium$onLoadEnd(CallbackInfo ci) {
        if (helium$failed) return;
        try {
            if (helium$tracked) {
                FastWorldLoadingOptimizer.onWorldLoadEnd();
                helium$tracked = false;
            }
        } catch (Throwable ignored) {}
    }
}
