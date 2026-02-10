package com.helium.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;

public final class BufferPool {

    private static final int[] BUCKET_SIZES = {256, 1024, 4096, 16384, 65536, 262144};
    @SuppressWarnings("unchecked")
    private static final ArrayDeque<ByteBuffer>[] BUCKETS = new ArrayDeque[BUCKET_SIZES.length];

    private static int maxPerBucket = 64;

    private BufferPool() {}

    public static void init(int poolSize) {
        maxPerBucket = poolSize;
        for (int i = 0; i < BUCKETS.length; i++) {
            BUCKETS[i] = new ArrayDeque<>(maxPerBucket);
        }
    }

    public static ByteBuffer borrow(int minCapacity) {
        int bucketIndex = findBucket(minCapacity);
        if (bucketIndex >= 0 && BUCKETS[bucketIndex] != null) {
            ByteBuffer buf = BUCKETS[bucketIndex].pollFirst();
            if (buf != null) {
                buf.clear();
                return buf;
            }
        }

        int size = bucketIndex >= 0 ? BUCKET_SIZES[bucketIndex] : minCapacity;
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public static void release(ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect()) return;

        int capacity = buffer.capacity();
        int bucketIndex = findExactBucket(capacity);
        if (bucketIndex >= 0 && BUCKETS[bucketIndex] != null) {
            if (BUCKETS[bucketIndex].size() < maxPerBucket) {
                buffer.clear();
                BUCKETS[bucketIndex].offerFirst(buffer);
            }
        }
    }

    private static int findBucket(int minCapacity) {
        for (int i = 0; i < BUCKET_SIZES.length; i++) {
            if (BUCKET_SIZES[i] >= minCapacity) {
                return i;
            }
        }
        return -1;
    }

    private static int findExactBucket(int capacity) {
        for (int i = 0; i < BUCKET_SIZES.length; i++) {
            if (BUCKET_SIZES[i] == capacity) {
                return i;
            }
        }
        return -1;
    }
}
