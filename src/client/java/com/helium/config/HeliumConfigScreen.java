package com.helium.config;

import com.helium.HeliumClient;
import com.helium.idle.IdleManager;
import com.helium.render.DisplaySyncOptimizer;
import com.helium.render.FastWorldLoadingOptimizer;
import com.helium.feature.FullbrightManager;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class HeliumConfigScreen {

    private static final Path EXPORT_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("helium-export.json");

    private static final int[] DISPLAY_SYNC_HZ = {0, 60, 75, 120, 144, 165, 240, 360, 500, -1};

    private HeliumConfigScreen() {}

    public static Screen create(Screen parent) {
        HeliumConfig config = HeliumClient.getConfig();
        HeliumConfig defaults = new HeliumConfig();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("helium.name"))
                .category(buildGeneralCategory(config, defaults))
                .category(buildRenderingCategory(config, defaults))
                .category(buildMultiplayerCategory(config, defaults))
                .category(buildOverlayCategory(config, defaults))
                .category(buildAdvancedCategory(config, defaults))
                .category(buildToolsCategory(config))
                .save(config::save)
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory(HeliumConfig config, HeliumConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("helium.page.general"))
                .option(bool("helium.config.enable", "helium.config.enable.description",
                        defaults.modEnabled, () -> config.modEnabled, v -> config.modEnabled = v))
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.engine"))
                        .option(bool("helium.option.memory_optimizations", defaults.memoryOptimizations, () -> config.memoryOptimizations, v -> config.memoryOptimizations = v))
                        .option(bool("helium.option.thread_optimizations", defaults.threadOptimizations, () -> config.threadOptimizations, v -> config.threadOptimizations = v))
                        .option(bool("helium.option.fast_startup", defaults.fastStartup, () -> config.fastStartup, v -> config.fastStartup = v))
                        .option(bool("helium.option.fast_world_loading", defaults.fastWorldLoading, () -> config.fastWorldLoading, v -> {
                            config.fastWorldLoading = v;
                            if (v && FastWorldLoadingOptimizer.isInitialized()) FastWorldLoadingOptimizer.enable();
                            else FastWorldLoadingOptimizer.disable();
                        }))
                        .option(bool("helium.option.reduced_allocations", defaults.reducedAllocations, () -> config.reducedAllocations, v -> config.reducedAllocations = v))
                        .option(bool("helium.option.network_optimizations", defaults.networkOptimizations, () -> config.networkOptimizations, v -> config.networkOptimizations = v))
                        .option(bool("helium.option.auto_pause_on_idle", defaults.autoPauseOnIdle, () -> config.autoPauseOnIdle, v -> config.autoPauseOnIdle = v))
                        .option(slider("helium.option.idle_timeout", defaults.idleTimeoutSeconds, 10, 300, 10, "helium.suffix.seconds",
                                () -> config.idleTimeoutSeconds, v -> { config.idleTimeoutSeconds = v; if (IdleManager.isInitialized()) IdleManager.setTimeoutSeconds(v); }))
                        .option(slider("helium.option.idle_fps_limit", defaults.idleFpsLimit, 1, 30, 1, "helium.suffix.fps",
                                () -> config.idleFpsLimit, v -> { config.idleFpsLimit = v; if (IdleManager.isInitialized()) IdleManager.setIdleFpsLimit(v); }))
                        .option(bool("helium.option.fullbright", defaults.fullbright, () -> config.fullbright, v -> { config.fullbright = v; FullbrightManager.setEnabled(v); }))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.config.category.developer"))
                        .option(bool("helium.config.dev_mode", defaults.devMode, () -> config.devMode, v -> config.devMode = v))
                        .build())
                .build();
    }

    private static ConfigCategory buildRenderingCategory(HeliumConfig config, HeliumConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("helium.page.rendering"))
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.distance_culling"))
                        .option(bool("helium.option.entity_culling", defaults.entityCulling, () -> config.entityCulling, v -> config.entityCulling = v))
                        .option(slider("helium.option.entity_cull_distance", defaults.entityCullDistance, 16, 128, 8, "helium.suffix.blocks",
                                () -> config.entityCullDistance, v -> config.entityCullDistance = v))
                        .option(bool("helium.option.block_entity_culling", defaults.blockEntityCulling, () -> config.blockEntityCulling, v -> config.blockEntityCulling = v))
                        .option(slider("helium.option.block_entity_cull_distance", defaults.blockEntityCullDistance, 16, 96, 8, "helium.suffix.blocks",
                                () -> config.blockEntityCullDistance, v -> config.blockEntityCullDistance = v))
                        .option(bool("helium.option.particle_culling", defaults.particleCulling, () -> config.particleCulling, v -> config.particleCulling = v))
                        .option(slider("helium.option.particle_cull_distance", defaults.particleCullDistance, 8, 64, 4, "helium.suffix.blocks",
                                () -> config.particleCullDistance, v -> config.particleCullDistance = v))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.particle_optimization"))
                        .option(bool("helium.option.particle_limiting", defaults.particleLimiting, () -> config.particleLimiting, v -> config.particleLimiting = v))
                        .option(slider("helium.option.max_particles", defaults.maxParticles, 100, 5000, 100, null,
                                () -> config.maxParticles, v -> config.maxParticles = v))
                        .option(bool("helium.option.particle_priority", defaults.particlePriority, () -> config.particlePriority, v -> config.particlePriority = v))
                        .option(bool("helium.option.particle_batching", defaults.particleBatching, () -> config.particleBatching, v -> config.particleBatching = v))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.render_pipeline"))
                        .option(bool("helium.option.animation_throttling", defaults.animationThrottling, () -> config.animationThrottling, v -> config.animationThrottling = v))
                        .option(bool("helium.option.fast_math", defaults.fastMath, () -> config.fastMath, v -> config.fastMath = v))
                        .option(bool("helium.option.gl_state_cache", defaults.glStateCache, () -> config.glStateCache, v -> config.glStateCache = v))
                        .option(bool("helium.option.fast_animations", defaults.fastAnimations, () -> config.fastAnimations, v -> config.fastAnimations = v))
                        .option(bool("helium.option.cached_enum_values", defaults.cachedEnumValues, () -> config.cachedEnumValues, v -> config.cachedEnumValues = v))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.caching"))
                        .option(bool("helium.option.model_cache", defaults.modelCache, () -> config.modelCache, v -> config.modelCache = v))
                        .option(slider("helium.option.model_cache_size", defaults.modelCacheMaxMb, 16, 256, 16, "helium.suffix.mb",
                                () -> config.modelCacheMaxMb, v -> config.modelCacheMaxMb = v))
                        .build())
                .build();
    }

    private static ConfigCategory buildMultiplayerCategory(HeliumConfig config, HeliumConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("helium.page.multiplayer"))
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.server_list"))
                        .option(bool("helium.option.fast_server_ping", defaults.fastServerPing, () -> config.fastServerPing, v -> config.fastServerPing = v))
                        .option(bool("helium.option.fast_ip_ping", defaults.fastIpPing, () -> config.fastIpPing, v -> config.fastIpPing = v))
                        .option(bool("helium.option.preserve_scroll", defaults.preserveScrollOnRefresh, () -> config.preserveScrollOnRefresh, v -> config.preserveScrollOnRefresh = v))
                        .option(bool("helium.option.direct_connect_preview", defaults.directConnectPreview, () -> config.directConnectPreview, v -> config.directConnectPreview = v))
                        .build())
                .build();
    }

    private static ConfigCategory buildOverlayCategory(HeliumConfig config, HeliumConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("helium.page.overlay"))
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.fps_overlay"))
                        .option(bool("helium.option.fps_overlay", defaults.fpsOverlay, () -> config.fpsOverlay, v -> config.fpsOverlay = v))
                        .option(slider("helium.option.overlay_transparency", defaults.overlayTransparency, 0, 100, 10, "helium.suffix.percent",
                                () -> config.overlayTransparency, v -> config.overlayTransparency = v))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.overlay_content"))
                        .option(bool("helium.option.show_fps", defaults.overlayShowFps, () -> config.overlayShowFps, v -> config.overlayShowFps = v))
                        .option(bool("helium.option.show_fps_stats", defaults.overlayShowFpsMinMaxAvg, () -> config.overlayShowFpsMinMaxAvg, v -> config.overlayShowFpsMinMaxAvg = v))
                        .option(bool("helium.option.show_memory", defaults.overlayShowMemory, () -> config.overlayShowMemory, v -> config.overlayShowMemory = v))
                        .option(bool("helium.option.show_particles", defaults.overlayShowParticles, () -> config.overlayShowParticles, v -> config.overlayShowParticles = v))
                        .option(bool("helium.option.show_coordinates", defaults.overlayShowCoordinates, () -> config.overlayShowCoordinates, v -> config.overlayShowCoordinates = v))
                        .option(bool("helium.option.show_biome", defaults.overlayShowBiome, () -> config.overlayShowBiome, v -> config.overlayShowBiome = v))
                        .build())
                .build();
    }

    private static ConfigCategory buildAdvancedCategory(HeliumConfig config, HeliumConfig defaults) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("helium.page.advanced"))
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.experimental"))
                        .option(bool("helium.option.native_memory", defaults.nativeMemory, () -> config.nativeMemory, v -> config.nativeMemory = v))
                        .option(slider("helium.option.native_memory_pool_size", defaults.nativeMemoryPoolMb, 16, 256, 16, "helium.suffix.mb",
                                () -> config.nativeMemoryPoolMb, v -> config.nativeMemoryPoolMb = v))
                        .option(bool("helium.option.render_pipelining", defaults.renderPipelining, () -> config.renderPipelining, v -> config.renderPipelining = v))
                        .option(bool("helium.option.simd_math", defaults.simdMath, () -> config.simdMath, v -> config.simdMath = v))
                        .option(bool("helium.option.async_light_updates", defaults.asyncLightUpdates, () -> config.asyncLightUpdates, v -> config.asyncLightUpdates = v))
                        .option(bool("helium.option.packet_batching", defaults.packetBatching, () -> config.packetBatching, v -> config.packetBatching = v))
                        .option(bool("helium.option.temporal_reprojection", defaults.temporalReprojection, () -> config.temporalReprojection, v -> config.temporalReprojection = v))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("helium.group.gpu_specific"))
                        .option(bool("helium.option.nvidia_optimizations", defaults.nvidiaOptimizations, () -> config.nvidiaOptimizations, v -> config.nvidiaOptimizations = v))
                        .option(bool("helium.option.amd_optimizations", defaults.amdOptimizations, () -> config.amdOptimizations, v -> config.amdOptimizations = v))
                        .option(bool("helium.option.intel_optimizations", defaults.intelOptimizations, () -> config.intelOptimizations, v -> config.intelOptimizations = v))
                        .option(bool("helium.option.adaptive_sync", defaults.adaptiveSync, () -> config.adaptiveSync, v -> config.adaptiveSync = v))
                        .option(displaySyncOption(config, defaults))
                        .build())
                .build();
    }

    private static ConfigCategory buildToolsCategory(HeliumConfig config) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("helium.config.category.tools"))
                .tooltip(Text.translatable("helium.config.category.tools.tooltip"))
                .option(ButtonOption.createBuilder()
                        .name(Text.translatable("helium.config.export"))
                        .description(OptionDescription.of(Text.translatable("helium.config.export.description")))
                        .action((screen, button) -> {
                            boolean success = config.exportToFile(EXPORT_PATH);
                            HeliumClient.LOGGER.info(success ? "config exported to " + EXPORT_PATH : "config export failed");
                        })
                        .build())
                .option(ButtonOption.createBuilder()
                        .name(Text.translatable("helium.config.import"))
                        .description(OptionDescription.of(Text.translatable("helium.config.import.description")))
                        .action((screen, button) -> {
                            HeliumConfig imported = HeliumConfig.importFromFile(EXPORT_PATH);
                            if (imported != null) {
                                config.copyFrom(imported);
                                config.save();
                                HeliumClient.LOGGER.info("config imported and saved");
                            } else {
                                HeliumClient.LOGGER.warn("config import failed - file not found or invalid");
                            }
                        })
                        .build())
                .build();
    }

    private static Option<Integer> displaySyncOption(HeliumConfig config, HeliumConfig defaults) {
        int defIdx = 9;
        return Option.<Integer>createBuilder()
                .name(Text.translatable("helium.option.display_sync_optimization"))
                .description(OptionDescription.of(Text.translatable("helium.option.display_sync_optimization.tooltip")))
                .binding(defIdx, () -> {
                    int val = config.displaySyncRefreshRate;
                    for (int i = 0; i < DISPLAY_SYNC_HZ.length; i++) {
                        if (DISPLAY_SYNC_HZ[i] == val) return i;
                    }
                    return 9;
                }, v -> {
                    config.displaySyncRefreshRate = DISPLAY_SYNC_HZ[Math.min(v, DISPLAY_SYNC_HZ.length - 1)];
                    DisplaySyncOptimizer.reset();
                })
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 9)
                        .step(1)
                        .formatValue(v -> {
                            int hz = DISPLAY_SYNC_HZ[Math.min(v, DISPLAY_SYNC_HZ.length - 1)];
                            if (hz == 0) return Text.translatable("helium.option.display_sync_optimization.off");
                            if (hz == -1) return Text.translatable("helium.option.display_sync_optimization.auto");
                            return Text.of(hz + " Hz");
                        }))
                .build();
    }

    private static Option<Boolean> bool(String key, boolean def, Supplier<Boolean> get, Consumer<Boolean> set) {
        return Option.<Boolean>createBuilder()
                .name(Text.translatable(key))
                .description(OptionDescription.of(Text.translatable(key + ".tooltip")))
                .binding(def, get, set)
                .controller(BooleanControllerBuilder::create)
                .build();
    }

    private static Option<Boolean> bool(String key, String descKey, boolean def, Supplier<Boolean> get, Consumer<Boolean> set) {
        return Option.<Boolean>createBuilder()
                .name(Text.translatable(key))
                .description(OptionDescription.of(Text.translatable(descKey)))
                .binding(def, get, set)
                .controller(BooleanControllerBuilder::create)
                .build();
    }

    private static Option<Integer> slider(String key, int def, int min, int max, int step, String suffix,
                                          Supplier<Integer> get, Consumer<Integer> set) {
        return Option.<Integer>createBuilder()
                .name(Text.translatable(key))
                .description(OptionDescription.of(Text.translatable(key + ".tooltip")))
                .binding(def, get, set)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(min, max)
                        .step(step)
                        .formatValue(v -> suffix != null ? Text.translatable(suffix, v) : Text.of(String.valueOf(v))))
                .build();
    }
}
