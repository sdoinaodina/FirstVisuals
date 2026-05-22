package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class VignetteEffect {
    private boolean enabled = true;
    private float intensity = 0.5f;
    private float targetIntensity = 0.5f;
    private int colorR = 0;
    private int colorG = 0;
    private int colorB = 0;
    private float pulseIntensity = 0f;
    private boolean animated = true;
    private boolean breathingEnabled = true;
    private float breathePhase = 0f;
    private Random random = new Random();

    public void trigger(float intensity) {
        this.targetIntensity = MathHelper.clamp(intensity, 0f, 1f);
        this.pulseIntensity = 1f;
    }

    public void onTick() {
        // Smooth intensity transition
        if (Math.abs(intensity - targetIntensity) > 0.01f) {
            intensity = MathHelper.lerp(0.05f, intensity, targetIntensity);
        }

        // Breathing animation
        if (breathingEnabled) {
            breathePhase += 0.02f;
        }

        // Pulse decay
        if (pulseIntensity > 0) {
            pulseIntensity = MathHelper.lerp(0.02f, pulseIntensity, 0f);
        }
    }

    public void renderVignette(GuiGraphics matrices) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        float currentIntensity = intensity;

        // Breathing effect modulation
        if (breathingEnabled) {
            float breatheMod = (float) Math.sin(breathePhase) * 0.1f;
            currentIntensity += breatheMod;
        }

        // Pulse boost
        if (pulseIntensity > 0) {
            currentIntensity += pulseIntensity * 0.3f;
        }

        currentIntensity = MathHelper.clamp(currentIntensity, 0f, 1f);

        int alpha = (int) (currentIntensity * 200);
        int color = (alpha & 0xFF) << 24 | (colorR << 16) | (colorG << 8) | colorB;

        // Draw vignette corners (simple approach)
        int vignetteSize = (int) (width * 0.3f);

        // Top-left corner
        matrices.fillGradient(0, 0, vignetteSize, vignetteSize, color, 0);
        // Top-right
        matrices.fillGradient(width - vignetteSize, 0, width, vignetteSize, 0, color);
        // Bottom-left
        matrices.fillGradient(0, height - vignetteSize, vignetteSize, height, color, 0);
        // Bottom-right
        matrices.fillGradient(width - vignetteSize, height - vignetteSize, width, height, 0, color);

        // Edge gradients
        matrices.fillGradient(0, 0, width, vignetteSize / 2, color, 0);
        matrices.fillGradient(0, height - vignetteSize / 2, width, height, 0, color);
        matrices.fillGradient(0, 0, vignetteSize / 2, height, color, 0);
        matrices.fillGradient(width - vignetteSize / 2, 0, width, height, 0, color);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setColor(int r, int g, int b) {
        this.colorR = MathHelper.clamp(r, 0, 255);
        this.colorG = MathHelper.clamp(g, 0, 255);
        this.colorB = MathHelper.clamp(b, 0, 255);
    }

    public void setIntensity(float intensity) {
        this.targetIntensity = MathHelper.clamp(intensity, 0f, 1f);
    }

    public void setBreathingEnabled(boolean breathing) {
        this.breathingEnabled = breathing;
    }
}