package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class NauseaEffect {
    private boolean enabled = true;
    private float nauseaIntensity = 0f;
    private float tunnelVision = 0f;
    private boolean animationEnabled = true;
    private int nauseaColor1 = 0x88AA0000;
    private int nauseaColor2 = 0x88880000;
    private float spinPhase = 0f;
    private float distortIntensity = 0f;
    private Random random = new Random();

    public void onNauseaStart() {
        nauseaIntensity = 1f;
        tunnelVision = 0.3f;
        distortIntensity = 1f;
    }

    public void onNauseaEnd() {
        nauseaIntensity = 0f;
        tunnelVision = 0f;
        distortIntensity = 0f;
    }

    public void onTick() {
        if (nauseaIntensity > 0) {
            nauseaIntensity = MathHelper.lerp(0.01f, nauseaIntensity, 0f);
        }

        if (tunnelVision > 0 && nauseaIntensity <= 0) {
            tunnelVision = MathHelper.lerp(0.03f, tunnelVision, 0f);
        }

        if (distortIntensity > 0) {
            distortIntensity = MathHelper.lerp(0.02f, distortIntensity, 0f);
        }

        if (animationEnabled && nauseaIntensity > 0) {
            spinPhase += 3f;
            if (spinPhase > 360f) spinPhase = 0f;
        }
    }

    public void renderNauseaOverlay(GuiGraphics matrices) {
        if (!enabled || nauseaIntensity <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int alpha = (int) (nauseaIntensity * 150);

        // Spinning vignette colors
        matrices.fillGradient(0, 0, width, height, (alpha << 24) | (nauseaColor1 & 0x00FFFFFF), (alpha << 24) | (nauseaColor2 & 0x00FFFFFF));

        // Tunnel vision edges
        if (tunnelVision > 0.1f) {
            int tunnelSize = (int) (width * 0.3f * tunnelVision);
            matrices.fill(0, 0, tunnelSize, height, ((int) (tunnelVision * 200) << 24));
            matrices.fill(width - tunnelSize, 0, width, height, ((int) (tunnelVision * 200) << 24));
            matrices.fill(0, 0, width, tunnelSize, ((int) (tunnelVision * 200) << 24));
            matrices.fill(0, height - tunnelSize, width, height, ((int) (tunnelVision * 200) << 24));
        }

        // Distortion overlay (simplified swirl effect)
        if (distortIntensity > 0.3f) {
            int distortAlpha = (int) ((distortIntensity - 0.3f) * 100);
            matrices.fill(0, 0, width, height, (distortAlpha << 24) | 0xAAAA00);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            nauseaIntensity = 0f;
            tunnelVision = 0f;
            distortIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setNauseaColors(int color1, int color2) {
        this.nauseaColor1 = 0xFF000000 | (color1 & 0x00FFFFFF);
        this.nauseaColor2 = 0xFF000000 | (color2 & 0x00FFFFFF);
    }
}