package com.helium.threading;

import com.helium.HeliumClient;

public final class ThreadPriorityManager {

    private ThreadPriorityManager() {}

    public static void init() {
        try {
            Thread initThread = Thread.currentThread();
            initThread.setPriority(Thread.MAX_PRIORITY);
            HeliumClient.LOGGER.info("init thread priority set to {}", initThread.getPriority());
        } catch (SecurityException e) {
            HeliumClient.LOGGER.warn("failed to set init thread priority", e);
        }
    }

    public static void initRenderThread() {
        try {
            Thread renderThread = Thread.currentThread();
            renderThread.setPriority(Thread.MAX_PRIORITY);
            HeliumClient.LOGGER.info("render thread priority set to {}", renderThread.getPriority());
        } catch (SecurityException e) {
            HeliumClient.LOGGER.warn("failed to set render thread priority", e);
        }
    }

    public static Thread createWorker(String name, Runnable task) {
        Thread thread = new Thread(task, "helium-" + name);
        thread.setDaemon(true);
        thread.setPriority(Thread.NORM_PRIORITY - 1);
        return thread;
    }
}
