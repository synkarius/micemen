package control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Block;
import model.CheeseGrid;
import model.Mouse;
import model.Block.Type;
import model.Mouse.Team;

public class BlockIter<T extends Block> implements Iterator<T> {
    
    private int             x;
    private int             y;
    
    private int             ox;       // original
    private int             oy;
    
    private int             lx;       // limit
    private int             ly;
    private int             tx;       // traversed
    private int             ty;
    
    private CheeseGrid      grid;
    private List<Direction> dirs;
    
    private Class<T>        blocktype;
    
    private Team            team;
    private int             halfway;
    
    /** controls how many get returned */
    private int             listLimit;
    
    public BlockIter(CheeseGrid grid, List<Direction> dirs, int x, int y, Class<T> clazz) {
        this.grid = grid;
        this.dirs = dirs;
        this.x = x;
        this.y = y;
        this.ox = x;
        this.oy = y;
        this.halfway = grid.width() / 2;
        this.blocktype = clazz;
    }
    
    /** optional param */
    public BlockIter<T> xLimit(int lx) {
        this.lx = lx;
        return this;
    }
    
    /** optional param */
    public BlockIter<T> yLimit(int ly) {
        this.ly = ly;
        return this;
    }
    
    /** optional param */
    // public BlockIter<T> type(Type type) {
    // this.blocktype = type;
    // return this;
    // }
    
    /** optional param -- side of the team, not mice of the team */
    public BlockIter<T> team(Team team) {
        this.team = team;
        return this;
    }
    
    /** optional param */
    public BlockIter<T> listLimit(int listLimit) {
        this.listLimit = listLimit;
        return this;
    }
    
    private boolean notHitListLimit(List<T> list) {
        return listLimit == 0 || list.size() < listLimit;
    }
    
    //TODO: move this logic into next() or currentBlock()
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        while (this.hasNext() && notHitListLimit(result) || false) {
            T block = this.next();
            if (block.getClass() == blocktype)
                result.add(block);
        }
        return result;
    }
    
    @Override
    public boolean hasNext() {
        return currentBlock() != null;
    }
    
    // TODO: cache currentblock??
    
    @Override
    public T next() {
        T next = currentBlock();
        setNextBlock();
        return next;
    }
    
    @SuppressWarnings("unchecked")
    private T currentBlock() {
        /**
         * edge of grid check -- you will never be gathering empty blocks or
         * mice from the very first or last columns
         */
        boolean edgeOfGrid = x < 0 || x > grid.wMax() || y < 0 || y > grid.hMax();
        
        /** limits check */
        boolean limits = false;
        boolean xLimitExists = lx != 0;
        boolean exceedXLimit = tx + 1 > lx;
        boolean yLimitExists = ly != 0;
        boolean exceedYLimit = ty + 1 > ly;
        if (xLimitExists && yLimitExists) {
            if (tx == lx && ty == ly) {
                limits = false;
            } else {
                limits = exceedXLimit && exceedYLimit;
            }
        } else if (xLimitExists) {
            if (tx == lx) {
                limits = false;
            } else {
                limits = exceedXLimit;
            }
        } else if (yLimitExists) {
            if (ty == ly) {
                limits = false;
            } else {
                limits = exceedYLimit;
            }
        }
        
        T result = (T) grid.get(x, y);
        
        /** team check */
        boolean wrongSide = false;
        if (team != null && blocktype == Mouse.class) {
            boolean red = team == Team.RED && result.isRedMouse();
            boolean blue = team == Team.BLUE && result.isBlueMouse();
            wrongSide = !(red || blue);
        }
        
        if (edgeOfGrid || limits || wrongSide) {
            return null;
        }
        
        return result;
    }
    
    private void setNextBlock() {
        int cx = x;
        int cy = y;
        
        Direction d1 = dirs.get(0);
        Direction d2 = null;
        if (dirs.size() > 1) {
            d2 = dirs.get(1);
        }
        
        boolean d1IsXAxis = d1 == Direction.LEFT || d1 == Direction.RIGHT;
        boolean d1IsYAxis = d1 == Direction.UP || d1 == Direction.DOWN;
        boolean hitLimit1 = (lx != 0 && d1IsXAxis && tx + 1 > lx) || (ly != 0 && d1IsYAxis && ty + 1 > ly);
        
        switch (d1) {
            case UP:
                cy -= 1;
                ty += 1;
                break;
            case DOWN:
                cy += 1;
                ty += 1;
                break;
            case LEFT:
                cx -= 1;
                tx += 1;
                break;
            case RIGHT:
                cx += 1;
                tx += 1;
                break;
            default:
                cy += 0;
        }
        
        if (d2 != null) {
            boolean edgeOfGrid = cx < 0 || cx > grid.wMax() || cy < 0 || cy > grid.hMax();
            
            if (edgeOfGrid || hitLimit1) {
                boolean d2IsXAxis = d2 == Direction.LEFT || d2 == Direction.RIGHT;
                boolean d2IsYAxis = d2 == Direction.UP || d2 == Direction.DOWN;
                boolean hitLimit2 = ((lx != 0) && d2IsXAxis && (tx + 1 > lx)) || (ly != 0 && d2IsYAxis && ty + 1 > ly);
                
                if (!hitLimit2) {
                    switch (d2) {
                        case UP:
                            cx = ox;
                            tx = 0;
                            cy -= 1;
                            ty += 1;
                            break;
                        case DOWN:
                            cx = ox;
                            tx = 0;
                            cy += 1;
                            ty += 1;
                            break;
                        case LEFT:
                            cy = oy;
                            ty = 0;
                            cx -= 1;
                            tx += 1;
                            break;
                        case RIGHT:
                            cy = oy;
                            ty = 0;
                            cx += 1;
                            tx += 1;
                            break;
                        default:
                            cy += 0;
                    }
                }
                
            }
        }
        
        x = cx;
        y = cy;
    }
    
}
