package com.helium.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class HeliumMixinPlugin implements IMixinConfigPlugin {

    private boolean hasOpenGlStateManager = false;
    private boolean hasPlatformGlStateManager = false;
    private boolean hasImmediatelyFast = false;
    private boolean hasSodiumConfigApi = false;
    private boolean hasSodiumOptionsGui = false;

    @Override
    public void onLoad(String mixinPackage) {
        hasOpenGlStateManager = classExistsOnClasspath("com/mojang/blaze3d/opengl/GlStateManager.class");
        hasPlatformGlStateManager = classExistsOnClasspath("com/mojang/blaze3d/platform/GlStateManager.class");
        hasImmediatelyFast = FabricLoader.getInstance().isModLoaded("immediatelyfast");
        hasSodiumConfigApi = classExistsOnClasspath("net/caffeinemc/mods/sodium/api/config/ConfigEntryPoint.class");
        hasSodiumOptionsGui = classExistsOnClasspath("net/caffeinemc/mods/sodium/client/gui/SodiumOptionsGUI.class");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("GlStateManagerMixin")) {
            return hasOpenGlStateManager && !hasImmediatelyFast;
        }
        if (mixinClassName.endsWith("GlStateManagerLegacyMixin")) {
            return hasPlatformGlStateManager && !hasImmediatelyFast;
        }
        if (mixinClassName.endsWith("SodiumOptionsGUIFallbackMixin")) {
            return !hasSodiumConfigApi && hasSodiumOptionsGui;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private boolean classExistsOnClasspath(String resourcePath) {
        return getClass().getClassLoader().getResource(resourcePath) != null;
    }
}
