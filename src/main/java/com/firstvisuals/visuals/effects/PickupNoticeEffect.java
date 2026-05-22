package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class PickupNoticeEffect {
    private boolean enabled = true;
    private String itemName = "";
    private float slideIn = 1f;
    private float fadeOut = 0f;
    private float bounceY = 0f;
    private int itemCount = 1;
    private boolean slideFromRight = true;
    private float backgroundAlpha = 0.8f;
    private int textColor = 0xFFFFFF;
    private int backgroundColor = 0x88000000;
    private Random random = new Random();

    public void onItemPickup(String itemName, int count) {
        this.itemName = itemName;
        this.itemCount = count;
        slideIn = 0f;
        fadeOut = 0f;
        bounceY = 0f;
    }

    public void onTick() {
        if (slideIn < 1f) {
            slideIn = MathHelper.lerp(0.15f, slideIn, 1f);
        }

        if (slideIn >= 1f && fadeOut < 1f && itemName.length() > 0) {
            fadeOut = MathHelper.lerp(0.005f, fadeOut, 1f);
        }

        // Bounce animation
        if (slideIn < 1f) {
            bounceY = (float) Math.sin(slideIn * Math.PI) * 5f;
        } else {
            bounceY = MathHelper.lerp(0.1f, bounceY, 0f);
        }
    }

    public void renderPickupNotice(GuiGraphics matrices, int screenWidth, int screenHeight) {
        if (!enabled || itemName.isEmpty() || fadeOut >= 1f) return;

        MinecraftClient client = MinecraftClient.getInstance();

        String displayText = itemCount > 1 ? itemName + " x" + itemCount : itemName;
        int textWidth = client.textRenderer.getWidth(displayText);

        int boxWidth = textWidth + 20;
        int boxHeight = 20;

        int baseX = screenWidth - boxWidth - 5;
        int baseY = screenHeight / 2;

        int offsetX = slideFromRight ? (int) ((1f - slideIn) * 150) : 0;
        int offsetY = (int) bounceY;

        int x = baseX + offsetX;
        int y = baseY + offsetY;

        int alpha = (int) ((1f - fadeOut) * backgroundAlpha * 255);
        int bgColor = (alpha << 24) | (backgroundColor & 0x00FFFFFF);

        // Background
        matrices.fill(x, y, x + boxWidth, y + boxHeight, bgColor);

        // Text
        int textCol = ((int)((1f - fadeOut) * 255) << 24) | (textColor & 0x00FFFFFF);
        matrices.drawString(client.textRenderer, displayText, x + 10, y + 5, textCol);

        // Icon placeholder (would be item sprite in real implementation)
        matrices.fill(x + 2, y + 2, x + 16, y + 16, 0xFF888888);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            itemName = "";
            slideIn = 1f;
            fadeOut = 1f;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTextColor(int color) {
        this.textColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setSlideFromRight(boolean fromRight) {
        this.slideFromRight = fromRight;
    }
}