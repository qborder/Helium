package com.helium.threading;

import com.helium.HeliumClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class ParticleWorkerPool {

    private static ExecutorService executor;
    private static int threadCount;
    private static volatile boolean initialized = false;

    private ParticleWorkerPool() {}

    public static void init(int threads) {
        threadCount = threads;
        AtomicInteger counter = new AtomicInteger(0);
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "helium-particle-" + counter.getAndIncrement());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY - 1);
            return t;
        };
        executor = Executors.newFixedThreadPool(threadCount, factory);
        initialized = true;
        HeliumClient.LOGGER.info("particle pool initialized with {} threads", threadCount);
    }

    public static <T> void processParallel(List<T> items, ParticleTask<T> task) {
        if (!initialized || items.isEmpty()) return;

        int size = items.size();
        if (size < 64) {
            for (int i = 0; i < size; i++) {
                task.process(items.get(i), i);
            }
            return;
        }

        int chunkSize = Math.max(32, size / threadCount);
        int chunks = (size + chunkSize - 1) / chunkSize;
        CountDownLatch latch = new CountDownLatch(chunks);

        for (int c = 0; c < chunks; c++) {
            int start = c * chunkSize;
            int end = Math.min(start + chunkSize, size);
            executor.submit(() -> {
                try {
                    for (int i = start; i < end; i++) {
                        task.process(items.get(i), i);
                    }
                } catch (Exception e) {
                    HeliumClient.LOGGER.error("particle worker error", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @FunctionalInterface
    public interface ParticleTask<T> {
        void process(T item, int index);
    }
}
