package dev.bench.client.events.game;

import net.minecraft.client.util.math.MatrixStack;

public final class WorldRenderEvent {

    private final MatrixStack matrixStack;
    private final float       tickDelta;

    public WorldRenderEvent(MatrixStack matrixStack, float tickDelta) {
        this.matrixStack = matrixStack;
        this.tickDelta   = tickDelta;
    }

    public MatrixStack getMatrixStack() { return matrixStack; }
    public float       getTickDelta()   { return tickDelta; }
}
