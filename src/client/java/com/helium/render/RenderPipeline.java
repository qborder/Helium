package com.helium.render;

import com.helium.HeliumClient;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class RenderPipeline {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicLong frameCount = new AtomicLong(0);
    private static final AtomicLong lastFrameTime = new AtomicLong(0);
    private static final AtomicLong frameBudgetNs = new AtomicLong(16_666_667L);

    private static volatile long[] frameTimes = new long[60];
    private static volatile int frameIndex = 0;
    private static volatile double smoothedFrameTime = 16.67;
    private static volatile boolean adaptivePacing = true;

    private RenderPipeline() {}

    public static void init() {
        if (initialized.getAndSet(true)) return;
        lastFrameTime.set(System.nanoTime());
        HeliumClient.LOGGER.info("render pipeline initialized (frame pacing mode)");
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static void onFrameStart() {
        if (!initialized.get()) return;
        
        long now = System.nanoTime();
        long last = lastFrameTime.getAndSet(now);
        long delta = now - last;
        
        if (delta > 0 && delta < 1_000_000_000L) {
            frameTimes[frameIndex] = delta;
            frameIndex = (frameIndex + 1) % frameTimes.length;
            
            long sum = 0;
            int count = 0;
            for (long t : frameTimes) {
                if (t > 0) {
                    sum += t;
                    count++;
                }
            }
            if (count > 0) {
                smoothedFrameTime = (double) sum / count / 1_000_000.0;
            }
        }
        
        frameCount.incrementAndGet();
    }

    public static void onFrameEnd() {
        if (!initialized.get() || !adaptivePacing) return;
        
        long budget = frameBudgetNs.get();
        long elapsed = System.nanoTime() - lastFrameTime.get();
        long remaining = budget - elapsed;
        
        if (remaining > 500_000L && remaining < 8_000_000L) {
            try {
                Thread.sleep(0, (int) Math.min(remaining / 2, 999_999L));
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void setTargetFps(int fps) {
        if (fps > 0 && fps <= 1000) {
            frameBudgetNs.set(1_000_000_000L / fps);
        }
    }

    public static void setAdaptivePacing(boolean enabled) {
        adaptivePacing = enabled;
    }

    public static double getSmoothedFrameTimeMs() {
        return smoothedFrameTime;
    }

    public static double getSmoothedFps() {
        return smoothedFrameTime > 0 ? 1000.0 / smoothedFrameTime : 0;
    }

    public static long getFrameCount() {
        return frameCount.get();
    }

    public static void shutdown() {
        initialized.set(false);
        frameCount.set(0);
        lastFrameTime.set(0);
        frameTimes = new long[60];
        frameIndex = 0;
        smoothedFrameTime = 16.67;
        HeliumClient.LOGGER.info("render pipeline shutdown");
    }
}
