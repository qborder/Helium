package com.helium.config;

import com.helium.HeliumClient;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.nio.file.Path;

public final class HeliumConfigScreen {

    private static final Path EXPORT_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("helium-export.json");

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
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("helium.config.category.tools"))
                        .tooltip(Text.translatable("helium.config.category.tools.tooltip"))
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable("helium.config.export"))
                                .description(OptionDescription.of(
                                        Text.translatable("helium.config.export.description")))
                                .action((screen, button) -> {
                                    boolean success = config.exportToFile(EXPORT_PATH);
                                    HeliumClient.LOGGER.info(success
                                            ? "config exported to " + EXPORT_PATH
                                            : "config export failed");
                                })
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable("helium.config.import"))
                                .description(OptionDescription.of(
                                        Text.translatable("helium.config.import.description")))
                                .action((screen, button) -> {
                                    HeliumConfig imported = HeliumConfig.importFromFile(EXPORT_PATH);
                                    if (imported != null) {
                                        config.copyFrom(imported);
                                        config.save();
                                        HeliumClient.LOGGER.info("config imported and saved");
                                    } else {
                                        HeliumClient.LOGGER.warn("config import failed - file not found or invalid");
                                    }
                                })
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("helium.config.category.developer"))
                        .tooltip(Text.translatable("helium.config.category.developer.tooltip"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("helium.config.dev_mode"))
                                .description(OptionDescription.of(
                                        Text.translatable("helium.config.dev_mode.description")))
                                .binding(defaults.devMode, () -> config.devMode, v -> config.devMode = v)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .save(config::save)
                .build()
                .generateScreen(parent);
    }
}
