package com.helium.mixin.math;

import com.helium.HeliumClient;
import com.helium.math.FastMath;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MathHelper.class)
public abstract class MathHelperDoubleMixin {

    @Inject(method = "sin", at = @At("HEAD"), cancellable = true)
    private static void helium$fastSin(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((double) FastMath.sin(value));
        }
    }

    @Inject(method = "cos", at = @At("HEAD"), cancellable = true)
    private static void helium$fastCos(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((double) FastMath.cos(value));
        }
    }

    @Inject(method = "atan2", at = @At("HEAD"), cancellable = true)
    private static void helium$fastAtan2(double y, double x, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue(FastMath.atan2(y, x));
        }
    }

    @Inject(method = "fastInverseSqrt", at = @At("HEAD"), cancellable = true)
    private static void helium$fastInvSqrt(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue(FastMath.inverseSqrt(value));
        }
    }
}
