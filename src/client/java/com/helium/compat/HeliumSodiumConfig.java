package com.helium.compat;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
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
        mod.setIcon(Identifier.of(NAMESPACE, "textures/icon-only.png"));

        OptionPageBuilder renderPage = builder.createOptionPage();
        renderPage.setName(Text.literal("Rendering"));

        OptionGroupBuilder cullingGroup = builder.createOptionGroup();
        cullingGroup.setName(Text.literal("Distance Culling"));

        BooleanOptionBuilder entityCull = builder.createBooleanOption(Identifier.of(NAMESPACE, "entity_culling"));
        entityCull.setName(Text.literal("Entity Distance Culling"));
        entityCull.setTooltip(Text.literal("Skip rendering entities beyond a set distance. Boosts FPS in crowded areas."));
        entityCull.setImpact(OptionImpact.MEDIUM);
        entityCull.setDefaultValue(true);
        entityCull.setStorageHandler(storage);
        entityCull.setBinding(v -> config.entityCulling = v, () -> config.entityCulling);
        cullingGroup.addOption(entityCull);

        IntegerOptionBuilder entityDist = builder.createIntegerOption(Identifier.of(NAMESPACE, "entity_cull_distance"));
        entityDist.setName(Text.literal("Entity Cull Distance"));
        entityDist.setTooltip(Text.literal("Max distance (blocks) to render entities. Lower = more FPS."));
        entityDist.setImpact(OptionImpact.MEDIUM);
        entityDist.setDefaultValue(64);
        entityDist.setRange(16, 128, 8);
        entityDist.setValueFormatter(v -> Text.literal(v + " blocks"));
        entityDist.setStorageHandler(storage);
        entityDist.setBinding(v -> config.entityCullDistance = v, () -> config.entityCullDistance);
        cullingGroup.addOption(entityDist);

        BooleanOptionBuilder blockEntityCull = builder.createBooleanOption(Identifier.of(NAMESPACE, "block_entity_culling"));
        blockEntityCull.setName(Text.literal("Block Entity Distance Culling"));
        blockEntityCull.setTooltip(Text.literal("Skip rendering distant block entities (chests, signs, etc). Big FPS boost."));
        blockEntityCull.setImpact(OptionImpact.HIGH);
        blockEntityCull.setDefaultValue(true);
        blockEntityCull.setStorageHandler(storage);
        blockEntityCull.setBinding(v -> config.blockEntityCulling = v, () -> config.blockEntityCulling);
        cullingGroup.addOption(blockEntityCull);

        IntegerOptionBuilder blockEntityDist = builder.createIntegerOption(Identifier.of(NAMESPACE, "block_entity_cull_distance"));
        blockEntityDist.setName(Text.literal("Block Entity Cull Distance"));
        blockEntityDist.setTooltip(Text.literal("Max distance (blocks) to render block entities. Lower = more FPS."));
        blockEntityDist.setImpact(OptionImpact.HIGH);
        blockEntityDist.setDefaultValue(48);
        blockEntityDist.setRange(16, 96, 8);
        blockEntityDist.setValueFormatter(v -> Text.literal(v + " blocks"));
        blockEntityDist.setStorageHandler(storage);
        blockEntityDist.setBinding(v -> config.blockEntityCullDistance = v, () -> config.blockEntityCullDistance);
        cullingGroup.addOption(blockEntityDist);

        BooleanOptionBuilder particleCull = builder.createBooleanOption(Identifier.of(NAMESPACE, "particle_culling"));
        particleCull.setName(Text.literal("Particle Distance Culling"));
        particleCull.setTooltip(Text.literal("Skip distant particles. Improves FPS in particle-heavy scenes."));
        particleCull.setImpact(OptionImpact.MEDIUM);
        particleCull.setDefaultValue(true);
        particleCull.setStorageHandler(storage);
        particleCull.setBinding(v -> config.particleCulling = v, () -> config.particleCulling);
        cullingGroup.addOption(particleCull);

        IntegerOptionBuilder particleDist = builder.createIntegerOption(Identifier.of(NAMESPACE, "particle_cull_distance"));
        particleDist.setName(Text.literal("Particle Cull Distance"));
        particleDist.setTooltip(Text.literal("Max distance (blocks) to render particles. Lower = more FPS."));
        particleDist.setImpact(OptionImpact.MEDIUM);
        particleDist.setDefaultValue(32);
        particleDist.setRange(8, 64, 4);
        particleDist.setValueFormatter(v -> Text.literal(v + " blocks"));
        particleDist.setStorageHandler(storage);
        particleDist.setBinding(v -> config.particleCullDistance = v, () -> config.particleCullDistance);
        cullingGroup.addOption(particleDist);

        renderPage.addOptionGroup(cullingGroup);

        OptionGroupBuilder renderOptGroup = builder.createOptionGroup();
        renderOptGroup.setName(Text.literal("Render Pipeline"));

        BooleanOptionBuilder animThrottle = builder.createBooleanOption(Identifier.of(NAMESPACE, "animation_throttling"));
        animThrottle.setName(Text.literal("Animation Throttling"));
        animThrottle.setTooltip(Text.literal("Reduce texture animation updates for off-screen or distant blocks. Saves GPU time."));
        animThrottle.setImpact(OptionImpact.LOW);
        animThrottle.setDefaultValue(true);
        animThrottle.setStorageHandler(storage);
        animThrottle.setBinding(v -> config.animationThrottling = v, () -> config.animationThrottling);
        renderOptGroup.addOption(animThrottle);

        BooleanOptionBuilder fastMathOpt = builder.createBooleanOption(Identifier.of(NAMESPACE, "fast_math"));
        fastMathOpt.setName(Text.literal("Fast Math"));
        fastMathOpt.setTooltip(Text.literal("Replace sin/cos/atan2 with faster approximations. Tiny accuracy loss, noticeable FPS gain."));
        fastMathOpt.setImpact(OptionImpact.LOW);
        fastMathOpt.setDefaultValue(true);
        fastMathOpt.setStorageHandler(storage);
        fastMathOpt.setBinding(v -> config.fastMath = v, () -> config.fastMath);
        renderOptGroup.addOption(fastMathOpt);

        BooleanOptionBuilder glCache = builder.createBooleanOption(Identifier.of(NAMESPACE, "gl_state_cache"));
        glCache.setName(Text.literal("GL State Cache"));
        glCache.setTooltip(Text.literal("Cache OpenGL state to skip redundant calls. Auto-disabled with ImmediatelyFast."));
        glCache.setImpact(OptionImpact.LOW);
        glCache.setDefaultValue(true);
        glCache.setStorageHandler(storage);
        glCache.setBinding(v -> config.glStateCache = v, () -> config.glStateCache);
        renderOptGroup.addOption(glCache);

        renderPage.addOptionGroup(renderOptGroup);
        mod.addPage(renderPage);

        OptionPageBuilder generalPage = builder.createOptionPage();
        generalPage.setName(Text.literal("General"));

        OptionGroupBuilder engineGroup = builder.createOptionGroup();
        engineGroup.setName(Text.literal("Engine"));

        BooleanOptionBuilder memOpt = builder.createBooleanOption(Identifier.of(NAMESPACE, "memory_optimizations"));
        memOpt.setName(Text.literal("Memory Optimizations"));
        memOpt.setTooltip(Text.literal("Reduce garbage collection pauses by reusing objects and buffers."));
        memOpt.setImpact(OptionImpact.LOW);
        memOpt.setDefaultValue(true);
        memOpt.setStorageHandler(storage);
        memOpt.setBinding(v -> config.memoryOptimizations = v, () -> config.memoryOptimizations);
        engineGroup.addOption(memOpt);

        BooleanOptionBuilder threadOpt = builder.createBooleanOption(Identifier.of(NAMESPACE, "thread_optimizations"));
        threadOpt.setName(Text.literal("Thread Optimizations"));
        threadOpt.setTooltip(Text.literal("Prioritize render thread and optimize event polling for smoother frames."));
        threadOpt.setImpact(OptionImpact.LOW);
        threadOpt.setDefaultValue(true);
        threadOpt.setStorageHandler(storage);
        threadOpt.setBinding(v -> config.threadOptimizations = v, () -> config.threadOptimizations);
        engineGroup.addOption(threadOpt);

        BooleanOptionBuilder startupOpt = builder.createBooleanOption(Identifier.of(NAMESPACE, "fast_startup"));
        startupOpt.setName(Text.literal("Fast Startup"));
        startupOpt.setTooltip(Text.literal("Parallel class loading for faster game launch. No effect after startup."));
        startupOpt.setImpact(OptionImpact.LOW);
        startupOpt.setDefaultValue(true);
        startupOpt.setStorageHandler(storage);
        startupOpt.setBinding(v -> config.fastStartup = v, () -> config.fastStartup);
        engineGroup.addOption(startupOpt);

        generalPage.addOptionGroup(engineGroup);

        BooleanOptionBuilder netOpt = builder.createBooleanOption(Identifier.of(NAMESPACE, "network_optimizations"));
        netOpt.setName(Text.literal("Network Optimizations"));
        netOpt.setTooltip(Text.literal("Background resource processing for smoother network handling."));
        netOpt.setImpact(OptionImpact.LOW);
        netOpt.setDefaultValue(true);
        netOpt.setStorageHandler(storage);
        netOpt.setBinding(v -> config.networkOptimizations = v, () -> config.networkOptimizations);
        engineGroup.addOption(netOpt);

        mod.addPage(generalPage);

        OptionPageBuilder mpPage = builder.createOptionPage();
        mpPage.setName(Text.literal("Multiplayer"));

        OptionGroupBuilder serverGroup = builder.createOptionGroup();
        serverGroup.setName(Text.literal("Server List"));

        BooleanOptionBuilder fastPing = builder.createBooleanOption(Identifier.of(NAMESPACE, "fast_server_ping"));
        fastPing.setName(Text.literal("Fast Server Pinging"));
        fastPing.setTooltip(Text.literal("Ping all servers simultaneously instead of one by one. Much faster server list loading."));
        fastPing.setImpact(OptionImpact.LOW);
        fastPing.setDefaultValue(true);
        fastPing.setStorageHandler(storage);
        fastPing.setBinding(v -> config.fastServerPing = v, () -> config.fastServerPing);
        serverGroup.addOption(fastPing);

        BooleanOptionBuilder scrollKeep = builder.createBooleanOption(Identifier.of(NAMESPACE, "preserve_scroll_on_refresh"));
        scrollKeep.setName(Text.literal("Keep Scroll on Refresh"));
        scrollKeep.setTooltip(Text.literal("Don't reset the server list scroll position when you press refresh."));
        scrollKeep.setImpact(OptionImpact.LOW);
        scrollKeep.setDefaultValue(true);
        scrollKeep.setStorageHandler(storage);
        scrollKeep.setBinding(v -> config.preserveScrollOnRefresh = v, () -> config.preserveScrollOnRefresh);
        serverGroup.addOption(scrollKeep);

        mpPage.addOptionGroup(serverGroup);
        mod.addPage(mpPage);
    }
}
