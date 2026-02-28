package com.helium.memory;

import com.helium.HeliumClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

public final class NativeMemoryManager {

    private static final int[] POOL_SIZES = {1024, 4096, 16384, 65536, 262144, 1048576};

    @SuppressWarnings("unchecked")
    private static final ConcurrentLinkedDeque<ByteBuffer>[] POOLS = new ConcurrentLinkedDeque[POOL_SIZES.length];

    private static final ConcurrentHashMap<Long, AllocationInfo> allocations = new ConcurrentHashMap<>();
    private static final AtomicLong cleanupCounter = new AtomicLong(0);
    private static final int CLEANUP_INTERVAL = 1000;
    private static final AtomicLong allocationIdCounter = new AtomicLong(0);
    private static final AtomicLong totalAllocatedBytes = new AtomicLong(0);
    private static final AtomicLong totalPooledBytes = new AtomicLong(0);

    private static volatile long maxMemoryBytes = 64L * 1024 * 1024;
    private static volatile boolean initialized = false;

    private NativeMemoryManager() {}

    public static void init(int maxMemoryMb) {
        maxMemoryBytes = (long) maxMemoryMb * 1024 * 1024;

        for (int i = 0; i < POOLS.length; i++) {
            POOLS[i] = new ConcurrentLinkedDeque<>();
        }

        initialized = true;
        HeliumClient.LOGGER.info("native memory manager initialized with {}MB limit", maxMemoryMb);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static ByteBuffer allocate(int size) {
        if (!initialized) return null;

        int poolIndex = findPoolIndex(size);
        int actualSize = poolIndex >= 0 ? POOL_SIZES[poolIndex] : size;

        if (totalAllocatedBytes.get() + actualSize > maxMemoryBytes) {
            evictFromPools(actualSize);
            if (totalAllocatedBytes.get() + actualSize > maxMemoryBytes) {
                HeliumClient.LOGGER.warn("native memory limit reached, cannot allocate {} bytes", size);
                return null;
            }
        }

        ByteBuffer buffer = null;

        if (poolIndex >= 0) {
            buffer = POOLS[poolIndex].pollFirst();
            if (buffer != null) {
                totalPooledBytes.addAndGet(-buffer.capacity());
                buffer.clear();
            }
        }

        if (buffer == null) {
            try {
                buffer = ByteBuffer.allocateDirect(actualSize).order(ByteOrder.nativeOrder());
                totalAllocatedBytes.addAndGet(actualSize);
            } catch (OutOfMemoryError e) {
                HeliumClient.LOGGER.error("failed to allocate {} bytes of native memory", actualSize);
                return null;
            }
        }

        long id = allocationIdCounter.incrementAndGet();
        allocations.put(id, new AllocationInfo(new WeakReference<>(buffer), actualSize, poolIndex));

        return buffer;
    }

    public static void free(ByteBuffer buffer) {
        if (!initialized || buffer == null || !buffer.isDirect()) return;

        Long idToRemove = null;
        AllocationInfo info = null;

        for (var entry : allocations.entrySet()) {
            AllocationInfo ai = entry.getValue();
            ByteBuffer ref = ai.bufferRef.get();
            if (ref == buffer) {
                idToRemove = entry.getKey();
                info = ai;
                break;
            }
        }

        if (idToRemove != null) {
            allocations.remove(idToRemove);
        }

        if (info != null && info.poolIndex >= 0) {
            buffer.clear();
            POOLS[info.poolIndex].offerFirst(buffer);
            totalPooledBytes.addAndGet(info.size);
        }

        if (cleanupCounter.incrementAndGet() % CLEANUP_INTERVAL == 0) {
            cleanupStaleAllocations();
        }
    }

    private static void cleanupStaleAllocations() {
        Iterator<Map.Entry<Long, AllocationInfo>> it = allocations.entrySet().iterator();
        while (it.hasNext()) {
            AllocationInfo info = it.next().getValue();
            if (info.bufferRef.get() == null) {
                it.remove();
            }
        }
    }

    private static int findPoolIndex(int size) {
        for (int i = 0; i < POOL_SIZES.length; i++) {
            if (POOL_SIZES[i] >= size) {
                return i;
            }
        }
        return -1;
    }

    private static void evictFromPools(long bytesNeeded) {
        long evicted = 0;
        for (int i = POOL_SIZES.length - 1; i >= 0 && evicted < bytesNeeded; i--) {
            ByteBuffer buf;
            while ((buf = POOLS[i].pollLast()) != null && evicted < bytesNeeded) {
                evicted += buf.capacity();
                totalPooledBytes.addAndGet(-buf.capacity());
                totalAllocatedBytes.addAndGet(-buf.capacity());
            }
        }
    }

    public static ByteBuffer allocateAligned(int size, int alignment) {
        int alignedSize = ((size + alignment - 1) / alignment) * alignment;
        return allocate(alignedSize);
    }

    public static void copy(ByteBuffer src, ByteBuffer dst, int length) {
        if (src == null || dst == null) return;
        int srcPos = src.position();
        int dstPos = dst.position();
        int copyLen = Math.min(length, Math.min(src.remaining(), dst.remaining()));

        for (int i = 0; i < copyLen; i++) {
            dst.put(dstPos + i, src.get(srcPos + i));
        }
    }

    public static void zero(ByteBuffer buffer) {
        if (buffer == null) return;
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(i, (byte) 0);
        }
    }

    public static long getTotalAllocatedBytes() {
        return totalAllocatedBytes.get();
    }

    public static long getTotalPooledBytes() {
        return totalPooledBytes.get();
    }

    public static long getActiveBytes() {
        return totalAllocatedBytes.get() - totalPooledBytes.get();
    }

    public static long getMaxMemoryBytes() {
        return maxMemoryBytes;
    }

    public static int getAllocationCount() {
        return allocations.size();
    }

    public static void shutdown() {
        for (int i = 0; i < POOLS.length; i++) {
            if (POOLS[i] != null) {
                POOLS[i].clear();
            }
        }
        allocations.clear();
        totalAllocatedBytes.set(0);
        totalPooledBytes.set(0);
        initialized = false;
        HeliumClient.LOGGER.info("native memory manager shutdown");
    }

    public static double getUsagePercent() {
        if (maxMemoryBytes <= 0) return 0;
        return (double) getActiveBytes() / maxMemoryBytes * 100.0;
    }

    public static String getStats() {
        return String.format("Native Memory: %dMB active / %dMB pooled / %dMB limit (%d allocations)",
            getActiveBytes() / (1024 * 1024),
            getTotalPooledBytes() / (1024 * 1024),
            maxMemoryBytes / (1024 * 1024),
            getAllocationCount());
    }

    private record AllocationInfo(WeakReference<ByteBuffer> bufferRef, int size, int poolIndex) {}
}
