package com.helium.mixin.compat;

import com.helium.render.PerformanceMetricsOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.fpshud.FPShudClient", remap = false)
public abstract class FPShudMetricsMixin {

    @Inject(method = "calcAvrFps", at = @At("RETURN"), cancellable = true, require = 0)
    private static void helium$optimizeAvrMetric(CallbackInfoReturnable<Double> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((int) Math.round(cir.getReturnValue()));
        cir.setReturnValue((double) optimized);
    }

    @Inject(method = "calcMaxFps", at = @At("RETURN"), cancellable = true, require = 0)
    private static void helium$optimizeMaxMetric(CallbackInfoReturnable<Double> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((int) Math.round(cir.getReturnValue()));
        cir.setReturnValue((double) optimized);
    }

    @Inject(method = "calcMinFps", at = @At("RETURN"), cancellable = true, require = 0)
    private static void helium$optimizeMinMetric(CallbackInfoReturnable<Double> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric((int) Math.round(cir.getReturnValue()));
        cir.setReturnValue((double) optimized);
    }
}
