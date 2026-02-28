package com.helium.render;

import java.util.concurrent.atomic.AtomicIntegerArray;

public final class GLStateCache {

    private static final int MAX_TEXTURE_UNITS = 16;
    private static final AtomicIntegerArray lastBoundTexturePerUnit = new AtomicIntegerArray(MAX_TEXTURE_UNITS);
    private static volatile int activeTextureUnit = 0;
    private static volatile int lastBoundProgram = -1;
    private static volatile int lastBoundVao = -1;
    private static volatile int lastBoundVbo = -1;

    private static volatile boolean blendEnabled = false;
    private static volatile boolean depthTestEnabled = false;
    private static volatile boolean cullFaceEnabled = false;

    private static volatile int blendSrcRgb = -1;
    private static volatile int blendDstRgb = -1;
    private static volatile int blendSrcAlpha = -1;
    private static volatile int blendDstAlpha = -1;

    private static volatile int depthFunc = -1;

    private static volatile boolean initialized = false;
    private static volatile boolean aggressiveMode = false;

    private GLStateCache() {}

    public static void setAggressiveMode(boolean enabled) {
        aggressiveMode = enabled;
    }

    public static boolean isAggressiveMode() {
        return aggressiveMode;
    }

    public static void init() {
        reset();
        initialized = true;
    }

    public static void reset() {
        for (int i = 0; i < MAX_TEXTURE_UNITS; i++) {
            lastBoundTexturePerUnit.set(i, -1);
        }
        activeTextureUnit = 0;
        lastBoundProgram = -1;
        lastBoundVao = -1;
        lastBoundVbo = -1;
        blendEnabled = false;
        depthTestEnabled = false;
        cullFaceEnabled = false;
        blendSrcRgb = -1;
        blendDstRgb = -1;
        blendSrcAlpha = -1;
        blendDstAlpha = -1;
        depthFunc = -1;
    }

    public static void setActiveTexture(int glTextureUnit) {
        int unit = glTextureUnit - 0x84C0;
        if (unit >= 0 && unit < MAX_TEXTURE_UNITS) {
            activeTextureUnit = unit;
        }
    }

    public static boolean shouldBindTexture(int textureId) {
        int unit = activeTextureUnit;
        if (unit >= 0 && unit < MAX_TEXTURE_UNITS) {
            int prev = lastBoundTexturePerUnit.getAndSet(unit, textureId);
            return prev != textureId;
        }
        return true;
    }

    public static boolean shouldUseProgram(int programId) {
        if (programId == lastBoundProgram) return false;
        lastBoundProgram = programId;
        return true;
    }

    public static boolean shouldBindVao(int vaoId) {
        if (vaoId == lastBoundVao) return false;
        lastBoundVao = vaoId;
        return true;
    }

    public static boolean shouldBindVbo(int vboId) {
        if (vboId == lastBoundVbo) return false;
        lastBoundVbo = vboId;
        return true;
    }

    public static boolean shouldEnableBlend(boolean enable) {
        if (enable == blendEnabled) return false;
        blendEnabled = enable;
        return true;
    }

    public static boolean shouldEnableDepthTest(boolean enable) {
        if (enable == depthTestEnabled) return false;
        depthTestEnabled = enable;
        return true;
    }

    public static boolean shouldEnableCullFace(boolean enable) {
        if (enable == cullFaceEnabled) return false;
        cullFaceEnabled = enable;
        return true;
    }

    public static boolean shouldSetBlendFunc(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
        if (srcRgb == blendSrcRgb && dstRgb == blendDstRgb && srcAlpha == blendSrcAlpha && dstAlpha == blendDstAlpha) return false;
        blendSrcRgb = srcRgb;
        blendDstRgb = dstRgb;
        blendSrcAlpha = srcAlpha;
        blendDstAlpha = dstAlpha;
        return true;
    }

    public static boolean shouldSetDepthFunc(int func) {
        if (func == depthFunc) return false;
        depthFunc = func;
        return true;
    }

    public static void invalidateTexture() {
        for (int i = 0; i < MAX_TEXTURE_UNITS; i++) {
            lastBoundTexturePerUnit.set(i, -1);
        }
    }

    public static void invalidateProgram() {
        lastBoundProgram = -1;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
