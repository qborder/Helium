package com.helium.render;

import com.helium.HeliumClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public final class DeferredRenderPrep {

    private static volatile boolean initialized = false;
    private static volatile boolean supported = false;

    private static int gBufferFbo = 0;
    private static int positionTexture = 0;
    private static int normalTexture = 0;
    private static int albedoTexture = 0;
    private static int depthRbo = 0;

    private static int width = 0;
    private static int height = 0;

    private DeferredRenderPrep() {}

    public static void init(int screenWidth, int screenHeight) {
        if (initialized) return;
        initialized = true;

        try {
            width = screenWidth;
            height = screenHeight;

            gBufferFbo = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBufferFbo);

            positionTexture = createTexture(width, height, GL30.GL_RGBA16F);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, positionTexture, 0);

            normalTexture = createTexture(width, height, GL30.GL_RGBA16F);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, normalTexture, 0);

            albedoTexture = createTexture(width, height, GL11.GL_RGBA8);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL11.GL_TEXTURE_2D, albedoTexture, 0);

            GL30.glDrawBuffers(new int[]{
                    GL30.GL_COLOR_ATTACHMENT0,
                    GL30.GL_COLOR_ATTACHMENT1,
                    GL30.GL_COLOR_ATTACHMENT2
            });

            depthRbo = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthRbo);
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthRbo);

            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            supported = (status == GL30.GL_FRAMEBUFFER_COMPLETE);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

            if (supported) {
                HeliumClient.LOGGER.info("deferred rendering g-buffer created ({}x{})", width, height);
            } else {
                HeliumClient.LOGGER.warn("deferred rendering g-buffer incomplete (status={})", status);
                cleanup();
            }
        } catch (Throwable t) {
            supported = false;
            HeliumClient.LOGGER.warn("deferred rendering setup failed", t);
            cleanup();
        }
    }

    private static int createTexture(int w, int h, int internalFormat) {
        int tex = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, w, h, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (java.nio.FloatBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        return tex;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isSupported() {
        return supported;
    }

    public static void bindGBuffer() {
        if (!supported) return;
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBufferFbo);
    }

    public static void unbindGBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public static int getPositionTexture() { return positionTexture; }
    public static int getNormalTexture() { return normalTexture; }
    public static int getAlbedoTexture() { return albedoTexture; }

    public static void resize(int newWidth, int newHeight) {
        if (!supported || (newWidth == width && newHeight == height)) return;
        cleanup();
        initialized = false;
        init(newWidth, newHeight);
    }

    private static void cleanup() {
        if (gBufferFbo != 0) { GL30.glDeleteFramebuffers(gBufferFbo); gBufferFbo = 0; }
        if (positionTexture != 0) { GL11.glDeleteTextures(positionTexture); positionTexture = 0; }
        if (normalTexture != 0) { GL11.glDeleteTextures(normalTexture); normalTexture = 0; }
        if (albedoTexture != 0) { GL11.glDeleteTextures(albedoTexture); albedoTexture = 0; }
        if (depthRbo != 0) { GL30.glDeleteRenderbuffers(depthRbo); depthRbo = 0; }
    }

    public static void shutdown() {
        cleanup();
        supported = false;
        initialized = false;
    }
}
