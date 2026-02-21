package com.helium;

import com.helium.config.HeliumConfig;
import com.helium.math.FastMath;
import com.helium.memory.BufferPool;
import com.helium.memory.NativeMemoryManager;
import com.helium.memory.ObjectPool;
import com.helium.render.GLStateCache;
import com.helium.render.HeliumBlockEntityCulling;
import com.helium.render.RenderPipeline;
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

    private static boolean fastMathFailed = false;
    private static boolean memoryOptsFailed = false;
    private static boolean glStateCacheFailed = false;
    private static boolean threadOptsFailed = false;
    private static boolean networkOptsFailed = false;
    private static boolean startupOptsFailed = false;
    private static boolean nativeMemoryFailed = false;
    private static boolean renderPipelineFailed = false;

    @Override
    public void onInitializeClient() {
        long start = System.nanoTime();

        config = HeliumConfig.load();

        if (!config.modEnabled) {
            LOGGER.info("helium is disabled via config");
            return;
        }

        detectCompatibleMods();

        initFeatureSafely("FastMath", () -> {
            if (config.fastMath) FastMath.init();
        }, () -> fastMathFailed = true);

        initFeatureSafely("MemoryOptimizations", () -> {
            if (config.memoryOptimizations) {
                ObjectPool.init(512);
                BufferPool.init(64);
            }
        }, () -> memoryOptsFailed = true);

        initFeatureSafely("GLStateCache", () -> {
            if (config.glStateCache && !hasImmediatelyFast) {
                GLStateCache.init();
            } else if (hasImmediatelyFast) {
                LOGGER.info("gl state cache disabled - ImmediatelyFast handles this");
            }
        }, () -> glStateCacheFailed = true);

        initFeatureSafely("ThreadOptimizations", () -> {
            if (config.threadOptimizations) {
                ThreadPriorityManager.init();
                EventPoller.init(1000);
            }
        }, () -> threadOptsFailed = true);

        initFeatureSafely("NetworkOptimizations", () -> {
            if (config.networkOptimizations) {
                BackgroundResourceProcessor.init();
            }
        }, () -> networkOptsFailed = true);

        initFeatureSafely("FastStartup", () -> {
            if (config.fastStartup) FastStartup.init();
        }, () -> startupOptsFailed = true);

        initFeatureSafely("NativeMemory", () -> {
            if (config.nativeMemory) NativeMemoryManager.init(config.nativeMemoryPoolMb);
        }, () -> nativeMemoryFailed = true);

        initFeatureSafely("RenderPipeline", () -> {
            if (config.renderPipelining) RenderPipeline.init();
        }, () -> renderPipelineFailed = true);

        initFeatureSafely("BlockEntityCulling", () -> {
            if (config.blockEntityCulling && !HeliumBlockEntityCulling.isRegistered()) {
                HeliumBlockEntityCulling.register();
            }
        }, () -> LOGGER.warn("block entity culling fallback failed, will rely on mixin"));

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

    private void initFeatureSafely(String name, Runnable init, Runnable onFailure) {
        try {
            init.run();
        } catch (Throwable t) {
            LOGGER.error("{} failed to initialize, feature disabled", name, t);
            onFailure.run();
        }
    }

    public static boolean isFastMathAvailable() { return !fastMathFailed; }
    public static boolean isMemoryOptsAvailable() { return !memoryOptsFailed; }
    public static boolean isGlStateCacheAvailable() { return !glStateCacheFailed; }
    public static boolean isThreadOptsAvailable() { return !threadOptsFailed; }
    public static boolean isNetworkOptsAvailable() { return !networkOptsFailed; }
    public static boolean isStartupOptsAvailable() { return !startupOptsFailed; }
    public static boolean isNativeMemoryAvailable() { return !nativeMemoryFailed; }
    public static boolean isRenderPipelineAvailable() { return !renderPipelineFailed; }
}
