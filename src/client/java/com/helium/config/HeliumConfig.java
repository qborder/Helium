package com.helium.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.helium.HeliumClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HeliumConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("helium.json");

    public boolean modEnabled = true;

    public boolean fastMath = true;
    public boolean glStateCache = true;
    public boolean memoryOptimizations = true;
    public boolean threadOptimizations = true;
    public boolean networkOptimizations = true;
    public boolean fastStartup = true;

    public boolean entityCulling = true;
    public int entityCullDistance = 64;
    public boolean blockEntityCulling = true;
    public int blockEntityCullDistance = 48;
    public boolean particleCulling = true;
    public int particleCullDistance = 32;
    public boolean animationThrottling = true;

    public boolean fastServerPing = true;
    public boolean preserveScrollOnRefresh = true;

    public static HeliumConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                HeliumConfig cfg = GSON.fromJson(json, HeliumConfig.class);
                if (cfg != null) {
                    cfg.save();
                    return cfg;
                }
            } catch (IOException e) {
                HeliumClient.LOGGER.warn("failed to load config, using defaults", e);
            }
        }

        HeliumConfig cfg = new HeliumConfig();
        cfg.save();
        return cfg;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            HeliumClient.LOGGER.warn("failed to save config", e);
        }
    }
}
