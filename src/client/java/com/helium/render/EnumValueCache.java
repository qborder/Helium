package com.helium.render;

import com.helium.HeliumClient;
import net.minecraft.util.math.Direction;

import java.lang.reflect.Method;

public final class EnumValueCache {

    private static volatile boolean initialized = false;

    private static Direction[] cachedDirections;
    private static Direction.Axis[] cachedAxes;
    private static Direction[] cachedHorizontal;

    private EnumValueCache() {}

    public static void init() {
        if (initialized) return;

        try {
            cachedDirections = Direction.values();
            cachedAxes = Direction.Axis.values();

            int hCount = 0;
            for (Direction d : cachedDirections) {
                if (d.getAxis().isHorizontal()) hCount++;
            }
            cachedHorizontal = new Direction[hCount];
            int idx = 0;
            for (Direction d : cachedDirections) {
                if (d.getAxis().isHorizontal()) {
                    cachedHorizontal[idx++] = d;
                }
            }

            cacheGenericEnums();

            initialized = true;
            HeliumClient.LOGGER.info("[helium] enum value cache initialized ({} directions, {} axes)",
                    cachedDirections.length, cachedAxes.length);
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("[helium] enum value cache init failed: {}", t.getMessage());
        }
    }

    private static Object[] cachedDyeColors;
    private static Object[] cachedFormattings;

    @SuppressWarnings("unchecked")
    private static void cacheGenericEnums() {
        try {
            Class<?> dyeColorClass = Class.forName("net.minecraft.util.DyeColor");
            Method valuesMethod = dyeColorClass.getMethod("values");
            cachedDyeColors = (Object[]) valuesMethod.invoke(null);
        } catch (Throwable ignored) {}

        try {
            Class<?> formattingClass = Class.forName("net.minecraft.util.Formatting");
            Method valuesMethod = formattingClass.getMethod("values");
            cachedFormattings = (Object[]) valuesMethod.invoke(null);
        } catch (Throwable ignored) {}
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static Direction[] getDirections() {
        return cachedDirections;
    }

    public static Direction.Axis[] getAxes() {
        return cachedAxes;
    }

    public static Direction[] getHorizontalDirections() {
        return cachedHorizontal;
    }

    public static Object[] getDyeColors() {
        return cachedDyeColors;
    }

    public static Object[] getFormattings() {
        return cachedFormattings;
    }
}
