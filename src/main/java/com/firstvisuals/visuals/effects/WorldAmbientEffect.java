package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class WorldAmbientEffect {
    private boolean enabled = true;
    private float fogDensity = 0f;
    private float skyColorR = 0.3f;
    private float skyColorG = 0.5f;
    private float skyColorB = 0.9f;
    private boolean underwaterEffect = false;
    private float waveIntensity = 0f;
    private boolean netherAmbient = false;
    private float netherHue = 0f;
    private Random random = new Random();

    public void onDimensionChange(String dimension) {
        if (dimension.contains("nether")) {
            netherAmbient = true;
            fogDensity = 0.5f;
            netherHue = 0f;
        } else if (dimension.contains("end")) {
            netherAmbient = false;
            fogDensity = 0.2f;
            skyColorR = 0.1f;
            skyColorG = 0.05f;
            skyColorB = 0.2f;
        } else {
            netherAmbient = false;
            fogDensity = 0f;
            skyColorR = 0.3f;
            skyColorG = 0.5f;
            skyColorB = 0.9f;
        }
    }

    public void onTick() {
        if (fogDensity > 0) {
            fogDensity = MathHelper.lerp(0.01f, fogDensity, 0f);
        }

        if (waveIntensity > 0) {
            waveIntensity = MathHelper.lerp(0.02f, waveIntensity, 0f);
        }

        if (netherAmbient) {
            netherHue += 0.005f;
            if (netherHue > 1f) netherHue = 0f;
            float[] rgb = hueToRgb(netherHue);
            skyColorR = rgb[0] * 0.8f;
            skyColorG = rgb[1] * 0.2f;
            skyColorB = rgb[2] * 0.3f;
        }
    }

    public void renderSky(GuiGraphics matrices) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Sky gradient overlay
        int skyColor = (200 << 24) | ((int)(skyColorR * 255) << 16) | ((int)(skyColorG * 255) << 8) | ((int)(skyColorB * 255));

        // Top gradient
        matrices.fillGradient(0, 0, width, height / 3, skyColor, 0);
    }

    public void renderFog(GuiGraphics matrices) {
        if (!enabled || fogDensity <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int fogAlpha = (int) (fogDensity * 150);
        int fogColor = (fogAlpha << 24) | ((int)(skyColorR * 255) << 16) | ((int)(skyColorG * 255) << 8) | ((int)(skyColorB * 255));

        // Fog overlay at edges
        matrices.fillGradient(0, 0, width, height, fogColor, 0);
        matrices.fillGradient(0, height - height/4, width, height, 0, fogColor);
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

    public void setFogDensity(float density) {
        this.fogDensity = MathHelper.clamp(density, 0f, 1f);
    }

    public void setSkyColor(float r, float g, float b) {
        this.skyColorR = MathHelper.clamp(r, 0f, 1f);
        this.skyColorG = MathHelper.clamp(g, 0f, 1f);
        this.skyColorB = MathHelper.clamp(b, 0f, 1f);
    }
}