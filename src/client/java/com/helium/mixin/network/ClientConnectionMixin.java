package com.helium.mixin.network;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.network.BufferOptimizer;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Unique
    private long helium$tickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void helium$optimizeBuffers(CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.networkOptimizations) return;

        BufferOptimizer.tick(helium$tickCounter++);
    }
}
