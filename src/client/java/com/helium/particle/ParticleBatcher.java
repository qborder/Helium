package com.helium.particle;

import com.helium.HeliumClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class ParticleBatcher {

    private static final ConcurrentHashMap<String, AtomicInteger> particleTypeCounts = new ConcurrentHashMap<>();
    private static final int BATCH_THRESHOLD = 10;
    private static volatile boolean initialized = false;

    private ParticleBatcher() {}

    public static void init() {
        initialized = true;
        HeliumClient.LOGGER.info("particle batcher initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void recordParticleType(String particleType) {
        if (!initialized) return;
        particleTypeCounts.computeIfAbsent(particleType, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public static int getTypeCount(String particleType) {
        AtomicInteger count = particleTypeCounts.get(particleType);
        return count != null ? count.get() : 0;
    }

    public static boolean shouldBatch(String particleType) {
        return getTypeCount(particleType) >= BATCH_THRESHOLD;
    }

    public static void resetCounts() {
        particleTypeCounts.values().forEach(c -> c.set(0));
    }

    public static void tick() {
        if (!initialized) return;
        resetCounts();
    }
}
