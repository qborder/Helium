package com.helium.network;

import com.helium.memory.BufferPool;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BufferOptimizer {

    private static final ConcurrentLinkedQueue<byte[]> PACKET_BUFFER_POOL = new ConcurrentLinkedQueue<>();
    private static final int MAX_POOLED_BUFFERS = 256;
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private static int pooledCount = 0;

    private BufferOptimizer() {}

    public static byte[] borrowPacketBuffer(int minSize) {
        if (minSize <= DEFAULT_BUFFER_SIZE) {
            byte[] buf = PACKET_BUFFER_POOL.poll();
            if (buf != null) {
                pooledCount--;
                return buf;
            }
            return new byte[DEFAULT_BUFFER_SIZE];
        }
        return new byte[minSize];
    }

    public static void returnPacketBuffer(byte[] buffer) {
        if (buffer != null && buffer.length == DEFAULT_BUFFER_SIZE && pooledCount < MAX_POOLED_BUFFERS) {
            PACKET_BUFFER_POOL.offer(buffer);
            pooledCount++;
        }
    }

    public static ByteBuffer borrowDirectBuffer(int minSize) {
        return BufferPool.borrow(minSize);
    }

    public static void returnDirectBuffer(ByteBuffer buffer) {
        BufferPool.release(buffer);
    }
}
