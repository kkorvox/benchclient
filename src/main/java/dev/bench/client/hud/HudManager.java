// src/main/java/dev/bench/client/hud/HudManager.java
package dev.bench.client.hud;

import dev.bench.client.hud.components.ArrayListHud;
import dev.bench.client.hud.components.KeystrokesHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class HudManager {

    private final List<HudComponent> components = new ArrayList<>();

    public void registerAll() {
        components.add(new ArrayListHud());
        components.add(new KeystrokesHud());
    }

    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        for (HudComponent c : components) {
            if (c.isEnabled()) c.render(ctx, tickDelta);
        }
    }

    public Collection<HudComponent> all()  { return components; }
    public int                      count(){ return components.size(); }
}
