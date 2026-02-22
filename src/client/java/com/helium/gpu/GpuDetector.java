package com.helium.gpu;

import com.helium.HeliumClient;
import org.lwjgl.opengl.GL11;

public final class GpuDetector {

    public enum GpuVendor {
        NVIDIA, AMD, INTEL, UNKNOWN
    }

    private static volatile GpuVendor vendor = GpuVendor.UNKNOWN;
    private static volatile String rendererString = "";
    private static volatile boolean initialized = false;

    private GpuDetector() {}

    public static void init() {
        if (initialized) return;

        try {
            rendererString = GL11.glGetString(GL11.GL_RENDERER);
            if (rendererString == null) rendererString = "";

            String lower = rendererString.toLowerCase();
            if (lower.contains("nvidia") || lower.contains("geforce") || lower.contains("quadro") || lower.contains("rtx") || lower.contains("gtx")) {
                vendor = GpuVendor.NVIDIA;
            } else if (lower.contains("amd") || lower.contains("radeon") || lower.contains("rx ")) {
                vendor = GpuVendor.AMD;
            } else if (lower.contains("intel") || lower.contains("iris") || lower.contains("uhd") || lower.contains("hd graphics")) {
                vendor = GpuVendor.INTEL;
            }

            initialized = true;
            HeliumClient.LOGGER.info("gpu detected: {} ({})", vendor, rendererString);
        } catch (Throwable t) {
            initialized = true;
            HeliumClient.LOGGER.warn("gpu detection failed", t);
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static GpuVendor getVendor() {
        return vendor;
    }

    public static boolean isNvidia() {
        return vendor == GpuVendor.NVIDIA;
    }

    public static boolean isAmd() {
        return vendor == GpuVendor.AMD;
    }

    public static boolean isIntel() {
        return vendor == GpuVendor.INTEL;
    }

    public static String getRendererString() {
        return rendererString;
    }
}
