package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class CrosshairEffect {
    private boolean enabled = true;
    private float dotScale = 1.0f;
    private float lineLength = 1.0f;
    private float gapSize = 1.0f;
    private boolean dynamicSpread = false;
    private float damagePulse = 1.0f;
    private int dotColor = 0xFFFFFFFF;
    private boolean glowEnabled = true;
    private float pulseTimer = 0f;
    private Random random = new Random();

    public void onHit() {
        damagePulse = 1.8f;
        dotScale = 2.0f;
        pulseTimer = 10;
    }

    public void onTick() {
        // Decay pulse effects
        if (damagePulse > 1.0f) {
            damagePulse = MathHelper.lerp(0.1f, damagePulse, 1.0f);
        }

        if (dotScale > 1.0f) {
            dotScale = MathHelper.lerp(0.15f, dotScale, 1.0f);
        }

        if (pulseTimer > 0) {
            pulseTimer--;
        }

        // Dynamic spread based on movement
        if (dynamicSpread) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player.isMoving()) {
                lineLength = MathHelper.lerp(0.05f, lineLength, 1.3f);
            } else {
                lineLength = MathHelper.lerp(0.1f, lineLength, 1.0f);
            }
        }
    }

    public void renderCrosshair(GuiGraphics matrices, int centerX, int centerY) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        float scale = damagePulse;
        int scaledGap = (int) (4 * gapSize * scale);
        int scaledLength = (int) (8 * lineLength * scale);
        int dotSize = (int) (2 * dotScale * scale);

        // Glow effect
        if (glowEnabled && damagePulse > 1.0f) {
            int glowColor = ((int)((damagePulse - 1f) * 100) & 0xFF) << 24 | 0x00FFFF;
            // Up
            matrices.fill(centerX - 1, centerY - scaledGap - scaledLength - 2,
                    centerX + 1, centerY - scaledGap, glowColor);
            // Down
            matrices.fill(centerX - 1, centerY + scaledGap,
                    centerX + 1, centerY + scaledGap + scaledLength + 2, glowColor);
            // Left
            matrices.fill(centerX - scaledGap - scaledLength - 2, centerY - 1,
                    centerX - scaledGap, centerY + 1, glowColor);
            // Right
            matrices.fill(centerX + scaledGap, centerY - 1,
                    centerX + scaledGap + scaledLength + 2, centerY + 1, glowColor);
        }

        // Dot
        matrices.fill(centerX - dotSize / 2, centerY - dotSize / 2,
                centerX + dotSize / 2, centerY + dotSize / 2, dotColor);

        // Static lines
        // Up
        matrices.fill(centerX - 1, centerY - scaledGap - scaledLength,
                centerX + 1, centerY - scaledGap, dotColor);
        // Down
        matrices.fill(centerX - 1, centerY + scaledGap,
                centerX + 1, centerY + scaledGap + scaledLength, dotColor);
        // Left
        matrices.fill(centerX - scaledGap - scaledLength, centerY - 1,
                centerX - scaledGap, centerY + 1, dotColor);
        // Right
        matrices.fill(centerX + scaledGap, centerY - 1,
                centerX + scaledGap + scaledLength, centerY + 1, dotColor);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setDotColor(int color) {
        this.dotColor = color;
    }

    public void setDynamicSpread(boolean dynamic) {
        this.dynamicSpread = dynamic;
    }

    public void setGlowEnabled(boolean glow) {
        this.glowEnabled = glow;
    }

    public void setLineLength(float length) {
        this.lineLength = MathHelper.clamp(length, 0.5f, 2f);
    }
}