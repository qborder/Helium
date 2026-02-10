package com.helium.mixin.multiplayer;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(MultiplayerServerListWidget.class)
public abstract class ServerListWidgetMixin {

    @Shadow
    @Mutable
    private static ThreadPoolExecutor SERVER_PINGER_THREAD_POOL;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void helium$expandPingerPool(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.fastServerPing) return;

        int poolSize = Math.max(Runtime.getRuntime().availableProcessors(), 8);
        SERVER_PINGER_THREAD_POOL = new ThreadPoolExecutor(
                poolSize, poolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    Thread t = new Thread(r, "Helium-ServerPinger");
                    t.setDaemon(true);
                    return t;
                }
        );
        SERVER_PINGER_THREAD_POOL.allowCoreThreadTimeOut(true);
    }
}
