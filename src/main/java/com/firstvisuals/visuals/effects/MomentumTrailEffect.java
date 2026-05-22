package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MomentumTrailEffect {
    private boolean enabled = true;
    private Map<Integer, TrailParticle> particles = new ConcurrentHashMap<>();
    private int nextId = 0;
    private float lastPlayerX = 0;
    private float lastPlayerY = 0;
    private float lastPlayerZ = 0;
    private float velocity = 0;
    private float maxTrailIntensity = 1.5f;
    private float trailDensity = 0.5f;
    private int maxParticles = 100;
    private boolean rainbowMode = false;
    private float hue = 0f;
    private Random random = new Random();

    private static class TrailParticle {
        float x, y, z;
        float alpha;
        float scale;
        float age;
        float maxAge;
        float velocityY;
        float r, g, b;
        int id;

        TrailParticle(float x, float y, float z, float scale, int id) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.scale = scale;
            this.alpha = 1f;
            this.age = 0;
            this.maxAge = 30 + random.nextInt(20);
            this.velocityY = 0.02f + random.nextFloat() * 0.02f;
            this.id = id;
            this.r = 0.3f;
            this.g = 0.8f;
            this.b = 1f;
        }
    }

    public void onPlayerMove(float x, float y, float z) {
        if (!enabled) return;

        float dx = x - lastPlayerX;
        float dy = y - lastPlayerY;
        float dz = z - lastPlayerZ;
        float newVelocity = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Update velocity
        velocity = MathHelper.lerp(0.1f, velocity, newVelocity);

        // Spawn particles based on velocity
        if (velocity > 0.05f && random.nextFloat() < trailDensity) {
            spawnParticle(x, y, z);
        }

        lastPlayerX = x;
        lastPlayerY = y;
        lastPlayerZ = z;
    }

    private void spawnParticle(float x, float y, float z) {
        if (particles.size() >= maxParticles) {
            // Remove oldest
            int oldestId = Integer.MAX_VALUE;
            for (Map.Entry<Integer, TrailParticle> entry : particles.entrySet()) {
                if (entry.getKey() < oldestId) {
                    oldestId = entry.getKey();
                }
            }
            particles.remove(oldestId);
        }

        float scale = 0.3f + velocity * 2f;
        if (rainbowMode) {
            float[] rgb = hueToRgb(hue);
            TrailParticle p = new TrailParticle(x, y, z, scale, nextId);
            p.r = rgb[0];
            p.g = rgb[1];
            p.b = rgb[2];
            particles.put(nextId++, p);
            hue += 0.02f;
            if (hue > 1f) hue = 0f;
        } else {
            particles.put(nextId++, new TrailParticle(x, y, z, scale, nextId));
        }
    }

    public void onTick() {
        // Update all particles
        for (Map.Entry<Integer, TrailParticle> entry : particles.entrySet()) {
            TrailParticle p = entry.getValue();
            p.age++;
            p.y += p.velocityY;
            p.alpha = 1f - (p.age / p.maxAge);
            p.scale = MathHelper.lerp(0.05f, p.scale, 0f);
        }

        // Remove dead particles
        particles.entrySet().removeIf(e -> e.getValue().age > e.getValue().maxAge);

        // Decay velocity
        if (velocity > 0) {
            velocity = MathHelper.lerp(0.05f, velocity, 0f);
        }
    }

    public void renderTrail(GuiGraphics matrices, float tickDelta) {
        if (!enabled || particles.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Render particles as glowing dots
        for (TrailParticle p : particles.values()) {
            if (p.alpha <= 0) continue;

            // Simplified 2D projection (would need proper 3D in real implementation)
            int screenX = (int) (client.getWindow().getScaledWidth() / 2f + (p.x - lastPlayerX) * 10);
            int screenY = (int) (client.getWindow().getScaledHeight() / 2f - (p.y - lastPlayerY) * 10);

            int alpha = (int) (p.alpha * 150);
            int color = (alpha & 0xFF) << 24
                    | ((int)(p.r * 255) & 0xFF) << 16
                    | ((int)(p.g * 255) & 0xFF) << 8
                    | ((int)(p.b * 255) & 0xFF);

            int size = Math.max(1, (int) (p.scale * 3));
            matrices.fill(screenX - size, screenY - size, screenX + size, screenY + size, color);
        }
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
        if (!enabled) {
            particles.clear();
            velocity = 0;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRainbowMode(boolean rainbow) {
        this.rainbowMode = rainbow;
    }

    public void setTrailDensity(float density) {
        this.trailDensity = MathHelper.clamp(density, 0f, 1f);
    }

    public void setMaxParticles(int max) {
        this.maxParticles = Math.max(10, max);
    }

    public float getVelocity() {
        return velocity;
    }
}