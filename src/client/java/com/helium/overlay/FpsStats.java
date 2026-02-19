package com.helium.overlay;

public final class FpsStats {

    private volatile int currentFps = 0;
    private volatile int minFps = Integer.MAX_VALUE;
    private volatile int maxFps = 0;
    private volatile long lastResetTime = System.currentTimeMillis();

    private static final long RESET_INTERVAL_MS = 5000;

    public void updateFps(int fps) {
        this.currentFps = fps;

        if (fps > 0) {
            if (fps < minFps) minFps = fps;
            if (fps > maxFps) maxFps = fps;
        }

        long now = System.currentTimeMillis();
        if (now - lastResetTime >= RESET_INTERVAL_MS) {
            minFps = fps;
            maxFps = fps;
            lastResetTime = now;
        }
    }

    public int getCurrentFps() {
        return currentFps;
    }

    public int getMinFps() {
        return minFps == Integer.MAX_VALUE ? 0 : minFps;
    }

    public int getMaxFps() {
        return maxFps;
    }

    public void reset() {
        currentFps = 0;
        minFps = Integer.MAX_VALUE;
        maxFps = 0;
        lastResetTime = System.currentTimeMillis();
    }
}
