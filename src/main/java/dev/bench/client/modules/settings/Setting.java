package dev.bench.client.modules.settings;

public abstract class Setting<T> {
    protected final String name;
    protected final String description;
    protected       T      value;

    protected Setting(String name, String description, T defaultValue) {
        this.name        = name;
        this.description = description;
        this.value       = defaultValue;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public T      getValue()       { return value; }

    public abstract void   setValue(T v);
    public abstract void   setFromString(String s);
    public abstract String toSerializedString();
}
