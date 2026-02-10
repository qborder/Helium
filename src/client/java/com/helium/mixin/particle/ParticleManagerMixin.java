package com.helium.mixin.particle;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.threading.ParticleWorkerPool;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void helium$initParticlePool(CallbackInfo ci) {
        if (HeliumClient.getConfig().threadOptimizations && !ParticleWorkerPool.isInitialized()) {
            ParticleWorkerPool.init(Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    private void helium$cullDistantParticles(Particle particle, CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.particleCulling) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        double dx = particle.getBoundingBox().getCenter().x - client.player.getX();
        double dy = particle.getBoundingBox().getCenter().y - client.player.getY();
        double dz = particle.getBoundingBox().getCenter().z - client.player.getZ();
        double dist = dx * dx + dy * dy + dz * dz;
        double maxDist = config.particleCullDistance * config.particleCullDistance;
        if (dist > maxDist) {
            ci.cancel();
        }
    }
}
