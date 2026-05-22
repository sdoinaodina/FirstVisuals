package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class FoodLevelEffect {
    private boolean enabled = true;
    private float saturationPulse = 0f;
    private float hungerFlash = 0f;
    private int lastFoodLevel = 20;
    private float animationIntensity = 1f;
    private boolean glowEffect = true;
    private boolean animationEnabled = true;
    private int warningThreshold = 6;
    private int warningColor = 0xFFFF0000;
    private Random random = new Random();

    public void onFoodChange(int oldFood, int newFood) {
        lastFoodLevel = newFood;

        if (newFood < oldFood) {
            hungerFlash = 1f;
            animationIntensity = 1f;
        }

        if (newFood <= warningThreshold && newFood > 0) {
            saturationPulse = 1f;
        }

        if (newFood > oldFood && newFood >= 20) {
            saturationPulse = 0.5f;
        }
    }

    public void onTick() {
        if (hungerFlash > 0) {
            hungerFlash = MathHelper.lerp(0.05f, hungerFlash, 0f);
        }

        if (saturationPulse > 0) {
            saturationPulse = MathHelper.lerp(0.02f, saturationPulse, 0f);
        }

        if (animationIntensity > 0.5f) {
            animationIntensity = MathHelper.lerp(0.03f, animationIntensity, 0.5f);
        }
    }

    public void renderFoodBar(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Saturation level indicator
        float saturationPercent = 0f; // Would be calculated from actual player data
        if (saturationPercent > 0) {
            int satWidth = (int) (width * saturationPercent);
            int satColor = (int) (saturationPulse * 150) << 24 | 0xFF8800;
            matrices.fill(x, y, x + satWidth, y + height, satColor);
        }

        // Hunger flash effect
        if (hungerFlash > 0) {
            int flashAlpha = (int) (hungerFlash * 100);
            matrices.fill(x, y, x + width, y + height, (flashAlpha << 24) | 0xFF0000);
        }

        // Warning flash for low food
        if (lastFoodLevel <= warningThreshold && lastFoodLevel > 0 && animationEnabled) {
            float flash = (float) Math.sin(System.currentTimeMillis() * 0.01f) * 0.5f + 0.5f;
            int warningAlpha = (int) (flash * 255);
            matrices.fill(x, y, x + width, y + height, (warningAlpha << 24) | warningColor);
        }

        // Glow effect when low
        if (glowEffect && lastFoodLevel <= warningThreshold) {
            int glowAlpha = (int) (saturationPulse * 100);
            matrices.fill(x - 2, y - 2, x + width + 2, y, (glowAlpha << 24) | warningColor);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            hungerFlash = 0f;
            saturationPulse = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setWarningThreshold(int threshold) {
        this.warningThreshold = MathHelper.clamp(threshold, 1, 20);
    }

    public void setGlowEffect(boolean glow) {
        this.glowEffect = glow;
    }
}