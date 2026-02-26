package com.helium.ui;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;

public final class ScrollMath {

    private static final double DEFAULT_SCROLL_SPEED = 0.5;
    private static final double DEFAULT_SCROLLBAR_DRAG = 0.025;
    private static final double DEFAULT_ANIMATION_DURATION = 1.0;
    private static final double DEFAULT_PUSHBACK_STRENGTH = 1.0;

    private ScrollMath() {}

    public static double getScrollSpeed() {
        return DEFAULT_SCROLL_SPEED;
    }

    public static double getScrollbarDrag() {
        return DEFAULT_SCROLLBAR_DRAG;
    }

    public static double getAnimationDuration() {
        return DEFAULT_ANIMATION_DURATION;
    }

    public static double getPushBackStrength() {
        return DEFAULT_PUSHBACK_STRENGTH;
    }

    public static double scrollbarVelocity(double timer, double factor) {
        return Math.pow(1 - getScrollbarDrag(), timer) * factor;
    }

    public static int dampenSquish(double squish, int height) {
        double proportion = Math.min(1, squish / 100);
        return (int) (Math.min(0.85, proportion) * height);
    }

    public static double pushBackStrength(double distance, float delta) {
        return ((distance + 4d) * delta / 0.3d) / (3.2d / getPushBackStrength());
    }

    public static boolean isEnabled() {
        HeliumConfig config = HeliumClient.getConfig();
        return config != null && config.modEnabled && config.smoothScrolling;
    }
}
