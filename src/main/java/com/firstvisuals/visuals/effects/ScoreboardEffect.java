package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class ScoreboardEffect {
    private boolean enabled = true;
    private float slideProgress = 1f;
    private boolean isSliding = false;
    private int displaySlot = 0;
    private float pulseAlpha = 0f;
    private boolean sidebarEnabled = true;
    private boolean belowNameEnabled = true;
    private boolean healthAboveEnabled = true;
    private float glowIntensity = 0f;
    private Random random = new Random();

    public void onScoreUpdate() {
        pulseAlpha = 1f;
        glowIntensity = 1f;
    }

    public void onTick() {
        // Slide animation
        if (isSliding) {
            slideProgress = MathHelper.lerp(0.1f, slideProgress, 1f);
            if (Math.abs(slideProgress - 1f) < 0.01f) {
                slideProgress = 1f;
                isSliding = false;
            }
        }

        // Pulse decay
        if (pulseAlpha > 0) {
            pulseAlpha = MathHelper.lerp(0.05f, pulseAlpha, 0f);
        }

        // Glow decay
        if (glowIntensity > 0) {
            glowIntensity = MathHelper.lerp(0.03f, glowIntensity, 0f);
        }
    }

    public void renderSidebar(GuiGraphics matrices, Scoreboard scoreboard) {
        if (!enabled || !sidebarEnabled || slideProgress <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Get sidebar position (right side)
        int width = client.getWindow().getScaledWidth();
        int x = width - 50;
        int y = 10;

        // Title glow
        if (glowIntensity > 0) {
            int glowColor = ((int)(glowIntensity * 80) & 0xFF) << 24 | 0x55FFFF;
            matrices.drawString(client.textRenderer, "SideBar", x, y, glowColor);
        }

        // Render scores
        int line = 0;
        for (ScoreboardEntry entry : scoreboard.getAllEntries()) {
            String text = entry.getObjective().getDisplayName() + ": " + entry.getScore();
            int color = getScoreColor(line);

            // Pulse effect on new scores
            if (pulseAlpha > 0 && line == 0) {
                color = blendColors(color, 0xFFFFFF, pulseAlpha);
            }

            matrices.drawString(client.textRenderer, text, x, y + 12 + line * 10, color);
            line++;
        }
    }

    private int getScoreColor(int line) {
        switch (line) {
            case 0: return 0xFFFF55;  // Gold
            case 1: return 0xFFFFFF;    // White
            case 2: return 0xAAAAAA;   // Gray
            default: return 0x888888;    // Dark gray
        }
    }

    private int blendColors(int color1, int color2, float factor) {
        int r = (int) ((color1 >> 16 & 0xFF) * (1 - factor) + (color2 >> 16 & 0xFF) * factor);
        int g = (int) ((color1 >> 8 & 0xFF) * (1 - factor) + (color2 >> 8 & 0xFF) * factor);
        int b = (int) ((color1 & 0xFF) * (1 - factor) + (color2 & 0xFF) * factor);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
