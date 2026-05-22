package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class PortalOverlayEffect {
    private boolean enabled = true;
    private float portalIntensity = 0f;
    private float colorShift = 0f;
    private boolean animatedPortal = true;
    private int portalColor1 = 0x8800AA;
    private int portalColor2 = 0xFF0055;
    private float wavePhase = 0f;
    private float distortionIntensity = 0f;
    private Random random = new Random();

    public void onEnterPortal() {
        portalIntensity = 1f;
        distortionIntensity = 1f;
    }

    public void onExitPortal() {
        portalIntensity = 0f;
        distortionIntensity = 0f;
    }

    public void onTick() {
        if (portalIntensity > 0) {
            portalIntensity = MathHelper.lerp(0.02f, portalIntensity, 0f);
        }

        if (distortionIntensity > 0) {
            distortionIntensity = MathHelper.lerp(0.03f, distortionIntensity, 0f);
        }

        if (animatedPortal) {
            wavePhase += 0.05f;
            colorShift += 0.01f;
            if (colorShift > 1f) colorShift = 0f;
        }
    }

    public void renderPortalOverlay(GuiGraphics matrices) {
        if (!enabled || portalIntensity <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int alpha = (int) (portalIntensity * 150);

        // Animated portal colors
        float[] rgb1 = hueToRgb(colorShift);
        float[] rgb2 = hueToRgb(colorShift + 0.5f);

        int color1 = (alpha << 24) | ((int)(rgb1[0] * 255) << 16) | ((int)(rgb1[1] * 255) << 8) | ((int)(rgb1[2] * 255));
        int color2 = (alpha << 24) | ((int)(rgb2[0] * 255) << 16) | ((int)(rgb2[1] * 255) << 8) | ((int)(rgb2[2] * 255));

        // Portal swirl effect (simplified)
        if (distortionIntensity > 0.3f) {
            matrices.fillGradient(0, 0, width, height, color1, color2);
        }

        // Edge glow
        matrices.fillGradient(0, 0, width, height / 4, color1, 0);
        matrices.fillGradient(0, height - height / 4, width, height, 0, color2);
    }

    private float[] hueToRgb(float hue) {
        float h = hue * 6f;
        float x = 1f - Math.abs((h % 2f) - 1f);
        if (h < 1f) return new float[]{1f, x, 0f};
        if (h < 2f) return new float[]{x, 1f, 0f};
        if (h < 3f) return new float[]{0f, 1f, x};
        if (h < 4f) return new float[]{0f, x, 1f};
        if (h < 5f) return new float[]{x, 0f, 1f};
        return new float[]{1f, 0f, x};
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setAnimated(boolean animated) {
        this.animatedPortal = animated;
    }
}