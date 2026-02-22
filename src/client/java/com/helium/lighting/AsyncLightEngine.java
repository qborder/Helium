package com.helium.lighting;

import com.helium.HeliumClient;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class AsyncLightEngine {

    private static ExecutorService executor;
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final ConcurrentLinkedQueue<LightUpdate> pendingUpdates = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<LightUpdate> completedUpdates = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger queuedCount = new AtomicInteger(0);

    private static final int MAX_QUEUED = 1024;
    private static final int MAX_APPLY_PER_TICK = 64;

    private AsyncLightEngine() {}

    public static void init() {
        if (initialized.getAndSet(true)) return;

        AtomicInteger counter = new AtomicInteger(0);
        executor = Executors.newFixedThreadPool(
                Math.max(1, Runtime.getRuntime().availableProcessors() / 4),
                r -> {
                    Thread t = new Thread(r, "helium-light-" + counter.getAndIncrement());
                    t.setDaemon(true);
                    t.setPriority(Thread.NORM_PRIORITY - 2);
                    return t;
                }
        );

        HeliumClient.LOGGER.info("async light engine initialized");
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static boolean submitUpdate(long posKey, int lightLevel, boolean isSky) {
        if (!initialized.get() || queuedCount.get() >= MAX_QUEUED) return false;

        LightUpdate update = new LightUpdate(posKey, lightLevel, isSky);
        pendingUpdates.offer(update);
        queuedCount.incrementAndGet();

        executor.submit(() -> {
            try {
                update.processed = true;
                completedUpdates.offer(update);
            } catch (Throwable t) {
                HeliumClient.LOGGER.warn("async light update failed", t);
            }
        });

        return true;
    }

    public static int applyCompleted() {
        if (!initialized.get()) return 0;

        int applied = 0;
        LightUpdate update;
        while (applied < MAX_APPLY_PER_TICK && (update = completedUpdates.poll()) != null) {
            queuedCount.decrementAndGet();
            applied++;
        }
        return applied;
    }

    public static int getQueuedCount() {
        return queuedCount.get();
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
        pendingUpdates.clear();
        completedUpdates.clear();
        queuedCount.set(0);
        initialized.set(false);
    }

    public static class LightUpdate {
        public final long posKey;
        public final int lightLevel;
        public final boolean isSky;
        public volatile boolean processed = false;

        public LightUpdate(long posKey, int lightLevel, boolean isSky) {
            this.posKey = posKey;
            this.lightLevel = lightLevel;
            this.isSky = isSky;
        }
    }
}
