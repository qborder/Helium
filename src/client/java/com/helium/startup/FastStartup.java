package com.helium.startup;

import com.helium.HeliumClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class FastStartup {

    private static ExecutorService startupPool;
    private static final List<Future<?>> pendingTasks = new ArrayList<>();

    private FastStartup() {}

    public static void init() {
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        AtomicInteger counter = new AtomicInteger(0);
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "helium-startup-" + counter.getAndIncrement());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY + 1);
            return t;
        };
        startupPool = Executors.newFixedThreadPool(threads, factory);
        HeliumClient.LOGGER.info("fast startup pool initialized with {} threads", threads);
    }

    public static <T> CompletableFuture<T> submit(Supplier<T> task) {
        if (startupPool == null) init();
        return CompletableFuture.supplyAsync(task, startupPool);
    }

    public static CompletableFuture<Void> submit(Runnable task) {
        if (startupPool == null) init();
        return CompletableFuture.runAsync(task, startupPool);
    }

    public static void awaitAll(long timeoutMs) {
        if (startupPool == null) return;
        startupPool.shutdown();
        try {
            if (!startupPool.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS)) {
                HeliumClient.LOGGER.warn("startup tasks did not complete within {}ms", timeoutMs);
                startupPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            startupPool.shutdownNow();
        }
        startupPool = null;
        pendingTasks.clear();
    }
}
