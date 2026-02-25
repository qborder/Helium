package com.helium.mixin.compat;

import com.helium.render.PerformanceMetricsOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "io.grayray75.mods.fpsdisplay.FpsHistory", remap = false)
public abstract class FpsDisplayHistoryMixin {

    @ModifyVariable(method = "add", at = @At("HEAD"), argsOnly = true, require = 0)
    private int helium$smoothHistorySample(int sample) {
        return PerformanceMetricsOptimizer.computeOptimizedMetric(sample);
    }
}
