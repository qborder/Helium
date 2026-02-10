package com.helium.config;

import com.helium.HeliumClient;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class HeliumConfigScreen {

    private HeliumConfigScreen() {}

    public static Screen create(Screen parent) {
        HeliumConfig config = HeliumClient.getConfig();
        HeliumConfig defaults = new HeliumConfig();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Helium"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .tooltip(Text.literal("Enable or disable the mod. Performance settings are in Sodium's video settings."))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable Helium"))
                                .description(OptionDescription.of(
                                        Text.literal("Turn Helium on or off. Requires a game restart to take effect.")))
                                .binding(defaults.modEnabled, () -> config.modEnabled, v -> config.modEnabled = v)
                                .controller(BooleanControllerBuilder::create)
                                .flag(OptionFlag.GAME_RESTART)
                                .build())
                        .build())
                .save(config::save)
                .build()
                .generateScreen(parent);
    }
}
