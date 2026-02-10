package com.helium.resource;

import com.helium.HeliumClient;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class BackgroundResourceProcessor {

    private static ExecutorService executor;
    private static boolean initialized = false;

    private BackgroundResourceProcessor() {}

    public static void init() {
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        AtomicInteger counter = new AtomicInteger(0);
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "helium-resource-" + counter.getAndIncrement());
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY - 2);
            return t;
        };
        executor = Executors.newFixedThreadPool(threads, factory);
        initialized = true;
        HeliumClient.LOGGER.info("background resource processor initialized with {} threads", threads);
    }

    public static <T> CompletableFuture<T> submitAsync(Supplier<T> task) {
        if (!initialized) init();
        return CompletableFuture.supplyAsync(task, executor);
    }

    public static CompletableFuture<Void> submitAsync(Runnable task) {
        if (!initialized) init();
        return CompletableFuture.runAsync(task, executor);
    }

    public static void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
