package com.helium.mixin.compat;

import com.helium.render.PerformanceMetricsOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.flashyreese.mods.sodiumextra.client.FrameCounter", remap = false)
public abstract class SodiumExtraFrameCounterMixin {

    @Inject(method = "getSmoothFps", at = @At("RETURN"), cancellable = true, require = 0)
    private void helium$optimizeSmoothFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric(cir.getReturnValue());
        cir.setReturnValue(optimized);
    }

    @Inject(method = "getAverageFps", at = @At("RETURN"), cancellable = true, require = 0)
    private void helium$optimizeAverageFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric(cir.getReturnValue());
        cir.setReturnValue(optimized);
    }

    @Inject(method = "getOnePercentLowFps", at = @At("RETURN"), cancellable = true, require = 0)
    private void helium$optimizeOnePercentLowFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric(cir.getReturnValue());
        cir.setReturnValue(optimized);
    }

    @Inject(method = "getPointOnePercentLowFps", at = @At("RETURN"), cancellable = true, require = 0)
    private void helium$optimizePointOnePercentLowFps(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric(cir.getReturnValue());
        cir.setReturnValue(optimized);
    }
}
