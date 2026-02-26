package com.helium.mixin.ui;

import com.helium.ui.ScrollMath;
import com.helium.ui.ScrollableWidgetManipulator;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin {

    @Inject(method = "renderWidget", at = @At("HEAD"), require = 0)
    private void helium$manipulateScrollAmount(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ScrollMath.isEnabled()) return;

        if (this instanceof ScrollableWidgetManipulator manipulator) {
            manipulator.helium$manipulateScrollAmount(delta);
        }
    }
}
