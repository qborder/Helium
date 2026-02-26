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
        if (updateIntervalNs == 0 || appliedConfigRate != configRate) {
            if (configRate == -1) {
                detectRefreshRate();
            } else {
                cachedRefreshRate = configRate;
                updateIntervalNs = 1_000_000_000L / (configRate + 30);
            }
            appliedConfigRate = configRate;
        }

        if (GpuDetector.isIntegratedOnly()) {
            return handleIntegratedGpu();
        }

        long now = System.nanoTime();
        long elapsed = now - lastDisplayUpdateTime;

        if (elapsed >= updateIntervalNs) {
            lastDisplayUpdateTime = now;
            return true;
        }

        return false;
    }

    private static boolean handleIntegratedGpu() {
        long now = System.nanoTime();
        long elapsed = now - lastDisplayUpdateTime;
        
        if (elapsed >= updateIntervalNs) {
            lastDisplayUpdateTime = now;
            return true;
        }
        
        long remaining = updateIntervalNs - elapsed;
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
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.getWindow() != null) {
                long monitor = GLFW.glfwGetWindowMonitor(client.getWindow().getHandle());
                if (monitor == 0L) {
                    monitor = GLFW.glfwGetPrimaryMonitor();
                }
                if (monitor != 0L) {
                    GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
                    if (vidMode != null) {
                        cachedRefreshRate = vidMode.refreshRate();
                        updateIntervalNs = 1_000_000_000L / (cachedRefreshRate + 30);
                        return;
                    }
                }
            }
        } catch (Exception ignored) {}
        cachedRefreshRate = 60;
        updateIntervalNs = 16_666_667L;
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
