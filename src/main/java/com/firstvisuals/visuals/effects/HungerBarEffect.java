package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;

public class HungerBarEffect {
    private boolean enabled = true;
    private float pulseScale = 1f;
    private float targetScale = 1f;
    private int lastFoodLevel = 20;
    private float shakeIntensity = 0f;
    private float colorTransition = 0f;
    private int warningThreshold = 6;
    private boolean animationEnabled = true;
    private boolean glowEnabled = true;

    public void onFoodChange(int oldFood, int newFood) {
        if (newFood < oldFood) {
            targetScale = 1.15f;
            shakeIntensity = 3f;
            colorTransition = 1f;
        } else if (newFood > oldFood) {
            targetScale = 1.05f;
        }
        lastFoodLevel = newFood;
    }

    public void onTick() {
        if (pulseScale > targetScale) {
            pulseScale = MathHelper.lerp(0.1f, pulseScale, targetScale);
        } else if (pulseScale < 1f) {
            pulseScale = MathHelper.lerp(0.05f, pulseScale, 1f);
        }

        if (Math.abs(pulseScale - targetScale) < 0.01f) {
            targetScale = 1f;
        }

        if (shakeIntensity > 0) {
            shakeIntensity = MathHelper.lerp(0.15f, shakeIntensity, 0f);
        }

        if (colorTransition > 0) {
            colorTransition = MathHelper.lerp(0.03f, colorTransition, 0f);
        }
    }

    public void renderHungerBar(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        float shakeX = shakeIntensity > 0 ? (random() - 0.5f) * shakeIntensity : 0;
        float shakeY = shakeIntensity > 0 ? (random() - 0.5f) * shakeIntensity : 0;

        int drawX = (int) (x + shakeX);
        int drawY = (int) (y + shakeY);

        int pulsedWidth = (int) (width * pulseScale);
        int offsetX = (width - pulsedWidth) / 2;

        // Background
        matrices.fill(drawX, drawY, drawX + width, drawY + height, 0x88000000);

        // Check if low hunger warning
        if (lastFoodLevel <= warningThreshold && lastFoodLevel > 0) {
            float flash = (float) Math.sin(System.currentTimeMillis() * 0.01f) * 0.5f + 0.5f;
            int warningColor = (int) (flash * 255) << 24 | 0xFF5500;
            matrices.fill(drawX, drawY, drawX + width, drawY + height, warningColor);
        }

        // Fill
        float foodPercent = (float) lastFoodLevel / 20f;
        int fillWidth = (int) (width * foodPercent);

        int fillColor = foodPercent > 0.5 ? 0xFF5500 : 0xFF0000;

        if (colorTransition > 0) {
            int flashColor = (int) (colorTransition * 255) << 24 | 0xFFFFFF;
            matrices.fill(drawX + offsetX, drawY, drawX + offsetX + pulsedWidth, drawY + height, flashColor);
        }

        if (glowEnabled && pulseScale > 1f) {
            int glowAlpha = (int) ((pulseScale - 1f) * 200);
            int glowColor = (glowAlpha << 24) | fillColor;
            matrices.fill(drawX + offsetX - 2, drawY - 2, drawX + offsetX + pulsedWidth + 2, drawY + height + 2, glowColor);
        }

        matrices.fill(drawX + offsetX, drawY, drawX + offsetX + fillWidth, drawY + height, fillColor);
    }

    private float random() {
        return (float) Math.random();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            pulseScale = 1f;
            targetScale = 1f;
            shakeIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setWarningThreshold(int threshold) {
        this.warningThreshold = threshold;
    }
}