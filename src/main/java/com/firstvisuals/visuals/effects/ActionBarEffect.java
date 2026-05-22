package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ActionBarEffect {
    private boolean enabled = true;
    private float[] charScales = new float[40]; // Max action bar length
    private boolean animationEnabled = true;
    private float wavePhase = 0f;
    private int[] charAlphas = new int[40];
    private float glowIntensity = 0f;
    private Random random = new Random();

    public ActionBarEffect() {
        for (int i = 0; i < 40; i++) {
            charScales[i] = 1f;
            charAlphas[i] = 255;
        }
    }

    public void onActionBar(String text) {
        if (!enabled || text == null) return;

        // Animate each character with wave effect
        wavePhase += 0.15f;
        int len = Math.min(text.length(), 40);

        for (int i = 0; i < len; i++) {
            charScales[i] = 1f + (float) Math.sin(wavePhase + i * 0.5f) * 0.1f;
            charAlphas[i] = 255;
        }

        glowIntensity = 1f;
    }

    public void onTick() {
        wavePhase += 0.05f;

        // Decay glow
        if (glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(0.03f, glowIntensity, 0f);
        }

        // Reset scales
        for (int i = 0; i < 40; i++) {
            if (charScales[i] > 1f) {
                charScales[i] = MathHelper.lerp(0.1f, charScales[i], 1f);
            }
        }
    }

    public void renderActionBar(GuiGraphics matrices, String text, int x, int y, int color) {
        if (!enabled || text == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int totalWidth = client.textRenderer.getWidth(text);
        int startX = x - totalWidth / 2;

        int len = Math.min(text.length(), 40);

        for (int i = 0; i < len; i++) {
            String charStr = text.substring(i, i + 1);
            int charWidth = client.textRenderer.getWidth(charStr);

            float scale = charScales[i];
            int alpha = (int) (charAlphas[i] * (charScales[i] / 1.2f));

            int scaledColor = (alpha & 0xFF) << 24 | (color & 0x00FFFFFF);

            // Glow for scaled characters
            if (scale > 1.05f && glowIntensity > 0) {
                int glowColor = ((int)(glowIntensity * 80) & 0xFF) << 24 | (color & 0x00FFFFFF);
                matrices.drawString(client.textRenderer, charStr,
                        (int) (startX + (charWidth / 2) * scale - charWidth / 2) - 1, (int) (y + (8 / 2) * scale - 4), glowColor);
            }

            matrices.drawString(client.textRenderer, charStr,
                    (int) (startX + (charWidth / 2) * scale - charWidth / 2), y, scaledColor);

            startX += charWidth;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            for (int i = 0; i < 40; i++) {
                charScales[i] = 1f;
            }
            glowIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}