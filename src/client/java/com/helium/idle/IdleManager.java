package com.helium.idle;

import com.helium.HeliumClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class IdleManager {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicLong lastActivityTime = new AtomicLong(0);
    private static final AtomicBoolean idle = new AtomicBoolean(false);
    private static final AtomicLong lastTickTime = new AtomicLong(0);

    private static volatile int timeoutSeconds = 60;
    private static volatile int idleFpsLimit = 5;
    private static volatile boolean windowFocused = true;

    private static volatile double prevCursorX = Double.NaN;
    private static volatile double prevCursorY = Double.NaN;
    private static volatile double prevPlayerX = Double.NaN;
    private static volatile double prevPlayerY = Double.NaN;
    private static volatile double prevPlayerZ = Double.NaN;
    private static volatile float prevYaw = Float.NaN;
    private static volatile float prevPitch = Float.NaN;

    private static final double CURSOR_THRESHOLD_SQ = 4.0;

    private static GLFWCursorPosCallback prevCursorCallback;
    private static GLFWKeyCallback prevKeyCallback;
    private static GLFWMouseButtonCallback prevMouseBtnCallback;
    private static GLFWWindowFocusCallback prevFocusCallback;
    private static GLFWScrollCallback prevScrollCallback;
    private static volatile long registeredWindowHandle = 0;

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

    public static void setWindow(long handle) {
        if (!initialized.get()) return;

        registeredWindowHandle = handle;
        prevCursorCallback = GLFW.glfwSetCursorPosCallback(handle, IdleManager::onCursorPos);
        prevKeyCallback = GLFW.glfwSetKeyCallback(handle, IdleManager::onKey);
        prevMouseBtnCallback = GLFW.glfwSetMouseButtonCallback(handle, IdleManager::onMouseButton);
        prevFocusCallback = GLFW.glfwSetWindowFocusCallback(handle, IdleManager::onWindowFocus);
        prevScrollCallback = GLFW.glfwSetScrollCallback(handle, IdleManager::onScroll);

        HeliumClient.LOGGER.info("idle manager: glfw callbacks registered");
    }

    private static void onCursorPos(long window, double x, double y) {
        if (!Double.isNaN(prevCursorX)) {
            double dx = x - prevCursorX;
            double dy = y - prevCursorY;
            if (dx * dx + dy * dy >= CURSOR_THRESHOLD_SQ) {
                onActivity();
            }
        }
        prevCursorX = x;
        prevCursorY = y;

        if (prevCursorCallback != null) {
            prevCursorCallback.invoke(window, x, y);
        }
    }

    private static void onKey(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
            onActivity();
        }

        if (prevKeyCallback != null) {
            prevKeyCallback.invoke(window, key, scancode, action, mods);
        }
    }

    private static void onMouseButton(long window, int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            onActivity();
        }

        if (prevMouseBtnCallback != null) {
            prevMouseBtnCallback.invoke(window, button, action, mods);
        }
    }

    private static void onWindowFocus(long window, boolean focused) {
        windowFocused = focused;
        if (focused) {
            onActivity();
        }

        if (prevFocusCallback != null) {
            prevFocusCallback.invoke(window, focused);
        }
    }

    private static void onScroll(long window, double xoffset, double yoffset) {
        onActivity();

        if (prevScrollCallback != null) {
            prevScrollCallback.invoke(window, xoffset, yoffset);
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
        if (now - prev < 50) return;
        lastTickTime.set(now);

        checkPlayerMovement();

        long lastActivity = lastActivityTime.get();
        int timeout = timeoutSeconds;
        long elapsed = now - lastActivity;
        long timeoutMs = timeout * 1000L;

        if (elapsed >= timeoutMs && !idle.getAndSet(true)) {
            HeliumClient.LOGGER.info("no activity for {}s, entering idle mode (fps={})", timeout, idleFpsLimit);
        }
    }

    private static void checkPlayerMovement() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.player == null) return;

            double px = client.player.getX();
            double py = client.player.getY();
            double pz = client.player.getZ();
            float yaw = client.player.getYaw();
            float pitch = client.player.getPitch();

            if (!Double.isNaN(prevPlayerX)) {
                double dx = px - prevPlayerX;
                double dy = py - prevPlayerY;
                double dz = pz - prevPlayerZ;
                float dyaw = yaw - prevYaw;
                float dpitch = pitch - prevPitch;

                if (dx * dx + dy * dy + dz * dz > 0.001 || Math.abs(dyaw) > 0.1f || Math.abs(dpitch) > 0.1f) {
                    onActivity();
                }
            }

            prevPlayerX = px;
            prevPlayerY = py;
            prevPlayerZ = pz;
            prevYaw = yaw;
            prevPitch = pitch;
        } catch (Throwable ignored) {}
    }

    public static boolean isIdle() {
        return idle.get();
    }

    public static boolean isWindowFocused() {
        return windowFocused;
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
        if (registeredWindowHandle != 0) {
            try {
                GLFW.glfwSetCursorPosCallback(registeredWindowHandle, prevCursorCallback);
                GLFW.glfwSetKeyCallback(registeredWindowHandle, prevKeyCallback);
                GLFW.glfwSetMouseButtonCallback(registeredWindowHandle, prevMouseBtnCallback);
                GLFW.glfwSetWindowFocusCallback(registeredWindowHandle, prevFocusCallback);
                GLFW.glfwSetScrollCallback(registeredWindowHandle, prevScrollCallback);
            } catch (Throwable ignored) {}
            registeredWindowHandle = 0;
        }
        prevCursorCallback = null;
        prevKeyCallback = null;
        prevMouseBtnCallback = null;
        prevFocusCallback = null;
        prevScrollCallback = null;
        idle.set(false);
        initialized.set(false);
    }
}
