package com.helium.render;

import com.helium.HeliumClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class FastWorldLoadingOptimizer {

    private static volatile boolean initialized = false;
    private static volatile boolean enabled = false;
    private static volatile long loadStartTime = 0;
    private static volatile long loadEndTime = 0;

    private FastWorldLoadingOptimizer() {}

    public static void init() {
        if (initialized) return;
        initialized = true;
        HeliumClient.LOGGER.info("[helium] fast world loading optimizer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled && initialized;
    }

    public static void onWorldLoadStart() {
        if (!enabled) return;
        loadStartTime = System.currentTimeMillis();
        HeliumClient.LOGGER.info("[helium] world load started");
    }

    public static void onWorldLoadEnd() {
        if (!enabled || loadStartTime == 0) return;
        loadEndTime = System.currentTimeMillis();
        long elapsed = loadEndTime - loadStartTime;
        HeliumClient.LOGGER.info("[helium] world load completed in {}ms", elapsed);
        loadStartTime = 0;
    }

    public static int getReducedSpawnRadius() {
        return 2;
    }

    public static boolean shouldSkipExtraSpawnChunks(int chunkX, int chunkZ, int spawnX, int spawnZ) {
        if (!enabled) return false;

        int dx = Math.abs(chunkX - spawnX);
        int dz = Math.abs(chunkZ - spawnZ);
        int radius = getReducedSpawnRadius();

        return dx > radius || dz > radius;
    }

    public static long getLastLoadTimeMs() {
        return loadEndTime - loadStartTime;
    }
}
