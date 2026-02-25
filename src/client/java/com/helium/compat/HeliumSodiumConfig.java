package com.helium.compat;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.util.VersionCompat;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.structure.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HeliumSodiumConfig implements ConfigEntryPoint {

    private static final String NAMESPACE = "helium";

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null) return;

        StorageEventHandler storage = config::save;

        ModOptionsBuilder mod = builder.registerModOptions(NAMESPACE);
        mod.setName("Helium");
        mod.setIcon(VersionCompat.createIdentifier(NAMESPACE, "textures/icon-only.png"));

        OptionPageBuilder renderPage = builder.createOptionPage();
        renderPage.setName(Text.translatable("helium.page.rendering"));

        OptionGroupBuilder cullingGroup = builder.createOptionGroup();
        cullingGroup.setName(Text.translatable("helium.group.distance_culling"));

        BooleanOptionBuilder entityCull = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "entity_culling"));
        entityCull.setName(Text.translatable("helium.option.entity_culling"));
        entityCull.setTooltip(Text.translatable("helium.option.entity_culling.tooltip"));
        entityCull.setImpact(OptionImpact.MEDIUM);
        entityCull.setDefaultValue(true);
        entityCull.setStorageHandler(storage);
        entityCull.setBinding(v -> config.entityCulling = v, () -> config.entityCulling);
        cullingGroup.addOption(entityCull);

        IntegerOptionBuilder entityDist = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "entity_cull_distance"));
        entityDist.setName(Text.translatable("helium.option.entity_cull_distance"));
        entityDist.setTooltip(Text.translatable("helium.option.entity_cull_distance.tooltip"));
        entityDist.setImpact(OptionImpact.MEDIUM);
        entityDist.setDefaultValue(64);
        entityDist.setRange(16, 128, 8);
        entityDist.setValueFormatter(v -> Text.translatable("helium.suffix.blocks", v));
        entityDist.setStorageHandler(storage);
        entityDist.setBinding(v -> config.entityCullDistance = v, () -> config.entityCullDistance);
        cullingGroup.addOption(entityDist);

        BooleanOptionBuilder blockEntityCull = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "block_entity_culling"));
        blockEntityCull.setName(Text.translatable("helium.option.block_entity_culling"));
        blockEntityCull.setTooltip(Text.translatable("helium.option.block_entity_culling.tooltip"));
        blockEntityCull.setImpact(OptionImpact.HIGH);
        blockEntityCull.setDefaultValue(true);
        blockEntityCull.setStorageHandler(storage);
        blockEntityCull.setBinding(v -> config.blockEntityCulling = v, () -> config.blockEntityCulling);
        cullingGroup.addOption(blockEntityCull);

        IntegerOptionBuilder blockEntityDist = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "block_entity_cull_distance"));
        blockEntityDist.setName(Text.translatable("helium.option.block_entity_cull_distance"));
        blockEntityDist.setTooltip(Text.translatable("helium.option.block_entity_cull_distance.tooltip"));
        blockEntityDist.setImpact(OptionImpact.HIGH);
        blockEntityDist.setDefaultValue(48);
        blockEntityDist.setRange(16, 96, 8);
        blockEntityDist.setValueFormatter(v -> Text.translatable("helium.suffix.blocks", v));
        blockEntityDist.setStorageHandler(storage);
        blockEntityDist.setBinding(v -> config.blockEntityCullDistance = v, () -> config.blockEntityCullDistance);
        cullingGroup.addOption(blockEntityDist);

        BooleanOptionBuilder particleCull = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_culling"));
        particleCull.setName(Text.translatable("helium.option.particle_culling"));
        particleCull.setTooltip(Text.translatable("helium.option.particle_culling.tooltip"));
        particleCull.setImpact(OptionImpact.MEDIUM);
        particleCull.setDefaultValue(true);
        particleCull.setStorageHandler(storage);
        particleCull.setBinding(v -> config.particleCulling = v, () -> config.particleCulling);
        cullingGroup.addOption(particleCull);

        IntegerOptionBuilder particleDist = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "particle_cull_distance"));
        particleDist.setName(Text.translatable("helium.option.particle_cull_distance"));
        particleDist.setTooltip(Text.translatable("helium.option.particle_cull_distance.tooltip"));
        particleDist.setImpact(OptionImpact.MEDIUM);
        particleDist.setDefaultValue(32);
        particleDist.setRange(8, 64, 4);
        particleDist.setValueFormatter(v -> Text.translatable("helium.suffix.blocks", v));
        particleDist.setStorageHandler(storage);
        particleDist.setBinding(v -> config.particleCullDistance = v, () -> config.particleCullDistance);
        cullingGroup.addOption(particleDist);

        renderPage.addOptionGroup(cullingGroup);

        OptionGroupBuilder particleGroup = builder.createOptionGroup();
        particleGroup.setName(Text.translatable("helium.group.particle_optimization"));

        BooleanOptionBuilder particleLimit = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_limiting"));
        particleLimit.setName(Text.translatable("helium.option.particle_limiting"));
        particleLimit.setTooltip(Text.translatable("helium.option.particle_limiting.tooltip"));
        particleLimit.setImpact(OptionImpact.HIGH);
        particleLimit.setDefaultValue(true);
        particleLimit.setStorageHandler(storage);
        particleLimit.setBinding(v -> config.particleLimiting = v, () -> config.particleLimiting);
        particleGroup.addOption(particleLimit);

        IntegerOptionBuilder maxParticles = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "max_particles"));
        maxParticles.setName(Text.translatable("helium.option.max_particles"));
        maxParticles.setTooltip(Text.translatable("helium.option.max_particles.tooltip"));
        maxParticles.setImpact(OptionImpact.HIGH);
        maxParticles.setDefaultValue(1000);
        maxParticles.setRange(100, 5000, 100);
        maxParticles.setValueFormatter(v -> Text.of(String.valueOf(v)));
        maxParticles.setStorageHandler(storage);
        maxParticles.setBinding(v -> config.maxParticles = v, () -> config.maxParticles);
        particleGroup.addOption(maxParticles);

        BooleanOptionBuilder particlePriorityOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_priority"));
        particlePriorityOpt.setName(Text.translatable("helium.option.particle_priority"));
        particlePriorityOpt.setTooltip(Text.translatable("helium.option.particle_priority.tooltip"));
        particlePriorityOpt.setImpact(OptionImpact.LOW);
        particlePriorityOpt.setDefaultValue(true);
        particlePriorityOpt.setStorageHandler(storage);
        particlePriorityOpt.setBinding(v -> config.particlePriority = v, () -> config.particlePriority);
        particleGroup.addOption(particlePriorityOpt);

        BooleanOptionBuilder particleBatch = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "particle_batching"));
        particleBatch.setName(Text.translatable("helium.option.particle_batching"));
        particleBatch.setTooltip(Text.translatable("helium.option.particle_batching.tooltip"));
        particleBatch.setImpact(OptionImpact.LOW);
        particleBatch.setDefaultValue(true);
        particleBatch.setStorageHandler(storage);
        particleBatch.setBinding(v -> config.particleBatching = v, () -> config.particleBatching);
        particleGroup.addOption(particleBatch);

        renderPage.addOptionGroup(particleGroup);

        OptionGroupBuilder renderOptGroup = builder.createOptionGroup();
        renderOptGroup.setName(Text.translatable("helium.group.render_pipeline"));

        BooleanOptionBuilder animThrottle = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "animation_throttling"));
        animThrottle.setName(Text.translatable("helium.option.animation_throttling"));
        animThrottle.setTooltip(Text.translatable("helium.option.animation_throttling.tooltip"));
        animThrottle.setImpact(OptionImpact.LOW);
        animThrottle.setDefaultValue(true);
        animThrottle.setStorageHandler(storage);
        animThrottle.setBinding(v -> config.animationThrottling = v, () -> config.animationThrottling);
        renderOptGroup.addOption(animThrottle);

        BooleanOptionBuilder fastMathOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_math"));
        fastMathOpt.setName(Text.translatable("helium.option.fast_math"));
        fastMathOpt.setTooltip(Text.translatable("helium.option.fast_math.tooltip"));
        fastMathOpt.setImpact(OptionImpact.LOW);
        fastMathOpt.setDefaultValue(true);
        fastMathOpt.setStorageHandler(storage);
        fastMathOpt.setBinding(v -> config.fastMath = v, () -> config.fastMath);
        renderOptGroup.addOption(fastMathOpt);

        BooleanOptionBuilder glCache = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "gl_state_cache"));
        glCache.setName(Text.translatable("helium.option.gl_state_cache"));
        if (HeliumClient.isAndroid()) {
            glCache.setTooltip(Text.translatable("helium.option.gl_state_cache.tooltip.android"));
            glCache.setEnabled(false);
            glCache.setDefaultValue(false);
            glCache.setBinding(v -> {}, () -> false);
        } else {
            glCache.setTooltip(Text.translatable("helium.option.gl_state_cache.tooltip"));
            glCache.setDefaultValue(true);
            glCache.setBinding(v -> config.glStateCache = v, () -> config.glStateCache);
        }
        glCache.setImpact(OptionImpact.LOW);
        glCache.setStorageHandler(storage);
        renderOptGroup.addOption(glCache);

        renderPage.addOptionGroup(renderOptGroup);

        OptionGroupBuilder cachingGroup = builder.createOptionGroup();
        cachingGroup.setName(Text.translatable("helium.group.caching"));

        BooleanOptionBuilder modelCacheOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "model_cache"));
        modelCacheOpt.setName(Text.translatable("helium.option.model_cache"));
        modelCacheOpt.setTooltip(Text.translatable("helium.option.model_cache.tooltip"));
        modelCacheOpt.setImpact(OptionImpact.MEDIUM);
        modelCacheOpt.setDefaultValue(true);
        modelCacheOpt.setStorageHandler(storage);
        modelCacheOpt.setBinding(v -> config.modelCache = v, () -> config.modelCache);
        cachingGroup.addOption(modelCacheOpt);

        IntegerOptionBuilder modelCacheSizeOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "model_cache_max_mb"));
        modelCacheSizeOpt.setName(Text.translatable("helium.option.model_cache_size"));
        modelCacheSizeOpt.setTooltip(Text.translatable("helium.option.model_cache_size.tooltip"));
        modelCacheSizeOpt.setImpact(OptionImpact.MEDIUM);
        modelCacheSizeOpt.setDefaultValue(64);
        modelCacheSizeOpt.setRange(16, 256, 16);
        modelCacheSizeOpt.setValueFormatter(v -> Text.translatable("helium.suffix.mb", v));
        modelCacheSizeOpt.setStorageHandler(storage);
        modelCacheSizeOpt.setBinding(v -> config.modelCacheMaxMb = v, () -> config.modelCacheMaxMb);
        cachingGroup.addOption(modelCacheSizeOpt);

        renderPage.addOptionGroup(cachingGroup);
        mod.addPage(renderPage);

        OptionPageBuilder generalPage = builder.createOptionPage();
        generalPage.setName(Text.translatable("helium.page.general"));

        OptionGroupBuilder engineGroup = builder.createOptionGroup();
        engineGroup.setName(Text.translatable("helium.group.engine"));

        BooleanOptionBuilder memOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "memory_optimizations"));
        memOpt.setName(Text.translatable("helium.option.memory_optimizations"));
        memOpt.setTooltip(Text.translatable("helium.option.memory_optimizations.tooltip"));
        memOpt.setImpact(OptionImpact.LOW);
        memOpt.setDefaultValue(true);
        memOpt.setStorageHandler(storage);
        memOpt.setBinding(v -> config.memoryOptimizations = v, () -> config.memoryOptimizations);
        engineGroup.addOption(memOpt);

        BooleanOptionBuilder threadOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "thread_optimizations"));
        threadOpt.setName(Text.translatable("helium.option.thread_optimizations"));
        threadOpt.setTooltip(Text.translatable("helium.option.thread_optimizations.tooltip"));
        threadOpt.setImpact(OptionImpact.LOW);
        threadOpt.setDefaultValue(true);
        threadOpt.setStorageHandler(storage);
        threadOpt.setBinding(v -> config.threadOptimizations = v, () -> config.threadOptimizations);
        engineGroup.addOption(threadOpt);

        BooleanOptionBuilder startupOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_startup"));
        startupOpt.setName(Text.translatable("helium.option.fast_startup"));
        startupOpt.setTooltip(Text.translatable("helium.option.fast_startup.tooltip"));
        startupOpt.setImpact(OptionImpact.LOW);
        startupOpt.setDefaultValue(true);
        startupOpt.setStorageHandler(storage);
        startupOpt.setBinding(v -> config.fastStartup = v, () -> config.fastStartup);
        engineGroup.addOption(startupOpt);

        BooleanOptionBuilder reducedAllocOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "reduced_allocations"));
        reducedAllocOpt.setName(Text.translatable("helium.option.reduced_allocations"));
        reducedAllocOpt.setTooltip(Text.translatable("helium.option.reduced_allocations.tooltip"));
        reducedAllocOpt.setImpact(OptionImpact.LOW);
        reducedAllocOpt.setDefaultValue(true);
        reducedAllocOpt.setStorageHandler(storage);
        reducedAllocOpt.setBinding(v -> config.reducedAllocations = v, () -> config.reducedAllocations);
        engineGroup.addOption(reducedAllocOpt);

        BooleanOptionBuilder idleOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "auto_pause_on_idle"));
        idleOpt.setName(Text.translatable("helium.option.auto_pause_on_idle"));
        idleOpt.setTooltip(Text.translatable("helium.option.auto_pause_on_idle.tooltip"));
        idleOpt.setImpact(OptionImpact.LOW);
        idleOpt.setDefaultValue(false);
        idleOpt.setStorageHandler(storage);
        idleOpt.setBinding(v -> config.autoPauseOnIdle = v, () -> config.autoPauseOnIdle);
        engineGroup.addOption(idleOpt);

        IntegerOptionBuilder idleTimeoutOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "idle_timeout_seconds"));
        idleTimeoutOpt.setName(Text.translatable("helium.option.idle_timeout"));
        idleTimeoutOpt.setTooltip(Text.translatable("helium.option.idle_timeout.tooltip"));
        idleTimeoutOpt.setImpact(OptionImpact.LOW);
        idleTimeoutOpt.setDefaultValue(60);
        idleTimeoutOpt.setRange(10, 300, 10);
        idleTimeoutOpt.setValueFormatter(v -> Text.translatable("helium.suffix.seconds", v));
        idleTimeoutOpt.setStorageHandler(storage);
        idleTimeoutOpt.setBinding(v -> config.idleTimeoutSeconds = v, () -> config.idleTimeoutSeconds);
        engineGroup.addOption(idleTimeoutOpt);

        IntegerOptionBuilder idleFpsOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "idle_fps_limit"));
        idleFpsOpt.setName(Text.translatable("helium.option.idle_fps_limit"));
        idleFpsOpt.setTooltip(Text.translatable("helium.option.idle_fps_limit.tooltip"));
        idleFpsOpt.setImpact(OptionImpact.LOW);
        idleFpsOpt.setDefaultValue(5);
        idleFpsOpt.setRange(1, 30, 1);
        idleFpsOpt.setValueFormatter(v -> Text.translatable("helium.suffix.fps", v));
        idleFpsOpt.setStorageHandler(storage);
        idleFpsOpt.setBinding(v -> config.idleFpsLimit = v, () -> config.idleFpsLimit);
        engineGroup.addOption(idleFpsOpt);

        generalPage.addOptionGroup(engineGroup);

        BooleanOptionBuilder fullbrightOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fullbright"));
        fullbrightOpt.setName(Text.translatable("helium.option.fullbright"));
        fullbrightOpt.setTooltip(Text.translatable("helium.option.fullbright.tooltip"));
        fullbrightOpt.setImpact(OptionImpact.LOW);
        fullbrightOpt.setDefaultValue(false);
        fullbrightOpt.setStorageHandler(storage);
        fullbrightOpt.setBinding(v -> {
            config.fullbright = v;
            com.helium.feature.FullbrightManager.setEnabled(v);
        }, () -> config.fullbright);
        engineGroup.addOption(fullbrightOpt);

        BooleanOptionBuilder netOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "network_optimizations"));
        netOpt.setName(Text.translatable("helium.option.network_optimizations"));
        netOpt.setTooltip(Text.translatable("helium.option.network_optimizations.tooltip"));
        netOpt.setImpact(OptionImpact.LOW);
        netOpt.setDefaultValue(true);
        netOpt.setStorageHandler(storage);
        netOpt.setBinding(v -> config.networkOptimizations = v, () -> config.networkOptimizations);
        engineGroup.addOption(netOpt);

        mod.addPage(generalPage);

        OptionPageBuilder mpPage = builder.createOptionPage();
        mpPage.setName(Text.translatable("helium.page.multiplayer"));

        OptionGroupBuilder serverGroup = builder.createOptionGroup();
        serverGroup.setName(Text.translatable("helium.group.server_list"));

        BooleanOptionBuilder fastPing = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fast_server_ping"));
        fastPing.setName(Text.translatable("helium.option.fast_server_ping"));
        fastPing.setTooltip(Text.translatable("helium.option.fast_server_ping.tooltip"));
        fastPing.setImpact(OptionImpact.LOW);
        fastPing.setDefaultValue(true);
        fastPing.setStorageHandler(storage);
        fastPing.setBinding(v -> config.fastServerPing = v, () -> config.fastServerPing);
        serverGroup.addOption(fastPing);

        BooleanOptionBuilder scrollKeep = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "preserve_scroll_on_refresh"));
        scrollKeep.setName(Text.translatable("helium.option.preserve_scroll"));
        scrollKeep.setTooltip(Text.translatable("helium.option.preserve_scroll.tooltip"));
        scrollKeep.setImpact(OptionImpact.LOW);
        scrollKeep.setDefaultValue(true);
        scrollKeep.setStorageHandler(storage);
        scrollKeep.setBinding(v -> config.preserveScrollOnRefresh = v, () -> config.preserveScrollOnRefresh);
        serverGroup.addOption(scrollKeep);

        BooleanOptionBuilder directPreview = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "direct_connect_preview"));
        directPreview.setName(Text.translatable("helium.option.direct_connect_preview"));
        directPreview.setTooltip(Text.translatable("helium.option.direct_connect_preview.tooltip"));
        directPreview.setImpact(OptionImpact.LOW);
        directPreview.setDefaultValue(true);
        directPreview.setStorageHandler(storage);
        directPreview.setBinding(v -> config.directConnectPreview = v, () -> config.directConnectPreview);
        serverGroup.addOption(directPreview);

        mpPage.addOptionGroup(serverGroup);
        mod.addPage(mpPage);

        OptionPageBuilder overlayPage = builder.createOptionPage();
        overlayPage.setName(Text.translatable("helium.page.overlay"));

        OptionGroupBuilder overlayGroup = builder.createOptionGroup();
        overlayGroup.setName(Text.translatable("helium.group.fps_overlay"));

        BooleanOptionBuilder fpsOverlayOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "fps_overlay"));
        fpsOverlayOpt.setName(Text.translatable("helium.option.fps_overlay"));
        fpsOverlayOpt.setTooltip(Text.translatable("helium.option.fps_overlay.tooltip"));
        fpsOverlayOpt.setImpact(OptionImpact.LOW);
        fpsOverlayOpt.setDefaultValue(true);
        fpsOverlayOpt.setStorageHandler(storage);
        fpsOverlayOpt.setBinding(v -> config.fpsOverlay = v, () -> config.fpsOverlay);
        overlayGroup.addOption(fpsOverlayOpt);

        IntegerOptionBuilder overlayTransOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_transparency"));
        overlayTransOpt.setName(Text.translatable("helium.option.overlay_transparency"));
        overlayTransOpt.setTooltip(Text.translatable("helium.option.overlay_transparency.tooltip"));
        overlayTransOpt.setImpact(OptionImpact.LOW);
        overlayTransOpt.setDefaultValue(60);
        overlayTransOpt.setRange(0, 100, 10);
        overlayTransOpt.setValueFormatter(v -> Text.translatable("helium.suffix.percent", v));
        overlayTransOpt.setStorageHandler(storage);
        overlayTransOpt.setBinding(v -> config.overlayTransparency = v, () -> config.overlayTransparency);
        overlayGroup.addOption(overlayTransOpt);

        overlayPage.addOptionGroup(overlayGroup);

        OptionGroupBuilder overlayContentGroup = builder.createOptionGroup();
        overlayContentGroup.setName(Text.translatable("helium.group.overlay_content"));

        BooleanOptionBuilder showFpsOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_fps"));
        showFpsOpt.setName(Text.translatable("helium.option.show_fps"));
        showFpsOpt.setTooltip(Text.translatable("helium.option.show_fps.tooltip"));
        showFpsOpt.setImpact(OptionImpact.LOW);
        showFpsOpt.setDefaultValue(true);
        showFpsOpt.setStorageHandler(storage);
        showFpsOpt.setBinding(v -> config.overlayShowFps = v, () -> config.overlayShowFps);
        overlayContentGroup.addOption(showFpsOpt);

        BooleanOptionBuilder showFpsMinMaxAvgOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_fps_stats"));
        showFpsMinMaxAvgOpt.setName(Text.translatable("helium.option.show_fps_stats"));
        showFpsMinMaxAvgOpt.setTooltip(Text.translatable("helium.option.show_fps_stats.tooltip"));
        showFpsMinMaxAvgOpt.setImpact(OptionImpact.LOW);
        showFpsMinMaxAvgOpt.setDefaultValue(false);
        showFpsMinMaxAvgOpt.setStorageHandler(storage);
        showFpsMinMaxAvgOpt.setBinding(v -> config.overlayShowFpsMinMaxAvg = v, () -> config.overlayShowFpsMinMaxAvg);
        overlayContentGroup.addOption(showFpsMinMaxAvgOpt);

        BooleanOptionBuilder showMemoryOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_memory"));
        showMemoryOpt.setName(Text.translatable("helium.option.show_memory"));
        showMemoryOpt.setTooltip(Text.translatable("helium.option.show_memory.tooltip"));
        showMemoryOpt.setImpact(OptionImpact.LOW);
        showMemoryOpt.setDefaultValue(false);
        showMemoryOpt.setStorageHandler(storage);
        showMemoryOpt.setBinding(v -> config.overlayShowMemory = v, () -> config.overlayShowMemory);
        overlayContentGroup.addOption(showMemoryOpt);

        BooleanOptionBuilder showParticlesOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_particles"));
        showParticlesOpt.setName(Text.translatable("helium.option.show_particles"));
        showParticlesOpt.setTooltip(Text.translatable("helium.option.show_particles.tooltip"));
        showParticlesOpt.setImpact(OptionImpact.LOW);
        showParticlesOpt.setDefaultValue(false);
        showParticlesOpt.setStorageHandler(storage);
        showParticlesOpt.setBinding(v -> config.overlayShowParticles = v, () -> config.overlayShowParticles);
        overlayContentGroup.addOption(showParticlesOpt);

        BooleanOptionBuilder showCoordsOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_coordinates"));
        showCoordsOpt.setName(Text.translatable("helium.option.show_coordinates"));
        showCoordsOpt.setTooltip(Text.translatable("helium.option.show_coordinates.tooltip"));
        showCoordsOpt.setImpact(OptionImpact.LOW);
        showCoordsOpt.setDefaultValue(false);
        showCoordsOpt.setStorageHandler(storage);
        showCoordsOpt.setBinding(v -> config.overlayShowCoordinates = v, () -> config.overlayShowCoordinates);
        overlayContentGroup.addOption(showCoordsOpt);

        BooleanOptionBuilder showBiomeOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "overlay_show_biome"));
        showBiomeOpt.setName(Text.translatable("helium.option.show_biome"));
        showBiomeOpt.setTooltip(Text.translatable("helium.option.show_biome.tooltip"));
        showBiomeOpt.setImpact(OptionImpact.LOW);
        showBiomeOpt.setDefaultValue(false);
        showBiomeOpt.setStorageHandler(storage);
        showBiomeOpt.setBinding(v -> config.overlayShowBiome = v, () -> config.overlayShowBiome);
        overlayContentGroup.addOption(showBiomeOpt);

        overlayPage.addOptionGroup(overlayContentGroup);
        mod.addPage(overlayPage);

        OptionPageBuilder advancedPage = builder.createOptionPage();
        advancedPage.setName(Text.translatable("helium.page.advanced"));

        OptionGroupBuilder advancedGroup = builder.createOptionGroup();
        advancedGroup.setName(Text.translatable("helium.group.experimental"));

        BooleanOptionBuilder nativeMemOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "native_memory"));
        nativeMemOpt.setName(Text.translatable("helium.option.native_memory"));
        nativeMemOpt.setTooltip(Text.translatable("helium.option.native_memory.tooltip"));
        nativeMemOpt.setImpact(OptionImpact.MEDIUM);
        nativeMemOpt.setDefaultValue(true);
        nativeMemOpt.setStorageHandler(storage);
        nativeMemOpt.setBinding(v -> config.nativeMemory = v, () -> config.nativeMemory);
        advancedGroup.addOption(nativeMemOpt);

        IntegerOptionBuilder nativeMemPoolOpt = builder.createIntegerOption(VersionCompat.createIdentifier(NAMESPACE, "native_memory_pool_mb"));
        nativeMemPoolOpt.setName(Text.translatable("helium.option.native_memory_pool_size"));
        nativeMemPoolOpt.setTooltip(Text.translatable("helium.option.native_memory_pool_size.tooltip"));
        nativeMemPoolOpt.setImpact(OptionImpact.MEDIUM);
        nativeMemPoolOpt.setDefaultValue(64);
        nativeMemPoolOpt.setRange(16, 256, 16);
        nativeMemPoolOpt.setValueFormatter(v -> Text.translatable("helium.suffix.mb", v));
        nativeMemPoolOpt.setStorageHandler(storage);
        nativeMemPoolOpt.setBinding(v -> config.nativeMemoryPoolMb = v, () -> config.nativeMemoryPoolMb);
        advancedGroup.addOption(nativeMemPoolOpt);

        BooleanOptionBuilder renderPipeOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "render_pipelining"));
        renderPipeOpt.setName(Text.translatable("helium.option.render_pipelining"));
        renderPipeOpt.setTooltip(Text.translatable("helium.option.render_pipelining.tooltip"));
        renderPipeOpt.setImpact(OptionImpact.HIGH);
        renderPipeOpt.setDefaultValue(false);
        renderPipeOpt.setStorageHandler(storage);
        renderPipeOpt.setBinding(v -> config.renderPipelining = v, () -> config.renderPipelining);
        advancedGroup.addOption(renderPipeOpt);

        BooleanOptionBuilder simdOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "simd_math"));
        simdOpt.setName(Text.translatable("helium.option.simd_math"));
        simdOpt.setTooltip(Text.translatable("helium.option.simd_math.tooltip"));
        simdOpt.setImpact(OptionImpact.MEDIUM);
        simdOpt.setDefaultValue(false);
        simdOpt.setStorageHandler(storage);
        simdOpt.setBinding(v -> config.simdMath = v, () -> config.simdMath);
        advancedGroup.addOption(simdOpt);

        BooleanOptionBuilder asyncLightOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "async_light_updates"));
        asyncLightOpt.setName(Text.translatable("helium.option.async_light_updates"));
        asyncLightOpt.setTooltip(Text.translatable("helium.option.async_light_updates.tooltip"));
        asyncLightOpt.setImpact(OptionImpact.MEDIUM);
        asyncLightOpt.setDefaultValue(false);
        asyncLightOpt.setStorageHandler(storage);
        asyncLightOpt.setBinding(v -> config.asyncLightUpdates = v, () -> config.asyncLightUpdates);
        advancedGroup.addOption(asyncLightOpt);

        BooleanOptionBuilder packetBatchOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "packet_batching"));
        packetBatchOpt.setName(Text.translatable("helium.option.packet_batching"));
        packetBatchOpt.setTooltip(Text.translatable("helium.option.packet_batching.tooltip"));
        packetBatchOpt.setImpact(OptionImpact.LOW);
        packetBatchOpt.setDefaultValue(false);
        packetBatchOpt.setStorageHandler(storage);
        packetBatchOpt.setBinding(v -> config.packetBatching = v, () -> config.packetBatching);
        advancedGroup.addOption(packetBatchOpt);

        BooleanOptionBuilder temporalOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "temporal_reprojection"));
        temporalOpt.setName(Text.translatable("helium.option.temporal_reprojection"));
        temporalOpt.setTooltip(Text.translatable("helium.option.temporal_reprojection.tooltip"));
        temporalOpt.setImpact(OptionImpact.HIGH);
        temporalOpt.setDefaultValue(false);
        temporalOpt.setStorageHandler(storage);
        temporalOpt.setBinding(v -> config.temporalReprojection = v, () -> config.temporalReprojection);
        advancedGroup.addOption(temporalOpt);

        advancedPage.addOptionGroup(advancedGroup);

        OptionGroupBuilder gpuGroup = builder.createOptionGroup();
        gpuGroup.setName(Text.translatable("helium.group.gpu_specific"));

        BooleanOptionBuilder nvidiaOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "nvidia_optimizations"));
        nvidiaOpt.setName(Text.translatable("helium.option.nvidia_optimizations"));
        nvidiaOpt.setTooltip(Text.translatable("helium.option.nvidia_optimizations.tooltip"));
        nvidiaOpt.setImpact(OptionImpact.MEDIUM);
        nvidiaOpt.setDefaultValue(true);
        nvidiaOpt.setStorageHandler(storage);
        nvidiaOpt.setBinding(v -> config.nvidiaOptimizations = v, () -> config.nvidiaOptimizations);
        gpuGroup.addOption(nvidiaOpt);

        BooleanOptionBuilder amdOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "amd_optimizations"));
        amdOpt.setName(Text.translatable("helium.option.amd_optimizations"));
        amdOpt.setTooltip(Text.translatable("helium.option.amd_optimizations.tooltip"));
        amdOpt.setImpact(OptionImpact.MEDIUM);
        amdOpt.setDefaultValue(true);
        amdOpt.setStorageHandler(storage);
        amdOpt.setBinding(v -> config.amdOptimizations = v, () -> config.amdOptimizations);
        gpuGroup.addOption(amdOpt);

        BooleanOptionBuilder intelOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "intel_optimizations"));
        intelOpt.setName(Text.translatable("helium.option.intel_optimizations"));
        intelOpt.setTooltip(Text.translatable("helium.option.intel_optimizations.tooltip"));
        intelOpt.setImpact(OptionImpact.MEDIUM);
        intelOpt.setDefaultValue(true);
        intelOpt.setStorageHandler(storage);
        intelOpt.setBinding(v -> config.intelOptimizations = v, () -> config.intelOptimizations);
        gpuGroup.addOption(intelOpt);

        BooleanOptionBuilder adaptiveSyncOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "adaptive_sync"));
        adaptiveSyncOpt.setName(Text.translatable("helium.option.adaptive_sync"));
        adaptiveSyncOpt.setTooltip(Text.translatable("helium.option.adaptive_sync.tooltip"));
        adaptiveSyncOpt.setImpact(OptionImpact.LOW);
        adaptiveSyncOpt.setDefaultValue(true);
        adaptiveSyncOpt.setStorageHandler(storage);
        adaptiveSyncOpt.setBinding(v -> config.adaptiveSync = v, () -> config.adaptiveSync);
        gpuGroup.addOption(adaptiveSyncOpt);

        BooleanOptionBuilder displaySyncOpt = builder.createBooleanOption(VersionCompat.createIdentifier(NAMESPACE, "display_sync_optimization"));
        displaySyncOpt.setName(Text.translatable("helium.option.display_sync_optimization"));
        displaySyncOpt.setTooltip(Text.translatable("helium.option.display_sync_optimization.tooltip"));
        displaySyncOpt.setImpact(OptionImpact.VARIES);
        displaySyncOpt.setDefaultValue(true);
        displaySyncOpt.setStorageHandler(storage);
        displaySyncOpt.setBinding(v -> config.displaySyncOptimization = v, () -> config.displaySyncOptimization);
        gpuGroup.addOption(displaySyncOpt);

        advancedPage.addOptionGroup(gpuGroup);
        mod.addPage(advancedPage);
    }
}
