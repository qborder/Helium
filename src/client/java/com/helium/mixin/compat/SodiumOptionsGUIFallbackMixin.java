package com.helium.mixin.compat;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI")
public abstract class SodiumOptionsGUIFallbackMixin extends Screen {

    protected SodiumOptionsGUIFallbackMixin(Text title) {
        super(title);
    }

    @Inject(method = "rebuildGUI", at = @At("TAIL"), require = 0, remap = false)
    private void helium$addConfigButton(CallbackInfo ci) {
        try {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.of("Helium"),
                    button -> {
                        if (this.client != null) {
                            this.client.setScreen(HeliumConfigScreen.create((Screen) (Object) this));
                        }
                    }
            ).dimensions(this.width - 86, 6, 80, 20).build());
        } catch (Throwable t) {
            HeliumClient.LOGGER.warn("failed to add helium button to sodium gui", t);
        }
    }
}
