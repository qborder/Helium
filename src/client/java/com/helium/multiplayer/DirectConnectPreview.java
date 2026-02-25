package com.helium.multiplayer;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.text.Text;

import java.net.UnknownHostException;

public final class DirectConnectPreview {

    private static String _lastAddress = "";
    private static long _lastPingTime = 0;
    private static final long DEBOUNCE_MS = 500;

    private DirectConnectPreview() {}

    public static void onAddressChanged(String address) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.directConnectPreview) {
            return;
        }

        if (address == null || address.isBlank()) return;

        long now = System.currentTimeMillis();
        if (address.equals(_lastAddress) && now - _lastPingTime < DEBOUNCE_MS) {
            return;
        }

        _lastAddress = address;
        _lastPingTime = now;

        Thread.startVirtualThread(() -> pingServer(address));
    }

    private static void pingServer(String address) {
        ServerInfo info = new ServerInfo(address, address, ServerInfo.ServerType.OTHER);
        MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();

        try {
            pinger.add(info, () -> {}, () -> {
                dispatchResult(info);
            }, NetworkingBackend.remote(true));
        } catch (UnknownHostException e) {
            info.label = Text.literal("Unknown host");
            info.playerCountLabel = Text.literal("0/0");
            info.setStatus(ServerInfo.Status.UNREACHABLE);
            dispatchResult(info);
        }
    }

    private static void dispatchResult(ServerInfo info) {
        if (info == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            Screen screen = client.currentScreen;
            if (screen instanceof ServerPreviewUpdater updater) {
                if (info.label != null) {
                    updater.helium$setMotdText(info.label);
                } else {
                    updater.helium$setMotdText(Text.empty());
                }

                String[] motdLines = info.label != null ? info.label.getString().split("\n") : new String[]{""};
                updater.helium$updateServerData(
                        info.name != null ? info.name : "",
                        motdLines,
                        info.playerCountLabel != null ? info.playerCountLabel.getString() : "0/0",
                        info.ping
                );

                updater.helium$updateFavicon(info.getFavicon());
            }
        });
    }
}
