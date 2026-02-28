package com.helium.platform;

import net.minecraft.util.Util;

import java.io.File;

public final class DeviceDetector {

    private static final Util.OperatingSystem OS = Util.getOperatingSystem();
    private static final boolean ANDROID = detectAndroid();

    private DeviceDetector() {}

    private static boolean detectAndroid() {
        if (new File("/system/build.prop").exists()) return true;
        if (new File("/system/app").isDirectory()) return true;
        String arch = System.getProperty("os.arch", "").toLowerCase();
        String name = System.getProperty("os.name", "").toLowerCase();
        return name.contains("linux") && arch.contains("aarch64") && new File("/data/data").isDirectory();
    }

    public static boolean isWindows() {
        return OS == Util.OperatingSystem.WINDOWS;
    }

    public static boolean isLinux() {
        return OS == Util.OperatingSystem.LINUX && !ANDROID;
    }

    public static boolean isMacOS() {
        return OS == Util.OperatingSystem.OSX;
    }

    public static boolean isAndroid() {
        return ANDROID;
    }
}
