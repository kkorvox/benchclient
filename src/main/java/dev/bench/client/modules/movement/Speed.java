package dev.bench.client.modules.movement;

import dev.bench.client.events.EventHandler;
import dev.bench.client.events.game.MoveEvent;
import dev.bench.client.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;

public final class Speed extends Module {

    private final EnumSetting<SpeedMode> mode = enumS("Mode", "Speed mode", SpeedMode.VANILLA);
    private final FloatSetting multiplier = floatS("Multiplier", "Speed multiplier", 1.5f, 1.0f, 5.0f);
    private final BoolSetting onlyGround = bool("Ground Only", "Only speed on ground", true);

    public enum SpeedMode { VANILLA, STRAFE, BHOP }

    public Speed() {
        super("Speed", "Increases player movement speed", Category.MOVEMENT);
    }

    @EventHandler
    public void onMove(MoveEvent e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (onlyGround.isEnabled() && !mc.player.isOnGround()) return;
        double baseSpeed = 0.2873;
        double targetSpeed = baseSpeed * multiplier.getValue();
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            int amp = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            targetSpeed *= 1.0 + 0.2 * (amp + 1);
        }
        switch (mode.getValue()) {
            case VANILLA -> applyVanilla(e, targetSpeed);
            case STRAFE  -> applyStrafe(e, mc, targetSpeed);
            case BHOP    -> applyBhop(e, mc, targetSpeed);
        }
    }

    private void applyVanilla(MoveEvent e, double speed) {
        if (e.getZ() == 0 && e.getX() == 0) return;
        double yaw = Math.toRadians(MinecraftClient.getInstance().player.getYaw());
        e.setX(-Math.sin(yaw) * speed);
        e.setZ(Math.cos(yaw) * speed);
    }

    private void applyStrafe(MoveEvent e, MinecraftClient mc, double speed) {
        double yaw = Math.toRadians(mc.player.getYaw());
        double sx = e.getX(), sz = e.getZ();
        if (sx == 0 && sz == 0) return;
        double angle = Math.atan2(sz, sx);
        e.setX(Math.cos(angle - yaw) * speed);
        e.setZ(Math.sin(angle - yaw) * speed);
    }

    private void applyBhop(MoveEvent e, MinecraftClient mc, double speed) {
        if (mc.player.isOnGround()) mc.player.jump();
        applyVanilla(e, speed * 1.02);
    }
}
