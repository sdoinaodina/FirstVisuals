package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class GravityEffect {
    private boolean enabled = true;
    private float particleIntensity = 0f;
    private float fallSpeed = 0f;
    private boolean fallIndicatorEnabled = true;
    private int warningColor = 0xFFFF0000;
    private float landingShock = 0f;
    private float velocityTrail = 0f;
    private Random random = new Random();

    public void onFallStart(float velocity) {
        fallSpeed = Math.min(Math.abs(velocity), 3f) / 3f;
        particleIntensity = 1f;
        velocityTrail = 1f;
    }

    public void onLanding(float impact) {
        landingShock = Math.min(impact / 3f, 1f);
        particleIntensity = 0f;
        fallSpeed = 0f;
    }

    public void onTick() {
        if (particleIntensity > 0 && fallSpeed > 0) {
            particleIntensity = MathHelper.lerp(0.02f, particleIntensity, 0.2f);
        }

        if (landingShock > 0) {
            landingShock = MathHelper.lerp(0.08f, landingShock, 0f);
        }

        if (velocityTrail > 0 && fallSpeed > 0) {
            velocityTrail = MathHelper.lerp(0.05f, velocityTrail, 0f);
        }
    }

    public void renderGravityEffect(GuiGraphics matrices, int centerX, int centerY) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Fall speed indicator
        if (fallIndicatorEnabled && fallSpeed > 0.3f) {
            float warningFlash = (float) Math.sin(System.currentTimeMillis() * 0.01f) * 0.5f + 0.5f;
            int warnAlpha = (int) (fallSpeed * warningFlash * 255);
            matrices.drawString(client.textRenderer, "FALLING", centerX - 30, centerY - 50, (warnAlpha << 24) | warningColor);
        }

        // Velocity trail particles
        if (velocityTrail > 0) {
            for (int i = 0; i < 5; i++) {
                int py = centerY + (int) (velocityTrail * 40);
                int px = centerX + (random.nextInt(30) - 15);
                int pAlpha = (int) (velocityTrail * 150);
                matrices.fill(px - 2, py - 2, px + 2, py + 2, (pAlpha << 24) | 0xAAAAAA);
            }
        }

        // Landing shock wave
        if (landingShock > 0) {
            int radius = (int) (landingShock * 40);
            int alpha = (int) (landingShock * 200);
            int color = (alpha << 24) | warningColor;

            matrices.fill(centerX - radius, centerY - 2, centerX + radius, centerY + 2, color);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            particleIntensity = 0f;
            fallSpeed = 0f;
            landingShock = 0f;
            velocityTrail = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setWarningColor(int color) {
        this.warningColor = 0xFF000000 | (color & 0x00FFFFFF);
    }
}