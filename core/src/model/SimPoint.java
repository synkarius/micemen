package model;

public class SimPoint {
    
    public SimPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    private int x;
    private int y;
    
    public int x() {
        return x;
    }
    
    public void x(int x) {
        this.x = x;
    }
    
    public int y() {
        return y;
    }
    
    public void y(int y) {
        this.y = y;
    }
    
    public SimPoint add(SimPoint other) {
        return new SimPoint(x + other.x, y + other.y);
    }
    
}
