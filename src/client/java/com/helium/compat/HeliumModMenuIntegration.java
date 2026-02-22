package com.helium.compat;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class HeliumModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (HeliumClient.isAndroid()) {
            return parent -> new HeliumIncompatibleScreen(parent);
        }
        return HeliumConfigScreen::create;
    }
}
