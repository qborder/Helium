package com.helium.render;

import com.helium.HeliumClient;

public final class FastAnimationOptimizer {

    private static volatile boolean initialized = false;

    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0);

    private static volatile long frameId = 0;
    private static volatile long cacheHits = 0;
    private static volatile long cacheMisses = 0;
    private static volatile long lastReportTime = 0;

    private FastAnimationOptimizer() {}

    public static void init() {
        if (initialized) return;
        initialized = true;
        HeliumClient.LOGGER.info("[helium] fast animation optimizer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void onFrameStart() {
        if (!initialized) return;
        frameId++;

        long now = System.currentTimeMillis();
        if (now - lastReportTime > 30000) {
            lastReportTime = now;
            long total = cacheHits + cacheMisses;
            if (total > 0) {
                HeliumClient.LOGGER.debug("[helium] fast anim: {} hits / {} total ({}% reuse)",
                        cacheHits, total, (cacheHits * 100) / total);
            }
            cacheHits = 0;
            cacheMisses = 0;
        }
    }

    public static void recordCacheHit() {
        cacheHits++;
    }

    public static void recordCacheMiss() {
        cacheMisses++;
    }

    public static float degToRad(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    public static long getCacheHits() { return cacheHits; }
    public static long getCacheMisses() { return cacheMisses; }
}
