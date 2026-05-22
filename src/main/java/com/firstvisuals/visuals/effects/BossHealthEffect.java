package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class BossHealthEffect {
    private boolean enabled = true;
    private float healthPercent = 1f;
    private float targetHealth = 1f;
    private float pulseEffect = 0f;
    private int shimmerTimer = 0;
    private boolean animationEnabled = true;
    private int barColor = 0xFF5500;
    private float glowIntensity = 0f;
    private boolean gradientFill = true;
    private Random random = new Random();

    public void onBossHealthChange(float current, float max) {
        targetHealth = current / max;
        if (current < targetHealth * max) {
            pulseEffect = 1f;
            glowIntensity = 1f;
        }
    }

    public void onTick() {
        if (Math.abs(healthPercent - targetHealth) > 0.01f) {
            healthPercent = MathHelper.lerp(0.05f, healthPercent, targetHealth);
        }

        if (pulseEffect > 0) {
            pulseEffect = MathHelper.lerp(0.02f, pulseEffect, 0f);
        }

        if (glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(0.03f, glowIntensity, 0f);
        }

        if (animationEnabled) {
            shimmerTimer++;
            if (shimmerTimer > 15) shimmerTimer = 0;
        }
    }

    public void renderBossBar(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Background
        matrices.fill(x, y, x + width, y + height, 0x88000000);

        // Health fill
        int fillWidth = (int) (width * healthPercent);

        int fillColor1 = barColor;
        int fillColor2 = darken(barColor, 0.3f);

        // Pulse on damage
        if (pulseEffect > 0) {
            int r = (fillColor1 >> 16) & 0xFF;
            int g = (fillColor1 >> 8) & 0xFF;
            int b = fillColor1 & 0xFF;
            r = Math.min(255, r + (int)(pulseEffect * 100));
            fillColor1 = 0xFF000000 | (r << 16) | (g << 8) | b;
        }

        if (gradientFill) {
            matrices.fillGradient(x, y, x + fillWidth, y + height, fillColor1, fillColor2);
        } else {
            matrices.fill(x, y, x + fillWidth, y + height, fillColor1);
        }

        // Shimmer effect
        if (shimmerTimer < 8) {
            float shimmerPos = shimmerTimer / 8f;
            int shimmerX = x + (int)(width * healthPercent * shimmerPos);
            int shimmerAlpha = (int)((1f - shimmerPos) * 150);
            matrices.fill(shimmerX - 1, y, shimmerX, y + height, (shimmerAlpha << 24) | 0xFFFFFF);
        }

        // Glow effect
        if (glowIntensity > 0) {
            int glowAlpha = (int)(glowIntensity * 100);
            int glowColor = (glowAlpha << 24) | fillColor1;
            matrices.fill(x - 2, y - 2, x + width + 2, y, glowColor);
            matrices.fill(x - 2, y + height, x + width + 2, y + height + 2, glowColor);
        }
    }

    private int darken(int color, float amount) {
        int r = (int) ((color >> 16 & 0xFF) * (1 - amount));
        int g = (int) ((color >> 8 & 0xFF) * (1 - amount));
        int b = (int) ((color & 0xFF) * (1 - amount));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setBarColor(int color) {
        this.barColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setGradientFill(boolean gradient) {
        this.gradientFill = gradient;
    }
}