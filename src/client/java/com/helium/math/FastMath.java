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

    public static void batchSin(float[] input, float[] output) {
        if (!initialized || input == null || output == null) return;
        int len = Math.min(input.length, output.length);
        for (int i = 0; i < len; i++) {
            int index = (int) (input[i] * precisionFactor) % precision;
            if (index < 0) index += precision;
            output[i] = sinTable[index];
        }
    }

    public static void batchCos(float[] input, float[] output) {
        if (!initialized || input == null || output == null) return;
        int len = Math.min(input.length, output.length);
        for (int i = 0; i < len; i++) {
            int index = (int) (input[i] * precisionFactor) % precision;
            if (index < 0) index += precision;
            output[i] = cosTable[index];
        }
    }

    public static void batchSinCos(float[] angles, float[] sinOut, float[] cosOut) {
        if (!initialized || angles == null || sinOut == null || cosOut == null) return;
        int len = Math.min(angles.length, Math.min(sinOut.length, cosOut.length));
        for (int i = 0; i < len; i++) {
            int index = (int) (angles[i] * precisionFactor) % precision;
            if (index < 0) index += precision;
            sinOut[i] = sinTable[index];
            cosOut[i] = cosTable[index];
        }
    }

    public static void batchTransformPositions(float[] positions, float[] matrix4x4, float[] output) {
        if (positions == null || matrix4x4 == null || output == null) return;
        if (matrix4x4.length < 16) return;

        int vertexCount = positions.length / 3;
        for (int i = 0; i < vertexCount; i++) {
            int srcIdx = i * 3;
            int dstIdx = i * 3;

            float x = positions[srcIdx];
            float y = positions[srcIdx + 1];
            float z = positions[srcIdx + 2];

            output[dstIdx] = matrix4x4[0] * x + matrix4x4[4] * y + matrix4x4[8] * z + matrix4x4[12];
            output[dstIdx + 1] = matrix4x4[1] * x + matrix4x4[5] * y + matrix4x4[9] * z + matrix4x4[13];
            output[dstIdx + 2] = matrix4x4[2] * x + matrix4x4[6] * y + matrix4x4[10] * z + matrix4x4[14];
        }
    }

    public static void batchNormalize(float[] vectors, float[] output) {
        if (vectors == null || output == null) return;
        int vectorCount = vectors.length / 3;

        for (int i = 0; i < vectorCount; i++) {
            int idx = i * 3;
            float x = vectors[idx];
            float y = vectors[idx + 1];
            float z = vectors[idx + 2];

            float invLen = (float) inverseSqrt(x * x + y * y + z * z);
            output[idx] = x * invLen;
            output[idx + 1] = y * invLen;
            output[idx + 2] = z * invLen;
        }
    }

    public static void batchDot(float[] a, float[] b, float[] output) {
        if (a == null || b == null || output == null) return;
        int vectorCount = Math.min(a.length, b.length) / 3;

        for (int i = 0; i < vectorCount; i++) {
            int idx = i * 3;
            output[i] = a[idx] * b[idx] + a[idx + 1] * b[idx + 1] + a[idx + 2] * b[idx + 2];
        }
    }

    public static void batchLerp(float[] a, float[] b, float t, float[] output) {
        if (a == null || b == null || output == null) return;
        int len = Math.min(a.length, Math.min(b.length, output.length));
        float oneMinusT = 1.0f - t;

        for (int i = 0; i < len; i++) {
            output[i] = a[i] * oneMinusT + b[i] * t;
        }
    }
}
