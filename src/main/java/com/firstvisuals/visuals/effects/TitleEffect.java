package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class TitleEffect {
    private boolean enabled = true;
    private String currentTitle = "";
    private String currentSubtitle = "";
    private float titleAlpha = 0f;
    private float subtitleAlpha = 0f;
    private float titleScale = 1f;
    private float subtitleScale = 1f;
    private float titleY = 0f;
    private float targetTitleY = 0f;
    private boolean fadeIn = false;
    private boolean fadeOut = false;
    private int fadeInTime = 10;
    private int stayTime = 70;
    private int fadeOutTime = 20;
    private int timer = 0;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private Random random = new Random();

    public void onTitle(String title, String subtitle) {
        currentTitle = title;
        currentSubtitle = subtitle;
        titleAlpha = 0f;
        subtitleAlpha = 0f;
        titleScale = 0.5f;
        subtitleScale = 0.5f;
        titleY = 20f;
        targetTitleY = 0f;
        fadeIn = true;
        fadeOut = false;
        timer = 0;
    }

    public void onTick() {
        timer++;

        if (fadeIn) {
            titleAlpha = Math.min(1f, titleAlpha + 1f / fadeInTime);
            subtitleAlpha = Math.min(1f, subtitleAlpha + 1f / fadeInTime);
            titleScale = MathHelper.lerp(0.15f, titleScale, 1f);
            subtitleScale = MathHelper.lerp(0.1f, subtitleScale, 1f);
            titleY = MathHelper.lerp(0.2f, titleY, targetTitleY);

            if (titleAlpha >= 1f) {
                fadeIn = false;
            }
        }

        if (fadeOut) {
            titleAlpha = Math.max(0f, titleAlpha - 1f / fadeOutTime);
            subtitleAlpha = Math.max(0f, subtitleAlpha - 1f / fadeOutTime);
        }

        if (!fadeIn && !fadeOut && timer > fadeInTime + stayTime) {
            fadeOut = true;
        }

        if (rainbowMode) {
            hue += 0.02f;
            if (hue > 1f) hue = 0f;
        }
    }

    public void renderTitle(GuiGraphics matrices) {
        if (!enabled || titleAlpha <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int titleX = width / 2;
        int titleYInt = (int) (height * 0.3f + titleY);

        // Title
        if (currentTitle != null && !currentTitle.isEmpty()) {
            int titleColor;
            if (rainbowMode) {
                titleColor = getRainbowColor() | ((int)(titleAlpha * 255) << 24);
            } else {
                titleColor = ((int)(titleAlpha * 255) << 24) | 0xFFFFFF;
            }

            int titleWidth = client.textRenderer.getWidth(currentTitle);
            matrices.drawString(client.textRenderer, currentTitle,
                    titleX - (int)(titleWidth * titleScale / 2),
                    titleYInt,
                    titleColor);
        }

        // Subtitle
        if (currentSubtitle != null && !currentSubtitle.isEmpty() && subtitleAlpha > 0) {
            int subtitleColor = ((int)(subtitleAlpha * 255) << 24) | 0xAAAAAA;
            int subWidth = client.textRenderer.getWidth(currentSubtitle);
            matrices.drawString(client.textRenderer, currentSubtitle,
                    titleX - (int)(subWidth * subtitleScale / 2),
                    titleYInt + (int)(30 * subtitleScale),
                    subtitleColor);
        }
    }

    private int getRainbowColor() {
        float[] rgb = hueToRgb(hue);
        return ((int)(rgb[0] * 255) << 16) | ((int)(rgb[1] * 255) << 8) | ((int)(rgb[2] * 255));
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

    public void clear() {
        currentTitle = "";
        currentSubtitle = "";
        titleAlpha = 0f;
        subtitleAlpha = 0f;
        fadeIn = false;
        fadeOut = false;
        timer = 0;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) clear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }
}