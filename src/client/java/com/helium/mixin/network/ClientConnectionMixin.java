package com.helium.mixin.network;

import com.helium.HeliumClient;
import com.helium.network.BufferOptimizer;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void helium$optimizeBuffers(CallbackInfo ci) {
        // placeholder: buffer optimization hooks are applied during connection tick
        // actual buffer reuse happens in BufferOptimizer when packets are decoded
    }
}
