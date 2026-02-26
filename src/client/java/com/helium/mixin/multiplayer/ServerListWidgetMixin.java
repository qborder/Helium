package com.helium.mixin.multiplayer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Mixin(MultiplayerServerListWidget.class)
public abstract class ServerListWidgetMixin {

    @Shadow
    @Mutable
    @Final
    private static ThreadPoolExecutor SERVER_PINGER_THREAD_POOL;

    @Shadow
    @Final
    private List<MultiplayerServerListWidget.ServerEntry> servers;

    @Unique
    private static final int HELIUM_THREAD_OVERHEAD = 5;

    @Unique
    private static boolean helium$poolInitialized = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void helium$initPingerPool(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.fastServerPing) return;

        if (!helium$poolInitialized) {
            helium$poolInitialized = true;
            helium$rebuildThreadPool();
        }
    }

    @Inject(method = "updateEntries", at = @At("HEAD"), require = 0)
    private void helium$onUpdateEntries(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.fastServerPing) return;

        if (SERVER_PINGER_THREAD_POOL.getActiveCount() >= HELIUM_THREAD_OVERHEAD) {
            helium$rebuildThreadPool();
        }
    }

    @Unique
    private void helium$rebuildThreadPool() {
        try {
            SERVER_PINGER_THREAD_POOL.shutdownNow();
        } catch (Exception ignored) {}

        int serverCount = servers != null ? servers.size() : 0;
        int poolSize = Math.max(serverCount + HELIUM_THREAD_OVERHEAD, Runtime.getRuntime().availableProcessors());

        SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
                poolSize,
                new ThreadFactoryBuilder()
                        .setNameFormat("Helium-ServerPinger-%d")
                        .setDaemon(true)
                        .build()
        );
    }
}
