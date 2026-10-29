package dev.bench.client.modules.visual;

import dev.bench.client.events.EventHandler;
import dev.bench.client.events.game.WorldRenderEvent;
import dev.bench.client.modules.Module;
import dev.bench.client.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public final class ESP extends Module {

    private final EnumSetting<ESPMode> mode       = enumS ("Mode",       "ESP render mode",    ESPMode.OUTLINE);
    private final BoolSetting          colorHealth = bool  ("Health Col", "Color by health %",  true);
    private final FloatSetting         lineWidth   = floatS("Width",      "Line thickness",     1.5f, 0.5f, 4.0f);
    private final BoolSetting          tracers     = bool  ("Tracers",    "Draw tracers",       false);
    private final BoolSetting          healthBar   = bool  ("Health Bar", "Draw health bar",    true);
    private final BoolSetting          nameTag     = bool  ("Nametag",    "Draw name + distance",true);

    public enum ESPMode { OUTLINE, BOX, CORNERS }

    public ESP() { super("ESP", "Highlights players through walls", Category.VISUAL); }

    @EventHandler
    public void onRender(WorldRenderEvent e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        MatrixStack ms = e.getMatrixStack();

        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;

            float health = p.getHealth() / p.getMaxHealth();
            int   color  = colorHealth.isEnabled()
                    ? RenderUtil.healthColor(health)
                    : 0xFF00E5A0;

            Box box = p.getBoundingBox();

            switch (mode.getValue()) {
                case BOX     -> RenderUtil.drawBox    (ms, box, color, lineWidth.getValue());
                case OUTLINE -> RenderUtil.drawOutline(ms, box, color, lineWidth.getValue());
                case CORNERS -> RenderUtil.drawCorners(ms, box, color, lineWidth.getValue());
            }

            if (tracers.isEnabled())  RenderUtil.drawTracer   (ms, mc.player, p, color);
            if (healthBar.isEnabled()) RenderUtil.drawHealthBar(ms, p, health);
            if (nameTag.isEnabled())   RenderUtil.drawNameTag  (ms, p);
        }
    }
}
