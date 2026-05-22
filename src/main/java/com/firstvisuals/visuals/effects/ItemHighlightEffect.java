package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ItemHighlightEffect {
    private boolean enabled = true;
    private int highlightedSlot = -1;
    private float pulseIntensity = 0f;
    private float glowRadius = 0f;
    private int highlightColor = 0x8800FFFF;
    private boolean rotationEnabled = true;
    private float itemRotate = 0f;
    private float bobOffset = 0f;
    private boolean durabilityWarningEnabled = true;
    private int lowDurabilityThreshold = 20;
    private Random random = new Random();

    public void onSlotHighlight(int slot) {
        this.highlightedSlot = slot;
        this.pulseIntensity = 1f;
        this.glowRadius = 1.3f;
        this.itemRotate = 0f;
    }

    public void onTick() {
        // Pulse animation
        if (pulseIntensity > 0) {
            pulseIntensity = MathHelper.lerp(0.05f, pulseIntensity, 0f);
        }

        // Glow radius animation
        if (glowRadius > 1f) {
            glowRadius = MathHelper.lerp(0.08f, glowRadius, 1f);
        }

        // Item rotation
        if (rotationEnabled && highlightedSlot >= 0 && pulseIntensity > 0.5f) {
            itemRotate += 3f;
            if (itemRotate > 360f) itemRotate -= 360f;
        }

        // Bobbing animation
        bobOffset = (float) Math.sin(System.currentTimeMillis() * 0.005f) * 2f;
    }

    public void renderItem(GuiGraphics matrices, ItemStack item, int x, int y, String itemName) {
        if (!enabled || item == null) return;

        // Glow effect
        if (glowRadius > 1f || pulseIntensity > 0) {
            int glowAlpha = (int)((glowRadius - 1f) * 150 + pulseIntensity * 50);
            int glowCol = ((glowAlpha & 0xFF) << 24) | (highlightColor & 0x00FFFFFF);
            matrices.fill(x - 3, y - 3, x + 19, y + 19, glowCol);
        }

        // Draw item with bob animation
        int drawY = (int) (y + (highlightedSlot >= 0 ? bobOffset : 0));

        // Durability warning
        if (durabilityWarningEnabled && item.isDamageable()) {
            int durability = item.getMaxDamage() - item.getDamage();
            int maxDurability = item.getMaxDamage();
            float percent = (float) durability / maxDurability;

            if (percent < lowDurabilityThreshold / 100f) {
                int warningColor = 0xFFFF0000;
                matrices.fill(x - 2, drawY - 2, x + 20, drawY + 20, warningColor);
            }
        }

        // Item rotate hint (draw a small indicator)
        if (itemRotate > 0) {
            matrices.drawString(MinecraftClient.getInstance().textRenderer, "↻", x + 5, drawY + 20, 0xFF00FFFF);
        }
    }

    public void renderHotbarSlot(GuiGraphics matrices, int slot, int x, int y, int width, int height) {
        if (!enabled || slot != highlightedSlot) return;

        // Border glow
        if (glowRadius > 1f) {
            int glowAlpha = (int)((glowRadius - 1f) * 200);
            int glowCol = ((glowAlpha & 0xFF) << 24) | (highlightColor & 0x00FFFFFF);

            // Top border
            matrices.fill(x - 2, y - 2, x + width + 2, y, glowCol);
            // Bottom
            matrices.fill(x - 2, y + height, x + width + 2, y + height + 2, glowCol);
            // Left
            matrices.fill(x - 2, y, x, y + height, glowCol);
            // Right
            matrices.fill(x + width, y, x + width + 2, y + height, glowCol);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setHighlightColor(int color) {
        this.highlightColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setLowDurabilityThreshold(int threshold) {
        this.lowDurabilityThreshold = MathHelper.clamp(threshold, 1, 100);
    }
}