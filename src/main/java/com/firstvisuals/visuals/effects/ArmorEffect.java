package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ArmorEffect {
    private boolean enabled = true;
    private float[] durabilityAlerts = new float[]{0f, 0f, 0f, 0f}; // For each armor piece
    private int[] lastDurability = new int[]{0, 0, 0, 0};
    private float damageFlash = 0f;
    private boolean animationEnabled = true;
    private float glowIntensity = 0f;
    private boolean durabilityWarningEnabled = true;
    private int warningThreshold = 20;
    private int warningColor = 0xFFFF0000;
    private Random random = new Random();

    public void onArmorDurabilityChange(int slot, int oldDur, int newDur, int maxDur) {
        float percent = (float) newDur / maxDur * 100f;

        if (newDur < oldDur) {
            // Took damage
            damageFlash = 1f;
            glowIntensity = 1f;
        }

        if (durabilityWarningEnabled && percent <= warningThreshold) {
            durabilityAlerts[slot] = 1f;
        }

        lastDurability[slot] = newDur;
    }

    public void onTick() {
        for (int i = 0; i < 4; i++) {
            if (durabilityAlerts[i] > 0) {
                durabilityAlerts[i] = MathHelper.lerp(0.02f, durabilityAlerts[i], 0f);
            }
        }

        if (damageFlash > 0) {
            damageFlash = MathHelper.lerp(0.05f, damageFlash, 0f);
        }

        if (glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(0.03f, glowIntensity, 0f);
        }
    }

    public void renderArmorPiece(GuiGraphics matrices, int slot, int x, int y, int width, int height, int durability, int maxDurability) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Background
        matrices.fill(x, y, x + width, y + height, 0x88000000);

        // Damage flash
        if (damageFlash > 0) {
            int flashAlpha = (int) (damageFlash * 100);
            int flashColor = (flashAlpha << 24) | 0xFF0000;
            matrices.fill(x, y, x + width, y + height, flashColor);
        }

        // Durability fill
        float percent = (float) durability / maxDurability;
        int fillWidth = (int) (width * percent);

        int fillColor;
        if (percent > 0.6f) {
            fillColor = 0xFF555555;
        } else if (percent > 0.3f) {
            fillColor = 0xFFAA5500;
        } else {
            fillColor = 0xFFFF0000;
        }

        matrices.fill(x, y, x + fillWidth, y + height, fillColor);

        // Glow for low durability
        if (durabilityAlerts[slot] > 0) {
            int glowAlpha = (int) (durabilityAlerts[slot] * 150);
            int glowColor = (glowAlpha << 24) | warningColor;
            matrices.fill(x - 2, y - 2, x + width + 2, y, glowColor);
        }

        // Flash animation for warning
        if (percent <= warningThreshold / 100f) {
            float flash = (float) Math.sin(System.currentTimeMillis() * 0.01f) * 0.5f + 0.5f;
            int warningAlpha = (int) (flash * 255);
            int warningFlashColor = (warningAlpha << 24) | warningColor;
            matrices.fill(x, y, x + width, y + height, warningFlashColor);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            for (int i = 0; i < 4; i++) {
                durabilityAlerts[i] = 0f;
            }
            damageFlash = 0f;
            glowIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setWarningThreshold(int threshold) {
        this.warningThreshold = MathHelper.clamp(threshold, 1, 100);
    }
}