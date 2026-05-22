package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class AirBubblesEffect {
    private boolean enabled = true;
    private int bubbleCount = 5;
    private float bubbleSpeed = 1f;
    private float bubbleSize = 1f;
    private boolean randomSize = true;
    private boolean glowEffect = false;
    private int bubbleColor = 0x88FFFFFF;
    private float underWaterIntensity = 0f;
    private Random random = new Random();

    public void onUnderWaterChange(boolean isUnderWater) {
        if (isUnderWater) {
            underWaterIntensity = 1f;
        } else {
            underWaterIntensity = 0f;
        }
    }

    public void onTick() {
        if (underWaterIntensity > 0 && enabled) {
            underWaterIntensity = MathHelper.lerp(0.01f, underWaterIntensity, 0f);
        }
    }

    public void renderBubbles(GuiGraphics matrices) {
        if (!enabled || underWaterIntensity <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int baseAlpha = (int) (underWaterIntensity * 100);

        for (int i = 0; i < bubbleCount; i++) {
            float size = randomSize ? (0.5f + random.nextFloat() * bubbleSize) : bubbleSize;
            int actualSize = (int) (3 * size);

            // Animated position
            float progress = (System.currentTimeMillis() * 0.001f * bubbleSpeed + i * 0.3f) % 1f;
            int bubbleX = (int) (width * 0.2f + random.nextInt(width / 2));
            int bubbleY = (int) (height * (1f - progress));

            int alpha = (int) ((1f - progress) * baseAlpha);
            int color = (alpha << 24) | (bubbleColor & 0x00FFFFFF);

            if (glowEffect) {
                // Glow effect
                int glowColor = (alpha / 2 << 24) | 0xFFFFFF;
                matrices.fill(bubbleX - actualSize, bubbleY - actualSize,
                    bubbleX + actualSize * 2, bubbleY + actualSize * 2, glowColor);
            }

            matrices.fill(bubbleX - actualSize / 2, bubbleY - actualSize / 2,
                bubbleX + actualSize / 2, bubbleY + actualSize / 2, color);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setBubbleCount(int count) {
        this.bubbleCount = MathHelper.clamp(count, 1, 20);
    }

    public void setBubbleSpeed(float speed) {
        this.bubbleSpeed = MathHelper.clamp(speed, 0.1f, 5f);
    }

    public void setBubbleColor(int color) {
        this.bubbleColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setGlowEffect(boolean glow) {
        this.glowEffect = glow;
    }
}