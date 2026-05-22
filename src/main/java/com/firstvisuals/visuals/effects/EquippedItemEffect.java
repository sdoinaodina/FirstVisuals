package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class EquippedItemEffect {
    private boolean enabled = true;
    private float itemGlow = 0f;
    private float scale = 1f;
    private float targetScale = 1f;
    private float bobY = 0f;
    private float rotation = 0f;
    private boolean enchantedGlow = false;
    private float enchantedHue = 0f;
    private int enchantColor = 0xAA00FF;
    private Random random = new Random();

    public void onItemEquipped() {
        targetScale = 1.3f;
        itemGlow = 1f;
        rotation = 0f;
    }

    public void onTick() {
        // Scale animation
        if (scale > targetScale) {
            scale = MathHelper.lerp(0.1f, scale, targetScale);
        }

        // Glow decay
        if (itemGlow > 0) {
            itemGlow = MathHelper.lerp(0.02f, itemGlow, 0f);
        }

        // Bob animation
        bobY = (float) Math.sin(System.currentTimeMillis() * 0.003f) * 2f;

        // Rotation
        if (rotation > 0) {
            rotation += 2f;
            if (rotation > 360f) rotation = 0f;
        }

        // Enchanted glow hue
        if (enchantedGlow) {
            enchantedHue += 0.01f;
            if (enchantedHue > 1f) enchantedHue = 0f;
            float[] rgb = hueToRgb(enchantedHue);
            enchantColor = 0xFF000000 | ((int)(rgb[0] * 255) << 16) | ((int)(rgb[1] * 255) << 8) | ((int)(rgb[2] * 255));
        }
    }

    public void renderEquipped(GuiGraphics matrices, int x, int y) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Glow effect
        if (itemGlow > 0) {
            int glowAlpha = (int)(itemGlow * 150);
            int glowCol = ((glowAlpha & 0xFF) << 24) | (enchantColor & 0x00FFFFFF);
            matrices.fill(x - 5, (int)(y + bobY - 5), x + 25, (int)(y + bobY + 25), glowCol);
        }

        // Scale effect for item display
        if (scale > 1f) {
            int scaledSize = (int) (18 * scale);
            int offset = (18 - scaledSize) / 2;
            // Item would be drawn here with scaled size
            matrices.drawString(client.textRenderer, "Item", x + offset, (int)(y + bobY + offset), 0xFFFFFF);
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
            itemGlow = 0f;
            scale = 1f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnchantedGlow(boolean enchanted) {
        this.enchantedGlow = enchanted;
    }
}