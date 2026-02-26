package com.helium.mixin.ui;

import com.helium.ui.ScrollMath;
import com.helium.ui.ScrollableWidgetManipulator;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScrollableWidget.class)
public abstract class ScrollableWidgetMixin implements ScrollableWidgetManipulator {

    @Shadow
    private double scrollY;

    @Shadow
    public abstract int getMaxScrollY();

    @Shadow
    public abstract void setScrollY(double scrollY);

    @Unique
    private double helium$animationTimer = 0;

    @Unique
    private double helium$scrollStartVelocity = 0;

    @Unique
    private boolean helium$renderSmooth = false;

    @Override
    public void helium$manipulateScrollAmount(float delta) {
        if (!ScrollMath.isEnabled()) return;

        helium$renderSmooth = true;
        helium$checkOutOfBounds(delta);

        if (Math.abs(ScrollMath.scrollbarVelocity(helium$animationTimer, helium$scrollStartVelocity)) < 1.0) return;
        helium$applyMotion(delta);
    }

    @Unique
    private void helium$applyMotion(float delta) {
        setScrollY(scrollY + ScrollMath.scrollbarVelocity(helium$animationTimer, helium$scrollStartVelocity) * delta);
        helium$animationTimer += delta * 10 / ScrollMath.getAnimationDuration();
    }

    @Unique
    private void helium$checkOutOfBounds(float delta) {
        if (scrollY < 0) {
            setScrollY(scrollY + ScrollMath.pushBackStrength(Math.abs(scrollY), delta));
            if (scrollY > -0.2) scrollY = 0;
        }
        if (scrollY > getMaxScrollY()) {
            setScrollY(scrollY - ScrollMath.pushBackStrength(scrollY - getMaxScrollY(), delta));
            if (scrollY < getMaxScrollY() + 0.2) scrollY = getMaxScrollY();
        }
    }

    @WrapOperation(
            method = "mouseScrolled",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ScrollableWidget;setScrollY(D)V"),
            require = 0
    )
    private void helium$captureVelocity(ScrollableWidget instance, double targetScrollY, Operation<Void> original) {
        if (!ScrollMath.isEnabled() || !helium$renderSmooth) {
            original.call(instance, targetScrollY);
            return;
        }

        double diff = targetScrollY - this.scrollY;
        diff = Math.signum(diff) * Math.min(Math.abs(diff), 10);
        diff *= ScrollMath.getScrollSpeed();

        if (Math.signum(diff) != Math.signum(helium$scrollStartVelocity)) {
            diff *= 2.5d;
        }

        helium$animationTimer *= 0.5;
        helium$scrollStartVelocity = ScrollMath.scrollbarVelocity(helium$animationTimer, helium$scrollStartVelocity) + diff;
        helium$animationTimer = 0;
    }

    @WrapOperation(
            method = "mouseDragged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ScrollableWidget;setScrollY(D)V"),
            require = 0
    )
    private void helium$clampDraggedScrollY(ScrollableWidget instance, double targetScrollY, Operation<Void> original) {
        original.call(instance, MathHelper.clamp(targetScrollY, 0.0, this.getMaxScrollY()));
    }

    @WrapMethod(method = "setScrollY", require = 0)
    private void helium$setScrollYUnclamped(double targetScrollY, Operation<Void> original) {
        if (!ScrollMath.isEnabled() || !helium$renderSmooth) {
            original.call(targetScrollY);
            return;
        }
        if (targetScrollY > getMaxScrollY() + 1e5 || targetScrollY < -1e5) {
            original.call(targetScrollY);
        } else {
            this.scrollY = targetScrollY;
        }
    }
}
