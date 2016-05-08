package control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import model.Block;
import model.CheeseGrid;
import model.Mouse;
import model.Mouse.Team;

public class BlockIter<T extends Block> implements Iterator<T> {
    
    /** ---- STATE ---- */
    
    /** current */
    private int              x;
    private int              y;
    
    /** original */
    private final int        ox;
    private final int        oy;
    
    /** limit */
    private int              lx;
    private int              ly;
    
    /** traversed */
    private int              tx;
    private int              ty;
    
    /** for listLimit */
    private int              returned;
    
    /** ---- CRITERIA ---- */
    private final CheeseGrid grid;
    private final Direction  dir1;
    private final Direction  dir2;
    
    private final Class<T>   blocktype;
    
    private Team             team;
    
    /** controls how many get returned */
    private int              max;
    private Predicate<T>     filter;
    
    public BlockIter(CheeseGrid grid, Direction dir1, Direction dir2, int x, int y, Class<T> clazz) {
        this.grid = grid;
        this.dir1 = dir1;
        this.dir2 = dir2;
        this.x = x;
        this.y = y;
        this.ox = x;
        this.oy = y;
        this.blocktype = clazz;
    }
    
    /** optional param */
    public BlockIter<T> filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
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
    
    /** optional param -- side of the team, not mice of the team */
    public BlockIter<T> team(Team team) {
        this.team = team;
        return this;
    }
    
    /** optional param */
    public BlockIter<T> max(int max) {
        this.max = max;
        return this;
    }
    
    @Override
    public T next() {
        @SuppressWarnings("unchecked")
        T next = (T) grid.get(x, y);
        setNextBlock();
        returned++;
        return next;
    }
    
    /**
     * whatever block we have landed on -- does it fit the specified criteria?
     * also, have we passed the quota (limit())
     */
    @Override
    public boolean hasNext() {
        
        if (ox == x && oy == y) {
            /**
             * on the very first call to hasNext(), the iterator will check the
             * origin block -- the origin block will automatically pass region
             * and limit checks, but it also needs to pass non-regional criteria
             * -- thus if it doesn't, we start with the next block
             */
            if (!currentBlockPassesNonRegionalCriteria())
                setNextBlock();
        }
        
        /** respect list limit */
        if (max != 0 && returned >= max)
            return false;
        
        /** have run out of blocks to check */
        boolean edgeOfGrid = x < 0 || x > grid.wMax() || y < 0 || y > grid.hMax();
        if (edgeOfGrid)
            return false;
        
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
        if (limits)
            return false;
        
        return true;
    }
    
    private void setNextBlock() {
        while (true) {
            
            int cx = x;
            int cy = y;
            
            boolean d1IsXAxis = dir1 == Direction.LEFT || dir1 == Direction.RIGHT;
            boolean d1IsYAxis = dir1 == Direction.UP || dir1 == Direction.DOWN;
            boolean hitLimit1 = (lx != 0 && d1IsXAxis && tx + 1 > lx) || (ly != 0 && d1IsYAxis && ty + 1 > ly);
            
            switch (dir1) {
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
            
            if (dir2 != null) {
                boolean edgeOfGrid = cx < 0 || cx > grid.wMax() || cy < 0 || cy > grid.hMax();
                
                if (edgeOfGrid || hitLimit1) {
                    boolean d2IsXAxis = dir2 == Direction.LEFT || dir2 == Direction.RIGHT;
                    boolean d2IsYAxis = dir2 == Direction.UP || dir2 == Direction.DOWN;
                    boolean hitLimit2 = ((lx != 0) && d2IsXAxis && (tx + 1 > lx))
                            || (ly != 0 && d2IsYAxis && ty + 1 > ly);
                    
                    if (!hitLimit2) {
                        switch (dir2) {
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
            
            /**
             * if the new coords are outside of the allowed range or outside the
             * grid, stop trying
             */
            if (!hasNext())
                break;
            
            /** also stop on a good block */
            if (currentBlockPassesNonRegionalCriteria())
                break;
        }
    }
    
    private boolean currentBlockPassesNonRegionalCriteria() {
        @SuppressWarnings("unchecked")
        T result = (T) grid.get(x, y);
        
        /** team check */
        boolean wrongSide = false;
        if (team != null && blocktype == Mouse.class) {
            boolean red = team == Team.RED && result.isRedMouse();
            boolean blue = team == Team.BLUE && result.isBlueMouse();
            wrongSide = !(red || blue);
        }
        if (wrongSide)
            return false;
        
        /** type check */
        if (result.getClass() != blocktype)
            return false;
        
        if (filter != null && !filter.test(result))
            return false;
            
            return true;
    }
    
}
