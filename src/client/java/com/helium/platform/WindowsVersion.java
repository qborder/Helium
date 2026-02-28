package com.helium.platform;

import com.helium.HeliumClient;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.WinBase;

public final class WindowsVersion {

    private static final int MINIMUM_BUILD = 22000;
    private static final int BACKDROP_BUILD = 22621;

    private static int majorVersion = -1;
    private static int buildNumber = -1;
    private static boolean initialized = false;

    private WindowsVersion() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        if (!DeviceDetector.isWindows()) return;

        try {
            long ntdll = WinBase.LoadLibrary("ntdll");
            if (ntdll == 0) return;

            long func = WinBase.GetProcAddress(ntdll, "RtlGetNtVersionNumbers");
            if (func == 0) return;

            try (MemoryStack stack = MemoryStack.stackPush()) {
                long majorPtr = stack.nmalloc(4, 4);
                long minorPtr = stack.nmalloc(4, 4);
                long buildPtr = stack.nmalloc(4, 4);
                MemoryUtil.memPutInt(majorPtr, 0);
                MemoryUtil.memPutInt(minorPtr, 0);
                MemoryUtil.memPutInt(buildPtr, 0);

                JNI.callPPPV(majorPtr, minorPtr, buildPtr, func);

                majorVersion = MemoryUtil.memGetInt(majorPtr);
                buildNumber = MemoryUtil.memGetInt(buildPtr) & ~0xF0000000;
            }

            HeliumClient.LOGGER.debug("windows version: {} build {}", majorVersion, buildNumber);
        } catch (Throwable t) {
            HeliumClient.LOGGER.debug("failed to detect windows version: {}", t.getMessage());
            majorVersion = -1;
            buildNumber = -1;
        }
    }

    public static boolean isCompatible() {
        if (!initialized) init();
        return majorVersion >= 10 && buildNumber >= MINIMUM_BUILD;
    }

    public static boolean supportsBackdrop() {
        if (!initialized) init();
        return buildNumber >= BACKDROP_BUILD;
    }

    public static int getBuildNumber() {
        if (!initialized) init();
        return buildNumber;
    }
}
