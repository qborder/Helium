package com.helium.mixin.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import com.helium.render.RenderPipeline;
import com.helium.render.TemporalReprojection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityCullingMixin<T extends Entity> {

    @Unique
    private static boolean helium$frustumFailed = false;

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void helium$cullDistantEntities(T entity, net.minecraft.client.render.Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        HeliumConfig config = HeliumClient.getConfig();
        if (config == null || !config.modEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (entity instanceof PlayerEntity) return;

        double dx = entity.getX() - client.player.getX();
        double dy = entity.getY() - client.player.getY();
        double dz = entity.getZ() - client.player.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;

        if (config.entityCulling) {
            double maxDist = config.entityCullDistance * config.entityCullDistance;
            if (distSq > maxDist) {
                cir.setReturnValue(false);
                return;
            }

            if (!helium$frustumFailed) {
                try {
                    float yaw = client.player.getYaw();
                    float yawRad = (float) Math.toRadians(yaw);
                    double forwardX = -Math.sin(yawRad);
                    double forwardZ = Math.cos(yawRad);
                    double dot = dx * forwardX + dz * forwardZ;
                    if (dot < -16.0 && distSq > 256.0) {
                        cir.setReturnValue(false);
                        return;
                    }
                } catch (Throwable t) {
                    helium$frustumFailed = true;
                }
            }
        }

        if (config.renderPipelining && RenderPipeline.isInitialized()) {
            if (!RenderPipeline.isEntityVisible(entity.getId())) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (config.temporalReprojection && TemporalReprojection.isInitialized()) {
            if (!(entity instanceof HostileEntity)) {
                if (TemporalReprojection.shouldSkipEntity(distSq)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
