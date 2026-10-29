package dev.bench.client.modules.settings;

import java.util.Arrays;

public final class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final Class<E> type;

    @SuppressWarnings("unchecked")
    public EnumSetting(String n, String d, E def) {
        super(n, d, def);
        this.type = (Class<E>) def.getClass();
    }

    @Override public void setValue(E v) { value = v; }

    @Override
    public void setFromString(String s) {
        Arrays.stream(type.getEnumConstants())
              .filter(c -> c.name().equalsIgnoreCase(s.trim()))
              .findFirst()
              .ifPresent(c -> value = c);
    }

    @Override public String toSerializedString() { return value.name(); }

    public E[] getConstants() { return type.getEnumConstants(); }

    public void cycle() {
        E[] consts = getConstants();
        value = consts[(value.ordinal() + 1) % consts.length];
    }
}
