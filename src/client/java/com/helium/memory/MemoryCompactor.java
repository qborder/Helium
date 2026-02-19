package com.helium.memory;

import com.helium.HeliumClient;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MemoryCompactor {

    private static final ConcurrentHashMap<String, WeakReference<String>> STRING_INTERN_POOL = new ConcurrentHashMap<>(4096);
    private static final ConcurrentHashMap<Long, int[]> PALETTE_DEDUP = new ConcurrentHashMap<>(1024);

    private static long lastCompactTick = 0;
    private static final int COMPACT_INTERVAL_TICKS = 600;

    private MemoryCompactor() {}

    private static final int MAX_STRING_POOL_SIZE = 16384;

    public static String deduplicateString(String value) {
        if (value == null) return null;
        WeakReference<String> ref = STRING_INTERN_POOL.get(value);
        if (ref != null) {
            String existing = ref.get();
            if (existing != null) return existing;
        }
        if (STRING_INTERN_POOL.size() < MAX_STRING_POOL_SIZE) {
            STRING_INTERN_POOL.put(value, new WeakReference<>(value));
        }
        return value;
    }

    public static int[] deduplicatePalette(long hash, int[] palette) {
        int[] existing = PALETTE_DEDUP.get(hash);
        if (existing != null && java.util.Arrays.equals(existing, palette)) {
            return existing;
        }
        PALETTE_DEDUP.put(hash, palette);
        return palette;
    }

    public static long hashPalette(int[] palette) {
        long hash = 0xcbf29ce484222325L;
        for (int v : palette) {
            hash ^= v;
            hash *= 0x100000001b3L;
        }
        return hash;
    }

    public static void tick(long currentTick) {
        if (currentTick - lastCompactTick >= COMPACT_INTERVAL_TICKS) {
            compact();
            lastCompactTick = currentTick;
        }
    }

    public static void compact() {
        STRING_INTERN_POOL.entrySet().removeIf(entry -> entry.getValue().get() == null);

        if (PALETTE_DEDUP.size() > 8192) {
            int toRemove = PALETTE_DEDUP.size() / 4;
            var it = PALETTE_DEDUP.entrySet().iterator();
            while (it.hasNext() && toRemove > 0) {
                it.next();
                it.remove();
                toRemove--;
            }
        }
    }
}
