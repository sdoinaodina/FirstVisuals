package com.firstvisuals.visuals.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;

public class DamageOverlayEffect {
    private boolean enabled = true;
    private float alpha = 0f;
    private int timer = 0;
    private int duration = 20;

    public void trigger(int duration) {
        this.duration = duration;
        this.timer = duration;
    }

    public void onTick() {
        if (timer > 0) {
            timer--;
            alpha = (float) timer / duration;
        } else {
            alpha = 0f;
        }
    }

    public void render(GuiGraphics matrices, float tickDelta) {
        if (!enabled || alpha <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Red damage overlay
        int color = ((int)(alpha * 180) & 0xFF) << 24 | 0x880000;
        matrices.fill(0, 0, width, height, color);
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
}