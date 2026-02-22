package com.helium.gpu;

import com.helium.HeliumClient;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.KHRParallelShaderCompile;

public final class NvidiaOptimizer {

    private static volatile boolean initialized = false;
    private static volatile boolean parallelShaderCompile = false;

    private NvidiaOptimizer() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        if (!GpuDetector.isNvidia()) {
            HeliumClient.LOGGER.info("nvidia optimizer skipped - not an nvidia gpu");
            return;
        }

        try {
            if (GL.getCapabilities().GL_KHR_parallel_shader_compile) {
                KHRParallelShaderCompile.glMaxShaderCompilerThreadsKHR(0xFFFFFFFF);
                parallelShaderCompile = true;
                HeliumClient.LOGGER.info("nvidia: parallel shader compile enabled");
            }
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("nvidia: parallel shader compile unavailable", t);
        }

        HeliumClient.LOGGER.info("nvidia optimizer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean hasParallelShaderCompile() {
        return parallelShaderCompile;
    }
}
