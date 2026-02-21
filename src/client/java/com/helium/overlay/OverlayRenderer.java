package com.helium.overlay;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.particle.ParticleLimiter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public final class OverlayRenderer {

    private static final int PADDING = 6;
    private static final int LINE_HEIGHT = 10;
    private static final int SHADOW_OFFSET = 1;

    private static final FpsStats fpsStats = new FpsStats();

    private OverlayRenderer() {}

    public static void render(DrawContext context, MinecraftClient client) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.fpsOverlay) return;
        if (client.player == null) return;
        if (client.options.hudHidden) return;

        fpsStats.updateFps(client.getCurrentFps());

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        String[] lines = buildOverlayLines(client, config);
        int maxWidth = 0;
        for (String line : lines) {
            int w = client.textRenderer.getWidth(line);
            if (w > maxWidth) maxWidth = w;
        }

        int totalHeight = lines.length * LINE_HEIGHT;
        int boxWidth = maxWidth + PADDING * 2;
        int boxHeight = totalHeight + PADDING * 2 - 2;

        OverlayPosition position = parsePosition(config.overlayPosition);
        int[] pos = calculatePosition(position, screenWidth, screenHeight, boxWidth, boxHeight);
        int boxX = pos[0];
        int boxY = pos[1];

        int bgColor = ColorUtils.parseColor(config.overlayBackgroundColor, config.overlayTransparency);
        int textColor = ColorUtils.parseColor(config.overlayTextColor, 100);

        drawShadow(context, boxX, boxY, boxWidth, boxHeight);
        drawRoundedBox(context, boxX, boxY, boxWidth, boxHeight, bgColor);

        int textX = boxX + PADDING;
        int textY = boxY + PADDING;
        for (String line : lines) {
            context.drawText(client.textRenderer, Text.literal(line), textX, textY, textColor, false);
            textY += LINE_HEIGHT;
        }
    }

    private static String[] buildOverlayLines(MinecraftClient client, HeliumConfig config) {
        java.util.List<String> lines = new java.util.ArrayList<>();

        if (config.overlayShowFps) {
            lines.add(String.format("%d FPS", fpsStats.getCurrentFps()));
        }

        if (config.overlayShowFpsMinMaxAvg) {
            lines.add(String.format("↓%d ↑%d ~%d", fpsStats.getMinFps(), fpsStats.getMaxFps(), fpsStats.getAvgFps()));
        }

        if (config.overlayShowMemory) {
            Runtime rt = Runtime.getRuntime();
            long usedMb = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
            long maxMb = rt.maxMemory() / 1024 / 1024;
            lines.add(String.format("%dMB / %dMB", usedMb, maxMb));
        }

        if (config.overlayShowParticles && config.particleLimiting && ParticleLimiter.isInitialized()) {
            lines.add(String.format("Particles: %d/%d", ParticleLimiter.getCurrentCount(), ParticleLimiter.getMaxParticles()));
        }

        if (lines.isEmpty()) {
            lines.add("Helium");
        }

        return lines.toArray(new String[0]);
    }

    private static OverlayPosition parsePosition(String posStr) {
        if (posStr == null) return OverlayPosition.TOP_LEFT;
        return switch (posStr.toUpperCase()) {
            case "TOP_RIGHT" -> OverlayPosition.TOP_RIGHT;
            case "BOTTOM_LEFT" -> OverlayPosition.BOTTOM_LEFT;
            case "BOTTOM_RIGHT" -> OverlayPosition.BOTTOM_RIGHT;
            default -> OverlayPosition.TOP_LEFT;
        };
    }

    private static int[] calculatePosition(OverlayPosition position, int screenWidth, int screenHeight, int boxWidth, int boxHeight) {
        int margin = 10;
        return switch (position) {
            case TOP_RIGHT -> new int[] { screenWidth - boxWidth - margin, margin };
            case BOTTOM_LEFT -> new int[] { margin, screenHeight - boxHeight - margin };
            case BOTTOM_RIGHT -> new int[] { screenWidth - boxWidth - margin, screenHeight - boxHeight - margin };
            default -> new int[] { margin, margin };
        };
    }

    private static void drawShadow(DrawContext context, int x, int y, int width, int height) {
        int shadowColor = ColorUtils.createColor(0, 0, 0, 40);
        context.fill(x + SHADOW_OFFSET, y + SHADOW_OFFSET, x + width + SHADOW_OFFSET, y + height + SHADOW_OFFSET, shadowColor);
    }

    private static void drawRoundedBox(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);

        int cornerRadius = 2;
        int cornerColor = ColorUtils.withAlpha(color, (color >> 24) & 0xFF);
        
        context.fill(x - 1, y + cornerRadius, x, y + height - cornerRadius, cornerColor);
        context.fill(x + width, y + cornerRadius, x + width + 1, y + height - cornerRadius, cornerColor);
        context.fill(x + cornerRadius, y - 1, x + width - cornerRadius, y, cornerColor);
        context.fill(x + cornerRadius, y + height, x + width - cornerRadius, y + height + 1, cornerColor);
    }

    public static FpsStats getFpsStats() {
        return fpsStats;
    }
}
