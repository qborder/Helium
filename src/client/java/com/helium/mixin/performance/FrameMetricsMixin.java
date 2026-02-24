package com.helium.mixin.performance;

import com.helium.render.PerformanceMetricsOptimizer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class FrameMetricsMixin {

    @Shadow
    private static int currentFps;

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void helium$optimizeFieldMetrics(CallbackInfo ci) {
        currentFps = PerformanceMetricsOptimizer.computeOptimizedMetric(currentFps);
    }

    @Inject(method = "getCurrentFps", at = @At("RETURN"), cancellable = true, require = 0)
    private static void helium$optimizeFrameMetrics(CallbackInfoReturnable<Integer> cir) {
        int optimized = PerformanceMetricsOptimizer.computeOptimizedMetric(cir.getReturnValue());
        cir.setReturnValue(optimized);
    }
}
