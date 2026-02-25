package com.helium.compat;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class HeliumIncompatibleScreen extends Screen {

    private static final int ANDROID_GREEN = 0xFF3DDC84;
    private static final int HELIUM_BLUE = 0xFF5BC0EB;
    private static final int WARNING_YELLOW = 0xFFFFAA00;
    private static final int TEXT_WHITE = 0xFFFFFFFF;
    private static final int TEXT_GRAY = 0xFFB0B0B0;
    private static final int BOX_BG = 0xE0101010;
    private static final int BOX_BORDER = 0xFF333333;

    private final Screen parent;

    public HeliumIncompatibleScreen(Screen parent) {
        super(Text.translatable("helium.android.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int buttonHeight = 20;
        int buttonX = (width - buttonWidth) / 2;
        int buttonY = height / 2 + 82;

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("helium.android.button"),
                button -> {
                    HeliumConfig config = HeliumClient.getConfig();
                    if (config != null) {
                        config.androidWarningShown = true;
                        config.save();
                    }
                    client.setScreen(parent);
                }
        ).dimensions(buttonX, buttonY, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int centerX = width / 2;
        int centerY = height / 2;

        int boxW = 340;
        int boxH = 180;
        int boxX = centerX - boxW / 2;
        int boxY = centerY - boxH / 2;

        context.fill(boxX - 1, boxY - 1, boxX + boxW + 1, boxY + boxH + 1, BOX_BORDER);
        context.fill(boxX, boxY, boxX + boxW, boxY + boxH, BOX_BG);

        int y = boxY + 14;

        drawCenteredText(context, Text.translatable("helium.android.title"), centerX, y, HELIUM_BLUE);
        y += 22;

        drawCenteredText(context, Text.translatable("helium.android.warning_title"), centerX, y, WARNING_YELLOW);
        y += 16;

        drawCenteredText(context, Text.translatable("helium.android.platform"), centerX, y, ANDROID_GREEN);
        y += 18;

        drawCenteredText(context, Text.translatable("helium.android.warning_line1"), centerX, y, TEXT_GRAY);
        y += 11;
        drawCenteredText(context, Text.translatable("helium.android.warning_line2"), centerX, y, TEXT_GRAY);
        y += 16;

        drawCenteredText(context, Text.translatable("helium.android.gl_disabled"), centerX, y, WARNING_YELLOW);
        y += 11;
        drawCenteredText(context, Text.translatable("helium.android.still_works"), centerX, y, TEXT_GRAY);
    }

    private void drawCenteredText(DrawContext context, Text text, int centerX, int y, int color) {
        int x = centerX - textRenderer.getWidth(text) / 2;
        try {
            context.drawTextWithShadow(textRenderer, text, x, y, color);
        } catch (Throwable e1) {
            try {
                context.drawText(textRenderer, text, x, y, color, true);
            } catch (Throwable e2) {
                try {
                    java.lang.reflect.Method m = context.getClass().getMethod("drawCenteredTextWithShadow", net.minecraft.client.font.TextRenderer.class, Text.class, int.class, int.class, int.class);
                    m.invoke(context, textRenderer, text, centerX, y, color);
                } catch (Throwable e3) {
                    context.drawText(textRenderer, text.getString(), x, y, color, true);
                }
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
