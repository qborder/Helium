package com.helium.mixin.math;

import com.helium.HeliumClient;
import com.helium.math.FastMath;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MathHelper.class)
public abstract class MathHelperMixin {

    @Inject(method = "sin(D)D", at = @At("HEAD"), cancellable = true, require = 0)
    private static void helium$fastSinDouble(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((double) FastMath.sin(value));
        }
    }

    @Inject(method = "sin(F)F", at = @At("HEAD"), cancellable = true, require = 0)
    private static void helium$fastSinFloat(float value, CallbackInfoReturnable<Float> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue(FastMath.sin(value));
        }
    }

    @Inject(method = "cos(D)D", at = @At("HEAD"), cancellable = true, require = 0)
    private static void helium$fastCosDouble(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue((double) FastMath.cos(value));
        }
    }

    @Inject(method = "cos(F)F", at = @At("HEAD"), cancellable = true, require = 0)
    private static void helium$fastCosFloat(float value, CallbackInfoReturnable<Float> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue(FastMath.cos(value));
        }
    }

    @Inject(method = "atan2(DD)D", at = @At("HEAD"), cancellable = true, require = 0)
    private static void helium$fastAtan2(double y, double x, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue(FastMath.atan2(y, x));
        }
    }

    @Inject(method = "fastInverseSqrt(D)D", at = @At("HEAD"), cancellable = true, require = 0)
    private static void helium$fastInvSqrt(double value, CallbackInfoReturnable<Double> cir) {
        if (FastMath.isInitialized() && HeliumClient.getConfig().fastMath) {
            cir.setReturnValue(FastMath.inverseSqrt(value));
        }
    }
}
