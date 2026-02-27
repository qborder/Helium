package com.helium.mixin.compat;

import com.helium.HeliumClient;
import com.helium.compat.HeliumSodiumLegacyPage;
import com.helium.config.HeliumConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI", remap = false)
public abstract class SodiumOptionsGUILegacyMixin {

    @Unique
    private Object helium$dummyPage;

    @Unique
    private Object helium$previousPage;

    @Unique
    private Field helium$pagesField;

    @Unique
    private Field helium$currentPageField;

    @Inject(method = "<init>", at = @At("TAIL"), require = 0)
    private void helium$addHeliumPage(Screen prevScreen, CallbackInfo ci) {
        try {
            Class<?> thisClass = this.getClass();

            helium$pagesField = helium$findField(thisClass, "pages");
            helium$currentPageField = helium$findField(thisClass, "currentPage");

            if (helium$pagesField == null || helium$currentPageField == null) {
                HeliumClient.LOGGER.debug("legacy sodium compat: could not find pages/currentPage fields");
                return;
            }

            helium$pagesField.setAccessible(true);
            helium$currentPageField.setAccessible(true);

            helium$dummyPage = HeliumSodiumLegacyPage.createHeliumPage();
            if (helium$dummyPage == null) return;

            @SuppressWarnings("unchecked")
            List<Object> pages = (List<Object>) helium$pagesField.get(this);
            pages.add(helium$dummyPage);

            helium$previousPage = helium$currentPageField.get(this);
        } catch (Throwable t) {
            HeliumClient.LOGGER.debug("legacy sodium compat: failed to add helium page - {}", t.getMessage());
        }
    }

    @Inject(method = "rebuildGUI", at = @At("HEAD"), require = 0, cancellable = true)
    private void helium$interceptRebuildGUI(CallbackInfo ci) {
        try {
            if (helium$dummyPage == null || helium$currentPageField == null || helium$pagesField == null) return;

            Object currentPage = helium$currentPageField.get(this);

            if (currentPage != null && currentPage != helium$dummyPage) {
                helium$previousPage = currentPage;
            }

            if (currentPage == helium$dummyPage) {
                @SuppressWarnings("unchecked")
                List<Object> pages = (List<Object>) helium$pagesField.get(this);
                Object restorePage = helium$previousPage != null ? helium$previousPage : pages.get(0);
                helium$currentPageField.set(this, restorePage);
                ci.cancel();

                Screen parentScreen = (Screen)(Object)this;
                MinecraftClient.getInstance().execute(() ->
                    MinecraftClient.getInstance().setScreen(HeliumConfigScreen.create(parentScreen))
                );
            }
        } catch (Throwable t) {
            HeliumClient.LOGGER.debug("legacy sodium compat: rebuildGUI intercept failed - {}", t.getMessage());
        }
    }
    
    @Unique
    private Field helium$findField(Class<?> clazz, String name) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
