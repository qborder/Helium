package com.helium.threading;

import com.helium.HeliumClient;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class EventPoller {

    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static final AtomicLong lastPollTime = new AtomicLong(0);
    private static long pollIntervalNanos = 1_000_000;

    private EventPoller() {}

    public static void init(int targetPollRateHz) {
        pollIntervalNanos = 1_000_000_000L / Math.max(1, targetPollRateHz);
        running.set(true);
        HeliumClient.LOGGER.info("event poller initialized at {}hz", targetPollRateHz);
    }

    public static boolean shouldPoll() {
        if (!running.get()) return true;
        long now = System.nanoTime();
        long last = lastPollTime.get();
        if (now - last >= pollIntervalNanos) {
            lastPollTime.set(now);
            return true;
        }
        return false;
    }

    public static void shutdown() {
        running.set(false);
    }
}
