package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class DamageOverlayEffect {
    private boolean enabled = true;
    private float alpha = 0f;
    private int timer = 0;
    private int duration = 25;
    private float colorR = 0.9f;
    private float colorG = 0.1f;
    private float colorB = 0.1f;
    private boolean gradientEnabled = true;
    private float pulseSpeed = 0.15f;
    private Map<Integer, DamageNumber> damageNumbers = new ConcurrentHashMap<>();
    private int nextId = 0;

    private static class DamageNumber {
        float x, y, startY;
        float alpha;
        float scale;
        int age;
        float damage;
        float velocityY;

        DamageNumber(float x, float y, float damage) {
            this.x = x;
            this.y = y;
            this.startY = y;
            this.damage = damage;
            this.alpha = 1f;
            this.scale = 1.5f;
            this.age = 0;
            this.velocityY = -2f;
        }
    }

    public void trigger(int duration) {
        this.duration = duration;
        this.timer = duration;
        this.alpha = 1f;
    }

    public void onTick() {
        // Update overlay
        if (timer > 0) {
            timer--;
            alpha = easeOutQuad((float) timer / duration);
        } else {
            alpha = 0f;
        }

        // Update damage numbers
        for (Map.Entry<Integer, DamageNumber> entry : damageNumbers.entrySet()) {
            DamageNumber dn = entry.getValue();
            dn.age++;
            dn.y += dn.velocityY;
            dn.velocityY *= 0.95f;
            dn.alpha = Math.max(0f, 1f - (dn.age / 40f));
            dn.scale = MathHelper.lerp(0.05f, dn.scale, 1f);
        }

        // Remove dead numbers
        damageNumbers.entrySet().removeIf(e -> e.getValue().age > 50);
    }

    public void addDamageNumber(float x, float y, float damage) {
        damageNumbers.put(nextId++, new DamageNumber(x, y, damage));
    }

    public void render(GuiGraphics matrices, float tickDelta) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Main damage overlay
        if (alpha > 0) {
            if (gradientEnabled) {
                int colorTop = ((int)(alpha * 200) & 0xFF) << 24 | ((int)(colorR * 255) & 0xFF) << 16 | ((int)(colorG * 100) & 0xFF) << 8 | 0xFF;
                int colorBottom = ((int)(alpha * 80) & 0xFF) << 24 | ((int)(colorR * 255) & 0xFF) << 16 | 0x0000AA;
                matrices.fillGradient(0, 0, width, height, colorTop, colorBottom);
            } else {
                int color = ((int)(alpha * 180) & 0xFF) << 24 | ((int)(colorR * 255) & 0xFF) << 16 | ((int)(colorG * 50) & 0xFF) << 8 | 0xAA;
                matrices.fill(0, 0, width, height, color);
            }
        }

        // Render damage numbers
        for (DamageNumber dn : damageNumbers.values()) {
            if (dn.alpha > 0) {
                String text = String.format("-%.1f", dn.damage);
                int textColor = ((int)(dn.alpha * 255) & 0xFF) << 24 | 0xFF3333;
                matrices.drawString(client.textRenderer, text, (int) dn.x, (int) dn.y, textColor);
            }
        }
    }

    private float easeOutQuad(float t) {
        return t * (2 - t);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            alpha = 0f;
            timer = 0;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setColor(float r, float g, float b) {
        this.colorR = MathHelper.clamp(r, 0f, 1f);
        this.colorG = MathHelper.clamp(g, 0f, 1f);
        this.colorB = MathHelper.clamp(b, 0f, 1f);
    }

    public void setGradientEnabled(boolean gradient) {
        this.gradientEnabled = gradient;
    }
}