package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class BlockHighlightEffect {
    private boolean enabled = true;
    private float pulseIntensity = 0f;
    private boolean gradientEnabled = true;
    private int highlightColor = 0x8800FF00;
    private float animationSpeed = 1f;
    private float thickness = 2f;
    private boolean wireframeMode = false;
    private float glowRadius = 0f;
    private Random random = new Random();

    public void onBlockHighlight() {
        pulseIntensity = 1f;
        glowRadius = 1.5f;
    }

    public void onTick() {
        if (pulseIntensity > 0) {
            pulseIntensity = MathHelper.lerp(0.05f, pulseIntensity, 0f);
        }

        if (glowRadius > 1f) {
            glowRadius = MathHelper.lerp(0.08f, glowRadius, 1f);
        }
    }

    public void renderBlockHighlight(GuiGraphics matrices, float x, float y, float z, int width, int height, int depth) {
        if (!enabled) return;

        int alpha = (int) ((pulseIntensity * 100 + 50) & 0xFF);
        int color = (alpha << 24) | (highlightColor & 0x00FFFFFF);

        if (wireframeMode) {
            // Wireframe style - just edges
            matrices.fill((int)x, (int)y, (int)x + width, (int)y + 1, color); // top
            matrices.fill((int)x, (int)(y + height) - 1, (int)x + width, (int)y + height, color); // bottom
            matrices.fill((int)x, (int)y, (int)x + 1, (int)y + height, color); // left
            matrices.fill((int)(x + width) - 1, (int)y, (int)x + width, (int)y + height, color); // right
        } else if (gradientEnabled) {
            // Gradient fill
            matrices.fillGradient((int)x, (int)y, (int)x + width, (int)y + height, color, color & 0x00FFFFFF);
        } else {
            // Solid fill
            matrices.fill((int)x, (int)y, (int)x + width, (int)y + height, color);
        }

        // Glow effect
        if (glowRadius > 1f) {
            int glowAlpha = (int) ((glowRadius - 1f) * 100);
            int glowCol = (glowAlpha << 24) | (highlightColor & 0x00FFFFFF);
            matrices.fill((int)x - 1, (int)y - 1, (int)x + width + 1, (int)y, glowCol);
            matrices.fill((int)x - 1, (int)y + height, (int)x + width + 1, (int)y + height + 1, glowCol);
            matrices.fill((int)x - 1, (int)y, (int)x, (int)y + height, glowCol);
            matrices.fill((int)x + width, (int)y, (int)x + width + 1, (int)y + height, glowCol);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setHighlightColor(int color) {
        this.highlightColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setWireframeMode(boolean wireframe) {
        this.wireframeMode = wireframe;
    }
}