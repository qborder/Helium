package com.helium.mixin.platform;

import com.helium.platform.DwmApi;
import com.helium.platform.WindowsVersion;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {

    @Shadow
    @Final
    private long handle;

    @Shadow
    private boolean fullscreen;

    @Inject(method = "<init>", at = @At("TAIL"), require = 0)
    private void helium$initWindowStyle(CallbackInfo ci) {
        if (Util.getOperatingSystem() != Util.OperatingSystem.WINDOWS) return;

        WindowsVersion.init();
        DwmApi.applyWindowStyle(this.fullscreen, this.handle);
    }

    @Inject(method = "setFullscreen", at = @At("TAIL"), require = 0)
    private void helium$onFullscreenToggle(boolean fullscreen, CallbackInfo ci) {
        if (Util.getOperatingSystem() != Util.OperatingSystem.WINDOWS) return;

        DwmApi.applyWindowStyle(fullscreen, this.handle);
    }
}
