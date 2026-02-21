package com.helium.network;

import com.helium.memory.BufferPool;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class BufferOptimizer {

    private static final ConcurrentLinkedQueue<byte[]> PACKET_BUFFER_POOL = new ConcurrentLinkedQueue<>();
    private static final int MAX_POOLED_BUFFERS = 256;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int TRIM_THRESHOLD = 192;

    private static final AtomicInteger pooledCount = new AtomicInteger(0);
    private static volatile long lastTrimTick = 0;
    private static final int TRIM_INTERVAL_TICKS = 200;

    private BufferOptimizer() {}

    public static byte[] borrowPacketBuffer(int minSize) {
        if (minSize <= DEFAULT_BUFFER_SIZE) {
            byte[] buf = PACKET_BUFFER_POOL.poll();
            if (buf != null) {
                pooledCount.decrementAndGet();
                return buf;
            }
            return new byte[DEFAULT_BUFFER_SIZE];
        }
        return new byte[minSize];
    }

    public static void returnPacketBuffer(byte[] buffer) {
        if (buffer != null && buffer.length == DEFAULT_BUFFER_SIZE && pooledCount.get() < MAX_POOLED_BUFFERS) {
            PACKET_BUFFER_POOL.offer(buffer);
            pooledCount.incrementAndGet();
        }
    }

    public static void tick(long currentTick) {
        if (currentTick - lastTrimTick < TRIM_INTERVAL_TICKS) return;
        lastTrimTick = currentTick;

        int count = pooledCount.get();
        if (count > TRIM_THRESHOLD) {
            int toRemove = count - TRIM_THRESHOLD;
            for (int i = 0; i < toRemove; i++) {
                byte[] removed = PACKET_BUFFER_POOL.poll();
                if (removed == null) break;
                pooledCount.decrementAndGet();
            }
        }
    }

    public static int getPooledCount() {
        return pooledCount.get();
    }

    public static ByteBuffer borrowDirectBuffer(int minSize) {
        return BufferPool.borrow(minSize);
    }

    public static void returnDirectBuffer(ByteBuffer buffer) {
        BufferPool.release(buffer);
    }
}
