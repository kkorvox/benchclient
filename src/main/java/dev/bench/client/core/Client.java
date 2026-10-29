package dev.bench.client.core;

import dev.bench.client.modules.ModuleManager;
import dev.bench.client.hud.HudManager;
import dev.bench.client.config.ConfigManager;
import dev.bench.client.events.EventBus;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Client implements ModInitializer {

    public static final String NAME    = "BenchClient";
    public static final String VERSION = "1.0.0";
    public static final Logger LOG     = LoggerFactory.getLogger(NAME);

    private static Client INSTANCE;

    private ModuleManager moduleManager;
    private HudManager    hudManager;
    private ConfigManager configManager;
    private EventBus      eventBus;

    public static Client get() { return INSTANCE; }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOG.info("{} {} initializing", NAME, VERSION);

        eventBus      = new EventBus();
        moduleManager = new ModuleManager(eventBus);
        hudManager    = new HudManager();
        configManager = new ConfigManager(moduleManager, hudManager);

        moduleManager.registerAll();
        hudManager.registerAll();
        configManager.loadDefault();

        // GUI keybind — RIGHT SHIFT
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(
            new net.minecraft.client.option.KeyBinding(
                "key.benchclient.gui",
                net.minecraft.client.util.InputUtil.Type.KEYSYM,
                org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT,
                "BenchClient"
            )
        );

        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
            .END_CLIENT_TICK.register(mc -> {
                if (mc.player == null) return;
                if (mc.currentScreen instanceof dev.bench.client.gui.VapeGUI) return;
                // keybind checked via ModuleManager.onKey via mixin
            });

        LOG.info("{} ready — {} modules", NAME, moduleManager.count());
    }

    public ModuleManager modules()  { return moduleManager; }
    public HudManager    hud()      { return hudManager; }
    public ConfigManager configs()  { return configManager; }
    public EventBus      events()   { return eventBus; }
}
