package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class SpeedEffect {
    private boolean enabled = true;
    private float speedLines = 0f;
    private float motionBlur = 0f;
    private float FOVChange = 0f;
    private boolean speedTrails = false;
    private float intensity = 0f;
    private int trailColor = 0x88FFFF00;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private Random random = new Random();

    public void onSpeedStart() {
        speedLines = 1f;
        motionBlur = 1f;
        FOVChange = 1f;
        intensity = 1f;
    }

    public void onSpeedStop() {
        speedLines = 0f;
        motionBlur = 0f;
        FOVChange = 0f;
    }

    public void onTick() {
        if (speedLines > 0 && intensity > 0) {
            speedLines = MathHelper.lerp(0.02f, speedLines, 0f);
        }

        if (motionBlur > 0) {
            motionBlur = MathHelper.lerp(0.05f, motionBlur, 0f);
        }

        if (FOVChange > 0) {
            FOVChange = MathHelper.lerp(0.03f, FOVChange, 0f);
        }

        if (rainbowMode && speedLines > 0) {
            hue += 0.02f;
            if (hue > 1f) hue = 0f;
        }
    }

    public void renderSpeedEffect(GuiGraphics matrices) {
        if (!enabled || speedLines <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int color;
        if (rainbowMode) {
            int[] rgb = hueToRgbInt(hue);
            color = ((int) (speedLines * 100) << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
        } else {
            color = ((int) (speedLines * 100) << 24) | (trailColor & 0x00FFFFFF);
        }

        // Speed lines from edges
        int lineCount = (int) (speedLines * 20);
        for (int i = 0; i < lineCount; i++) {
            // Horizontal speed lines
            int y = random.nextInt(height);
            int lineLength = (int) (speedLines * 100);
            matrices.fill(0, y, lineLength, y + 1, color);
            matrices.fill(width - lineLength, y, width, y + 1, color);
        }

        // Motion blur effect (vignette style)
        if (motionBlur > 0.3f) {
            int blurAlpha = (int) (motionBlur * 100);
            matrices.fillGradient(0, 0, width, 10, (blurAlpha << 24), 0);
            matrices.fillGradient(0, height - 10, width, height, 0, (blurAlpha << 24));
        }
    }

    private int[] hueToRgbInt(float hue) {
        float h = hue * 6f;
        float x = 1f - Math.abs((h % 2f) - 1f);
        if (h < 1f) return new int[]{255, (int)(x * 255), 0};
        if (h < 2f) return new int[]{(int)(x * 255), 255, 0};
        if (h < 3f) return new int[]{0, 255, (int)(x * 255)};
        if (h < 4f) return new int[]{0, (int)(x * 255), 255};
        if (h < 5f) return new int[]{(int)(x * 255), 0, 255};
        return new int[]{255, 0, (int)(x * 255)};
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            speedLines = 0f;
            motionBlur = 0f;
            FOVChange = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }

    public void setTrailColor(int color) {
        this.trailColor = 0xFF000000 | (color & 0x00FFFFFF);
    }
}