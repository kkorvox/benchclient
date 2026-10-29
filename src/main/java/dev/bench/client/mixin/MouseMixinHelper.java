package dev.bench.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public final class MouseMixinHelper {
    private MouseMixinHelper() {}

    public static void simulatePress(int button) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null) return;
        long handle = mc.getWindow().getHandle();
        GLFW.glfwSetCursorPos(handle,
            mc.getWindow().getWidth()  / 2.0,
            mc.getWindow().getHeight() / 2.0);
        // inject click via mouse callback
        net.minecraft.client.Mouse mouse = mc.mouse;
        mouse.onMouseButton(handle, button, GLFW.GLFW_PRESS, 0);
    }
}
