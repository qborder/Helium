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
                .title(Text.translatable("helium.name"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("helium.config.category.general"))
                        .tooltip(Text.translatable("helium.config.category.general.tooltip"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("helium.config.enable"))
                                .description(OptionDescription.of(
                                        Text.translatable("helium.config.enable.description")))
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
