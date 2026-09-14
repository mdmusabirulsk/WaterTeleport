package com.waterteleport.client;

import com.waterteleport.WaterTeleportMod;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import org.jspecify.annotations.NullMarked;

/**
 * Client-side entrypoint for the WaterTeleport mod.
 * Handles registering client-only entity renderers.
 */
@NullMarked
public class WaterTeleportClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register client-side entity renderer to render the thrown water bucket as a 3D flying item
        EntityRenderers.register(
                WaterTeleportMod.THROWN_WATER_BUCKET,
                ThrownItemRenderer::new
        );
    }
}
