package com.helium.feature;

import com.helium.HeliumClient;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.atomic.AtomicBoolean;

public final class FullbrightManager {

    private static final AtomicBoolean enabled = new AtomicBoolean(false);
    private static volatile double originalGamma = 1.0;
    private static final double FULLBRIGHT_GAMMA = 16.0;

    private FullbrightManager() {}

    public static void toggle() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        if (enabled.get()) {
            disable();
        } else {
            enable();
        }
    }

    public static void enable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        try {
            originalGamma = client.options.getGamma().getValue();
        } catch (Throwable ignored) {
            originalGamma = 1.0;
        }
        enabled.set(true);
        applyGamma(FULLBRIGHT_GAMMA);
        HeliumClient.LOGGER.info("fullbright enabled");
    }

    public static void disable() {
        enabled.set(false);
        applyGamma(originalGamma);
        HeliumClient.LOGGER.info("fullbright disabled");
    }

    public static void setEnabled(boolean state) {
        if (state && !enabled.get()) {
            enable();
        } else if (!state && enabled.get()) {
            disable();
        }
    }

    public static boolean isEnabled() {
        return enabled.get();
    }

    private static void applyGamma(double value) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.options == null) return;
            client.options.getGamma().setValue(value);
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("failed to set gamma", t);
        }
    }
}
