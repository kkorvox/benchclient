package dev.bench.client.events.game;

public final class MoveEvent {

    private double x, y, z;
    private boolean modified = false;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double  getX()        { return x; }
    public double  getY()        { return y; }
    public double  getZ()        { return z; }
    public boolean isModified()  { return modified; }

    public void setX(double x)   { this.x = x; modified = true; }
    public void setY(double y)   { this.y = y; modified = true; }
    public void setZ(double z)   { this.z = z; modified = true; }
}
