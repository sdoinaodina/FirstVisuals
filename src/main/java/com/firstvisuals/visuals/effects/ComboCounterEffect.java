package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

public class ComboCounterEffect {
    private boolean enabled = true;
    private int comboCount = 0;
    private int maxCombo = 0;
    private float comboTimer = 0;
    private float displayScale = 1f;
    private float displayAlpha = 1f;
    private float velocityY = 0f;
    private int lastKillTime = 0;
    private Map<Integer, ComboNumber> numbers = new ConcurrentHashMap<>();
    private int nextId = 0;
    private Random random = new Random();

    private static class ComboNumber {
        float x, y, startY;
        float value;
        float alpha;
        float scale;
        int age;
        float velocityY;
        float velocityX;

        ComboNumber(float x, float y, float value) {
            this.x = x;
            this.y = y;
            this.startY = y;
            this.value = value;
            this.alpha = 1f;
            this.scale = 1.5f;
            this.age = 0;
            this.velocityY = -1.5f;
            this.velocityX = (random.nextFloat() - 0.5f) * 2f;
        }
    }

    public void onHit(float damage) {
        comboCount++;
        comboTimer = 200; // 10 seconds at 20 ticks/sec
        displayScale = 1.4f;
        lastKillTime = (int) (System.currentTimeMillis() / 1000);
    }

    public void onTick() {
        // Combo timer countdown
        if (comboTimer > 0) {
            comboTimer--;
            if (comboTimer <= 0) {
                if (comboCount > maxCombo) maxCombo = comboCount;
                comboCount = 0;
            }
        }

        // Scale animation
        if (displayScale > 1f) {
            displayScale = MathHelper.lerp(0.08f, displayScale, 1f);
        }

        // Update floating numbers
        for (Map.Entry<Integer, ComboNumber> entry : numbers.entrySet()) {
            ComboNumber cn = entry.getValue();
            cn.age++;
            cn.y += cn.velocityY;
            cn.x += cn.velocityX;
            cn.velocityY *= 0.98f;
            cn.velocityX *= 0.98f;
            cn.alpha = Math.max(0f, 1f - (cn.age / 50f));
            cn.scale = MathHelper.lerp(0.03f, cn.scale, 1f);
        }

        numbers.entrySet().removeIf(e -> e.getValue().age > 60);
    }

    public void addHitNumber(float x, float y, float damage) {
        numbers.put(nextId++, new ComboNumber(x, y, damage));
    }

    public void renderCombo(GuiGraphics matrices) {
        if (!enabled || comboCount == 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int centerX = width / 2;

        // Combo display position (top center)
        int y = 50;

        // Main combo text
        String comboText = comboCount + "x COMBO";
        int comboColor = getComboColor(comboCount);

        // Scale animation
        int scaledWidth = (int) (client.textRenderer.getWidth(comboText) * displayScale);
        int textX = centerX - scaledWidth / 2;

        // Glow effect for high combos
        if (comboCount >= 5) {
            int glowColor = ((int)(Math.min(1f, displayScale - 1f) * 150) & 0xFF) << 24 | comboColor;
            matrices.drawString(client.textRenderer, comboText, textX - 1, y, glowColor);
            matrices.drawString(client.textRenderer, comboText, textX + 1, y, glowColor);
            matrices.drawString(client.textRenderer, comboText, textX, y - 1, glowColor);
            matrices.drawString(client.textRenderer, comboText, textX, y + 1, glowColor);
        }

        matrices.drawString(client.textRenderer, comboText, textX, y, comboColor);

        // Timer bar under combo
        if (comboTimer > 0) {
            int barWidth = 100;
            int barX = centerX - barWidth / 2;
            int barY = y + 15;
            float timerPercent = comboTimer / 200f;

            matrices.fill(barX, barY, barX + (int)(barWidth * timerPercent), barY + 3, comboColor);
        }

        // Render hit numbers
        for (ComboNumber cn : numbers.values()) {
            if (cn.alpha > 0) {
                String numText = String.format("+%.1f", cn.value);
                int numColor = ((int)(cn.alpha * 255) & 0xFF) << 24 | 0x00FF55;
                matrices.drawString(client.textRenderer, numText, (int) cn.x, (int) cn.y, numColor);
            }
        }
    }

    private int getComboColor(int combo) {
        if (combo >= 20) return 0xFFFF00FF; // Pink for 20+
        if (combo >= 15) return 0xFFFF5500; // Orange for 15+
        if (combo >= 10) return 0xFFFFFF00; // Yellow for 10+
        if (combo >= 5) return 0xFF55FF55;  // Green for 5+
        return 0xFFFFFFFF;                  // White for <5
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            comboCount = 0;
            comboTimer = 0;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getComboCount() {
        return comboCount;
    }

    public int getMaxCombo() {
        return maxCombo;
    }
}