package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class EntityGlowEffect {
    private boolean enabled = true;
    private boolean teammatesOnly = false;
    private float glowIntensity = 0.7f;
    private int glowColor = 0x8800FF00;
    private boolean pulseEnabled = true;
    private float pulsePhase = 0f;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private boolean nameTagGlow = true;
    private boolean outlineEnabled = true;
    private Random random = new Random();

    public void onEntityHighlight() {
        pulsePhase = 0f;
        glowIntensity = 1f;
    }

    public void onTick() {
        if (pulseEnabled) {
            pulsePhase += 0.05f;
            if (pulsePhase > 360f) pulsePhase = 0f;
        }

        if (rainbowMode) {
            hue += 0.01f;
            if (hue > 1f) hue = 0f;
            int[] rgb = hueToRgbInt(hue);
            glowColor = 0x88000000 | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
        }
    }

    public void renderEntityGlow(GuiGraphics matrices, int x, int y, int width, int height, String entityName) {
        if (!enabled) return;

        float pulseMod = pulseEnabled ? (float) Math.sin(pulsePhase) * 0.2f + 1f : 1f;
        int alpha = (int) (glowIntensity * 150 * pulseMod);
        int color = (alpha << 24) | (glowColor & 0x00FFFFFF);

        // Outline glow
        if (outlineEnabled) {
            matrices.fill(x - 2, y - 2, x + width + 2, y, color); // top
            matrices.fill(x - 2, y + height, x + width + 2, y + height + 2, color); // bottom
            matrices.fill(x - 2, y, x, y + height, color); // left
            matrices.fill(x + width, y, x + width + 2, y + height, color); // right
        }

        // Name tag glow
        if (nameTagGlow && entityName != null) {
            matrices.drawString(MinecraftClient.getInstance().textRenderer, entityName,
                    x + width / 2 - MinecraftClient.getInstance().textRenderer.getWidth(entityName) / 2,
                    y - 12, color);
        }
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
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setGlowColor(int color) {
        this.glowColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }

    public void setTeammatesOnly(boolean teammatesOnly) {
        this.teammatesOnly = teammatesOnly;
    }
}