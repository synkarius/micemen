package control;

import java.util.ArrayList;
import java.util.List;

import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public class SimulationNode {
    
    private SimulationNode parent;
    List<SimulationNode>   children;
    
    private int            x;
    private Direction      dir;
    private int            value;
    
    public SimulationNode(int x, Direction dir, int value) {
        this();
        this.x = x;
        this.dir = dir;
        this.value = value;
    }
    
    public SimulationNode() {
        this.children = new ArrayList<>();
    }
    
    public int averageEndValue() {
        if (children.isEmpty())
            return value;
        
        int sum = 0;
        for (SimulationNode child : children)
            sum += child.averageEndValue();
        return (int) (sum / children.size());
    }
    
    public void linkToChild(SimulationNode child) {
        child.parent = this;
        this.children.add(child);
    }
    
    public SimulationNode getTopLevel() {
        if (this.parent.parent == null)
            return this;
        else
            return this.parent.getTopLevel();
    }
    
    public int x() {
        return x;
    }
    
    public Direction dir() {
        return dir;
    }
    
    public int value() {
        return value;
    }
    
    public static SimulationNode analyzeShift(ColumnShift shift, CheeseGrid copygrid, Team team)
            throws CheeseException {
        
        // grid.nullifyParentLinks();
        copygrid.ctrl().orders().add(shift);
        copygrid.ctrl().executeAll();
        int postValue = ComputerPlayer.measureGridValue(copygrid, team);
        
        return new SimulationNode(shift.x(), shift.dir(), postValue);
    }
}
