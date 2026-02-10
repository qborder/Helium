package com.helium.mixin.render;

import com.helium.render.HeliumBlockEntityCulling;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class BlockEntityCullingMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void helium$registerBlockEntityCulling(CallbackInfo ci) {
        HeliumBlockEntityCulling.register();
    }
}
