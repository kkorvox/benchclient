package dev.bench.client.modules.movement;

import dev.bench.client.events.EventHandler;
import dev.bench.client.events.game.MoveEvent;
import dev.bench.client.modules.Module;
import net.minecraft.client.MinecraftClient;

public final class Fly extends Module {

    private final EnumSetting<FlyMode> mode  = enumS ("Mode",  "Fly mode",  FlyMode.VANILLA);
    private final FloatSetting         speed = floatS("Speed", "Fly speed", 1.0f, 0.1f, 5.0f);

    public enum FlyMode { VANILLA, CREATIVE }

    public Fly() {
        super("Fly", "Allows the player to fly freely", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().flying      = true;
    }

    @Override
    protected void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.player.getAbilities().flying      = false;
        mc.player.getAbilities().allowFlying = false;
    }

    @EventHandler
    public void onMove(MoveEvent e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().flying      = true;

        double vertical = mc.options.jumpKey.isPressed()  ?  speed.getValue() :
                          mc.options.sneakKey.isPressed() ? -speed.getValue() : 0;

        mc.player.setVelocity(
            mc.player.getVelocity().x,
            vertical,
            mc.player.getVelocity().z
        );
    }
}
