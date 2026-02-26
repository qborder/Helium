package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.FastAnimationOptimizer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public abstract class ModelPartAnimationMixin {

    @Shadow public float pitch;
    @Shadow public float yaw;
    @Shadow public float roll;
    @Shadow public float xScale;
    @Shadow public float yScale;
    @Shadow public float zScale;
    @Shadow public float originX;
    @Shadow public float originY;
    @Shadow public float originZ;

    @Unique
    private float helium$cachedPitch = Float.NaN;
    @Unique
    private float helium$cachedYaw = Float.NaN;
    @Unique
    private float helium$cachedRoll = Float.NaN;
    @Unique
    private final Quaternionf helium$cachedQuat = new Quaternionf();
    @Unique
    private boolean helium$quatValid = false;

    @Inject(method = "applyTransform", at = @At("HEAD"), cancellable = true, require = 0)
    private void helium$fastApplyTransform(MatrixStack matrices, CallbackInfo ci) {
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastAnimations) return;
            if (!FastAnimationOptimizer.isInitialized()) return;

            boolean hasRotation = pitch != 0f || yaw != 0f || roll != 0f;
            boolean hasScale = xScale != 1f || yScale != 1f || zScale != 1f;

            matrices.translate(originX / 16.0f, originY / 16.0f, originZ / 16.0f);

            if (hasRotation) {
                if (helium$quatValid
                        && Float.floatToRawIntBits(pitch) == Float.floatToRawIntBits(helium$cachedPitch)
                        && Float.floatToRawIntBits(yaw) == Float.floatToRawIntBits(helium$cachedYaw)
                        && Float.floatToRawIntBits(roll) == Float.floatToRawIntBits(helium$cachedRoll)) {
                    matrices.multiply(helium$cachedQuat);
                    FastAnimationOptimizer.recordCacheHit();
                } else {
                    helium$cachedQuat.identity().rotateZYX(roll, yaw, pitch);
                    helium$cachedPitch = pitch;
                    helium$cachedYaw = yaw;
                    helium$cachedRoll = roll;
                    helium$quatValid = true;
                    matrices.multiply(helium$cachedQuat);
                    FastAnimationOptimizer.recordCacheMiss();
                }
            }

            if (hasScale) {
                matrices.scale(xScale, yScale, zScale);
            }

            ci.cancel();
        } catch (Throwable ignored) {}
    }
}
