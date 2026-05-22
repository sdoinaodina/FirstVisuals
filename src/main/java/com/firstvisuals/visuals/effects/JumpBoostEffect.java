package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class JumpBoostEffect {
    private boolean enabled = true;
    private float jumpParticles = 0f;
    private float landingShock = 0f;
    private float springBounce = 0f;
    private boolean particlesEnabled = true;
    private int particleColor = 0xFF00FF88;
    private float intensity = 1f;
    private Random random = new Random();

    public void onJump() {
        jumpParticles = 1f;
        springBounce = 1f;
    }

    public void onLand() {
        landingShock = 1f;
        springBounce = 1f;
    }

    public void onTick() {
        if (jumpParticles > 0) {
            jumpParticles = MathHelper.lerp(0.05f, jumpParticles, 0f);
        }

        if (landingShock > 0) {
            landingShock = MathHelper.lerp(0.08f, landingShock, 0f);
        }

        if (springBounce > 0) {
            springBounce = MathHelper.lerp(0.1f, springBounce, 0f);
        }
    }

    public void renderJumpEffect(GuiGraphics matrices, int x, int y) {
        if (!enabled || (jumpParticles <= 0 && landingShock <= 0)) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Jump particles rising
        if (jumpParticles > 0 && particlesEnabled) {
            for (int i = 0; i < 8; i++) {
                float progress = 1f - jumpParticles;
                int py = y - (int) (progress * 40);
                int px = x + (random.nextInt(20) - 10);
                int pAlpha = (int) (jumpParticles * 200);
                matrices.fill(px - 1, py - 1, px + 1, py + 1, (pAlpha << 24) | particleColor);
            }
        }

        // Landing shock wave
        if (landingShock > 0) {
            int radius = (int) (landingShock * 30);
            int alpha = (int) (landingShock * 150);
            // Simple shock ring
            matrices.fill(x - radius, y - 2, x + radius, y, (alpha << 24) | particleColor);
        }

        // Spring bounce indicator
        if (springBounce > 0.5f) {
            int glowAlpha = (int) ((springBounce - 0.5f) * 100);
            matrices.fill(x - 5, y - 5, x + 5, y + 5, (glowAlpha << 24) | particleColor);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            jumpParticles = 0f;
            landingShock = 0f;
            springBounce = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setParticleColor(int color) {
        this.particleColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setParticlesEnabled(boolean particles) {
        this.particlesEnabled = particles;
    }
}