package com.helium.render;

import com.helium.HeliumClient;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModelCache {

    private static final Object LOCK = new Object();
    private static Map<Long, Object> cache;
    private static volatile boolean initialized = false;
    private static volatile int maxEntries = 8192;
    private static volatile int hits = 0;
    private static volatile int misses = 0;

    private ModelCache() {}

    public static void init(int maxSizeMb) {
        if (initialized) return;

        maxEntries = Math.max(1024, (maxSizeMb * 1024 * 1024) / 512);

        cache = new LinkedHashMap<>(1024, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Object> eldest) {
                return size() > maxEntries;
            }
        };

        initialized = true;
        HeliumClient.LOGGER.info("model cache initialized (max {} entries, ~{}mb)", maxEntries, maxSizeMb);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(long key) {
        if (!initialized) return null;
        synchronized (LOCK) {
            Object val = cache.get(key);
            if (val != null) {
                hits++;
                return (T) val;
            }
            misses++;
            return null;
        }
    }

    public static void put(long key, Object value) {
        if (!initialized || value == null) return;
        synchronized (LOCK) {
            cache.put(key, value);
        }
    }

    public static void invalidate(long key) {
        if (!initialized) return;
        synchronized (LOCK) {
            cache.remove(key);
        }
    }

    public static void invalidateAll() {
        if (!initialized) return;
        synchronized (LOCK) {
            cache.clear();
        }
        hits = 0;
        misses = 0;
    }

    public static int size() {
        if (!initialized) return 0;
        synchronized (LOCK) {
            return cache.size();
        }
    }

    public static int getHits() {
        return hits;
    }

    public static int getMisses() {
        return misses;
    }

    public static float getHitRate() {
        int total = hits + misses;
        return total > 0 ? (float) hits / total : 0f;
    }
}
