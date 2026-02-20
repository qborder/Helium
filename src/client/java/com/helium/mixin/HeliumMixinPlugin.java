package com.helium.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class HeliumMixinPlugin implements IMixinConfigPlugin {

    private boolean hasOpenGlStateManager = false;
    private boolean hasPlatformGlStateManager = false;
    private boolean hasImmediatelyFast = false;
    private boolean isDoubleMathVersion = false;

    @Override
    public void onLoad(String mixinPackage) {
        hasOpenGlStateManager = classExistsOnClasspath("com/mojang/blaze3d/opengl/GlStateManager.class");
        hasPlatformGlStateManager = classExistsOnClasspath("com/mojang/blaze3d/platform/GlStateManager.class");
        hasImmediatelyFast = FabricLoader.getInstance().isModLoaded("immediatelyfast");
        isDoubleMathVersion = detectDoubleMathVersion();
    }

    private boolean detectDoubleMathVersion() {
        try {
            String mcVersionString = FabricLoader.getInstance()
                    .getModContainer("minecraft")
                    .map(c -> c.getMetadata().getVersion().getFriendlyString())
                    .orElse(null);

            if (mcVersionString == null) return false;

            // Parse version to compare major.minor.patch
            String[] parts = mcVersionString.split("\\.");
            if (parts.length < 2) return false;
            
            try {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                
                // Minecraft 1.21.2+ uses double for sin/cos
                if (major > 1) return true;
                if (major == 1 && minor > 21) return true;
                if (major == 1 && minor == 21 && parts.length > 2) {
                    int patch = Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
                    return patch >= 2;
                }
            } catch (NumberFormatException e) {
                // If parsing fails, assume it's a newer version and use double
                return true;
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
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
        if (mixinClassName.endsWith("MathHelperMixin")) {
            return !isDoubleMathVersion;
        }
        if (mixinClassName.endsWith("MathHelperDoubleMixin")) {
            return isDoubleMathVersion;
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
