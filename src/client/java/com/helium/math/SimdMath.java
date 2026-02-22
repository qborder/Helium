package com.helium.math;

import com.helium.HeliumClient;

public final class SimdMath {

    private static volatile boolean initialized = false;
    private static volatile boolean vectorApiAvailable = false;

    private SimdMath() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            Class.forName("jdk.incubator.vector.FloatVector");
            vectorApiAvailable = true;
            HeliumClient.LOGGER.info("simd math initialized - vector api available");
        } catch (ClassNotFoundException e) {
            vectorApiAvailable = false;
            HeliumClient.LOGGER.info("simd math initialized - vector api not available, using scalar fallback");
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isVectorApiAvailable() {
        return vectorApiAvailable;
    }

    public static void batchTransformPositions(float[] positions, float offsetX, float offsetY, float offsetZ, int count) {
        if (!initialized) return;

        int limit = Math.min(count * 3, positions.length);
        for (int i = 0; i < limit; i += 3) {
            positions[i] += offsetX;
            positions[i + 1] += offsetY;
            positions[i + 2] += offsetZ;
        }
    }

    public static void batchNormalize(float[] vectors, int count) {
        if (!initialized) return;

        int limit = Math.min(count * 3, vectors.length);
        for (int i = 0; i < limit; i += 3) {
            float x = vectors[i];
            float y = vectors[i + 1];
            float z = vectors[i + 2];
            float lenSq = x * x + y * y + z * z;
            if (lenSq > 1e-8f) {
                float invLen = FastMath.isInitialized()
                        ? (float) FastMath.inverseSqrt(lenSq)
                        : (float) (1.0 / Math.sqrt(lenSq));
                vectors[i] = x * invLen;
                vectors[i + 1] = y * invLen;
                vectors[i + 2] = z * invLen;
            }
        }
    }

    public static void batchMultiply(float[] a, float[] b, float[] result, int count) {
        if (!initialized) return;

        int limit = Math.min(count, Math.min(a.length, Math.min(b.length, result.length)));
        for (int i = 0; i < limit; i++) {
            result[i] = a[i] * b[i];
        }
    }

    public static float batchDot(float[] a, float[] b, int count) {
        if (!initialized) return 0f;

        float sum = 0f;
        int limit = Math.min(count, Math.min(a.length, b.length));
        for (int i = 0; i < limit; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
}
