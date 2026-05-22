package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class HitMarkerEffect {
    private boolean enabled = true;
    private float hitFade = 0f;
    private float hitScale = 1f;
    private float crosshairExpand = 0f;
    private int hitDirection = 0;
    private int hitColor = 0xFFFFFFFF;
    private float ringPulse = 0f;
    private boolean audioVisualEnabled = true;
    private Random random = new Random();

    public void onHit(int direction) {
        hitFade = 1f;
        hitScale = 1.8f;
        crosshairExpand = 1f;
        hitDirection = direction;
        ringPulse = 1f;
    }

    public void onTick() {
        if (hitFade > 0) {
            hitFade = MathHelper.lerp(0.08f, hitFade, 0f);
        }

        if (hitScale > 1f) {
            hitScale = MathHelper.lerp(0.15f, hitScale, 1f);
        }

        if (crosshairExpand > 0) {
            crosshairExpand = MathHelper.lerp(0.12f, crosshairExpand, 0f);
        }

        if (ringPulse > 0) {
            ringPulse = MathHelper.lerp(0.06f, ringPulse, 0f);
        }
    }

    public void renderHitMarker(GuiGraphics matrices, int centerX, int centerY) {
        if (!enabled || hitFade <= 0) return;

        int alpha = (int) (hitFade * 255);
        int color = (alpha << 24) | (hitColor & 0x00FFFFFF);

        int size = (int) (5 * hitScale);
        int gap = (int) (3 * hitScale);

        // Standard hit marker cross
        matrices.fill(centerX - size - gap, centerY - 1, centerX - gap, centerY + 1, color);
        matrices.fill(centerX + gap, centerY - 1, centerX + size + gap, centerY + 1, color);
        matrices.fill(centerX - 1, centerY - size - gap, centerX + 1, centerY - gap, color);
        matrices.fill(centerX - 1, centerY + gap, centerX + 1, centerY + size + gap, color);

        // Directional hit marker
        if (hitDirection > 0 && crosshairExpand > 0) {
            int dirSize = (int) (crosshairExpand * 8);
            switch (hitDirection) {
                case 1: // Top
                    matrices.fill(centerX - 2, centerY - size - gap - dirSize, centerX + 2, centerY - size - gap, color);
                    break;
                case 2: // Bottom
                    matrices.fill(centerX - 2, centerY + size + gap, centerX + 2, centerY + size + gap + dirSize, color);
                    break;
                case 3: // Left
                    matrices.fill(centerX - size - gap - dirSize, centerY - 2, centerX - size - gap, centerY + 2, color);
                    break;
                case 4: // Right
                    matrices.fill(centerX + size + gap, centerY - 2, centerX + size + gap + dirSize, centerY + 2, color);
                    break;
            }
        }

        // Ring pulse effect
        if (ringPulse > 0) {
            int ringRadius = (int) (ringPulse * 30);
            int ringAlpha = (int) (ringPulse * 150);
            int ringColor = (ringAlpha << 24) | 0xFFFFFF;
            // Simple ring representation
            matrices.fill(centerX - ringRadius, centerY - 1, centerX + ringRadius, centerY + 1, ringColor);
            matrices.fill(centerX - 1, centerY - ringRadius, centerX + 1, centerY + ringRadius, ringColor);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            hitFade = 0f;
            hitScale = 1f;
            crosshairExpand = 0f;
            ringPulse = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setHitColor(int color) {
        this.hitColor = 0xFF000000 | (color & 0x00FFFFFF);
    }
}