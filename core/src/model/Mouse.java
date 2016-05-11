package model;

import orders.MouseMove;

public class Mouse extends Block {
    
    public enum Team {
        RED, BLUE;
    }
    
    protected Team team;
    
    public Mouse(Team team, CheeseGrid grid) {
        super(grid);
        this.type = Type.MOUSE;
        this.team = team;
    }
    
    public Team team() {
        return team;
    }
    
    
}
