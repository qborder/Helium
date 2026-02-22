package com.helium.mixin.idle;

import com.helium.idle.IdleManager;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void helium$onMouseMove(long window, double x, double y, CallbackInfo ci) {
        if (IdleManager.isInitialized()) IdleManager.onActivity();
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void helium$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (IdleManager.isInitialized()) IdleManager.onActivity();
    }
}
