package com.helium;

import com.helium.config.HeliumConfig;
import com.helium.math.FastMath;
import com.helium.memory.BufferPool;
import com.helium.memory.ObjectPool;
import com.helium.render.GLStateCache;
import com.helium.resource.BackgroundResourceProcessor;
import com.helium.startup.FastStartup;
import com.helium.threading.EventPoller;
import com.helium.threading.ThreadPriorityManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeliumClient implements ClientModInitializer {

    public static final String MOD_ID = "helium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static HeliumConfig config;

    private static boolean hasLithium = false;
    private static boolean hasIris = false;
    private static boolean hasImmediatelyFast = false;

    @Override
    public void onInitializeClient() {
        long start = System.nanoTime();

        config = HeliumConfig.load();

        if (!config.modEnabled) {
            LOGGER.info("helium is disabled via config");
            return;
        }

        detectCompatibleMods();

        if (config.fastMath) {
            FastMath.init();
        }

        if (config.memoryOptimizations) {
            ObjectPool.init(512);
            BufferPool.init(64);
        }

        if (config.glStateCache && !hasImmediatelyFast) {
            GLStateCache.init();
        } else if (hasImmediatelyFast) {
            LOGGER.info("gl state cache disabled - ImmediatelyFast handles this");
        }

        if (config.threadOptimizations) {
            ThreadPriorityManager.init();
            EventPoller.init(1000);
        }

        if (config.networkOptimizations) {
            BackgroundResourceProcessor.init();
        }

        if (config.fastStartup) {
            FastStartup.init();
        }

        long elapsed = (System.nanoTime() - start) / 1_000_000;
        LOGGER.info("initialized in {}ms", elapsed);
    }

    private void detectCompatibleMods() {
        FabricLoader loader = FabricLoader.getInstance();
        hasLithium = loader.isModLoaded("lithium");
        hasIris = loader.isModLoaded("iris");
        hasImmediatelyFast = loader.isModLoaded("immediatelyfast");

        if (hasLithium) LOGGER.info("lithium detected - compatible");
        if (hasIris) LOGGER.info("iris detected - compatible");
        if (hasImmediatelyFast) LOGGER.info("immediatelyfast detected - disabling gl state cache");
    }

    public static boolean hasImmediatelyFast() { return hasImmediatelyFast; }
    public static boolean hasLithium() { return hasLithium; }
    public static boolean hasIris() { return hasIris; }

    public static HeliumConfig getConfig() {
        return config;
    }
}
