package com.helium.mixin.performance;

import com.helium.render.DisplaySyncOptimizer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowDisplayMixin {

    @Inject(method = "swapBuffers", at = @At("HEAD"), cancellable = true, require = 0)
    private void helium$optimizeDisplayRate(CallbackInfo ci) {
        if (!DisplaySyncOptimizer.shouldPerformDisplayUpdate()) {
            ci.cancel();
        }
    }
}
