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
import java.util.concurrent.atomic.AtomicReference;

@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen", remap = false)
public abstract class ReesesOptionsLegacyMixin {

    @Unique
    private Object helium$heliumPage;

    @Unique
    private Object helium$heliumPageName;

    @Unique
    private Field helium$pagesField;

    @Unique
    private Field helium$tabSelectedField;

    @Inject(method = "<init>", at = @At("TAIL"), require = 0)
    private void helium$init(CallbackInfo ci) {
        try {
            Class<?> thisClass = this.getClass();

            helium$pagesField = helium$findField(thisClass, "pages");
            helium$tabSelectedField = helium$findStaticField(thisClass, "tabFrameSelectedTab");

            if (helium$pagesField == null) {
                HeliumClient.LOGGER.debug("reeses compat: pages field not found");
                return;
            }

            helium$pagesField.setAccessible(true);
            if (helium$tabSelectedField != null) helium$tabSelectedField.setAccessible(true);

            helium$heliumPage = HeliumSodiumLegacyPage.createHeliumPage();
            if (helium$heliumPage == null) return;

            helium$heliumPageName = helium$heliumPage.getClass().getMethod("getName").invoke(helium$heliumPage);

            @SuppressWarnings("unchecked")
            List<Object> pages = (List<Object>) helium$pagesField.get(this);
            pages.add(helium$heliumPage);
        } catch (Throwable t) {
            HeliumClient.LOGGER.debug("reeses compat: init failed - {}", t.getMessage());
        }
    }

    @Inject(method = "updateControls", at = @At("HEAD"), require = 0)
    private void helium$checkTabSwitch(CallbackInfo ci) {
        try {
            if (helium$heliumPage == null || helium$tabSelectedField == null) return;

            @SuppressWarnings("unchecked")
            AtomicReference<Object> tabRef = (AtomicReference<Object>) helium$tabSelectedField.get(null);
            if (tabRef == null) return;

            Object selected = tabRef.get();
            if (selected == null) return;

            boolean isHeliumTab = (selected == helium$heliumPageName);
            if (!isHeliumTab) {
                try {
                    String s = (String) selected.getClass().getMethod("getString").invoke(selected);
                    isHeliumTab = "Helium".equals(s);
                } catch (Throwable ignored) {
                    isHeliumTab = selected.toString().contains("Helium");
                }
            }

            if (isHeliumTab) {
                @SuppressWarnings("unchecked")
                List<Object> pages = (List<Object>) helium$pagesField.get(this);
                for (Object page : pages) {
                    if (page != helium$heliumPage) {
                        Object pageName = page.getClass().getMethod("getName").invoke(page);
                        tabRef.set(pageName);
                        break;
                    }
                }

                Screen parent = (Screen)(Object)this;
                MinecraftClient.getInstance().execute(() ->
                        MinecraftClient.getInstance().setScreen(HeliumConfigScreen.create(parent))
                );
            }
        } catch (Throwable t) {
            HeliumClient.LOGGER.debug("reeses compat: updateControls check failed - {}", t.getMessage());
        }
    }

    @Unique
    private Field helium$findField(Class<?> clazz, String name) {
        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            try { return c.getDeclaredField(name); }
            catch (NoSuchFieldException e) { c = c.getSuperclass(); }
        }
        return null;
    }

    @Unique
    private Field helium$findStaticField(Class<?> clazz, String name) {
        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            try {
                Field f = c.getDeclaredField(name);
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) return f;
            } catch (NoSuchFieldException e) { /* continue */ }
            c = c.getSuperclass();
        }
        return null;
    }
}
