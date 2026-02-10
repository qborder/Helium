package com.helium.render;

public final class GLStateCache {

    private static int lastBoundTexture = -1;
    private static int lastBoundProgram = -1;
    private static int lastBoundVao = -1;
    private static int lastBoundVbo = -1;

    private static boolean blendEnabled = false;
    private static boolean depthTestEnabled = false;
    private static boolean cullFaceEnabled = false;

    private static int blendSrcRgb = -1;
    private static int blendDstRgb = -1;
    private static int blendSrcAlpha = -1;
    private static int blendDstAlpha = -1;

    private static int depthFunc = -1;

    private static boolean initialized = false;

    private GLStateCache() {}

    public static void init() {
        reset();
        initialized = true;
    }

    public static void reset() {
        lastBoundTexture = -1;
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

    public static boolean shouldBindTexture(int textureId) {
        if (textureId == lastBoundTexture) return false;
        lastBoundTexture = textureId;
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
        lastBoundTexture = -1;
    }

    public static void invalidateProgram() {
        lastBoundProgram = -1;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
