package dev.bench.client.modules.settings;

public final class BoolSetting extends Setting<Boolean> {
    public BoolSetting(String n, String d, boolean def) { super(n, d, def); }

    @Override public void    setValue(Boolean v)     { value = v; }
    @Override public void    setFromString(String s) { value = Boolean.parseBoolean(s.trim()); }
    @Override public String  toSerializedString()    { return value.toString(); }

    public boolean isEnabled() { return value; }
    public void    toggle()    { value = !value; }
}
