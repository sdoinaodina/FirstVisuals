package com.firstvisuals.visuals.effects;

import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class HudPulseEffect {
    private boolean enabled = true;
    private float scaleFactor = 1.0f;
    private float targetScale = 1.0f;
    private float maxScale = 1.2f;
    private float pulseSpeed = 0.1f;
    private float glowIntensity = 0f;
    private boolean glowEnabled = true;
    private int pulseCount = 0;
    private Random random = new Random();

    public void trigger() {
        this.targetScale = maxScale;
        this.glowIntensity = 1.0f;
        this.pulseCount++;
    }

    public void onTick() {
        // Scale animation
        if (scaleFactor > targetScale) {
            scaleFactor = MathHelper.lerp(pulseSpeed, scaleFactor, targetScale);
        } else if (scaleFactor < 1.0f) {
            scaleFactor = MathHelper.lerp(pulseSpeed * 0.5f, scaleFactor, 1.0f);
        }

        // Glow decay
        if (glowEnabled && glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(0.05f, glowIntensity, 0f);
        }

        // Snap to target when close
        if (Math.abs(scaleFactor - targetScale) < 0.01f) {
            targetScale = 1.0f;
        }
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public float getGlowIntensity() {
        return glowIntensity;
    }

    public int getPulseCount() {
        return pulseCount;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            scaleFactor = 1.0f;
            targetScale = 1.0f;
            glowIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = MathHelper.clamp(maxScale, 1f, 2f);
    }

    public void setPulseSpeed(float speed) {
        this.pulseSpeed = MathHelper.clamp(speed, 0.01f, 0.5f);
    }

    public void setGlowEnabled(boolean glow) {
        this.glowEnabled = glow;
    }
}