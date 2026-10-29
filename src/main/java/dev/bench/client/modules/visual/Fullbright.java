package dev.bench.client.modules.visual;

import dev.bench.client.events.EventHandler;
import dev.bench.client.events.game.TickEvent;
import dev.bench.client.modules.Module;
import net.minecraft.client.MinecraftClient;

public final class Fullbright extends Module {

    private final FloatSetting gamma = floatS("Gamma", "Gamma override level", 10.0f, 1.0f, 16.0f);

    private double originalGamma = 1.0;

    public Fullbright() {
        super("Fullbright", "Removes darkness by boosting gamma", Category.VISUAL);
    }

    @Override
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        originalGamma = mc.options.getGamma().getValue();
    }

    @Override
    protected void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        mc.options.getGamma().setValue(originalGamma);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        mc.options.getGamma().setValue((double) gamma.getValue());
    }
}
