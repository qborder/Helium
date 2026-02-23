package com.helium.util;

import com.helium.HeliumClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class VersionCompat {

    private static Method identifierOfMethod = null;
    private static Constructor<?> identifierConstructor = null;
    private static boolean identifierResolved = false;

    private static Method cameraPositionMethod = null;
    private static boolean cameraResolved = false;

    private VersionCompat() {}

    public static Identifier createIdentifier(String namespace, String path) {
        if (!identifierResolved) {
            identifierResolved = true;
            try {
                identifierOfMethod = Identifier.class.getMethod("of", String.class, String.class);
            } catch (NoSuchMethodException e) {
                try {
                    identifierConstructor = Identifier.class.getConstructor(String.class, String.class);
                } catch (NoSuchMethodException e2) {
                    HeliumClient.LOGGER.warn("could not resolve identifier creation method");
                }
            }
        }

        try {
            if (identifierOfMethod != null) {
                return (Identifier) identifierOfMethod.invoke(null, namespace, path);
            } else if (identifierConstructor != null) {
                return (Identifier) identifierConstructor.newInstance(namespace, path);
            }
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("identifier creation failed", t);
        }

        return Identifier.of(namespace, path);
    }

    public static Vec3d getCameraPosition(Camera camera) {
        if (!cameraResolved) {
            cameraResolved = true;
            try {
                cameraPositionMethod = Camera.class.getMethod("getCameraPos");
            } catch (NoSuchMethodException e) {
                try {
                    cameraPositionMethod = Camera.class.getMethod("getPos");
                } catch (NoSuchMethodException e2) {
                    HeliumClient.LOGGER.warn("could not resolve camera position method");
                }
            }
        }

        if (cameraPositionMethod != null) {
            try {
                return (Vec3d) cameraPositionMethod.invoke(camera);
            } catch (Throwable ignored) {}
        }

        return Vec3d.ZERO;
    }
}
