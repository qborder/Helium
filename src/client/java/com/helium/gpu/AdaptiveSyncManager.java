package com.helium.gpu;

import com.helium.HeliumClient;
import org.lwjgl.glfw.GLFW;

public final class AdaptiveSyncManager {

    private static volatile boolean initialized = false;
    private static volatile boolean adaptiveSyncDetected = false;
    private static volatile int refreshRate = 60;

    private AdaptiveSyncManager() {}

    public static void init(long windowHandle) {
        if (initialized) return;
        initialized = true;

        try {
            long monitor = GLFW.glfwGetPrimaryMonitor();
            if (monitor != 0) {
                var vidMode = GLFW.glfwGetVideoMode(monitor);
                if (vidMode != null) {
                    refreshRate = vidMode.refreshRate();
                }
            }

            adaptiveSyncDetected = detectAdaptiveSync();

            if (adaptiveSyncDetected) {
                HeliumClient.LOGGER.info("adaptive sync detected ({}hz refresh)", refreshRate);
            } else {
                HeliumClient.LOGGER.info("no adaptive sync detected ({}hz refresh)", refreshRate);
            }
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("adaptive sync detection failed", t);
        }
    }

    private static boolean detectAdaptiveSync() {
        if (GpuDetector.isNvidia()) {
            String renderer = GpuDetector.getRendererString().toLowerCase();
            return renderer.contains("gtx 10") || renderer.contains("gtx 16")
                    || renderer.contains("rtx") || renderer.contains("gtx 9");
        }

        if (GpuDetector.isAmd()) {
            return true;
        }

        return false;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isAdaptiveSyncDetected() {
        return adaptiveSyncDetected;
    }

    public static int getRefreshRate() {
        return refreshRate;
    }

    public static int getTargetFps() {
        if (adaptiveSyncDetected) {
            return refreshRate - 3;
        }
        return refreshRate;
    }
}
