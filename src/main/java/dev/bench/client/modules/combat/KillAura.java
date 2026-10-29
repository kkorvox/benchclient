package dev.bench.client.modules.combat;

import dev.bench.client.events.EventHandler;
import dev.bench.client.events.game.TickEvent;
import dev.bench.client.modules.Module;
import dev.bench.client.util.EntityUtil;
import dev.bench.client.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

public final class KillAura extends Module {

    private final FloatSetting range       = floatS("Range",        "Attack range",         3.8f, 1.0f, 6.0f);
    private final IntSetting   cps         = intS  ("CPS",          "Attacks per second",   12,   1,    20);
    private final EnumSetting<TargetMode> targetMode = enumS("Target", "Target priority",   TargetMode.CLOSEST);
    private final BoolSetting  rotations   = bool  ("Rotations",    "Send rotation packets",true);
    private final BoolSetting  onlyPlayers = bool  ("Players Only", "Only target players",  true);
    private final BoolSetting  throughWalls= bool  ("Through Walls","Attack through blocks",false);
    private final IntSetting   fov         = intS  ("FOV",          "Target FOV limit",     360,  10,   360);

    private long lastAttack = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
    }

    public enum TargetMode { CLOSEST, LOWEST_HEALTH, HIGHEST_HEALTH, FOV }

    @EventHandler
    public void onTick(TickEvent e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        long now      = System.currentTimeMillis();
        long interval = 1000L / cps.getValue();
        if (now - lastAttack < interval) return;

        LivingEntity target = EntityUtil.getBestTarget(
                mc, range.getValue(), targetMode.getValue(),
                onlyPlayers.isEnabled(), throughWalls.isEnabled(), fov.getValue());
        if (target == null) return;

        if (rotations.isEnabled())
            RotationUtil.faceEntity(mc.player, target);

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        lastAttack = now;
    }
}
