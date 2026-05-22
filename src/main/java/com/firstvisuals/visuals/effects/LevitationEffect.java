package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class LevitationEffect {
    private boolean enabled = true;
    private float levitationGlow = 0f;
    private float floatOffset = 0f;
    private boolean particleTrail = true;
    private float particleIntensity = 0f;
    private float hoverBob = 0f;
    private int effectColor = 0x88DDDDFF;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private Random random = new Random();

    public void onLevitationStart() {
        levitationGlow = 1f;
        particleIntensity = 1f;
        hoverBob = 0f;
    }

    public void onLevitationEnd() {
        levitationGlow = 0f;
        particleIntensity = 0f;
    }

    public void onTick() {
        if (levitationGlow > 0) {
            levitationGlow = MathHelper.lerp(0.02f, levitationGlow, 0f);
        }

        if (particleIntensity > 0 && particleTrail) {
            particleIntensity = MathHelper.lerp(0.03f, particleIntensity, 0.2f);
        }

        // Hover bob animation
        hoverBob += 0.05f;
        if (hoverBob > 360f) hoverBob = 0f;

        floatOffset = (float) Math.sin(hoverBob) * 5f;

        if (rainbowMode) {
            hue += 0.01f;
            if (hue > 1f) hue = 0f;
        }
    }

    public void renderLevitationEffect(GuiGraphics matrices, int x, int y) {
        if (!enabled || levitationGlow <= 0) return;

        // Glow aura
        int glowAlpha = (int) (levitationGlow * 150);
        if (rainbowMode) {
            int[] rgb = hueToRgbInt(hue);
            effectColor = (glowAlpha << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
        }
        int glowColor = (glowAlpha << 24) | (effectColor & 0x00FFFFFF);

        int size = (int) (20 + levitationGlow * 10);
        matrices.fill(x - size, y - size, x + size, y + size, glowColor);

        // Particle trail
        if (particleTrail && particleIntensity > 0) {
            for (int i = 0; i < 8; i++) {
                float angle = (float) Math.toRadians(i * 45 + hoverBob * 10);
                float dist = (1f - particleIntensity) * 30;
                int px = (int) (x + Math.cos(angle) * dist);
                int py = (int) (y + Math.sin(angle) * dist + floatOffset);
                int pAlpha = (int) (particleIntensity * 200);
                matrices.fill(px - 1, py - 1, px + 1, py + 1, (pAlpha << 24) | effectColor);
            }
        }

        // Floating indicator
        matrices.drawString(MinecraftClient.getInstance().textRenderer, "LEVITATING",
                x - 35, (int) (y + floatOffset - 30), glowColor);
    }

    private int[] hueToRgbInt(float hue) {
        float h = hue * 6f;
        float x = 1f - Math.abs((h % 2f) - 1f);
        if (h < 1f) return new int[]{255, (int)(x * 255), 0};
        if (h < 2f) return new int[]{(int)(x * 255), 255, 0};
        if (h < 3f) return new int[]{0, 255, (int)(x * 255)};
        if (h < 4f) return new int[]{0, (int)(x * 255), 255};
        if (h < 5f) return new int[]{(int)(x * 255), 0, 255};
        return new int[]{255, 0, (int)(x * 255)};
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            levitationGlow = 0f;
            particleIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }
}