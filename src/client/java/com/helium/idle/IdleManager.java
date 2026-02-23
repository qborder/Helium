package com.helium.idle;

import com.helium.HeliumClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class IdleManager {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicLong lastActivityTime = new AtomicLong(0);
    private static final AtomicBoolean idle = new AtomicBoolean(false);
    private static final AtomicLong lastTickTime = new AtomicLong(0);

    private static volatile int timeoutSeconds = 60;
    private static volatile int idleFpsLimit = 5;

    private static GLFWCursorPosCallback previousCursorCallback;

    private static final long TICK_DEDUP_MS = 5;

    private IdleManager() {}

    public static void init(int timeout, int fpsLimit) {
        if (initialized.getAndSet(true)) return;
        timeoutSeconds = timeout;
        idleFpsLimit = fpsLimit;
        lastActivityTime.set(Util.getMeasuringTimeMs());
        HeliumClient.LOGGER.info("idle manager initialized (timeout={}s, idle fps={})", timeout, fpsLimit);
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    public static void setWindow(long windowHandle) {
        if (!initialized.get()) return;
        previousCursorCallback = GLFW.glfwSetCursorPosCallback(windowHandle, IdleManager::onCursorMove);
    }

    private static void onCursorMove(long window, double x, double y) {
        onActivity();
        if (previousCursorCallback != null) {
            previousCursorCallback.invoke(window, x, y);
        }
    }

    public static void onActivity() {
        if (!initialized.get()) return;
        lastActivityTime.set(Util.getMeasuringTimeMs());

        if (idle.getAndSet(false)) {
            HeliumClient.LOGGER.info("activity detected, resuming normal rendering");
        }
    }

    public static void tick() {
        if (!initialized.get()) return;

        long now = Util.getMeasuringTimeMs();
        long prev = lastTickTime.get();
        if (now - prev < TICK_DEDUP_MS) return;
        lastTickTime.set(now);

        long elapsed = now - lastActivityTime.get();
        long timeoutMs = timeoutSeconds * 1000L;

        if (elapsed >= timeoutMs) {
            if (!idle.getAndSet(true)) {
                HeliumClient.LOGGER.info("no activity for {}s, entering idle mode (fps={})", timeoutSeconds, idleFpsLimit);
            }
        }
    }

    public static boolean isIdle() {
        return idle.get();
    }

    public static int getIdleFpsLimit() {
        return idleFpsLimit;
    }

    public static void setTimeoutSeconds(int timeout) {
        timeoutSeconds = Math.max(10, timeout);
    }

    public static void setIdleFpsLimit(int limit) {
        idleFpsLimit = Math.max(1, Math.min(30, limit));
    }

    public static int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public static long getTimeSinceLastActivity() {
        return Util.getMeasuringTimeMs() - lastActivityTime.get();
    }

    public static void shutdown() {
        idle.set(false);
        initialized.set(false);
    }
}
