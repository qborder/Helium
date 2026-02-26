package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.EnumValueCache;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Direction.class)
public abstract class DirectionValuesMixin {

    @Unique
    private static boolean helium$failed = false;

    @Inject(method = "values", at = @At("HEAD"), cancellable = true, require = 0)
    private static void helium$cachedValues(CallbackInfoReturnable<Direction[]> cir) {
        if (helium$failed) return;
        try {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.cachedEnumValues) return;
            if (!EnumValueCache.isInitialized()) return;

            Direction[] cached = EnumValueCache.getDirections();
            if (cached != null) {
                cir.setReturnValue(cached);
            }
        } catch (Throwable t) {
            helium$failed = true;
        }
    }
}
