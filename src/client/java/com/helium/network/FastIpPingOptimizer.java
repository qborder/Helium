package com.helium.network;

import com.helium.HeliumClient;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public final class FastIpPingOptimizer {

    private static volatile boolean initialized = false;
    private static Field holderField;
    private static Field hostNameField;

    private FastIpPingOptimizer() {}

    public static void init() {
        try {
            holderField = InetAddress.class.getDeclaredField("holder");
            holderField.setAccessible(true);
            Object testHolder = holderField.get(InetAddress.getLoopbackAddress());
            hostNameField = testHolder.getClass().getDeclaredField("hostName");
            hostNameField.setAccessible(true);
            initialized = true;
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("fast ip ping unavailable - reflection failed", t);
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void patchAddress(InetSocketAddress socketAddr) {
        if (!initialized || socketAddr == null) return;

        InetAddress addr = socketAddr.getAddress();
        if (addr == null) return;

        try {
            Object holder = holderField.get(addr);
            if (holder != null && hostNameField.get(holder) == null) {
                hostNameField.set(holder, addr.getHostAddress());
            }
        } catch (Throwable ignored) {}
    }
}
