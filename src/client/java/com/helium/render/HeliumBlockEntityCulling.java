package com.helium.render;

import com.helium.HeliumClient;
import com.helium.config.HeliumConfig;
import net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderHandler;
import net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderPredicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public final class HeliumBlockEntityCulling {

    private static volatile boolean registered = false;

    private HeliumBlockEntityCulling() {}

    public static boolean isRegistered() {
        return registered;
    }

    @SuppressWarnings("unchecked")
    public static void register() {
        if (registered) return;
        registered = true;

        BlockEntityRenderPredicate<BlockEntity> predicate = (world, pos, entity) -> {
            HeliumConfig config = HeliumClient.getConfig();
            if (config == null || !config.modEnabled || !config.blockEntityCulling) return true;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return true;

            double dist = client.player.getBlockPos().getSquaredDistance(pos);
            double maxDist = config.blockEntityCullDistance * config.blockEntityCullDistance;
            return dist <= maxDist;
        };

        try {
            BlockEntityRenderHandler handler = BlockEntityRenderHandler.instance();
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.CHEST, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.SIGN, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.HANGING_SIGN, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.BANNER, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.BELL, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.CAMPFIRE, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.ENCHANTING_TABLE, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.END_PORTAL, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.END_GATEWAY, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.DECORATED_POT, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.BED, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.SHULKER_BOX, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.SKULL, predicate);
            handler.addRenderPredicate((BlockEntityType<BlockEntity>) (BlockEntityType<?>) BlockEntityType.CONDUIT, predicate);
            HeliumClient.LOGGER.info("block entity culling registered via sodium api");
        } catch (Exception e) {
            HeliumClient.LOGGER.warn("failed to register block entity culling", e);
        }
    }
}
