package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ShieldEffect {
    private boolean enabled = true;
    private float shieldPulse = 0f;
    private float shieldBreakAnimation = 0f;
    private boolean breakEffectEnabled = true;
    private int shieldColor = 0x8800AAAA;
    private float glowIntensity = 0f;
    private boolean shimmerEnabled = true;
    private float shimmerPhase = 0f;
    private Random random = new Random();

    public void onShieldHit() {
        shieldPulse = 1f;
        glowIntensity = 1f;
    }

    public void onShieldBreak() {
        shieldBreakAnimation = 1f;
    }

    public void onTick() {
        if (shieldPulse > 0) {
            shieldPulse = MathHelper.lerp(0.05f, shieldPulse, 0f);
        }

        if (shieldBreakAnimation > 0) {
            shieldBreakAnimation = MathHelper.lerp(0.03f, shieldBreakAnimation, 0f);
        }

        if (glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(0.04f, glowIntensity, 0f);
        }

        if (shimmerEnabled) {
            shimmerPhase += 0.03f;
            if (shimmerPhase > 360f) shimmerPhase = 0f;
        }
    }

    public void renderShieldOverlay(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Shield damage pulse
        if (shieldPulse > 0) {
            int pulseAlpha = (int) (shieldPulse * 150);
            int pulseColor = (pulseAlpha << 24) | (shieldColor & 0x00FFFFFF);
            matrices.fill(x, y, x + width, y + height, pulseColor);
        }

        // Break effect
        if (shieldBreakAnimation > 0 && breakEffectEnabled) {
            for (int i = 0; i < 12; i++) {
                float angle = (float) Math.toRadians(i * 30);
                float dist = shieldBreakAnimation * 50;
                int px = x + width / 2 + (int) (Math.cos(angle) * dist);
                int py = y + height / 2 + (int) (Math.sin(angle) * dist);
                int pAlpha = (int) (shieldBreakAnimation * 255);
                matrices.fill(px - 2, py - 2, px + 2, py + 2, (pAlpha << 24) | shieldColor);
            }
        }

        // Glow effect
        if (glowIntensity > 0) {
            int glowAlpha = (int) (glowIntensity * 100);
            int glowColor = (glowAlpha << 24) | (shieldColor & 0x00FFFFFF);
            matrices.fill(x - 3, y - 3, x + width + 3, y, glowColor);
            matrices.fill(x - 3, y + height, x + width + 3, y + height + 3, glowColor);
            matrices.fill(x - 3, y, x, y + height, glowColor);
            matrices.fill(x + width, y, x + width + 3, y + height, glowColor);
        }

        // Shimmer effect
        if (shimmerEnabled && shieldPulse <= 0) {
            float shimmerPos = (float) Math.sin(shimmerPhase) * 0.5f + 0.5f;
            int shimmerX = x + (int) (width * shimmerPos);
            int shimmerAlpha = (int) (50 + Math.sin(shimmerPhase) * 30);
            matrices.fill(shimmerX, y, shimmerX + 2, y + height, (shimmerAlpha << 24) | 0xFFFFFF);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            shieldPulse = 0f;
            shieldBreakAnimation = 0f;
            glowIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setShieldColor(int color) {
        this.shieldColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setBreakEffectEnabled(boolean breakEffect) {
        this.breakEffectEnabled = breakEffect;
    }
}