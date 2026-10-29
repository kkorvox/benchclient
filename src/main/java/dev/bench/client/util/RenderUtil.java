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
        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        drawBoxLines(ms.peek().getPositionMatrix(), buf, box, color);
        BufferRenderer.drawWithGlobalProgram(buf.end());
        teardownLineRender();
    }

    public static void drawOutline(MatrixStack ms, Box box, int color, float lineWidth) {
        drawBox(ms, box, color, lineWidth);
    }

    public static void drawCorners(MatrixStack ms, Box box, int color, float lineWidth) {
        setupLineRender(lineWidth);
        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        float cx = (float)(box.maxX - box.minX) * 0.25f;
        float cy = (float)(box.maxY - box.minY) * 0.25f;
        float cz = (float)(box.maxZ - box.minZ) * 0.25f;
        drawCornerLines(ms.peek().getPositionMatrix(), buf, box, color, cx, cy, cz);
        BufferRenderer.drawWithGlobalProgram(buf.end());
        teardownLineRender();
    }

    public static void drawTracer(MatrixStack ms, PlayerEntity from,
                                   LivingEntity to, int color) {
