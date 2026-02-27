package com.helium.compat;

import com.helium.HeliumClient;
import net.minecraft.text.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;

public final class HeliumSodiumLegacyPage {

    private HeliumSodiumLegacyPage() {}

    public static Object createHeliumPage() {
        try {
            Class<?> optionClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.Option");
            Class<?> optionGroupClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionGroup");
            Class<?> immutableListClass = Class.forName("com.google.common.collect.ImmutableList");

            Object emptyOptions = immutableListClass.getMethod("of").invoke(null);

            Constructor<?> groupCtor = optionGroupClass.getDeclaredConstructor(immutableListClass);
            groupCtor.setAccessible(true);
            Object emptyGroup = groupCtor.newInstance(emptyOptions);

            Object groupsList = immutableListClass.getMethod("of", Object.class).invoke(null, emptyGroup);

            Class<?> optionPageClass = Class.forName("net.caffeinemc.mods.sodium.client.gui.options.OptionPage");
            Text heliumName = Text.literal("Helium");
            Constructor<?> pageCtor = optionPageClass.getConstructor(Text.class, immutableListClass);
            return pageCtor.newInstance(heliumName, groupsList);
        } catch (Throwable t) {
            HeliumClient.LOGGER.debug("helium legacy sodium: failed to create helium page - {}", t.getMessage());
            return null;
        }
    }
}
