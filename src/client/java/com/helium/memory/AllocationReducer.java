package com.helium.memory;

import com.helium.HeliumClient;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayDeque;

public final class AllocationReducer {

    private static final int MAX_POOL_SIZE = 256;

    private static final ThreadLocal<ArrayDeque<Vector3d>> VEC3D_POOL = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<ArrayDeque<Vector3f>> VEC3F_POOL = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<ArrayDeque<Matrix4f>> MAT4F_POOL = ThreadLocal.withInitial(ArrayDeque::new);

    private static volatile boolean initialized = false;

    private AllocationReducer() {}

    public static void init() {
        if (initialized) return;
        initialized = true;
        HeliumClient.LOGGER.info("allocation reducer initialized");
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static Vector3d borrowVec3d() {
        if (!initialized) return new Vector3d();
        ArrayDeque<Vector3d> pool = VEC3D_POOL.get();
        Vector3d v = pool.pollFirst();
        return v != null ? v.set(0, 0, 0) : new Vector3d();
    }

    public static void returnVec3d(Vector3d v) {
        if (!initialized || v == null) return;
        ArrayDeque<Vector3d> pool = VEC3D_POOL.get();
        if (pool.size() < MAX_POOL_SIZE) {
            pool.offerFirst(v);
        }
    }

    public static Vector3f borrowVec3f() {
        if (!initialized) return new Vector3f();
        ArrayDeque<Vector3f> pool = VEC3F_POOL.get();
        Vector3f v = pool.pollFirst();
        return v != null ? v.set(0, 0, 0) : new Vector3f();
    }

    public static void returnVec3f(Vector3f v) {
        if (!initialized || v == null) return;
        ArrayDeque<Vector3f> pool = VEC3F_POOL.get();
        if (pool.size() < MAX_POOL_SIZE) {
            pool.offerFirst(v);
        }
    }

    public static Matrix4f borrowMat4f() {
        if (!initialized) return new Matrix4f();
        ArrayDeque<Matrix4f> pool = MAT4F_POOL.get();
        Matrix4f m = pool.pollFirst();
        return m != null ? m.identity() : new Matrix4f();
    }

    public static void returnMat4f(Matrix4f m) {
        if (!initialized || m == null) return;
        ArrayDeque<Matrix4f> pool = MAT4F_POOL.get();
        if (pool.size() < MAX_POOL_SIZE) {
            pool.offerFirst(m);
        }
    }
}
