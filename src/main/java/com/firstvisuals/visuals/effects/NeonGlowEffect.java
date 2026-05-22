package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class NeonGlowEffect {
    private boolean enabled = true;
    private float glowIntensity = 0f;
    private float targetIntensity = 0f;
    private float colorR = 0.3f;
    private float colorG = 0.8f;
    private float colorB = 1.0f;
    private float pulseSpeed = 0.08f;
    private boolean rainbowMode = false;
    private float rainbowHue = 0f;
    private int glowRadius = 5;
    private Random random = new Random();
    private long lastUpdate = 0;

    public void trigger(float intensity) {
        this.targetIntensity = MathHelper.clamp(intensity, 0f, 2f);
    }

    public void onTick() {
        // Smooth intensity transition
        if (glowIntensity < targetIntensity) {
            glowIntensity = MathHelper.lerp(pulseSpeed, glowIntensity, targetIntensity);
        } else if (glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(pulseSpeed * 0.5f, glowIntensity, 0f);
        }

        // Rainbow mode rotation
        if (rainbowMode) {
            rainbowHue += 0.01f;
            if (rainbowHue > 1f) rainbowHue = 0f;
            float[] rgb = hueToRgb(rainbowHue);
            colorR = rgb[0];
            colorG = rgb[1];
            colorB = rgb[2];
        }

        lastUpdate = System.currentTimeMillis();
    }

    public void renderGui(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled || glowIntensity <= 0) return;

        int alpha = (int)(glowIntensity * 100);
        if (alpha <= 0) return;

        // Draw glow border around GUI element
        int glowColor = (alpha & 0xFF) << 24
                | ((int)(colorR * 255) & 0xFF) << 16
                | ((int)(colorG * 255) & 0xFF) << 8
                | ((int)(colorB * 255) & 0xFF);

        // Top edge
        matrices.fill(x - glowRadius, y - glowRadius, x + width + glowRadius, y, glowColor);
        // Bottom edge
        matrices.fill(x - glowRadius, y + height, x + width + glowRadius, y + height + glowRadius, glowColor);
        // Left edge
        matrices.fill(x - glowRadius, y, x, y + height, glowColor);
        // Right edge
        matrices.fill(x + width, y, x + width + glowRadius, y + height, glowColor);

        // Corner glow
        for (int i = 0; i < glowRadius; i++) {
            float cornerAlpha = (1f - (float) i / glowRadius) * alpha;
            int cornerColor = ((int)(cornerAlpha) & 0xFF) << 24 | glowColor & 0x00FFFFFF;
            // Top-left
            matrices.fill(x - glowRadius + i, y - glowRadius + i, x - glowRadius + i + 1, y - glowRadius + i + 1, cornerColor);
        }
    }

    public void renderTextGlow(GuiGraphics matrices, String text, int x, int y, int color) {
        if (!enabled || glowIntensity <= 0) return;

        int glowAlpha = (int)(glowIntensity * 150);
        int glowColor = (glowAlpha & 0xFF) << 24 | color & 0x00FFFFFF;

        // Render text with glow (offset copies)
        MinecraftClient client = MinecraftClient.getInstance();
        matrices.drawString(client.textRenderer, text, x - 1, y, glowColor);
        matrices.drawString(client.textRenderer, text, x + 1, y, glowColor);
        matrices.drawString(client.textRenderer, text, x, y - 1, glowColor);
        matrices.drawString(client.textRenderer, text, x, y + 1, glowColor);
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
        if (!enabled) {
            glowIntensity = 0f;
            targetIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setColor(float r, float g, float b) {
        this.colorR = MathHelper.clamp(r, 0f, 1f);
        this.colorG = MathHelper.clamp(g, 0f, 1f);
        this.colorB = MathHelper.clamp(b, 0f, 1f);
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }

    public boolean isRainbowMode() {
        return rainbowMode;
    }

    public float getGlowIntensity() {
        return glowIntensity;
    }
}