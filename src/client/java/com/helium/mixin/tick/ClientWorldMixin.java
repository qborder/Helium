package com.helium.mixin.tick;

import com.helium.HeliumClient;
import com.helium.memory.MemoryCompactor;
import com.helium.tick.ClientTickCache;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Unique
    private long helium$tickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void helium$tickCache(CallbackInfo ci) {
        if (HeliumClient.getConfig() == null || !HeliumClient.getConfig().modEnabled) return;
        long time = helium$tickCounter++;
        ClientTickCache.tick(time);
        if (HeliumClient.getConfig().memoryOptimizations) {
            MemoryCompactor.tick(time);
        }
    }
}
