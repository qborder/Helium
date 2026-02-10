package com.helium.tick;

import java.util.concurrent.ConcurrentHashMap;

public final class ClientTickCache {

    private static final ConcurrentHashMap<Long, Integer> biomeColorCache = new ConcurrentHashMap<>(4096);
    private static final ConcurrentHashMap<Long, Integer> lightLevelCache = new ConcurrentHashMap<>(4096);

    private static long lastClearTick = 0;
    private static final int CACHE_LIFETIME_TICKS = 100;

    private ClientTickCache() {}

    public static int getCachedBiomeColor(long posKey, int fallback) {
        Integer cached = biomeColorCache.get(posKey);
        return cached != null ? cached : fallback;
    }

    public static void cacheBiomeColor(long posKey, int color) {
        if (biomeColorCache.size() < 16384) {
            biomeColorCache.put(posKey, color);
        }
    }

    public static int getCachedLightLevel(long posKey, int fallback) {
        Integer cached = lightLevelCache.get(posKey);
        return cached != null ? cached : fallback;
    }

    public static void cacheLightLevel(long posKey, int level) {
        if (lightLevelCache.size() < 16384) {
            lightLevelCache.put(posKey, level);
        }
    }

    public static void tick(long currentTick) {
        if (currentTick - lastClearTick >= CACHE_LIFETIME_TICKS) {
            biomeColorCache.clear();
            lightLevelCache.clear();
            lastClearTick = currentTick;
        }
    }

    public static void invalidateAll() {
        biomeColorCache.clear();
        lightLevelCache.clear();
    }

    public static long packPos(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) y & 0xFFFL) << 26 | ((long) z & 0x3FFFFFFL);
    }
}
