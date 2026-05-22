package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class MiningFatigueEffect {
    private boolean enabled = true;
    private float blockBreakProgress = 0f;
    private float miningSlowdown = 0f;
    private float crackIntensity = 0f;
    private boolean animationEnabled = true;
    private int progressColor = 0xFF888888;
    private boolean particlesEnabled = true;
    private float particleIntensity = 0f;
    private Random random = new Random();

    public void onMiningStart() {
        blockBreakProgress = 0f;
        miningSlowdown = 0.5f;
    }

    public void onMiningTick(float progress) {
        blockBreakProgress = progress;
        crackIntensity = progress > 0.7f ? (progress - 0.7f) / 0.3f : 0f;
    }

    public void onMiningStop() {
        blockBreakProgress = 0f;
        crackIntensity = 0f;
        miningSlowdown = 0f;
    }

    public void onTick() {
        if (crackIntensity > 0 && blockBreakProgress < 1f) {
            crackIntensity = MathHelper.lerp(0.02f, crackIntensity, (blockBreakProgress - 0.7f) / 0.3f);
        }

        if (particleIntensity > 0) {
            particleIntensity = MathHelper.lerp(0.05f, particleIntensity, 0f);
        }
    }

    public void renderMiningOverlay(GuiGraphics matrices, int x, int y, int width, int height) {
        if (!enabled || blockBreakProgress <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Progress bar background
        matrices.fill(x, y, x + width, y + height, 0x88000000);

        // Progress fill
        int fillWidth = (int) (width * blockBreakProgress);
        matrices.fill(x, y, x + fillWidth, y + height, progressColor);

        // Crack overlay
        if (crackIntensity > 0) {
            int crackAlpha = (int) (crackIntensity * 200);
            int crackColor = (crackAlpha << 24) | 0x333333;
            int crackX = x + (int) (width * blockBreakProgress);

            // Draw crack lines
            for (int i = 0; i < 5; i++) {
                int cy = y + (height / 5) * i + random.nextInt(height / 5);
                matrices.fill(crackX - 2, cy, crackX, cy + 1, crackColor);
            }
        }

        // Mining particles
        if (particlesEnabled && blockBreakProgress > 0.3f && random.nextFloat() < blockBreakProgress) {
            int px = x + random.nextInt(width);
            int py = y + random.nextInt(height);
            int pAlpha = (int) ((1f - blockBreakProgress) * 150);
            matrices.fill(px, py, px + 2, py + 2, (pAlpha << 24) | 0x888888);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            blockBreakProgress = 0f;
            crackIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setProgressColor(int color) {
        this.progressColor = 0xFF000000 | (color & 0x00FFFFFF);
    }
}