package com.helium.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.gpu.GpuDetector;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public final class DisplaySyncOptimizer {

    private static volatile long lastDisplayUpdateTime = 0;
    private static volatile long updateIntervalNs = 0;
    private static volatile int cachedRefreshRate = 0;
    private static volatile int appliedConfigRate = 0;

    private DisplaySyncOptimizer() {}

    public static boolean shouldPerformDisplayUpdate() {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || config.displaySyncRefreshRate == 0) {
            return true;
        }

        int configRate = config.displaySyncRefreshRate;
        long interval = updateIntervalNs;
        int applied = appliedConfigRate;
        
        if (interval == 0 || applied != configRate) {
            if (configRate == -1) {
                detectRefreshRate();
            } else {
                cachedRefreshRate = configRate;
                updateIntervalNs = 1_000_000_000L / (configRate + 30);
            }
            appliedConfigRate = configRate;
            interval = updateIntervalNs;
        }

        long now = System.nanoTime();
        long lastUpdate = lastDisplayUpdateTime;
        long elapsed = now - lastUpdate;

        if (GpuDetector.isIntegratedOnly()) {
            return handleIntegratedGpu(now, elapsed, interval);
        }

        if (elapsed >= interval) {
            lastDisplayUpdateTime = now;
            return true;
        }

        return false;
    }

    private static boolean handleIntegratedGpu(long now, long elapsed, long interval) {
        if (elapsed >= interval) {
            lastDisplayUpdateTime = now;
            return true;
        }
        
        long remaining = interval - elapsed;
        if (remaining > 1_000_000L && remaining < 8_000_000L) {
            try {
                Thread.sleep(0, (int) Math.min(remaining, 999_999L));
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            lastDisplayUpdateTime = System.nanoTime();
            return true;
        }
        
        return true;
    }

    private static void detectRefreshRate() {
        try {
            long monitor = 0L;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.getWindow() != null) {
                monitor = GLFW.glfwGetWindowMonitor(client.getWindow().getHandle());
            }
            if (monitor == 0L) {
                monitor = GLFW.glfwGetPrimaryMonitor();
            }
            if (monitor != 0L) {
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
                if (vidMode != null && vidMode.refreshRate() > 0) {
                    cachedRefreshRate = vidMode.refreshRate();
                    updateIntervalNs = 1_000_000_000L / (cachedRefreshRate + 30);
                    return;
                }
            }
        } catch (Exception ignored) {}
        cachedRefreshRate = 60;
        updateIntervalNs = 1_000_000_000L / (60 + 30);
    }

    public static void reset() {
        lastDisplayUpdateTime = 0;
        updateIntervalNs = 0;
        cachedRefreshRate = 0;
        appliedConfigRate = 0;
    }

    public static int getDetectedRefreshRate() {
        return cachedRefreshRate;
    }
}
