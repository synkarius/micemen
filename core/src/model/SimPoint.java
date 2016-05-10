package model;

public class SimPoint {
    
    public SimPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int x;
    public int y;
    
    public SimPoint add(SimPoint other) {
        return new SimPoint(x + other.x, y + other.y);
    }
    
}
