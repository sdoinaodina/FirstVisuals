package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class SprintIndicatorEffect {
    private boolean enabled = true;
    private float sprintProgress = 0f;
    private float targetProgress = 0f;
    private float boostIntensity = 0f;
    private boolean particlesEnabled = true;
    private float particleIntensity = 0f;
    private boolean footstepGlow = false;
    private int sprintColor = 0xFF00FF00;
    private Random random = new Random();

    public void onSprintStart() {
        targetProgress = 1f;
        boostIntensity = 1f;
        particleIntensity = 1f;
    }

    public void onSprintStop() {
        targetProgress = 0f;
        particleIntensity = 0f;
    }

    public void onTick() {
        if (sprintProgress < targetProgress) {
            sprintProgress = MathHelper.lerp(0.1f, sprintProgress, targetProgress);
        } else if (sprintProgress > targetProgress) {
            sprintProgress = MathHelper.lerp(0.05f, sprintProgress, targetProgress);
        }

        if (boostIntensity > 0) {
            boostIntensity = MathHelper.lerp(0.02f, boostIntensity, 0f);
        }

        if (particleIntensity > 0) {
            particleIntensity = MathHelper.lerp(0.05f, particleIntensity, 0f);
        }
    }

    public void renderSprintIndicator(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled || sprintProgress <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Sprint bar at bottom of screen
        int barHeight = (int) (4 * sprintProgress);
        int barY = y + height - barHeight;

        int alpha = (int) (sprintProgress * 150);
        int color = (alpha << 24) | (sprintColor & 0x00FFFFFF);

        matrices.fill(x, barY, x + width, y + height, color);

        // Boost particles effect
        if (particlesEnabled && particleIntensity > 0 && random.nextFloat() < particleIntensity) {
            int particleX = x + random.nextInt(width);
            int particleY = y + height;
            int particleColor = (int) (particleIntensity * 255) << 24 | 0x00FF00;
            matrices.fill(particleX, particleY, particleX + 2, particleY + 2, particleColor);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            sprintProgress = 0f;
            targetProgress = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setSprintColor(int color) {
        this.sprintColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public float getSprintProgress() {
        return sprintProgress;
    }
}