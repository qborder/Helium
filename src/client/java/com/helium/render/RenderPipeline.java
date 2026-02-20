package com.helium.render;

import com.helium.HeliumClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class RenderPipeline {

    private static ExecutorService cullingExecutor;
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicReference<CullingResult> pendingCullingResult = new AtomicReference<>(null);
    private static final AtomicReference<CullingResult> currentCullingResult = new AtomicReference<>(null);

    private RenderPipeline() {}

    public static void init() {
        if (initialized.getAndSet(true)) return;

        cullingExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "helium-culling");
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY - 1);
            return t;
        });

        HeliumClient.LOGGER.info("render pipeline initialized");
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static void submitCullingWork(CullingTask task) {
        if (!initialized.get() || cullingExecutor == null) return;

        CompletableFuture.supplyAsync(() -> {
            try {
                return task.compute();
            } catch (Throwable t) {
                HeliumClient.LOGGER.warn("culling task failed", t);
                return null;
            }
        }, cullingExecutor).thenAccept(result -> {
            if (result != null) {
                pendingCullingResult.set(result);
            }
        });
    }

    public static void swapBuffers() {
        CullingResult pending = pendingCullingResult.getAndSet(null);
        if (pending != null) {
            currentCullingResult.set(pending);
        }
    }

    public static CullingResult getCurrentCullingResult() {
        return currentCullingResult.get();
    }

    public static void shutdown() {
        if (cullingExecutor != null) {
            cullingExecutor.shutdownNow();
            cullingExecutor = null;
        }
        initialized.set(false);
        pendingCullingResult.set(null);
        currentCullingResult.set(null);
        HeliumClient.LOGGER.info("render pipeline shutdown");
    }

    @FunctionalInterface
    public interface CullingTask {
        CullingResult compute();
    }

    public static class CullingResult {
        public final int[] visibleEntityIds;
        public final int[] visibleBlockEntityIds;
        public final long frameId;

        public CullingResult(int[] visibleEntityIds, int[] visibleBlockEntityIds, long frameId) {
            this.visibleEntityIds = visibleEntityIds;
            this.visibleBlockEntityIds = visibleBlockEntityIds;
            this.frameId = frameId;
        }
    }
}
