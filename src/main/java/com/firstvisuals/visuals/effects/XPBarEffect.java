package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class XPBarEffect {
    private boolean enabled = true;
    private float levelPulse = 0f;
    private float progressPulse = 0f;
    private int lastLevel = 0;
    private float progressScale = 1f;
    private boolean levelUpAnimation = false;
    private int levelUpEffect = 0;
    private boolean rainbowProgress = false;
    private float hue = 0f;
    private Random random = new Random();

    public void onXPChange(int level, float progress) {
        if (level > lastLevel) {
            levelPulse = 1f;
            levelUpAnimation = true;
            levelUpEffect = 30;
        }
        progressPulse = 1f;
        progressScale = 1.2f;
        lastLevel = level;
    }

    public void onTick() {
        if (levelPulse > 0) {
            levelPulse = MathHelper.lerp(0.03f, levelPulse, 0f);
        }

        if (progressScale > 1f) {
            progressScale = MathHelper.lerp(0.1f, progressScale, 1f);
        }

        if (levelUpEffect > 0) {
            levelUpEffect--;
            if (levelUpEffect <= 0) {
                levelUpAnimation = false;
            }
        }

        if (rainbowProgress) {
            hue += 0.02f;
            if (hue > 1f) hue = 0f;
        }
    }

    public void renderXPBar(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        int scaledHeight = (int) (height * progressScale);
        int offsetY = (height - scaledHeight) / 2;

        // Background
        matrices.fill(x, y + offsetY, x + width, y + offsetY + scaledHeight, 0x88000000);

        // Level up flash
        if (levelUpAnimation && levelUpEffect > 20) {
            int flashColor = 0xFFFFAA00;
            matrices.drawString(client.textRenderer, "LEVEL UP!", x, y - 15, flashColor);
        }

        // Progress fill
        int fillWidth = (int) (width * 0.75f); // Typical XP progress

        int progressColor;
        if (rainbowProgress) {
            float[] rgb = hueToRgb(hue);
            progressColor = 0xFF000000 | ((int)(rgb[0] * 255) << 16) | ((int)(rgb[1] * 255) << 8) | ((int)(rgb[2] * 255));
        } else {
            progressColor = 0xFF55FF55;
        }

        if (progressPulse > 0) {
            int flashAlpha = (int) (progressPulse * 100);
            int flashColor = (flashAlpha << 24) | 0xFFFFFF;
            matrices.fill(x, y + offsetY, x + width, y + offsetY + scaledHeight, flashColor);
        }

        matrices.fill(x, y + offsetY, x + fillWidth, y + offsetY + scaledHeight, progressColor);

        // Level badge glow
        if (levelPulse > 0) {
            int glowAlpha = (int) (levelPulse * 150);
            int glowColor = (glowAlpha << 24) | 0xFFFF00;
            matrices.drawString(client.textRenderer, "" + lastLevel, x + 2, y + offsetY + 2, glowColor);
        }
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowProgress(boolean rainbow) {
        this.rainbowProgress = rainbow;
    }
}