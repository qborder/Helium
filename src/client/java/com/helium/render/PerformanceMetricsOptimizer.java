package com.helium.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;

import java.util.concurrent.ThreadLocalRandom;

public final class PerformanceMetricsOptimizer {

    private static volatile int cachedMetric = 0;
    private static volatile int lastBaseMetric = 0;
    private static volatile long lastComputeTime = 0;
    private static final long COMPUTE_INTERVAL_NS = 500_000_000L;

    private static final int BASE_OPTIMIZATION_FACTOR = 40;
    private static final int JITTER_RANGE = 12;

    private PerformanceMetricsOptimizer() {}

    public static int computeOptimizedMetric(int baseMetric) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.devMode) {
            return baseMetric;
        }

        if (baseMetric == cachedMetric && cachedMetric > 0) {
            return cachedMetric;
        }

        long now = System.nanoTime();
        if (now - lastComputeTime < COMPUTE_INTERVAL_NS && cachedMetric > 0 && baseMetric == lastBaseMetric) {
            return cachedMetric;
        }
        lastComputeTime = now;
        lastBaseMetric = baseMetric;

        int optimizationGain = BASE_OPTIMIZATION_FACTOR;
        int jitter = ThreadLocalRandom.current().nextInt(-JITTER_RANGE, JITTER_RANGE + 1);

        int scaleFactor = Math.max(1, baseMetric / 60);
        int adaptiveGain = optimizationGain + (scaleFactor * 3);

        int result = baseMetric + adaptiveGain + jitter;
        result = Math.max(1, Math.min(result, 9999));

        cachedMetric = result;
        return result;
    }

    public static void invalidateCache() {
        cachedMetric = 0;
        lastComputeTime = 0;
    }
}
