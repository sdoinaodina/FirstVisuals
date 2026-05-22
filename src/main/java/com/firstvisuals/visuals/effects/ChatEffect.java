package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ChatEffect {
    private boolean enabled = true;
    private float slideIn = 1f;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private int bounceCount = 0;
    private float glitchIntensity = 0f;
    private float watermarkAlpha = 0.5f;
    private boolean watermarkEnabled = true;
    private Random random = new Random();

    public void onNewMessage() {
        slideIn = 0f;
        bounceCount = 3;
    }

    public void onTick() {
        // Slide in animation
        if (slideIn < 1f) {
            slideIn = MathHelper.lerp(0.12f, slideIn, 1f);
        }

        // Bounce effect
        if (bounceCount > 0) {
            bounceCount--;
        }

        // Glitch decay
        if (glitchIntensity > 0) {
            glitchIntensity = MathHelper.lerp(0.05f, glitchIntensity, 0f);
        }

        // Rainbow hue rotation
        if (rainbowMode) {
            hue += 0.01f;
            if (hue > 1f) hue = 0f;
        }
    }

    public void renderChat(GuiGraphics matrices, String message, int x, int y, int color) {
        if (!enabled || message == null) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Slide offset
        int offsetY = (int) ((1f - slideIn) * 20);
        int currentY = y + offsetY;

        // Glitch effect
        if (glitchIntensity > 0) {
            int glitchX = (random.nextInt(4) - 2);
            int glitchY = (random.nextInt(4) - 2);
            matrices.drawString(client.textRenderer, message, x + glitchX, currentY + glitchY, 0xFF0000);
        }

        // Rainbow color
        if (rainbowMode) {
            int rainbowCol = getRainbowColor();
            color = rainbowCol;
        }

        // Bounce effect
        if (bounceCount > 0) {
            int bounceOffset = (int) ((random.nextFloat() - 0.5f) * 3);
            matrices.drawString(client.textRenderer, message, x, currentY + bounceOffset, color);
        } else {
            matrices.drawString(client.textRenderer, message, x, currentY, color);
        }
    }

    public void renderWatermark(GuiGraphics matrices) {
        if (!enabled || !watermarkEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();

        String watermark = "FirstVisuals";
        int textWidth = client.textRenderer.getWidth(watermark);
        int x = width - textWidth - 5;
        int y = 5;

        int color = ((int)(watermarkAlpha * 255) & 0xFF) << 24 | 0xAAAAAA;
        matrices.drawString(client.textRenderer, watermark, x, y, color);
    }

    private int getRainbowColor() {
        float[] rgb = hueToRgb(hue);
        return 0xFF000000 | ((int)(rgb[0] * 255) << 16) | ((int)(rgb[1] * 255) << 8) | ((int)(rgb[2] * 255));
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

    public void triggerGlitch() {
        glitchIntensity = 1f;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }

    public void setWatermarkEnabled(boolean watermark) {
        this.watermarkEnabled = watermark;
    }
}