package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class SaturationEffect {
    private boolean enabled = true;
    private float saturationPulse = 0f;
    private float foodExhaustion = 0f;
    private boolean glowEnabled = true;
    private int saturationColor = 0xFFFF8800;
    private float effectIntensity = 0f;
    private Random random = new Random();

    public void onSaturationChange(float oldSat, float newSat) {
        if (newSat > oldSat) {
            saturationPulse = 1f;
            effectIntensity = 1f;
        }
        foodExhaustion = newSat;
    }

    public void onTick() {
        if (saturationPulse > 0) {
            saturationPulse = MathHelper.lerp(0.03f, saturationPulse, 0f);
        }

        if (effectIntensity > 0) {
            effectIntensity = MathHelper.lerp(0.02f, effectIntensity, 0f);
        }
    }

    public void renderSaturationBar(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled || foodExhaustion <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();

        if (saturationPulse > 0 && glowEnabled) {
            int glowAlpha = (int) (saturationPulse * 150);
            int glowColor = (glowAlpha << 24) | (saturationColor & 0x00FFFFFF);
            matrices.fill(x - 2, y - 2, x + width + 2, y, glowColor);
            matrices.fill(x - 2, y + height, x + width + 2, y + height + 2, glowColor);
        }

        matrices.fill(x, y, x + width, y + height, 0x88000000);

        float fillPercent = Math.min(foodExhaustion / 20f, 1f);
        int fillWidth = (int) (width * fillPercent);
        int fillColor = ((int) (effectIntensity * 255) << 24) | (saturationColor & 0x00FFFFFF);

        matrices.fill(x, y, x + fillWidth, y + height, fillColor);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setSaturationColor(int color) {
        this.saturationColor = 0xFF000000 | (color & 0x00FFFFFF);
    }
}