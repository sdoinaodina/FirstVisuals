package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class TabListEffect {
    private boolean enabled = true;
    private float entryPulse = 0f;
    private int newEntryIndex = -1;
    private boolean animationEnabled = true;
    private float slideIn = 1f;
    private int glowColor = 0xFF55FFFF;
    private boolean alphabeticalSort = false;
    private Random random = new Random();

    public void onEntryAdded(int index) {
        newEntryIndex = index;
        entryPulse = 1f;
        slideIn = 0f;
    }

    public void onTick() {
        // Slide in animation
        if (slideIn < 1f) {
            slideIn = MathHelper.lerp(0.15f, slideIn, 1f);
        }

        // Entry highlight decay
        if (entryPulse > 0) {
            entryPulse = MathHelper.lerp(0.03f, entryPulse, 0f);
        }
    }

    public void renderTabList(GuiGraphics matrices, int x, int y) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Slide animation offset
        int offsetX = (int) ((1f - slideIn) * 100);

        // Header glow
        if (entryPulse > 0) {
            int glowAlpha = (int)(entryPulse * 100);
            int glowCol = ((glowAlpha & 0xFF) << 24) | (glowColor & 0x00FFFFFF);
            matrices.drawString(client.textRenderer, "Online", x + offsetX, y, glowCol);
        }

        // Render player list with optional alphabetical sorting
        // (simplified - would need actual player data)
        if (newEntryIndex >= 0) {
            // Highlight new entry
            matrices.drawString(client.textRenderer, "Player" + newEntryIndex,
                    x + offsetX, y + 12 + newEntryIndex * 10, 0xFF55FF);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setGlowColor(int color) {
        this.glowColor = 0xFF000000 | (color & 0x00FFFFFF);
    }

    public void setAlphabeticalSort(boolean sort) {
        this.alphabeticalSort = sort;
    }
}