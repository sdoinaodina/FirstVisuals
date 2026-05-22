package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.entity boss.BossBarColor;
import net.minecraft.util.math.MathHelper;

public class BossBarEffect {
    private boolean enabled = true;
    private float fillPercent = 1f;
    private float targetFill = 1f;
    private float pulseIntensity = 0f;
    private int shimmerTimer = 0;
    private boolean gradientEnabled = true;
    private int baseColor = 0xFF5555;
    private float darkenAmount = 0f;

    public void onBossBarUpdate(float percent) {
        this.targetFill = MathHelper.clamp(percent, 0f, 1f);
        this.fillPercent = MathHelper.lerp(0.05f, fillPercent, targetFill);

        if (percent < targetFill) {
            pulseIntensity = 1f;
        }

        shimmerTimer++;
    }

    public void onTick() {
        // Smooth fill transition
        if (Math.abs(fillPercent - targetFill) > 0.01f) {
            fillPercent = MathHelper.lerp(0.05f, fillPercent, targetFill);
        }

        // Pulse decay
        if (pulseIntensity > 0) {
            pulseIntensity = MathHelper.lerp(0.02f, pulseIntensity, 0f);
        }

        // Shimmer effect (health segment flash)
        if (shimmerTimer > 20) shimmerTimer = 0;
    }

    public void renderBossBar(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Background
        matrices.fill(x, y, x + width, y + height, 0x88000000);

        // Fill
        int fillWidth = (int) (width * fillPercent);

        if (gradientEnabled) {
            int color1 = baseColor;
            if (pulseIntensity > 0) {
                int r = (baseColor >> 16) & 0xFF;
                int g = (baseColor >> 8) & 0xFF;
                int b = (baseColor >> 0) & 0xFF;
                r = Math.min(255, r + (int)(pulseIntensity * 100));
                color1 = 0xFF000000 | (r << 16) | (g << 8) | b;
            }

            int color2 = darkenAmount > 0 ? darken(color1, darkenAmount) : color1;

            matrices.fillGradient(x, y, x + fillWidth, y + height, color1, color2);
        } else {
            int fillColor = baseColor;
            if (pulseIntensity > 0) {
                int r = (baseColor >> 16) & 0xFF;
                int g = (baseColor >> 8) & 0xFF;
                int b = (baseColor >> 0) & 0xFF;
                r = Math.min(255, r + (int)(pulseIntensity * 100));
                fillColor = 0xFF000000 | (r << 16) | (g << 8) | b;
            }

            matrices.fill(x, y, x + fillWidth, y + height, fillColor);
        }

        // Shimmer line (segment dividers)
        if (fillWidth > 10 && shimmerTimer < 10) {
            float shimmerPos = shimmerTimer / 10f;
            int shimmerX = x + (int)(width * fillPercent * shimmerPos);
            int shimmerAlpha = (int)((1f - shimmerPos) * 150);
            matrices.fill(shimmerX - 1, y, shimmerX, y + height, (shimmerAlpha << 24) | 0xFFFFFF);
        }

        // Border glow
        if (pulseIntensity > 0) {
            int glowColor = ((int)(pulseIntensity * 100) & 0xFF) << 24 | baseColor;
            matrices.fill(x - 2, y - 2, x + width + 2, y, glowColor);
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

    public void setBaseColor(int color) {
        this.baseColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setGradientEnabled(boolean gradient) {
        this.gradientEnabled = gradient;
    }
}