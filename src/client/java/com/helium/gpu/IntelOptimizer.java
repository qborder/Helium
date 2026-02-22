package com.helium.gpu;

import com.helium.HeliumClient;
import com.helium.render.GLStateCache;

public final class IntelOptimizer {

    private static volatile boolean initialized = false;
    private static volatile boolean aggressiveCaching = false;

    private IntelOptimizer() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        if (!GpuDetector.isIntel()) {
            HeliumClient.LOGGER.info("intel optimizer skipped - not an intel gpu");
            return;
        }

        aggressiveCaching = true;

        if (GLStateCache.isInitialized()) {
            GLStateCache.setAggressiveMode(true);
            HeliumClient.LOGGER.info("intel: aggressive gl state caching enabled");
        }

        HeliumClient.LOGGER.info("intel optimizer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isAggressiveCaching() {
        return aggressiveCaching;
    }
}
