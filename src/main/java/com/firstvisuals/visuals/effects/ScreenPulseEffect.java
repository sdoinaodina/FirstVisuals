package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;

public class ScreenPulseEffect {
    private boolean enabled = true;
    private float alpha = 0f;
    private int timer = 0;
    private int duration = 300;
    private float intensity = 1.0f;
    private float colorR = 0.5f;
    private float colorG = 0f;
    private float colorB = 0f;

    public void trigger(float intensity, int duration) {
        this.alpha = MathHelper.clamp(intensity, 0f, 1f);
        this.timer = duration;
        this.intensity = intensity;
        this.duration = duration;
    }

    public void onTick() {
        if (timer > 0) {
            timer--;
            alpha = ((float) timer / duration) * intensity;
        } else {
            alpha = 0f;
        }
    }

    public void render(GuiGraphics matrices, float tickDelta) {
        if (!enabled || alpha <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int color = ((int)(alpha * 100) & 0xFF) << 24
                | ((int)(colorR * 255) & 0xFF) << 16
                | ((int)(colorG * 255) & 0xFF) << 8
                | ((int)(colorB * 255) & 0xFF);

        matrices.fill(0, 0, width, height, color);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setColor(float r, float g, float b) {
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
    }
}