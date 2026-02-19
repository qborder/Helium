package com.helium.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleTypes;

public final class ParticlePriority {

    public static final int PRIORITY_CRITICAL = 100;
    public static final int PRIORITY_HIGH = 75;
    public static final int PRIORITY_MEDIUM = 50;
    public static final int PRIORITY_LOW = 25;
    public static final int PRIORITY_AMBIENT = 10;

    private ParticlePriority() {}

    public static int getPriority(Particle particle) {
        if (particle == null) return PRIORITY_LOW;

        String className = particle.getClass().getSimpleName().toLowerCase();

        if (className.contains("explosion") || className.contains("damage") || className.contains("crit")) {
            return PRIORITY_CRITICAL;
        }

        if (className.contains("enchant") || className.contains("portal") || className.contains("flame")) {
            return PRIORITY_HIGH;
        }

        if (className.contains("block") || className.contains("item") || className.contains("dust")) {
            return PRIORITY_MEDIUM;
        }

        if (className.contains("bubble") || className.contains("splash") || className.contains("drip")) {
            return PRIORITY_LOW;
        }

        if (className.contains("ambient") || className.contains("ash") || className.contains("spore")) {
            return PRIORITY_AMBIENT;
        }

        return PRIORITY_MEDIUM;
    }

    public static boolean shouldKeep(int priority, int currentCount, int maxCount) {
        if (currentCount < maxCount * 0.5) return true;

        if (priority >= PRIORITY_CRITICAL) return true;

        if (currentCount < maxCount * 0.75) {
            return priority >= PRIORITY_HIGH;
        }

        if (currentCount < maxCount * 0.9) {
            return priority >= PRIORITY_MEDIUM;
        }

        return priority >= PRIORITY_CRITICAL;
    }
}
