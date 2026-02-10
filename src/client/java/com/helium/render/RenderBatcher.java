package com.helium.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public final class RenderBatcher {

    private static final int INITIAL_BUFFER_SIZE = 65536;
    private static final int MAX_BATCH_SIZE = 4096;

    private static ByteBuffer vertexBuffer;
    private static int vertexCount = 0;
    private static int currentTexture = -1;
    private static boolean batching = false;

    private RenderBatcher() {}

    public static void beginBatch(int textureId) {
        if (vertexBuffer == null) {
            vertexBuffer = ByteBuffer.allocateDirect(INITIAL_BUFFER_SIZE).order(ByteOrder.nativeOrder());
        }
        vertexBuffer.clear();
        vertexCount = 0;
        currentTexture = textureId;
        batching = true;
    }

    public static boolean isBatching() {
        return batching;
    }

    public static boolean canBatch(int textureId) {
        return batching && textureId == currentTexture && vertexCount < MAX_BATCH_SIZE;
    }

    public static void addVertex(float x, float y, float z, float u, float v, int color) {
        if (!batching || vertexCount >= MAX_BATCH_SIZE) return;
        if (vertexBuffer.remaining() < 24) {
            growBuffer();
        }
        vertexBuffer.putFloat(x);
        vertexBuffer.putFloat(y);
        vertexBuffer.putFloat(z);
        vertexBuffer.putFloat(u);
        vertexBuffer.putFloat(v);
        vertexBuffer.putInt(color);
        vertexCount++;
    }

    public static int getVertexCount() {
        return vertexCount;
    }

    public static ByteBuffer getBuffer() {
        if (vertexBuffer != null) {
            vertexBuffer.flip();
        }
        return vertexBuffer;
    }

    public static void endBatch() {
        batching = false;
        vertexCount = 0;
        currentTexture = -1;
    }

    private static void growBuffer() {
        int newCapacity = vertexBuffer.capacity() * 2;
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(newCapacity).order(ByteOrder.nativeOrder());
        vertexBuffer.flip();
        newBuffer.put(vertexBuffer);
        vertexBuffer = newBuffer;
    }
}
