package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class TotemEffect {
    private boolean enabled = true;
    private float popAnimation = 0f;
    private float glowRadius = 0f;
    private float particleBurst = 0f;
    private int totemCount = 0;
    private boolean animationEnabled = true;
    private int popColor = 0xFF00FF00;
    private float bounceIntensity = 0f;
    private Random random = new Random();

    public void onTotemPop() {
        popAnimation = 1f;
        glowRadius = 1.5f;
        particleBurst = 1f;
        bounceIntensity = 1f;
    }

    public void onTick() {
        if (popAnimation > 0) {
            popAnimation = MathHelper.lerp(0.03f, popAnimation, 0f);
        }

        if (glowRadius > 1f) {
            glowRadius = MathHelper.lerp(0.05f, glowRadius, 1f);
        }

        if (particleBurst > 0) {
            particleBurst = MathHelper.lerp(0.05f, particleBurst, 0f);
        }

        if (bounceIntensity > 0) {
            bounceIntensity = MathHelper.lerp(0.1f, bounceIntensity, 0f);
        }
    }

    public void renderTotemEffect(GuiGraphics matrices, int x, int y) {
        if (!enabled || popAnimation <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Glow effect
        if (glowRadius > 1f) {
            int glowAlpha = (int) ((glowRadius - 1f) * 150);
            int glowColor = (glowAlpha << 24) | popColor;
            int size = (int) (20 * glowRadius);
            matrices.fill(x - size, y - size, x + size, y + size, glowColor);
        }

        // Particle burst
        if (particleBurst > 0) {
            for (int i = 0; i < 12; i++) {
                float angle = (float) Math.toRadians(i * 30);
                float dist = particleBurst * 30;
                int px = (int) (x + Math.cos(angle) * dist);
                int py = (int) (y + Math.sin(angle) * dist);
                int pColor = ((int) (particleBurst * 255) << 24) | popColor;
                matrices.fill(px - 2, py - 2, px + 2, py + 2, pColor);
            }
        }

        // Totem icon (bouncing)
        float bounce = (float) Math.sin(popAnimation * Math.PI * 4) * 3 * bounceIntensity;
        int drawY = (int) (y + bounce);
        matrices.fill(x - 8, drawY - 8, x + 8, drawY + 8, popColor);
        matrices.drawString(client.textRenderer, "T", x - 4, drawY - 4, 0xFFFFFF);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            popAnimation = 0f;
            glowRadius = 1f;
            particleBurst = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPopColor(int color) {
        this.popColor = 0xFF000000 | (color & 0x00FFFFFF);
    }
}