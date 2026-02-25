package com.helium;

import com.helium.config.HeliumConfig;
import com.helium.gpu.AdaptiveSyncManager;
import com.helium.gpu.AmdOptimizer;
import com.helium.gpu.GpuDetector;
import com.helium.gpu.IntelOptimizer;
import com.helium.gpu.NvidiaOptimizer;
import com.helium.idle.IdleManager;
import com.helium.lighting.AsyncLightEngine;
import com.helium.math.FastMath;
import com.helium.math.SimdMath;
import com.helium.memory.AllocationReducer;
import com.helium.memory.BufferPool;
import com.helium.memory.NativeMemoryManager;
import com.helium.memory.ObjectPool;
import com.helium.network.PacketBatcher;
import com.helium.render.GLStateCache;
import com.helium.render.HeliumBlockEntityCulling;
import com.helium.render.ModelCache;
import com.helium.render.RenderPipeline;
import com.helium.render.TemporalReprojection;
import com.helium.compat.HeliumIncompatibleScreen;
import com.helium.feature.FullbrightManager;
import com.helium.resource.BackgroundResourceProcessor;
import com.helium.startup.FastStartup;
import com.helium.threading.EventPoller;
import com.helium.threading.ThreadPriorityManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class HeliumClient implements ClientModInitializer {

    public static final String MOD_ID = "helium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static HeliumConfig config;
    private static KeyBinding fullbrightKey;

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
    private static boolean modelCacheFailed = false;
    private static boolean allocationReducerFailed = false;
    private static boolean simdMathFailed = false;
    private static boolean asyncLightFailed = false;
    private static boolean packetBatcherFailed = false;
    private static boolean idleManagerFailed = false;
    private static boolean gpuDetectorFailed = false;
    private static boolean gpuOptsFailed = false;
    private static boolean adaptiveSyncFailed = false;
    private static boolean temporalReprojectionFailed = false;
    private static boolean gpuInitDeferred = true;
    private static boolean isAndroid = false;

    @Override
    public void onInitializeClient() {
        long start = System.nanoTime();

        config = HeliumConfig.load();

        isAndroid = detectAndroid();
        if (isAndroid) {
            LOGGER.warn("android detected - disabling gl state cache for compatibility");
            config.glStateCache = false;

            if (!config.androidWarningShown) {
                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    if (client.currentScreen instanceof TitleScreen && !config.androidWarningShown) {
                        client.setScreen(new HeliumIncompatibleScreen(client.currentScreen));
                    }
                });
            }

            config.save();
        }

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

        initFeatureSafely("ModelCache", () -> {
            if (config.modelCache) ModelCache.init(config.modelCacheMaxMb);
        }, () -> modelCacheFailed = true);

        initFeatureSafely("AllocationReducer", () -> {
            if (config.reducedAllocations) AllocationReducer.init();
        }, () -> allocationReducerFailed = true);

        initFeatureSafely("SimdMath", () -> {
            if (config.simdMath) SimdMath.init();
        }, () -> simdMathFailed = true);

        initFeatureSafely("AsyncLightEngine", () -> {
            if (config.asyncLightUpdates) AsyncLightEngine.init();
        }, () -> asyncLightFailed = true);

        initFeatureSafely("PacketBatcher", () -> {
            if (config.packetBatching) PacketBatcher.init();
        }, () -> packetBatcherFailed = true);

        initFeatureSafely("IdleManager", () -> {
            IdleManager.init(config.idleTimeoutSeconds, config.idleFpsLimit);
        }, () -> idleManagerFailed = true);

        initFeatureSafely("TemporalReprojection", () -> {
            if (config.temporalReprojection) TemporalReprojection.init();
        }, () -> temporalReprojectionFailed = true);

        fullbrightKey = KeyBindingHelper.registerKeyBinding(createKeyBinding(
                "helium.key.fullbright",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "helium.key.category"
        ));

        if (config.fullbright) {
            FullbrightManager.enable();
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (fullbrightKey.wasPressed()) {
                FullbrightManager.toggle();
                config.fullbright = FullbrightManager.isEnabled();
                config.save();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (gpuInitDeferred && client.getWindow() != null) {
                gpuInitDeferred = false;
                initDeferredGpuFeatures();
            }
        });

        long elapsed = (System.nanoTime() - start) / 1_000_000;
        LOGGER.info("initialized in {}ms", elapsed);
    }

    private void initDeferredGpuFeatures() {
        initFeatureSafely("GpuDetector", () -> {
            GpuDetector.init();
        }, () -> gpuDetectorFailed = true);

        initFeatureSafely("GpuOptimizations", () -> {
            if (config.nvidiaOptimizations && GpuDetector.isNvidia()) NvidiaOptimizer.init();
            if (config.amdOptimizations && GpuDetector.isAmd()) AmdOptimizer.init();
            if (config.intelOptimizations && GpuDetector.isIntel()) IntelOptimizer.init();
        }, () -> gpuOptsFailed = true);

        initFeatureSafely("AdaptiveSync", () -> {
            if (config.adaptiveSync) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null && mc.getWindow() != null) {
                    AdaptiveSyncManager.init(mc.getWindow().getHandle());
                }
            }
        }, () -> adaptiveSyncFailed = true);

        LOGGER.info("deferred gpu features initialized");
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
    public static boolean isModelCacheAvailable() { return !modelCacheFailed; }
    public static boolean isAllocationReducerAvailable() { return !allocationReducerFailed; }
    public static boolean isSimdMathAvailable() { return !simdMathFailed; }
    public static boolean isAsyncLightAvailable() { return !asyncLightFailed; }
    public static boolean isPacketBatcherAvailable() { return !packetBatcherFailed; }
    public static boolean isIdleManagerAvailable() { return !idleManagerFailed; }
    public static boolean isGpuDetectorAvailable() { return !gpuDetectorFailed; }
    public static boolean isGpuOptsAvailable() { return !gpuOptsFailed; }
    public static boolean isAdaptiveSyncAvailable() { return !adaptiveSyncFailed; }
    public static boolean isTemporalReprojectionAvailable() { return !temporalReprojectionFailed; }
    public static boolean isAndroid() { return isAndroid; }

    private static boolean detectAndroid() {
        if (new File("/system/build.prop").exists()) return true;
        if (new File("/system/app").isDirectory()) return true;

        String osArch = System.getProperty("os.arch", "").toLowerCase();
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("linux") && osArch.contains("aarch64")) {
            if (new File("/data/data").isDirectory()) return true;
        }

        return false;
    }

    private static KeyBinding createKeyBinding(String id, InputUtil.Type type, int code, String category) {
        try {
            java.lang.reflect.Method createMethod = KeyBinding.Category.class.getMethod("create", net.minecraft.util.Identifier.class);
            Object categoryObj = createMethod.invoke(null, com.helium.util.VersionCompat.createIdentifier(MOD_ID, "keys"));
            return new KeyBinding(id, type, code, (KeyBinding.Category) categoryObj);
        } catch (Throwable e1) {
            try {
                java.lang.reflect.Constructor<?> ctor = KeyBinding.class.getConstructor(String.class, InputUtil.Type.class, int.class, String.class);
                return (KeyBinding) ctor.newInstance(id, type, code, category);
            } catch (Throwable e2) {
                LOGGER.warn("failed to create keybinding, using fallback");
                return new KeyBinding(id, code, KeyBinding.Category.MISC);
            }
        }
    }
}
