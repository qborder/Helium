package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.render.GLStateCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlStateManager", remap = false)
public abstract class GlStateManagerMixin {

    @Inject(method = "_activeTexture", at = @At("HEAD"), remap = false)
    private static void helium$trackActiveTexture(int texture, CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            GLStateCache.setActiveTexture(texture);
        }
    }

    @Inject(method = "_bindTexture", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheBindTexture(int texture, CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldBindTexture(texture)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_enableBlend", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheEnableBlend(CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldEnableBlend(true)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_disableBlend", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheDisableBlend(CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldEnableBlend(false)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_enableDepthTest", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheEnableDepth(CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldEnableDepthTest(true)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_disableDepthTest", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheDisableDepth(CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldEnableDepthTest(false)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_enableCull", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheEnableCull(CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldEnableCullFace(true)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_disableCull", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheDisableCull(CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldEnableCullFace(false)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_blendFuncSeparate", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheBlendFunc(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldSetBlendFunc(srcRgb, dstRgb, srcAlpha, dstAlpha)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "_depthFunc", at = @At("HEAD"), cancellable = true, remap = false)
    private static void helium$cacheDepthFunc(int func, CallbackInfo ci) {
        if (GLStateCache.isInitialized() && HeliumClient.getConfig().glStateCache) {
            if (!GLStateCache.shouldSetDepthFunc(func)) {
                ci.cancel();
            }
        }
    }
}
