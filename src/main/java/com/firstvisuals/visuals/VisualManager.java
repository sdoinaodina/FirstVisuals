package com.firstvisuals.visuals;

import com.firstvisuals.FirstVisualsMod;
import com.firstvisuals.visuals.effects.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;

public class VisualManager {
    private final ScreenPulseEffect screenPulse;
    private final HudPulseEffect hudPulse;
    private final ScreenShakeEffect screenShake;
    private final DamageOverlayEffect damageOverlay;
    private final NeonGlowEffect neonGlow;
    private final HealthBarEffect healthBar;
    private final HotbarEffect hotbar;
    private final CrosshairEffect crosshair;

    private boolean globalEnabled = true;

    public VisualManager() {
        this.screenPulse = new ScreenPulseEffect();
        this.hudPulse = new HudPulseEffect();
        this.screenShake = new ScreenShakeEffect();
        this.damageOverlay = new DamageOverlayEffect();
        this.neonGlow = new NeonGlowEffect();
        this.healthBar = new HealthBarEffect();
        this.hotbar = new HotbarEffect();
        this.crosshair = new CrosshairEffect();

        // Enable all by default
        screenPulse.setEnabled(true);
        hudPulse.setEnabled(true);
        screenShake.setEnabled(true);
        damageOverlay.setEnabled(true);
        neonGlow.setEnabled(true);
        healthBar.setEnabled(true);
        hotbar.setEnabled(true);
        crosshair.setEnabled(true);
    }

    public void onTick() {
        if (!globalEnabled) return;

        screenPulse.onTick();
        hudPulse.onTick();
        screenShake.onTick();
        damageOverlay.onTick();
        neonGlow.onTick();
        healthBar.onTick();
        hotbar.onTick();
        crosshair.onTick();
    }

    public void renderHud(GuiGraphics matrices, float tickDelta) {
        if (!globalEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        // Render effects
        damageOverlay.render(matrices, tickDelta);
        screenPulse.render(matrices, tickDelta);
        crosshair.renderCrosshair(matrices, centerX, centerY);
    }

    public void onDamageTaken(float damage) {
        if (!globalEnabled) return;

        screenPulse.trigger(1.0f, 300);
        hudPulse.trigger();
        screenShake.trigger(damage * 0.4f, (int)(damage * 15));
        damageOverlay.trigger((int)(damage * 10));
        crosshair.onHit();
        neonGlow.trigger(1.5f);
    }

    public void onHealthChange(int oldHealth, int newHealth) {
        if (!globalEnabled) return;

        healthBar.onHealthChange(oldHealth, newHealth);
    }

    public void onSlotChange(int newSlot) {
        if (!globalEnabled) return;

        hotbar.onSlotChange(newSlot);
    }

    public void setGlobalEnabled(boolean enabled) {
        this.globalEnabled = enabled;
    }

    public boolean isGlobalEnabled() {
        return globalEnabled;
    }

    // Effect getters
    public ScreenPulseEffect getScreenPulse() {
        return screenPulse;
    }

    public HudPulseEffect getHudPulse() {
        return hudPulse;
    }

    public ScreenShakeEffect getScreenShake() {
        return screenShake;
    }

    public DamageOverlayEffect getDamageOverlay() {
        return damageOverlay;
    }

    public NeonGlowEffect getNeonGlow() {
        return neonGlow;
    }

    public HealthBarEffect getHealthBar() {
        return healthBar;
    }

    public HotbarEffect getHotbar() {
        return hotbar;
    }

    public CrosshairEffect getCrosshair() {
        return crosshair;
    }
}