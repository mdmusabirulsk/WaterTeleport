package com.waterteleport.client;

import com.waterteleport.WaterTeleportMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.jspecify.annotations.NullMarked;

/**
 * Client-side entrypoint for the WaterTeleport mod on Minecraft 1.20.1.
 */
@NullMarked
public class WaterTeleportClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                WaterTeleportMod.THROWN_WATER_BUCKET,
                ThrownItemRenderer::new
        );
    }
}
