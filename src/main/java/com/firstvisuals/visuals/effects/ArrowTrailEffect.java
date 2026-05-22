package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

public class ArrowTrailEffect {
    private boolean enabled = true;
    private Map<Integer, TrailPoint> trails = new ConcurrentHashMap<>();
    private int nextId = 0;
    private float intensity = 1f;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private int maxTrailPoints = 50;
    private float fadeSpeed = 0.95f;
    private boolean glowEnabled = true;
    private Random random = new Random();

    private static class TrailPoint {
        float x, y, z;
        float alpha;
        float size;
        int color;
        int id;
        long timestamp;

        TrailPoint(float x, float y, float z, float size, int id) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.alpha = 1f;
            this.size = size;
            this.id = id;
            this.timestamp = System.currentTimeMillis();
            this.color = 0xFFFF00;
        }
    }

    public void onArrowHit(float x, float y, float z) {
        if (!enabled || trails.size() >= maxTrailPoints) return;

        float size = 0.3f + random.nextFloat() * 0.3f;
        if (rainbowMode) {
            TrailPoint p = new TrailPoint(x, y, z, size, nextId);
            p.color = getRainbowColor();
            trails.put(nextId++, p);
            hue += 0.05f;
            if (hue > 1f) hue = 0f;
        } else {
            trails.put(nextId++, new TrailPoint(x, y, z, size, nextId));
        }
    }

    public void onTick() {
        for (Map.Entry<Integer, TrailPoint> entry : trails.entrySet()) {
            TrailPoint p = entry.getValue();
            p.alpha *= fadeSpeed;
            p.size *= 0.99f;

            if (p.alpha < 0.01f) {
                trails.remove(entry.getKey());
            }
        }

        if (rainbowMode && !trails.isEmpty()) {
            hue += 0.02f;
            if (hue > 1f) hue = 0f;
        }
    }

    public void renderTrails(GuiGraphics matrices) {
        if (!enabled || trails.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int centerX = client.getWindow().getScaledWidth() / 2;
        int centerY = client.getWindow().getScaledHeight() / 2;

        for (TrailPoint p : trails.values()) {
            if (p.alpha < 0.01f) continue;

            // Simplified 3D to 2D projection
            int screenX = (int) (centerX + (p.x - 0) * 20);
            int screenY = (int) (centerY - (p.y - 0) * 20);

            int color = ((int)(p.alpha * 255) << 24) | (p.color & 0x00FFFFFF);
            int size = Math.max(1, (int)(p.size * 4));

            if (glowEnabled && p.alpha > 0.5f) {
                int glowColor = ((int)(p.alpha * 80) << 24) | 0xFFFF00;
                matrices.fill(screenX - size - 1, screenY - size - 1,
                    screenX + size + 1, screenY + size + 1, glowColor);
            }

            matrices.fill(screenX - size, screenY - size, screenX + size, screenY + size, color);
        }
    }

    private int getRainbowColor() {
        float[] rgb = hueToRgb(hue);
        return ((int)(rgb[0] * 255) << 16) | ((int)(rgb[1] * 255) << 8) | ((int)(rgb[2] * 255));
    }

    private float[] hueToRgb(float hue) {
        float h = hue * 6f;
        float x = 1f - Math.abs((h % 2f) - 1f);
        if (h < 1f) return new float[]{1f, x, 0f};
        if (h < 2f) return new float[]{x, 1f, 0f};
        if (h < 3f) return new float[]{0f, 1f, x};
        if (h < 4f) return new float[]{0f, x, 1f};
        if (h < 5f) return new float[]{x, 0f, 1f};
        return new float[]{1f, 0f, x};
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) trails.clear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }

    public void setIntensity(float intensity) {
        this.intensity = MathHelper.clamp(intensity, 0f, 2f);
    }
}