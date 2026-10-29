package dev.bench.client.modules.combat;

import dev.bench.client.events.EventHandler;
import dev.bench.client.events.game.TickEvent;
import dev.bench.client.modules.Module;
import dev.bench.client.util.EntityUtil;
import dev.bench.client.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

public final class AimAssist extends Module {

    private final FloatSetting range       = floatS("Range",        "Max assist range",     5.0f, 1.0f, 10.0f);
    private final FloatSetting speed       = floatS("Speed",        "Rotation speed",       3.0f, 0.5f, 10.0f);
    private final IntSetting   fov         = intS  ("FOV",          "Assist field of view", 60,   5,    180);
    private final EnumSetting<AimPoint> aimPoint = enumS("Aim Point","Where to aim",        AimPoint.BODY);

    public enum AimPoint { HEAD, BODY, FEET }

    public AimAssist() {
        super("AimAssist", "Smoothly guides aim toward targets", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        LivingEntity target = EntityUtil.getBestTarget(
                mc, range.getValue(), KillAura.TargetMode.FOV,
                true, false, fov.getValue());
        if (target == null) return;

        RotationUtil.smoothFace(mc.player, target, speed.getValue(), aimPoint.getValue());
    }
}
