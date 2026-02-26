package com.helium.render;

import com.helium.HeliumClient;

public final class FastAnimationOptimizer {

    private static volatile boolean initialized = false;

    private FastAnimationOptimizer() {}

    public static void init() {
        if (initialized) return;
        initialized = true;
        HeliumClient.LOGGER.info("[helium] fast animation optimizer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

}
