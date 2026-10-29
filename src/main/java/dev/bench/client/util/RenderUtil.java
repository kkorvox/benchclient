package dev.bench.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class RenderUtil {
    private RenderUtil() {}

    public static int healthColor(float hp) {
        int r = (int)(255 * (1f - hp));
        int g = (int)(255 * hp);
        return 0xFF000000 | (r << 16) | (g << 8);
    }

    public static void drawBox(MatrixStack ms, Box box, int color, float lineWidth) {
        setupLineRender(lineWidth);
        BufferBuilder buf =
