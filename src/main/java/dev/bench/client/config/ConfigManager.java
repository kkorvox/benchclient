// src/main/java/dev/bench/client/config/ConfigManager.java
package dev.bench.client.config;

import com.google.gson.*;
import dev.bench.client.core.Client;
import dev.bench.client.hud.HudManager;
import dev.bench.client.modules.Module;
import dev.bench.client.modules.ModuleManager;
import dev.bench.client.modules.settings.Setting;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ConfigManager {

    private final ModuleManager modules;
    private final HudManager    hud;
    private final Path          dir;
    private       String        currentProfile = "default";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager(ModuleManager modules, HudManager hud) {
        this.modules = modules;
        this.hud     = hud;
        this.dir     = Path.of(
            net.fabricmc.loader.api.FabricLoader.getInstance()
               .getGameDir().toString(), "benchclient", "configs");
        try { Files.createDirectories(dir); }
        catch (IOException e) { Client.LOG.error("Config dir creation failed", e); }
    }

    public void save(String name) {
        JsonObject root = new JsonObject();
        JsonArray  mods = new JsonArray();
        for (Module m : modules.all()) {
            JsonObject mObj = new JsonObject();
            mObj.addProperty("name",    m.getName());
            mObj.addProperty("enabled", m.isEnabled());
            mObj.addProperty("keybind", m.getKeybind());
            JsonObject settings = new JsonObject();
            for (Setting<?> s : m.getSettings())
                settings.addProperty(s.getName(), s.toSerializedString());
            mObj.add("settings", settings);
            mods.add(mObj);
        }
        root.add("modules", mods);
        try {
            Files.writeString(dir.resolve(name + ".json"), GSON.toJson(root));
        } catch (IOException e) {
            Client.LOG.error("Config save failed: {}", name, e);
        }
    }

    public void load(String name) {
        Path file = dir.resolve(name + ".json");
        if (!Files.exists(file)) return;
        try {
            JsonObject root = JsonParser.parseString(
                Files.readString(file)).getAsJsonObject();
            JsonArray  mods = root.getAsJsonArray("modules");
            for (JsonElement el : mods) {
                JsonObject mObj = el.getAsJsonObject();
                modules.getByName(mObj.get("name").getAsString()).ifPresent(m -> {
                    m.setEnabled(mObj.get("enabled").getAsBoolean());
                    m.setKeybind (mObj.get("keybind").getAsInt());
                    JsonObject s = mObj.getAsJsonObject("settings");
                    if (s != null)
                        for (Setting<?> setting : m.getSettings()) {
                            JsonElement sv = s.get(setting.getName());
                            if (sv != null) setting.setFromString(sv.getAsString());
                        }
                });
            }
            currentProfile = name;
        } catch (Exception e) {
            Client.LOG.error("Config load failed: {}", name, e);
        }
    }

    public void loadDefault() { load("default"); }
    public void saveDefault() { save("default"); }
    public String getCurrentProfile() { return currentProfile; }

    public List<String> listProfiles() {
        try {
            return Files.list(dir)
                .filter(p -> p.toString().endsWith(".json"))
                .map(p -> p.getFileName().toString().replace(".json",""))
                .toList();
        } catch (IOException e) { return List.of(); }
    }
}
