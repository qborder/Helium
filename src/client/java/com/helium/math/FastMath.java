package com.helium.math;

public final class FastMath {

    private static float[] sinTable;
    private static float[] cosTable;
    private static int precision;
    private static double precisionFactor;
    private static boolean initialized = false;

    private FastMath() {}

    public static void init() {
        init(65536);
    }

    public static void init(int lutSize) {
        precision = lutSize;
        precisionFactor = lutSize / (Math.PI * 2.0);
        sinTable = new float[lutSize];
        cosTable = new float[lutSize];

        for (int i = 0; i < lutSize; i++) {
            double angle = (double) i * Math.PI * 2.0 / lutSize;
            sinTable[i] = (float) Math.sin(angle);
            cosTable[i] = (float) Math.cos(angle);
        }

        initialized = true;
    }

    public static float sin(double radians) {
        int index = (int) (radians * precisionFactor) % precision;
        if (index < 0) index += precision;
        return sinTable[index];
    }

    public static float cos(double radians) {
        int index = (int) (radians * precisionFactor) % precision;
        if (index < 0) index += precision;
        return cosTable[index];
    }

    public static double atan2(double y, double x) {
        double absX = Math.abs(x);
        double absY = Math.abs(y);
        if (absX < 1e-8 && absY < 1e-8) return 0.0;
        double a = Math.min(absX, absY) / Math.max(absX, absY);
        double s = a * a;

        double r = ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a;

        if (absY > absX) r = 1.5707963267948966 - r;
        if (x < 0) r = 3.141592653589793 - r;
        if (y < 0) r = -r;

        return r;
    }

    public static double inverseSqrt(double x) {
        double halfX = 0.5 * x;
        long i = Double.doubleToRawLongBits(x);
        i = 0x5FE6EB50C7B537A9L - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5 - halfX * x * x);
        x *= (1.5 - halfX * x * x);
        return x;
    }

    public static int min(int a, int b) {
        return a + ((b - a) & ((b - a) >> 31));
    }

    public static int max(int a, int b) {
        return a - ((a - b) & ((a - b) >> 31));
    }

    public static int abs(int a) {
        int mask = a >> 31;
        return (a ^ mask) - mask;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
