package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ScreenPulseEffect {
    private boolean enabled = true;
    private float alpha = 0f;
    private int timer = 0;
    private int duration = 300;
    private float intensity = 1.0f;
    private float colorR = 0.8f;
    private float colorG = 0.2f;
    private float colorB = 0.2f;
    private boolean gradientEnabled = true;
    private float animationPhase = 0f;
    private Random random = new Random();

    public void trigger(float intensity, int duration) {
        this.alpha = MathHelper.clamp(intensity, 0f, 1f);
        this.timer = duration;
        this.intensity = intensity;
        this.duration = duration;
        this.animationPhase = 0f;
    }

    public void onTick() {
        if (timer > 0) {
            timer--;
            float progress = 1f - ((float) timer / duration);
            alpha = (1f - easeOutQuad(progress)) * intensity;
            animationPhase += 0.1f;
        } else {
            alpha = 0f;
        }
    }

    private float easeOutQuad(float t) {
        return t * (2 - t);
    }

    public void render(GuiGraphics matrices, float tickDelta) {
        if (!enabled || alpha <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        if (gradientEnabled) {
            // Animated gradient effect
            float wave = (float) Math.sin(animationPhase) * 0.2f;
            int colorTop = ((int)(alpha * 150) & 0xFF) << 24
                    | ((int)((colorR + wave) * 255) & 0xFF) << 16
                    | ((int)((colorG + wave * 0.5f) * 255) & 0xFF) << 8
                    | ((int)(colorB * 255) & 0xFF);

            int colorBottom = ((int)(alpha * 100) & 0xFF) << 24
                    | ((int)(colorR * 255) & 0xFF) << 16
                    | ((int)(colorG * 255) & 0xFF) << 8
                    | ((int)(colorB * 255) & 0xFF);

            matrices.fillGradient(0, 0, width, height, colorTop, colorBottom);
        } else {
            int color = ((int)(alpha * 150) & 0xFF) << 24
                    | ((int)(colorR * 255) & 0xFF) << 16
                    | ((int)(colorG * 255) & 0xFF) << 8
                    | ((int)(colorB * 255) & 0xFF);
            matrices.fill(0, 0, width, height, color);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setColor(float r, float g, float b) {
        this.colorR = MathHelper.clamp(r, 0f, 1f);
        this.colorG = MathHelper.clamp(g, 0f, 1f);
        this.colorB = MathHelper.clamp(b, 0f, 1f);
    }

    public void setGradientEnabled(boolean gradient) {
        this.gradientEnabled = gradient;
    }

    public float[] getColor() {
        return new float[]{colorR, colorG, colorB};
    }
}