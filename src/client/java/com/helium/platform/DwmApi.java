package com.helium.platform;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.WinBase;

public final class DwmApi {

    private static final int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;
    private static final int DWMWA_WINDOW_CORNER_PREFERENCE = 33;
    private static final int DWMWA_SYSTEMBACKDROP_TYPE = 38;

    private static final int WCA_ACCENT_POLICY = 19;
    private static final int ACCENT_DISABLED = 0;
    private static final int ACCENT_ENABLE_ACRYLICBLURBEHIND = 4;

    private static volatile long setAttrFunc = -1;
    private static volatile long setWcaFunc = -1;

    private DwmApi() {}

    private static long getSetAttrFunc() {
        if (setAttrFunc == -1) {
            try {
                long module = WinBase.LoadLibrary("dwmapi");
                setAttrFunc = module != 0 ? WinBase.GetProcAddress(module, "DwmSetWindowAttribute") : 0;
            } catch (Throwable t) {
                HeliumClient.LOGGER.debug("dwm: failed to load dwmapi - {}", t.getMessage());
                setAttrFunc = 0;
            }
        }
        return setAttrFunc;
    }

    private static long getSetWcaFunc() {
        if (setWcaFunc == -1) {
            try {
                long module = WinBase.LoadLibrary("user32");
                setWcaFunc = module != 0 ? WinBase.GetProcAddress(module, "SetWindowCompositionAttribute") : 0;
            } catch (Throwable t) {
                HeliumClient.LOGGER.debug("dwm: failed to load SetWindowCompositionAttribute - {}", t.getMessage());
                setWcaFunc = 0;
            }
        }
        return setWcaFunc;
    }

    private static void setAttribute(long hwnd, int attribute, int value) {
        long func = getSetAttrFunc();
        if (func == 0) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long pvAttr = stack.nmalloc(4, 4);
            MemoryUtil.memPutInt(pvAttr, value);
            JNI.callPPI(hwnd, attribute, pvAttr, 4, func);
        }
    }

    private static void setAccentPolicy(long hwnd, int accentState, int gradientColor) {
        long func = getSetWcaFunc();
        if (func == 0) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // ACCENT_POLICY: AccentState(4) + AccentFlags(4) + GradientColor(4) + AnimationId(4) = 16 bytes
            long policy = stack.nmalloc(4, 16);
            MemoryUtil.memPutInt(policy + 0, accentState);
            MemoryUtil.memPutInt(policy + 4, 0);
            MemoryUtil.memPutInt(policy + 8, gradientColor);
            MemoryUtil.memPutInt(policy + 12, 0);

            // WINDOWCOMPOSITIONATTRIBDATA (64-bit layout):
            // Attribute(4) + pad(4) + pvData(8) + cbData(4) + pad(4) = 24 bytes
            long data = stack.nmalloc(8, 24);
            MemoryUtil.memPutInt(data + 0, WCA_ACCENT_POLICY);
            MemoryUtil.memPutLong(data + 8, policy);
            MemoryUtil.memPutInt(data + 16, 16);

            // BOOL SetWindowCompositionAttribute(HWND, WINDOWCOMPOSITIONATTRIBDATA*)
            JNI.callPPI(hwnd, data, func);
        }
    }

    public static void applyWindowStyle(boolean fullscreen, long windowHandle) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.windowStyle) return;
        if (!WindowsVersion.isCompatible()) return;

        try {
            long hwnd = GLFWNativeWin32.glfwGetWin32Window(windowHandle);
            if (hwnd == 0) return;

            if (fullscreen) {
                resetWindowStyle(hwnd);
                return;
            }

            setAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE, 1);

            DwmEnums.WindowMaterial material = DwmEnums.WindowMaterial.fromString(config.windowMaterial);

            if (material == DwmEnums.WindowMaterial.ACRYLIC) {
                if (WindowsVersion.supportsBackdrop()) {
                    setAttribute(hwnd, DWMWA_SYSTEMBACKDROP_TYPE, DwmEnums.WindowMaterial.NONE.ordinal());
                }
                // 0x00FFFFFF = no tint, full blur (AABBGGRR format)
                setAccentPolicy(hwnd, ACCENT_ENABLE_ACRYLICBLURBEHIND, 0x00FFFFFF);
            } else {
                setAccentPolicy(hwnd, ACCENT_DISABLED, 0);
                if (WindowsVersion.supportsBackdrop()) {
                    setAttribute(hwnd, DWMWA_SYSTEMBACKDROP_TYPE, material.ordinal());
                }
            }

            DwmEnums.WindowCorner corner = DwmEnums.WindowCorner.fromString(config.windowCorner);
            setAttribute(hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, corner.ordinal());

        } catch (Throwable t) {
            HeliumClient.LOGGER.debug("dwm api failed: {}", t.getMessage());
        }
    }

    private static void resetWindowStyle(long hwnd) {
        try {
            setAccentPolicy(hwnd, ACCENT_DISABLED, 0);
            if (WindowsVersion.supportsBackdrop()) {
                setAttribute(hwnd, DWMWA_SYSTEMBACKDROP_TYPE, DwmEnums.WindowMaterial.AUTO.ordinal());
            }
            setAttribute(hwnd, DWMWA_WINDOW_CORNER_PREFERENCE, DwmEnums.WindowCorner.DEFAULT.ordinal());
        } catch (Throwable ignored) {}
    }
}
