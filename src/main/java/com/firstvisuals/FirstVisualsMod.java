package com.firstvisuals;

import com.firstvisuals.visuals.VisualManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstVisualsMod implements ClientModInitializer {
    public static final String MOD_ID = "firstvisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static VisualManager VISUAL_MANAGER;

    @Override
    public void onInitializeClient() {
        LOGGER.info("FirstVisuals initializing...");

        VISUAL_MANAGER = new VisualManager();

        // Register tick events for animation updates
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            VISUAL_MANAGER.onTick();
        });

        // Register HUD rendering
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            VISUAL_MANAGER.renderHud(matrices, tickDelta);
        });

        LOGGER.info("FirstVisuals initialized!");
    }
}