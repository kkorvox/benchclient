package dev.bench.client.modules.settings;

public final class IntSetting extends Setting<Integer> {
    private final int min, max;

    public IntSetting(String n, String d, int def, int lo, int hi) {
        super(n, d, def);
        this.min = lo;
        this.max = hi;
    }

    @Override
    public void setValue(Integer v) {
        value = Math.max(min, Math.min(max, v));
    }

    @Override
    public void setFromString(String s) {
        try { setValue(Integer.parseInt(s.trim())); }
        catch (NumberFormatException ignored) {}
    }

    @Override
    public String toSerializedString() {
        return value.toString();
    }

    public int getMin() { return min; }
    public int getMax() { return max; }
}
