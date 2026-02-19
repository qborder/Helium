package com.helium.tick;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ClientTickCache {

    private static final int MAX_CACHE_SIZE = 16384;

    private static final Map<Long, Integer> biomeColorCache = new LinkedHashMap<>(1024, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private static final Map<Long, Integer> lightLevelCache = new LinkedHashMap<>(1024, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private static final Object biomeLock = new Object();
    private static final Object lightLock = new Object();

    private ClientTickCache() {}

    public static int getCachedBiomeColor(long posKey, int fallback) {
        synchronized (biomeLock) {
            Integer cached = biomeColorCache.get(posKey);
            return cached != null ? cached : fallback;
        }
    }

    public static void cacheBiomeColor(long posKey, int color) {
        synchronized (biomeLock) {
            biomeColorCache.put(posKey, color);
        }
    }

    public static int getCachedLightLevel(long posKey, int fallback) {
        synchronized (lightLock) {
            Integer cached = lightLevelCache.get(posKey);
            return cached != null ? cached : fallback;
        }
    }

    public static void cacheLightLevel(long posKey, int level) {
        synchronized (lightLock) {
            lightLevelCache.put(posKey, level);
        }
    }

    public static void tick(long currentTick) {
    }

    public static void invalidateAll() {
        synchronized (biomeLock) {
            biomeColorCache.clear();
        }
        synchronized (lightLock) {
            lightLevelCache.clear();
        }
    }

    public static void invalidatePosition(long posKey) {
        synchronized (biomeLock) {
            biomeColorCache.remove(posKey);
        }
        synchronized (lightLock) {
            lightLevelCache.remove(posKey);
        }
    }

    public static long packPos(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) y & 0xFFFL) << 26 | ((long) z & 0x3FFFFFFL);
    }
}
