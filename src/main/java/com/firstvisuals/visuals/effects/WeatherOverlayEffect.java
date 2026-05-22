package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class WeatherOverlayEffect {
    private boolean enabled = true;
    private float rainIntensity = 0f;
    private float snowIntensity = 0f;
    private boolean animatedRain = true;
    private boolean thunderEnabled = true;
    private float lightningFlash = 0f;
    private int[] rainDrops = new int[100];
    private int[] snowFlakes = new int[100];
    private float fogDensity = 0f;
    private int weatherColor = 0x8888AA;
    private Random random = new Random();

    public void onWeatherChange(float rain, float snow) {
        rainIntensity = rain;
        snowIntensity = snow;
        if (rain > 0.5f) {
            fogDensity = rain * 0.3f;
        } else {
            fogDensity = 0f;
        }
    }

    public void onLightning() {
        lightningFlash = 1f;
    }

    public void onTick() {
        if (rainIntensity > 0 && animatedRain) {
            for (int i = 0; i < rainDrops.length; i++) {
                rainDrops[i] += (int) (rainIntensity * 10);
                if (rainDrops[i] > 100) {
                    rainDrops[i] = 0;
                }
            }
        }

        if (snowIntensity > 0) {
            for (int i = 0; i < snowFlakes.length; i++) {
                snowFlakes[i] += (int) (snowIntensity * 3);
                if (snowFlakes[i] > 100) {
                    snowFlakes[i] = 0;
                }
            }
        }

        if (lightningFlash > 0) {
            lightningFlash = MathHelper.lerp(0.1f, lightningFlash, 0f);
        }

        if (fogDensity > 0) {
            fogDensity = MathHelper.lerp(0.01f, fogDensity, 0f);
        }
    }

    public void renderWeather(GuiGraphics matrices) {
        if (!enabled || (rainIntensity <= 0 && snowIntensity <= 0)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Lightning flash
        if (lightningFlash > 0.5f) {
            int flashAlpha = (int) (lightningFlash * 200);
            matrices.fill(0, 0, width, height, (flashAlpha << 24) | 0xFFFFFF);
        }

        // Rain drops
        if (rainIntensity > 0) {
            int rainAlpha = (int) (rainIntensity * 100);
            int rainColor = (rainAlpha << 24) | (weatherColor & 0x00FFFFFF);

            for (int drop : rainDrops) {
                int x = random.nextInt(width);
                int y = drop * height / 100;
                matrices.fill(x, y, x + 1, y + 10, rainColor);
            }
        }

        // Snow
        if (snowIntensity > 0) {
            int snowAlpha = (int) (snowIntensity * 80);
            int snowColor = (snowAlpha << 24) | 0xFFFFFF;

            for (int flake : snowFlakes) {
                int x = random.nextInt(width);
                int y = flake * height / 100;
                matrices.fill(x - 1, y - 1, x + 1, y + 1, snowColor);
            }
        }

        // Fog overlay
        if (fogDensity > 0) {
            int fogAlpha = (int) (fogDensity * 150);
            matrices.fillGradient(0, 0, width, height / 3, (fogAlpha << 24) | weatherColor, 0);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            rainIntensity = 0f;
            snowIntensity = 0f;
            lightningFlash = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setThunderEnabled(boolean thunder) {
        this.thunderEnabled = thunder;
    }
}