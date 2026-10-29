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
        Vec3d cam  = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        Vec3d feet = to.getPos();
        setupLineRender(1.0f);
        BufferBuilder buf = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f m = ms.peek().getPositionMatrix();
        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >>  8 & 0xFF) / 255f;
        float b = (color       & 0xFF) / 255f;
        buf.vertex(m, 0f, 0f, 0f).color(r, g, b, a);
        buf.vertex(m,
            (float)(feet.x - cam.x),
            (float)(feet.y - cam.y),
            (float)(feet.z - cam.z)).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buf.end());
        teardownLineRender();
    }

    public static void drawHealthBar(MatrixStack ms, LivingEntity e, float hp) {}
    public static void drawNameTag(MatrixStack ms, LivingEntity e) {}

    private static void setupLineRender(float width) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(width);
    }

    private static void teardownLineRender() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
    }

    private static void drawBoxLines(Matrix4f m, BufferBuilder buf, Box box, int color) {
        float a=(color>>24&0xFF)/255f, r=(color>>16&0xFF)/255f,
              g=(color>>8 &0xFF)/255f, b=(color    &0xFF)/255f;
        float x0=(float)box.minX, y0=(float)box.minY, z0=(float)box.minZ;
        float x1=(float)box.maxX, y1=(float)box.maxY, z1=(float)box.maxZ;
        line(buf,m,x0,y0,z0,x1,y0,z0,r,g,b,a); line(buf,m,x1,y0,z0,x1,y0,z1,r,g,b,a);
        line(buf,m,x1,y0,z1,x0,y0,z1,r,g,b,a); line(buf,m,x0,y0,z1,x0,y0,z0,r,g,b,a);
        line(buf,m,x0,y1,z0,x1,y1,z0,r,g,b,a); line(buf,m,x1,y1,z0,x1,y1,z1,r,g,b,a);
        line(buf,m,x1,y1,z1,x0,y1,z1,r,g,b,a); line(buf,m,x0,y1,z1,x0,y1,z0,r,g,b,a);
        line(buf,m,x0,y0,z0,x0,y1,z0,r,g,b,a); line(buf,m,x1,y0,z0,x1,y1,z0,r,g,b,a);
        line(buf,m,x1,y0,z1,x1,y1,z1,r,g,b,a); line(buf,m,x0,y0,z1,x0,y1,z1,r,g,b,a);
    }

    private static void drawCornerLines(Matrix4f m, BufferBuilder buf,
                                         Box box, int color,
                                         float cx, float cy, float cz) {
        float a=(color>>24&0xFF)/255f, r=(color>>16&0xFF)/255f,
              g=(color>>8 &0xFF)/255f, b=(color    &0xFF)/255f;
        float x0=(float)box.minX, y0=(float)box.minY, z0=(float)box.minZ;
        float x1=(float)box.maxX, y1=(float)box.maxY, z1=(float)box.maxZ;
        for (int[] c : new int[][]{{0,0,0},{1,0,0},{0,1,0},{1,1,0},{0,0,1},{1,0,1},{0,1,1},{1,1,1}}) {
            float px=c[0]==0?x0:x1, py=c[1]==0?y0:y1, pz=c[2]==0?z0:z1;
            float dx=c[0]==0?cx:-cx, dy=c[1]==0?cy:-cy, dz=c[2]==0?cz:-cz;
            line(buf,m,px,py,pz,px+dx,py,pz,r,g,b,a);
            line(buf,m,px,py,pz,px,py+dy,pz,r,g,b,a);
            line(buf,m,px,py,pz,px,py,pz+dz,r,g,b,a);
        }
    }

    private static void line(BufferBuilder b, Matrix4f m,
                              float x0,float y0,float z0,
                              float x1,float y1,float z1,
                              float r,float g,float bl,float a) {
        b.vertex(m,x0,y0,z0).color(r,g,bl,a);
        b.vertex(m,x1,y1,z1).color(r,g,bl,a);
    }
}
