package entity.sim;

import java.util.Optional;

import entity.graphical.SlickSprite;
import entity.sim.Mouse.Team;
import model.CheeseGrid;
import model.SimPoint;

public abstract class Block {
    public enum Type {
        MOUSE, CHEESE, EMPTY;
    }
    
    private static int nextID = 0;
    
    protected int      gridID;
    
    public int gridID() {
        return gridID;
    }
    
    private static int getID() {
        if (nextID > 1000000)// 1m
            nextID = 0;
        return nextID++;
    }
    
    protected Type                  type;
    private int                     id;
    protected Optional<SlickSprite> graphic;
    
    public Block(CheeseGrid grid) {
        this.id = getID();
        this.gridID = grid.id();
        this.graphic = Optional.empty();
    }
    
    public SimPoint pos(CheeseGrid grid) {
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.width(); y++) {
                Block block = grid.get(x, y);
                if (block.id() == id)
                    return new SimPoint(x, y);
            }
        }
        return null;
    }
    
    public Type type() {
        return type;
    }
    
    public int id() {
        return id;
    }
    
    public Optional<SlickSprite> graphic() {
        return graphic;
    }
    
    public boolean isMouse() {
        return type == Type.MOUSE;
    }
    
    public boolean isRedMouse() {
        return isMouse() && ((Mouse) this).team() == Team.RED;
    }
    
    public boolean isBlueMouse() {
        return isMouse() && ((Mouse) this).team() == Team.BLUE;
    }
    
    public boolean isTeam(Team team) {
        return isMouse() && ((Mouse) this).team() == team;
    }
    
    public boolean isEmpty() {
        return this.type == Type.EMPTY;
    }
    
    public boolean isCheese() {
        return this.type == Type.CHEESE;
    }
    
    @Override
    public String toString() {
        return this.type.toString().toLowerCase();
    }
    
    @Override
    public boolean equals(Object o) {
        Block other = (Block) o;
        return id == other.id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
}
