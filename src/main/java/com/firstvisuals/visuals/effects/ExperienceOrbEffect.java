package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ExperienceOrbEffect {
    private boolean enabled = true;
    private float orbGlow = 0f;
    private int orbCount = 0;
    private float collectionPulse = 0f;
    private boolean sparkleEffect = true;
    private int sparkleColor = 0xFFFFFF55;
    private float orbSize = 1f;
    private float rotationSpeed = 1f;
    private float floatPhase = 0f;
    private Random random = new Random();

    public void onOrbCollect(int count) {
        orbCount = count;
        orbGlow = 1f;
        collectionPulse = 1f;
    }

    public void onTick() {
        if (orbGlow > 0) {
            orbGlow = MathHelper.lerp(0.02f, orbGlow, 0f);
        }

        if (collectionPulse > 0) {
            collectionPulse = MathHelper.lerp(0.05f, collectionPulse, 0f);
        }

        floatPhase += 0.05f * rotationSpeed;
        if (floatPhase > 360f) floatPhase -= 360f;
    }

    public void renderOrbTrail(GuiGraphics matrices, int x, int y) {
        if (!enabled || orbCount <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Glow effect
        if (orbGlow > 0) {
            int glowAlpha = (int) (orbGlow * 150);
            int glowColor = (glowAlpha << 24) | 0xFFFF00;
            int size = (int) (5 * orbSize + orbGlow * 3);
            matrices.fill(x - size, y - size, x + size, y + size, glowColor);
        }

        // Sparkle effect
        if (sparkleEffect && collectionPulse > 0) {
            for (int i = 0; i < 5; i++) {
                float angle = (float) Math.toRadians(i * 72 + floatPhase * 10);
                float dist = collectionPulse * 20;
                int sparkX = (int) (x + Math.cos(angle) * dist);
                int sparkY = (int) (y + Math.sin(angle) * dist);
                matrices.fill(sparkX - 1, sparkY - 1, sparkX + 1, sparkY + 1, sparkleColor);
            }
        }

        // Floating orb count
        if (orbCount > 1) {
            int bobY = (int) (y + Math.sin(floatPhase) * 3);
            matrices.drawString(client.textRenderer, "+" + orbCount, x + 5, bobY, 0xFFFF55);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            orbGlow = 0f;
            collectionPulse = 0f;
            orbCount = 0;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setSparkleEffect(boolean sparkle) {
        this.sparkleEffect = sparkle;
    }

    public void setOrbSize(float size) {
        this.orbSize = MathHelper.clamp(size, 0.5f, 3f);
    }
}