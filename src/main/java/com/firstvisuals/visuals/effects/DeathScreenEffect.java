package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class DeathScreenEffect {
    private boolean enabled = true;
    private float fadeIntensity = 0f;
    private float respawnPulse = 0f;
    private int countdown = 0;
    private boolean grayscaleEnabled = true;
    private float vignetteIntensity = 0f;
    private int deathMessageColor = 0xFF555555;
    private Random random = new Random();

    public void onDeath() {
        fadeIntensity = 1f;
        vignetteIntensity = 1f;
        respawnPulse = 0f;
        countdown = 0;
    }

    public void onRespawn() {
        fadeIntensity = 0f;
        vignetteIntensity = 0f;
        respawnPulse = 0f;
    }

    public void onTick() {
        if (fadeIntensity > 0) {
            fadeIntensity = MathHelper.lerp(0.01f, fadeIntensity, 0f);
        }

        if (vignetteIntensity > 0) {
            vignetteIntensity = MathHelper.lerp(0.02f, vignetteIntensity, 0f);
        }

        if (countdown > 0) {
            countdown--;
        } else {
            respawnPulse = 1f;
        }
    }

    public void renderDeathScreen(GuiGraphics matrices) {
        if (!enabled || fadeIntensity <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Grayscale overlay
        int grayAlpha = (int) (fadeIntensity * 200);
        int grayColor = (grayAlpha << 24) | 0x808080;
        matrices.fill(0, 0, width, height, grayColor);

        // Death message
        if (fadeIntensity > 0.5f) {
            matrices.drawString(client.textRenderer, "You died!", width / 2 - 30, height / 2, deathMessageColor);
        }

        // Vignette effect
        if (vignetteIntensity > 0) {
            int vignetteAlpha = (int) (vignetteIntensity * 180);
            int vignetteColor = (vignetteAlpha << 24) | 0x000000;
            matrices.fillGradient(0, 0, width, height / 4, vignetteColor, 0);
            matrices.fillGradient(0, height - height / 4, width, height, 0, vignetteColor);
            matrices.fillGradient(0, 0, width / 4, height, vignetteColor, 0);
            matrices.fillGradient(width - width / 4, 0, width, height, 0, vignetteColor);
        }

        // Respawn prompt pulse
        if (respawnPulse > 0) {
            int pulseAlpha = (int) (respawnPulse * 255);
            int pulseColor = (pulseAlpha << 24) | 0xFFFFFF;
            matrices.drawString(client.textRenderer, "Click to respawn", width / 2 - 50, height / 2 + 20, pulseColor);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            fadeIntensity = 0f;
            vignetteIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setCountdown(int ticks) {
        this.countdown = ticks;
    }
}