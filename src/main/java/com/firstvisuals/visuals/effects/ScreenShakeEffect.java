package com.firstvisuals.visuals.effects;

import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ScreenShakeEffect {
    private boolean enabled = true;
    private float intensity = 0f;
    private int timer = 0;
    private float currentOffsetX = 0f;
    private float currentOffsetY = 0f;
    private float decayRate = 0.92f;
    private boolean rotationEnabled = true;
    private float rotationAngle = 0f;
    private Random random = new Random();

    public void trigger(float intensity, int duration) {
        this.intensity = MathHelper.clamp(intensity, 0f, 15f);
        this.timer = duration;
        this.decayRate = 0.92f;
    }

    public void onTick() {
        if (timer > 0) {
            timer--;

            // Apply decay
            float decay = (float) timer / 100f;
            intensity = intensity * decayRate * decay;

            // Update offsets with smoothing
            float targetX = (float) (random.nextGaussian() * intensity);
            float targetY = (float) (random.nextGaussian() * intensity);

            currentOffsetX = MathHelper.lerp(0.3f, currentOffsetX, targetX);
            currentOffsetY = MathHelper.lerp(0.3f, currentOffsetY, targetY);

            // Rotation effect
            if (rotationEnabled && intensity > 1f) {
                rotationAngle = (float) Math.sin(timer * 0.5f) * intensity * 0.5f;
            }
        } else {
            intensity = 0f;
            currentOffsetX = 0f;
            currentOffsetY = 0f;
            rotationAngle = 0f;
        }
    }

    public float getOffsetX() {
        return currentOffsetX;
    }

    public float getOffsetY() {
        return currentOffsetY;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            reset();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRotationEnabled(boolean rotation) {
        this.rotationEnabled = rotation;
    }

    private void reset() {
        intensity = 0f;
        currentOffsetX = 0f;
        currentOffsetY = 0f;
        rotationAngle = 0f;
        timer = 0;
    }
}