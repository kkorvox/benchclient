package dev.bench.client.util;

import dev.bench.client.modules.combat.AimAssist.AimPoint;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class RotationUtil {
    private RotationUtil() {}

    // stored rotations sent on next packet via mixin
    private static float serverYaw   = 0f;
    private static float serverPitch = 0f;
    private static boolean dirty     = false;

    public static void faceEntity(PlayerEntity player, LivingEntity target) {
        float[] yp = calcYawPitch(player.getEyePos(), target.getEyePos());
        setServerRotation(yp[0], yp[1]);
    }

    public static void smoothFace(PlayerEntity player, LivingEntity target,
                                   float speed, AimPoint ap) {
        Vec3d aimPos = switch (ap) {
            case HEAD -> target.getEyePos();
            case FEET -> target.getPos();
            case BODY -> target.getPos().add(0, target.getHeight() * 0.5, 0);
        };
        float[] desired = calcYawPitch(player.getEyePos(), aimPos);
        float   curYaw  = player.getYaw();
        float   curPit  = player.getPitch();

        float newYaw = lerpAngle(curYaw, desired[0], speed * 0.1f);
        float newPit = lerpAngle(curPit, desired[1], speed * 0.1f);

        setServerRotation(newYaw, newPit);
    }

    public static void setServerRotation(float yaw, float pitch) {
        serverYaw   = yaw;
        serverPitch = pitch;
        dirty       = true;
    }

    public static float getServerYaw()   { return serverYaw; }
    public static float getServerPitch() { return serverPitch; }

    public static boolean isDirty() { return dirty; }
    public static void    clean()   { dirty = false; }

    public static float[] calcYawPitch(Vec3d from, Vec3d to) {
        double dX   = to.x - from.x;
        double dY   = to.y - from.y;
        double dZ   = to.z - from.z;
        double dist = Math.sqrt(dX * dX + dZ * dZ);
        float  yaw  = (float)(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0);
        float  pit  = (float)(-Math.toDegrees(Math.atan2(dY, dist)));
        return new float[]{ wrapDegrees(yaw), Math.max(-90, Math.min(90, pit)) };
    }

    private static float lerpAngle(float
