package com.helium.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;

public final class DisplaySyncOptimizer {

    private static volatile long lastDisplayUpdateTime = 0;
    private static final long DEFAULT_UPDATE_INTERVAL_NS = 16_666_667L;

    private DisplaySyncOptimizer() {}

    public static boolean shouldPerformDisplayUpdate() {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.displaySyncOptimization) {
            return true;
        }

        long now = System.nanoTime();
        long elapsed = now - lastDisplayUpdateTime;

        if (elapsed >= DEFAULT_UPDATE_INTERVAL_NS) {
            lastDisplayUpdateTime = now;
            return true;
        }

        return false;
    }

    public static void reset() {
        lastDisplayUpdateTime = 0;
    }
}
