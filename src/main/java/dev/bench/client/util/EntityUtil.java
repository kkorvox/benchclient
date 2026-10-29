package dev.bench.client.util;

import dev.bench.client.modules.combat.KillAura.TargetMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public final class EntityUtil {
    private EntityUtil() {}

    public static LivingEntity getBestTarget(
            MinecraftClient mc,
            float range,
            TargetMode mode,
            boolean onlyPlayers,
            boolean throughWalls,
            int fov) {

        if (mc.player == null || mc.world == null) return null;

        Vec3d eyes = mc.player.getEyePos();

        List<LivingEntity> candidates = mc.world.getEntitiesByClass(
                LivingEntity.class,
                mc.player.getBoundingBox().expand(range),
                e -> {
                    if (e == mc.player)   return false;
                    if (e.isDead())       return false;
                    if (onlyPlayers && !(e instanceof PlayerEntity)) return false;
                    if (eyes.distanceTo(e.getEyePos()) > range)      return false;
                    if (!throughWalls && !mc.player.canSee(e))       return false;
                    if (fov < 360 && angleTo(mc.player, e) > fov / 2.0f) return false;
                    return true;
                });

        if (candidates.isEmpty()) return null;

        return switch (mode) {
            case CLOSEST        -> candidates.stream()
                                       .min(Comparator.comparingDouble(
                                           e -> eyes.distanceTo(e.getEyePos())))
                                       .orElse(null);
            case LOWEST_HEALTH  -> candidates.stream()
                                       .min(Comparator.comparingDouble(
                                           LivingEntity::getHealth))
                                       .orElse(null);
            case HIGHEST_HEALTH -> candidates.stream()
                                       .max(Comparator.comparingDouble(
                                           LivingEntity::getHealth))
                                       .orElse(null);
            case FOV            -> candidates.stream()
                                       .min(Comparator.comparingDouble(
                                           e -> angleTo(mc.player, e)))
                                       .orElse(null);
        };
    }

    public static float angleTo(PlayerEntity from, LivingEntity to) {
        Vec3d look = from.getRotationVec(1.0f).normalize();
        Vec3d dir  = to.getEyePos().subtract(from.getEyePos()).normalize();
        double dot = Math.max(-1.0, Math.min(1.0, look.dotProduct(dir)));
        return (float) Math.toDegrees(Math.acos(dot));
    }
}
