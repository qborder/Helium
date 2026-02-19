package com.helium.particle;

import com.helium.HeliumClient;
import net.minecraft.client.particle.Particle;

import java.util.concurrent.atomic.AtomicInteger;

public final class ParticleLimiter {

    private static final AtomicInteger currentParticleCount = new AtomicInteger(0);
    private static volatile int maxParticles = 1000;
    private static volatile boolean initialized = false;

    private ParticleLimiter() {}

    public static void init(int max) {
        maxParticles = max;
        initialized = true;
        HeliumClient.LOGGER.info("particle limiter initialized with max {}", max);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean canAddParticle(Particle particle) {
        if (!initialized) return true;

        int count = currentParticleCount.get();
        int priority = ParticlePriority.getPriority(particle);

        return ParticlePriority.shouldKeep(priority, count, maxParticles);
    }

    public static void onParticleAdded() {
        currentParticleCount.incrementAndGet();
    }

    public static void onParticleRemoved() {
        currentParticleCount.updateAndGet(c -> Math.max(0, c - 1));
    }

    public static void setParticleCount(int count) {
        currentParticleCount.set(count);
    }

    public static int getCurrentCount() {
        return currentParticleCount.get();
    }

    public static int getMaxParticles() {
        return maxParticles;
    }

    public static void setMaxParticles(int max) {
        maxParticles = max;
    }
}
