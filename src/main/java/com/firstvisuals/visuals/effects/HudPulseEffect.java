package com.firstvisuals.visuals.effects;

import net.minecraft.util.math.MathHelper;

public class HudPulseEffect {
    private boolean enabled = true;
    private float scaleFactor = 1.0f;
    private float targetScale = 1.0f;
    private float maxScale = 1.15f;
    private float pulseSpeed = 0.08f;

    public void trigger() {
        this.targetScale = maxScale;
    }

    public void onTick() {
        if (scaleFactor > targetScale) {
            scaleFactor = MathHelper.lerp(pulseSpeed, scaleFactor, targetScale);
        } else if (scaleFactor < 1.0f) {
            scaleFactor = MathHelper.lerp(pulseSpeed, scaleFactor, 1.0f);
        }

        if (Math.abs(scaleFactor - targetScale) < 0.01f) {
            targetScale = 1.0f;
        }
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            scaleFactor = 1.0f;
            targetScale = 1.0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public void setPulseSpeed(float speed) {
        this.pulseSpeed = speed;
    }
}