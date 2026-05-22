package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class HotbarEffect {
    private boolean enabled = true;
    private float[] slotScales = new float[9];
    private int selectedSlot = 0;
    private int lastSelectedSlot = 0;
    private float glowIntensity = 0f;
    private boolean animationEnabled = true;
    private float bounceSpeed = 0.15f;
    private float maxBounce = 0.12f;
    private Random random = new Random();

    public HotbarEffect() {
        for (int i = 0; i < 9; i++) {
            slotScales[i] = 1.0f;
        }
    }

    public void onSlotChange(int newSlot) {
        if (!enabled) return;

        lastSelectedSlot = selectedSlot;
        selectedSlot = newSlot;

        // Bounce effect on selection
        slotScales[newSlot] = 1f + maxBounce;
        glowIntensity = 1f;
    }

    public void onTick() {
        // Animate all slots
        for (int i = 0; i < 9; i++) {
            float target = (i == selectedSlot) ? slotScales[i] : 1f;
            if (i == selectedSlot && slotScales[i] > 1f) {
                slotScales[i] = MathHelper.lerp(bounceSpeed, slotScales[i], 1f);
            } else if (slotScales[i] > 1f) {
                slotScales[i] = MathHelper.lerp(bounceSpeed * 0.7f, slotScales[i], 1f);
            }
        }

        // Decay glow
        if (glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(0.05f, glowIntensity, 0f);
        }
    }

    public void renderHotbar(GuiGraphics matrices, int x, int y, int slotWidth, int slotHeight, int spacing) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        for (int i = 0; i < 9; i++) {
            float scale = slotScales[i];
            int scaledWidth = (int) (slotWidth * scale);
            int scaledHeight = (int) (slotHeight * scale);

            int slotX = x + i * (slotWidth + spacing);
            int slotY = y;

            // Center the scaled slot
            int offsetX = (slotWidth - scaledWidth) / 2;
            int offsetY = (slotHeight - scaledHeight) / 2;

            // Glow effect for selected slot
            if (glowIntensity > 0 && i == selectedSlot) {
                int glowColor = ((int)(glowIntensity * 100) & 0xFF) << 24 | 0x00FFFF;
                matrices.fill(slotX + offsetX - 2, slotY + offsetY - 2,
                        slotX + offsetX + scaledWidth + 2, slotY + offsetY + scaledHeight + 2, glowColor);
            }

            // Draw slot background
            int bgColor = (i == selectedSlot) ? 0x8800AAAA : 0x88000000;
            matrices.fill(slotX + offsetX, slotY + offsetY,
                    slotX + offsetX + scaledWidth, slotY + offsetY + scaledHeight, bgColor);

            // Slot border
            int borderColor = (i == selectedSlot) ? 0xFF00FFFF : 0xFF555555;
            matrices.fill(slotX + offsetX, slotY + offsetY,
                    slotX + offsetX + scaledWidth, slotY + offsetY + 1, borderColor);
            matrices.fill(slotX + offsetX, slotY + offsetY + scaledHeight - 1,
                    slotX + offsetX + scaledWidth, slotY + offsetY + scaledHeight, borderColor);
            matrices.fill(slotX + offsetX, slotY + offsetY,
                    slotX + offsetX + 1, slotY + offsetY + scaledHeight, borderColor);
            matrices.fill(slotX + offsetX + scaledWidth - 1, slotY + offsetY,
                    slotX + offsetX + scaledWidth, slotY + offsetY + scaledHeight, borderColor);
        }
    }

    public float getSlotScale(int slot) {
        return slot >= 0 && slot < 9 ? slotScales[slot] : 1f;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            for (int i = 0; i < 9; i++) {
                slotScales[i] = 1f;
            }
            glowIntensity = 0f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}