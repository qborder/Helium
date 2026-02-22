package com.helium.mixin.idle;

import com.helium.idle.IdleManager;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void helium$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (IdleManager.isInitialized()) IdleManager.onActivity();
    }

    @Inject(method = "onChar", at = @At("HEAD"))
    private void helium$onChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (IdleManager.isInitialized()) IdleManager.onActivity();
    }
}
