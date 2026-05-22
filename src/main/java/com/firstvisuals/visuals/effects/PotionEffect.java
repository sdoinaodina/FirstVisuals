package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class PotionEffect {
    private boolean enabled = true;
    private float particleIntensity = 0f;
    private float effectColor = 0.7f;
    private int activeEffects = 0;
    private boolean particleTrail = false;
    private float glowRadius = 1f;
    private boolean bubblesEnabled = true;
    private int potionColor = 0xFF55AAFF;
    private Random random = new Random();

    public void onPotionStart(int effectId) {
        activeEffects++;
        particleIntensity = 1f;
        effectColor = (effectId % 20) / 20f;
        glowRadius = 1.5f;
    }

    public void onPotionEnd(int effectId) {
        activeEffects = Math.max(0, activeEffects - 1);
    }

    public void onTick() {
        if (particleIntensity > 0 && activeEffects > 0) {
            particleIntensity = MathHelper.lerp(0.01f, particleIntensity, 0.3f);
        }

        if (glowRadius > 1f) {
            glowRadius = MathHelper.lerp(0.05f, glowRadius, 1f);
        }

        if (particleTrail && activeEffects > 0 && random.nextFloat() < particleIntensity) {
            spawnPotionParticle();
        }
    }

    private void spawnPotionParticle() {
        // Particle spawning logic would go here
    }

    public void renderPotionOverlay(GuiGraphics matrices, int width, int height) {
        if (!enabled || activeEffects <= 0) return;

        if (bubblesEnabled) {
            int bubbleCount = (int) (particleIntensity * 10);
            for (int i = 0; i < bubbleCount; i++) {
                int bx = random.nextInt(width);
                int by = random.nextInt(height);
                int bubbleSize = random.nextInt(3) + 1;
                int alpha = (int) (particleIntensity * 100);
                int color = (alpha << 24) | (potionColor & 0x00FFFFFF);
                matrices.fill(bx, by, bx + bubbleSize, by + bubbleSize, color);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            particleIntensity = 0f;
            activeEffects = 0;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPotionColor(int color) {
        this.potionColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public int getActiveEffects() {
        return activeEffects;
    }
}