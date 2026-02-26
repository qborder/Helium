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
    private final Quaternionf helium$reusedQuat = new Quaternionf();

    @Inject(method = "applyTransform", at = @At("HEAD"), cancellable = true, require = 0)
    private void helium$fastApplyTransform(MatrixStack matrices, CallbackInfo ci) {
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastAnimations) return;
            if (!FastAnimationOptimizer.isInitialized()) return;

            matrices.translate(originX / 16.0f, originY / 16.0f, originZ / 16.0f);

            if (pitch != 0f || yaw != 0f || roll != 0f) {
                matrices.multiply(helium$reusedQuat.rotationZYX(roll, yaw, pitch));
            }

            if (xScale != 1f || yScale != 1f || zScale != 1f) {
                matrices.scale(xScale, yScale, zScale);
            }

            ci.cancel();
        } catch (Throwable ignored) {}
    }
}
