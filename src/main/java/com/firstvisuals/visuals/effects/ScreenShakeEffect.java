package com.firstvisuals.visuals.effects;

import net.minecraft.util.math.MathHelper;

public class ScreenShakeEffect {
    private boolean enabled = true;
    private float intensity = 0f;
    private int timer = 0;

    public void trigger(float intensity, int duration) {
        this.intensity = MathHelper.clamp(intensity, 0f, 10f);
        this.timer = duration;
    }

    public void onTick() {
        if (timer > 0) {
            timer--;
            intensity = ((float) timer / 100) * intensity;
        } else {
            intensity = 0f;
        }
    }

    public float getOffsetX() {
        if (intensity <= 0) return 0f;
        return (float) (Math.random() - 0.5) * intensity * 2;
    }

    public float getOffsetY() {
        if (intensity <= 0) return 0f;
        return (float) (Math.random() - 0.5) * intensity * 2;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            intensity = 0f;
            timer = 0;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}