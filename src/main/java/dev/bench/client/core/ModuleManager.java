package dev.bench.client.core;

import dev.bench.client.events.EventBus;
import dev.bench.client.modules.Module;
import dev.bench.client.modules.combat.*;
import dev.bench.client.modules.movement.*;
import dev.bench.client.modules.visual.*;

import java.util.*;
import java.util.stream.Collectors;

public final class ModuleManager {

    private final EventBus            bus;
    private final Map<String, Module> byName = new LinkedHashMap<>();

    public ModuleManager(EventBus bus) { this.bus = bus; }

    public void registerAll() {
        // COMBAT
        register(new KillAura());
        register(new AimAssist());
        register(new AutoClicker());

        // MOVEMENT
        register(new Speed());
        register(new Fly());

        // VISUAL
        register(new ESP());
        register(new Fullbright());
    }

    private void register(Module m) {
        byName.put(m.getName().toLowerCase(), m);
    }

    public Optional<Module>   getByName(String name) {
        return Optional.ofNullable(byName.get(name.toLowerCase()));
    }

    public Collection<Module> all()   { return byName.values(); }
    public int                count() { return byName.size(); }

    public List<Module> byCategory(Module.Category cat) {
        return byName.values().stream()
                     .filter(m -> m.getCategory() == cat)
                     .collect(Collectors.toList());
    }
}
