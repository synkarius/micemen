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
    
    public MouseMove getMoves(CheeseGrid grid, boolean fallsOnly) throws CheeseException {
        SimPoint origin = grid.get(this);
        int x = origin.x;
        int y = origin.y;
        
        MouseMove result = new MouseMove(this, x, y);
        while (true) {
            int ox = x;
            int oy = y;
            
            if (mustFall(grid, x, y)) {
                y += 1;
                result.add(0, 1);
            } else if (canMove(grid, x, y, team) && !fallsOnly) {
                int dir = walkingDir(team);
                x += dir;
                result.add(dir, 0);
            } else {
                break;
            }
            
            if (!grid.contains(x, y))
                throw new CheeseException("Bad move detected.");
            
            grid.switcH(ox, oy, x, y);
        }
        
        return result;
    }
    
    private static int walkingDir(Team team) {
        return team == Team.RED ? 1 : -1;
    }
    
    private static boolean mustFall(CheeseGrid grid, int x, int y) {
        if (y + 1 > grid.hMax())
            return false;
        return grid.get(x, y + 1).isEmpty();
    }
    
    private static boolean canMove(CheeseGrid grid, int x, int y, Team team) {
        int dir = walkingDir(team);
        int newX = x + dir;
        if (newX < 0 || newX > grid.wMax())
            return false;
        return grid.get(newX, y).isEmpty();
    }
}
