package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityCullingMixin<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void helium$cullDistantEntities(T entity, net.minecraft.client.render.Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled || !config.entityCulling) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (entity instanceof PlayerEntity) return;

        double dist = client.player.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ());
        double maxDist = config.entityCullDistance * config.entityCullDistance;
        if (dist > maxDist) {
            cir.setReturnValue(false);
        }
    }
}
