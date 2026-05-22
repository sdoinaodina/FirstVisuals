package com.firstvisuals.visuals;

import com.firstvisuals.visuals.effects.ScreenPulseEffect;
import com.firstvisuals.visuals.effects.HudPulseEffect;
import com.firstvisuals.visuals.effects.ScreenShakeEffect;
import com.firstvisuals.visuals.effects.DamageOverlayEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;

public class VisualManager {
    private final ScreenPulseEffect screenPulse;
    private final HudPulseEffect hudPulse;
    private final ScreenShakeEffect screenShake;
    private final DamageOverlayEffect damageOverlay;

    public VisualManager() {
        this.screenPulse = new ScreenPulseEffect();
        this.hudPulse = new HudPulseEffect();
        this.screenShake = new ScreenShakeEffect();
        this.damageOverlay = new DamageOverlayEffect();

        screenPulse.setEnabled(true);
        hudPulse.setEnabled(true);
        screenShake.setEnabled(true);
        damageOverlay.setEnabled(true);
    }

    public void onTick() {
        screenPulse.onTick();
        hudPulse.onTick();
        screenShake.onTick();
        damageOverlay.onTick();
    }

    public void renderHud(GuiGraphics matrices, float tickDelta) {
        damageOverlay.render(matrices, tickDelta);
        screenPulse.render(matrices, tickDelta);
    }

    // Trigger effects when damage is taken
    public void onDamageTaken(float damage) {
        screenPulse.trigger(1.0f, 300);
        hudPulse.trigger();
        screenShake.trigger(damage * 0.3f, (int)(damage * 15));
        damageOverlay.trigger((int)(damage * 10));
    }

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
}