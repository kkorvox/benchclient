package dev.bench.client.modules.combat;

import dev.bench.client.events.EventHandler;
import dev.bench.client.events.game.TickEvent;
import dev.bench.client.modules.Module;
import net.minecraft.client.MinecraftClient;
import java.util.concurrent.ThreadLocalRandom;

public final class AutoClicker extends Module {

    private final IntSetting  minCPS  = intS ("Min CPS",   "Minimum clicks per second", 10, 1, 20);
    private final IntSetting  maxCPS  = intS ("Max CPS",   "Maximum clicks per second", 14, 1, 20);
    private final BoolSetting onlyHeld= bool ("Only Held", "Only when holding mouse",   true);
    private final EnumSetting<ClickButton> button = enumS("Button", "Which button",     ClickButton.LEFT);

    private long lastClick = 0;

    public enum ClickButton { LEFT, RIGHT, BOTH }

    public AutoClicker() {
        super("AutoClicker", "Automates mouse clicks at randomized CPS", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null) return;

        int  targetCPS = ThreadLocalRandom.current().nextInt(
                minCPS.getValue(), maxCPS.getValue() + 1);
        long interval  = 1000L / targetCPS;
        long now       = System.currentTimeMillis();
        if (now - lastClick < interval) return;
        if (onlyHeld.isEnabled() && !mc.options.attackKey.isPressed()) return;

        switch (button.getValue()) {
            case LEFT  -> dev.bench.client.mixin.MouseMixinHelper.simulatePress(0);
            case RIGHT -> dev.bench.client.mixin.MouseMixinHelper.simulatePress(1);
            case BOTH  -> {
                dev.bench.client.mixin.MouseMixinHelper.simulatePress(0);
                dev.bench.client.mixin.MouseMixinHelper.simulatePress(1);
            }
        }
        lastClick = now;
    }
}
