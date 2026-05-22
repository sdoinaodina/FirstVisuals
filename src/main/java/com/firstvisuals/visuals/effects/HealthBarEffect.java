package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;

public class HealthBarEffect {
    private boolean enabled = true;
    private float pulseScale = 1.0f;
    private float targetPulse = 1.0f;
    private float maxPulse = 1.15f;
    private float colorTransition = 0f;
    private boolean animationEnabled = true;
    private boolean glowEnabled = true;
    private float shakeIntensity = 0f;
    private int lastHealth = 20;
    private int displayHealth = 20;

    public void onHealthChange(int oldHealth, int newHealth) {
        if (!enabled) return;

        if (newHealth < oldHealth) {
            // Damage taken - pulse and shake
            targetPulse = maxPulse;
            shakeIntensity = (oldHealth - newHealth) * 0.3f;
            colorTransition = 1f;
        } else if (newHealth > oldHealth) {
            // Health regen - subtle pulse
            targetPulse = 1.05f;
            colorTransition = 0.5f;
        }

        lastHealth = newHealth;
    }

    public void onTick() {
        // Pulse animation
        if (pulseScale > targetPulse) {
            pulseScale = MathHelper.lerp(0.1f, pulseScale, targetPulse);
        } else if (pulseScale < 1.0f) {
            pulseScale = MathHelper.lerp(0.05f, pulseScale, 1.0f);
        }

        // Color transition decay
        if (colorTransition > 0) {
            colorTransition = MathHelper.lerp(0.03f, colorTransition, 0f);
        }

        // Shake decay
        if (shakeIntensity > 0) {
            shakeIntensity = MathHelper.lerp(0.15f, shakeIntensity, 0f);
        }

        // Smooth health display interpolation
        if (displayHealth != lastHealth) {
            displayHealth = (int) MathHelper.lerp(0.1f, displayHealth, lastHealth);
        }

        // Snap when close
        if (Math.abs(pulseScale - targetPulse) < 0.01f) {
            targetPulse = 1.0f;
        }
    }

    public void renderHealthBar(GuiGraphics matrices, int x, int y, int width, int height, int currentHealth, int maxHealth) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Apply shake offset
        float shakeX = shakeIntensity > 0 ? (float) (Math.random() - 0.5) * shakeIntensity : 0;
        float shakeY = shakeIntensity > 0 ? (float) (Math.random() - 0.5) * shakeIntensity : 0;

        int drawX = (int) (x + shakeX);
        int drawY = (int) (y + shakeY);

        // Apply pulse scale
        int pulsedWidth = (int) (width * pulseScale);
        int offsetX = (width - pulsedWidth) / 2;

        // Background
        matrices.fill(drawX, drawY, drawX + width, drawY + height, 0x88000000);

        // Health bar
        float healthPercent = (float) currentHealth / maxHealth;
        int healthWidth = (int) (width * healthPercent);

        // Color based on health percentage and damage state
        int healthColor;
        if (healthPercent > 0.6f) {
            healthColor = 0xFF3333;
        } else if (healthPercent > 0.3f) {
            healthColor = 0xFFAA00;
        } else {
            healthColor = 0xFF0000;
        }

        // Add damage flash
        if (colorTransition > 0) {
            int flashColor = (int)(colorTransition * 255) << 24 | 0xFFFFFF;
            matrices.fill(drawX + offsetX, drawY, drawX + offsetX + pulsedWidth, drawY + height, flashColor);
        }

        // Glow effect
        if (glowEnabled && pulseScale > 1.0f) {
            int glowAlpha = (int)((pulseScale - 1f) * 200);
            int glowColor = (glowAlpha & 0xFF) << 24 | healthColor;
            matrices.fill(drawX + offsetX - 2, drawY - 2, drawX + offsetX + pulsedWidth + 2, drawY + height + 2, glowColor);
        }

        matrices.fill(drawX + offsetX, drawY, drawX + offsetX + healthWidth, drawY + height, healthColor);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            pulseScale = 1.0f;
            targetPulse = 1.0f;
            shakeIntensity = 0f;
            colorTransition = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setMaxPulse(float max) {
        this.maxPulse = MathHelper.clamp(max, 1f, 2f);
    }

    public void setGlowEnabled(boolean glow) {
        this.glowEnabled = glow;
    }

    public float getPulseScale() {
        return pulseScale;
    }
}