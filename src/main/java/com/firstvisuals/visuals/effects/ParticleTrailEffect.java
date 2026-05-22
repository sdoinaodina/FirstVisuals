package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

public class ParticleTrailEffect {
    private boolean enabled = true;
    private Map<Integer, Particle> particles = new ConcurrentHashMap<>();
    private int nextId = 0;
    private float intensity = 1f;
    private float spawnRate = 0.5f;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private ParticleType particleType = ParticleType.SPARKLE;
    private Random random = new Random();

    public enum ParticleType {
        SPARKLE, FLAME, SMOKE, HEART, STAR
    }

    private static class Particle {
        float x, y;
        float vx, vy;
        float alpha;
        float scale;
        float age;
        float maxAge;
        int color;
        int id;
        ParticleType type;
        float rotation;
        float rotationSpeed;

        Particle(float x, float y, ParticleType type, int id) {
            this.x = x;
            this.y = y;
            this.vx = (random.nextFloat() - 0.5f) * 2f;
            this.vy = -1f - random.nextFloat() * 2f;
            this.alpha = 1f;
            this.scale = 0.5f + random.nextFloat() * 0.5f;
            this.age = 0;
            this.maxAge = 20 + random.nextInt(30);
            this.type = type;
            this.id = id;
            this.rotation = random.nextFloat() * 360f;
            this.rotationSpeed = (random.nextFloat() - 0.5f) * 10f;
        }
    }

    public void onEvent(float x, float y) {
        if (!enabled || random.nextFloat() > spawnRate) return;

        if (particles.size() < 200) {
            if (rainbowMode) {
                Particle p = new Particle(x, y, particleType, nextId);
                p.color = getRainbowColor();
                particles.put(nextId++, p);
            } else {
                particles.put(nextId++, new Particle(x, y, particleType, nextId));
            }
        }
    }

    public void onTick() {
        for (Map.Entry<Integer, Particle> entry : particles.entrySet()) {
            Particle p = entry.getValue();
            p.age++;
            p.x += p.vx;
            p.y += p.vy;
            p.vy += 0.05f; // gravity
            p.vx *= 0.98f;
            p.alpha = 1f - (p.age / p.maxAge);
            p.scale = MathHelper.lerp(0.02f, p.scale, 0.2f);
            p.rotation += p.rotationSpeed;

            if (p.age > p.maxAge) {
                particles.remove(entry.getKey());
            }
        }

        if (rainbowMode) {
            hue += 0.02f;
            if (hue > 1f) hue = 0f;
        }
    }

    public void renderParticles(GuiGraphics matrices) {
        if (!enabled || particles.isEmpty()) return;

        for (Particle p : particles.values()) {
            if (p.alpha <= 0) continue;

            int color;
            if (p.color != 0) {
                color = ((int)(p.alpha * 255) & 0xFF) << 24 | (p.color & 0x00FFFFFF);
            } else {
                color = ((int)(p.alpha * 200) & 0xFF) << 24 | 0xFFFF00;
            }

            int size = Math.max(1, (int)(p.scale * 4));

            switch (p.type) {
                case SPARKLE:
                    // Draw 4-pointed star
                    matrices.fill((int)p.x - size, (int)p.y, (int)p.x + size, (int)p.y + 1, color);
                    matrices.fill((int)p.x, (int)p.y - size, (int)p.x + 1, (int)p.y + size, color);
                    break;
                case FLAME:
                    matrices.fill((int)p.x - size/2, (int)p.y - size, (int)p.x + size/2, (int)p.y, color);
                    break;
                case SMOKE:
                    matrices.fill((int)p.x - size, (int)p.y - size, (int)p.x + size, (int)p.y + size, (color >> 1) & 0x7F7F7F7F);
                    break;
                case HEART:
                    // Draw heart shape
                    matrices.fill((int)p.x - size, (int)p.y, (int)p.x, (int)p.y + size, color);
                    matrices.fill((int)p.x, (int)p.y, (int)p.x + size, (int)p.y + size, color);
                    break;
                case STAR:
                    matrices.fill((int)p.x - size, (int)p.y, (int)p.x + size, (int)p.y + 1, color);
                    matrices.fill((int)p.x, (int)p.y - size, (int)p.x + 1, (int)p.y + size, color);
                    matrices.fill((int)p.x - size, (int)p.y + size, (int)p.x + size, (int)p.y + size + 1, color);
                    break;
            }
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
        if (!enabled) particles.clear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }

    public void setParticleType(ParticleType type) {
        this.particleType = type;
    }

    public void setIntensity(float intensity) {
        this.intensity = MathHelper.clamp(intensity, 0f, 2f);
    }
}