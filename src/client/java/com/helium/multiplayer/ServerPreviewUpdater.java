package com.helium.multiplayer;

import net.minecraft.text.Text;

public interface ServerPreviewUpdater {
    void helium$updateServerData(String name, String[] motd, String players, long ping);
    void helium$updateFavicon(byte[] favicon);
    void helium$setMotdText(Text motd);
}
