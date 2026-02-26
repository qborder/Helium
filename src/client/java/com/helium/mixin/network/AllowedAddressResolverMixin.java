package com.helium.mixin.network;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.network.FastIpPingOptimizer;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;
import java.util.Optional;

@Mixin(AllowedAddressResolver.class)
public abstract class AllowedAddressResolverMixin {

    @Inject(method = "resolve", at = @At("RETURN"), require = 0)
    private void helium$patchReverseDns(ServerAddress address, CallbackInfoReturnable<Optional<Address>> cir) {
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.fastIpPing) return;
            if (!FastIpPingOptimizer.isInitialized()) return;

            Optional<Address> result = cir.getReturnValue();
            if (result == null || result.isEmpty()) return;

            InetSocketAddress socketAddr = result.get().getInetSocketAddress();
            FastIpPingOptimizer.patchAddress(socketAddr);
        } catch (Throwable ignored) {}
    }
}
