package dev.bench.client.modules.settings;

public final class FloatSetting extends Setting<Float> {
    private final float min, max;

    public FloatSetting(String n, String d, float def, float lo, float hi) {
        super(n, d, def);
        this.min = lo;
        this.max = hi;
    }

    @Override
    public void setValue(Float v) {
        value = Math.max(min, Math.min(max, v));
    }

    @Override
    public void setFromString(String s) {
        try { setValue(Float.parseFloat(s.trim())); }
        catch (NumberFormatException ignored) {}
    }

    @Override
    public String toSerializedString() {
        return value.toString();
    }

    public float getMin() { return min; }
    public float getMax() { return max; }
}
