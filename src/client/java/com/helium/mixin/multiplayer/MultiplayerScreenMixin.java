package com.helium.mixin.multiplayer;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin {

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void helium$preserveScrollOnRefresh(MinecraftClient client, Screen newScreen) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.preserveScrollOnRefresh
                || !(newScreen instanceof MultiplayerScreen) || this.serverListWidget == null) {
            client.setScreen(newScreen);
            return;
        }

        double scrollY = this.serverListWidget.getScrollY();
        client.setScreen(newScreen);
        MultiplayerServerListWidget newWidget = ((MultiplayerScreenAccessor) newScreen).helium$getServerListWidget();
        if (newWidget != null) {
            newWidget.setScrollY(scrollY);
        }
    }
}
