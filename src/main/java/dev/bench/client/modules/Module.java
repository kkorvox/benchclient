package dev.bench.client.modules;

import dev.bench.client.core.Client;
import dev.bench.client.modules.settings.*;
import net.minecraft.client.util.InputUtil;
import java.util.*;

public abstract class Module {

    public enum Category { COMBAT, MOVEMENT, VISUAL, UTILITY }

    private final String   name;
    private final String   description;
    private final Category category;
    private boolean        enabled = false;
    private int            keybind = InputUtil.UNKNOWN_KEY.getCode();

    private final List<Setting<?>> settings = new ArrayList<>();

    protected Module(String name, String description, Category category) {
        this.name        = name;
        this.description = description;
        this.category    = category;
    }

    public final void toggle() {
        enabled = !enabled;
        if (enabled) { Client.get().events().subscribe(this); onEnable(); }
        else         { Client.get().events().unsubscribe(this); onDisable(); }
    }

    public final void setEnabled(boolean state) { if (state != enabled) toggle(); }

    protected void onEnable()  {}
    protected void onDisable() {}

    protected <T extends Setting<?>> T register(T s) { settings.add(s); return s; }

    protected BoolSetting  bool  (String n, String d, boolean def)                    { return register(new BoolSetting(n, d, def)); }
    protected IntSetting   intS  (String n, String d, int def, int lo, int hi)        { return register(new IntSetting(n, d, def, lo, hi)); }
    protected FloatSetting floatS(String n, String d, float def, float lo, float hi)  { return register(new FloatSetting(n, d, def, lo, hi)); }
    protected <E extends Enum<E>> EnumSetting<E> enumS(String n, String d, E def)     { return register(new EnumSetting<>(n, d, def)); }

    public String           getName()        { return name; }
    public String           getDescription() { return description; }
    public Category         getCategory()    { return category; }
    public boolean          isEnabled()      { return enabled; }
    public int              getKeybind()     { return keybind; }
    public void             setKeybind(int k){ keybind = k; }
    public List<Setting<?>> getSettings()    { return Collections.unmodifiableList(settings); }
}
