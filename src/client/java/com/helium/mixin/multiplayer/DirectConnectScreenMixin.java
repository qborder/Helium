package com.helium.mixin.multiplayer;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.multiplayer.DirectConnectPreview;
import com.helium.multiplayer.ServerPreviewUpdater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(DirectConnectScreen.class)
public abstract class DirectConnectScreenMixin extends Screen implements ServerPreviewUpdater {

    @Unique private static final Identifier PING_1 = Identifier.of("helium", "gui/serverlist/ping_1.png");
    @Unique private static final Identifier PING_2 = Identifier.of("helium", "gui/serverlist/ping_2.png");
    @Unique private static final Identifier PING_3 = Identifier.of("helium", "gui/serverlist/ping_3.png");
    @Unique private static final Identifier PING_4 = Identifier.of("helium", "gui/serverlist/ping_4.png");
    @Unique private static final Identifier PING_5 = Identifier.of("helium", "gui/serverlist/ping_5.png");
    @Unique private static final Identifier PINGING_1 = Identifier.of("helium", "gui/serverlist/pinging_1.png");
    @Unique private static final Identifier PINGING_2 = Identifier.of("helium", "gui/serverlist/pinging_2.png");
    @Unique private static final Identifier PINGING_3 = Identifier.of("helium", "gui/serverlist/pinging_3.png");
    @Unique private static final Identifier PINGING_4 = Identifier.of("helium", "gui/serverlist/pinging_4.png");
    @Unique private static final Identifier PINGING_5 = Identifier.of("helium", "gui/serverlist/pinging_5.png");
    @Unique private static final Identifier DEFAULT_ICON = Identifier.of("helium", "gui/serverlist/default_icon.png");

    @Shadow private TextFieldWidget addressField;

    @Unique private String helium$lastAddress = "";
    @Unique private String helium$serverName = "";
    @Unique private Text helium$motdText = Text.empty();
    @Unique private String helium$playerCount = "0/0";
    @Unique private long helium$pingValue = -1;
    @Unique private WorldIcon helium$serverIcon = null;
    @Unique private byte[] helium$lastFavicon = null;

    protected DirectConnectScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void helium$updateServerData(String name, String[] motd, String players, long ping) {
        this.helium$serverName = name != null ? name : "";
        this.helium$playerCount = players != null ? players : "0/0";
        this.helium$pingValue = ping;
    }

    @Override
    public void helium$setMotdText(Text motd) {
        this.helium$motdText = motd != null ? motd : Text.empty();
    }

    @Override
    public void helium$updateFavicon(byte[] faviconBytes) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isOnThread()) {
            client.execute(() -> helium$updateFavicon(faviconBytes));
            return;
        }

        if (faviconBytes == null) {
            if (helium$serverIcon != null) {
                helium$serverIcon.close();
                helium$serverIcon = null;
            }
            helium$lastFavicon = null;
            return;
        }

        if (helium$lastFavicon != null && Arrays.equals(faviconBytes, helium$lastFavicon)) {
            return;
        }
        helium$lastFavicon = faviconBytes;

        byte[] valid = ServerInfo.validateFavicon(faviconBytes);
        if (valid == null) {
            return;
        }

        try {
            if (helium$serverIcon != null) {
                helium$serverIcon.close();
                helium$serverIcon = null;
            }

            String idSource = helium$lastAddress != null && !helium$lastAddress.isBlank() ? helium$lastAddress : "helium_preview";
            helium$serverIcon = WorldIcon.forServer(client.getTextureManager(), idSource);

            NativeImage img = NativeImage.read(valid);
            helium$serverIcon.load(img);
        } catch (Exception e) {
            if (helium$serverIcon != null) {
                helium$serverIcon.close();
                helium$serverIcon = null;
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void helium$renderServerPreview(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.directConnectPreview) {
            return;
        }

        if (addressField != null) {
            String address = addressField.getText();
            if (address != null && !address.isBlank() && !address.equals(helium$lastAddress)) {
                helium$lastAddress = address;
                DirectConnectPreview.onAddressChanged(address);
            }
        }

        int maxRowWidth = 305;
        int minRowWidth = 200;
        int sidePadding = 20;
        int rowWidth = Math.min(maxRowWidth, Math.max(minRowWidth, this.width - sidePadding * 2));
        int baseX = (this.width - rowWidth) / 2;

        int baseY = (addressField != null)
                ? addressField.getY() + addressField.getHeight() + 8
                : this.height / 2 + 30;

        int iconSize = 32;

        if (helium$serverIcon != null) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, helium$serverIcon.getTextureId(),
                    baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, DEFAULT_ICON,
                    baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);
        }

        int textX = baseX + iconSize + 3;
        context.drawTextWithShadow(this.textRenderer, Text.literal(helium$serverName), textX, baseY, 0xFFFFFFFF);

        if (helium$motdText != null && !helium$motdText.getString().isEmpty()) {
            int motdY = baseY + 12;
            int lineHeight = this.textRenderer.fontHeight;
            int availableWidth = rowWidth - iconSize - 10;
            List<OrderedText> lines = this.textRenderer.wrapLines(helium$motdText, availableWidth);
            for (OrderedText line : lines) {
                context.drawTextWithShadow(this.textRenderer, line, textX, motdY, 0xFFFFFFFF);
                motdY += lineHeight;
            }
        }

        Identifier pingTexture;
        if (helium$pingValue < 0) {
            long tick = System.currentTimeMillis() / 100L;
            int frame = (int) (tick % 8);
            if (frame > 4) frame = 8 - frame;

            pingTexture = switch (frame) {
                case 1 -> PINGING_2;
                case 2 -> PINGING_3;
                case 3 -> PINGING_4;
                case 4 -> PINGING_5;
                default -> PINGING_1;
            };
        } else if (helium$pingValue < 150L) pingTexture = PING_5;
        else if (helium$pingValue < 300L) pingTexture = PING_4;
        else if (helium$pingValue < 600L) pingTexture = PING_3;
        else if (helium$pingValue < 1000L) pingTexture = PING_2;
        else pingTexture = PING_1;

        int pingX = baseX + rowWidth - 10 - 5;
        int pingWidth = 10;
        int pingHeight = 8;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, pingTexture, pingX, baseY,
                0, 0, pingWidth, pingHeight, pingWidth, pingHeight);

        if (helium$pingValue >= 0) {
            String players = helium$playerCount.contains("/") ? helium$playerCount.split("/")[0] : helium$playerCount;
            String maxPlayers = helium$playerCount.contains("/") ? helium$playerCount.split("/")[1] : "0";
            String slash = "/";

            int playersWidth = this.textRenderer.getWidth(players);
            int slashWidth = this.textRenderer.getWidth(slash);
            int maxPlayersWidth = this.textRenderer.getWidth(maxPlayers);

            int playerTextX = pingX - (playersWidth + slashWidth + maxPlayersWidth) - 5;

            context.drawTextWithShadow(this.textRenderer, Text.literal(players), playerTextX, baseY, 0xFFAAAAAA);
            context.drawTextWithShadow(this.textRenderer, Text.literal(slash), playerTextX + playersWidth, baseY, 0xFF555555);
            context.drawTextWithShadow(this.textRenderer, Text.literal(maxPlayers), playerTextX + playersWidth + slashWidth, baseY, 0xFFAAAAAA);
        }
    }
}
