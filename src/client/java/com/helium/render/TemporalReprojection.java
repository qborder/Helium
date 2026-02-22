package com.helium.render;

import com.helium.HeliumClient;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class TemporalReprojection {

    private static volatile boolean initialized = false;
    private static final Matrix4f previousViewProjection = new Matrix4f();
    private static final Matrix4f currentViewProjection = new Matrix4f();
    private static final Matrix4f reprojectionMatrix = new Matrix4f();

    private static volatile long previousFrameId = -1;
    private static volatile long currentFrameId = 0;
    private static volatile int reusedPixels = 0;
    private static volatile int totalPixels = 0;
    private static volatile float reprojectionConfidence = 0f;

    private static final float GHOSTING_THRESHOLD = 0.02f;
    private static final float MIN_CONFIDENCE = 0.3f;

    private TemporalReprojection() {}

    public static void init() {
        if (initialized) return;
        initialized = true;
        previousViewProjection.identity();
        currentViewProjection.identity();
        reprojectionMatrix.identity();
        HeliumClient.LOGGER.info("temporal reprojection initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void updateMatrices(Matrix4f viewProjection, long frameId) {
        if (!initialized) return;

        previousViewProjection.set(currentViewProjection);
        currentViewProjection.set(viewProjection);
        previousFrameId = currentFrameId;
        currentFrameId = frameId;

        Matrix4f invCurrent = new Matrix4f();
        currentViewProjection.invert(invCurrent);
        previousViewProjection.mul(invCurrent, reprojectionMatrix);
    }

    public static boolean canReproject(float screenX, float screenY) {
        if (!initialized || previousFrameId < 0) return false;

        Vector4f clipPos = new Vector4f(screenX * 2f - 1f, screenY * 2f - 1f, 0f, 1f);
        reprojectionMatrix.transform(clipPos);

        if (clipPos.w == 0f) return false;
        float reprojX = (clipPos.x / clipPos.w + 1f) * 0.5f;
        float reprojY = (clipPos.y / clipPos.w + 1f) * 0.5f;

        float dx = reprojX - screenX;
        float dy = reprojY - screenY;
        float motionSq = dx * dx + dy * dy;

        return motionSq < GHOSTING_THRESHOLD;
    }

    public static void recordFrameStats(int reused, int total) {
        reusedPixels = reused;
        totalPixels = total;
        reprojectionConfidence = total > 0 ? (float) reused / total : 0f;
    }

    public static float getReprojectionConfidence() {
        return reprojectionConfidence;
    }

    public static boolean isConfident() {
        return reprojectionConfidence >= MIN_CONFIDENCE;
    }

    public static Matrix4f getReprojectionMatrix() {
        return reprojectionMatrix;
    }

    public static int getReusedPixels() { return reusedPixels; }
    public static int getTotalPixels() { return totalPixels; }

    public static void shutdown() {
        initialized = false;
        previousFrameId = -1;
        currentFrameId = 0;
        previousViewProjection.identity();
        currentViewProjection.identity();
        reprojectionMatrix.identity();
    }
}
