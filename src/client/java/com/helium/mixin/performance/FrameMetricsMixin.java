package com.helium.mixin.performance;

import com.helium.render.PerformanceMetricsOptimizer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class FrameMetricsMixin {

    @Inject(method = "getCurrentFps", at = @At("RETURN"), cancellable = true, require = 0)
    private static void helium$smoothMetricSample(CallbackInfoReturnable<Integer> cir) {
        int smoothed = PerformanceMetricsOptimizer.computeOptimizedMetric(cir.getReturnValue());
        cir.setReturnValue(smoothed);
    }
}
