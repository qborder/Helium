package com.helium.gpu;

import com.helium.HeliumClient;
import org.lwjgl.opengl.GL;

public final class AmdOptimizer {

    private static volatile boolean initialized = false;
    private static volatile boolean pinnedMemory = false;
    private static final int BUFFER_ALIGNMENT = 256;

    private AmdOptimizer() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        if (!GpuDetector.isAmd()) {
            HeliumClient.LOGGER.info("amd optimizer skipped - not an amd gpu");
            return;
        }

        try {
            if (GL.getCapabilities().GL_AMD_pinned_memory) {
                pinnedMemory = true;
                HeliumClient.LOGGER.info("amd: pinned memory extension available");
            }
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("amd: pinned memory check failed", t);
        }

        HeliumClient.LOGGER.info("amd optimizer initialized (alignment={})", BUFFER_ALIGNMENT);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static int getBufferAlignment() {
        return BUFFER_ALIGNMENT;
    }

    public static int alignSize(int size) {
        return (size + BUFFER_ALIGNMENT - 1) & ~(BUFFER_ALIGNMENT - 1);
    }

    public static boolean hasPinnedMemory() {
        return pinnedMemory;
    }
}
