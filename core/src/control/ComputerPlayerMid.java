package control;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Block;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.SetHand;

public class ComputerPlayerMid extends ComputerPlayerBasic {
    
    private ComputerPlayer          opponent  = new ComputerPlayerBasic();
    private int                     lookAhead = 2;
    private Comparator<ColumnShift> comparator;
    
    @Override
    public ComputerPlayer team(Team team) {
        super.team(team);
        comparator = team == Team.RED ? RED_SORT : BLUE_SORT;
        return this;
    }
    
    public ComputerPlayerMid opponent(ComputerPlayer opponent) {
        this.opponent = opponent.copy();
        if (this.opponent instanceof ComputerPlayerMid)
            throw new CheeseException("Decision process for Mid vs Mid create infinite recursion.");
        return this;
    }
    
    public ComputerPlayerMid lookAhead(int lookAhead) {
        this.lookAhead = lookAhead;
        return this;
    }
    
    private int gap() {
        int min = grid.wMax();
        int max = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                boolean countMouse = (block.isBlueMouse() && team == Team.BLUE)
                        || (block.isRedMouse() && team == Team.RED);
                if (countMouse) {
                    if (x > max)
                        max = x;
                    if (x < min)
                        x = min;
                }
            }
        }
        return Math.abs(max - min);
    }
    
    @Override
    public IOrder getOrder() throws CheeseException {
        if (grid.ctrl().score(team) > 0)
            return super.getOrder();
        
        // save state for multiple lookahead:
        CheeseGrid copygrid = new CheeseGrid(grid);
        // set opponent's grid for this getOrder()
        opponent.grid(copygrid);
        opponent.isCPUvsCPUOpponent = true;
        
        //
        SimulationNode allFather = new SimulationNode();
        branch(lookAhead, copygrid, allFather);
        
        SimulationNode best = bestInitialChoice(allFather);
        
        Combo combo = new Combo();
        ;
        combo.add(new SetHand(grid, best.x()));
        combo.add(new ColumnShift(best.x(), best.dir()));
        
        grid.activePole(best.x());
        
        return combo;
    }
    
    private static SimulationNode bestInitialChoice(SimulationNode allFather) {
        SimulationNode best = null;
        for (SimulationNode child : allFather.children) {
            if (best == null || best.averageEndValue() < child.averageEndValue())
                best = child;
        }
        return best;
    }
    
    public void branch(int rounds, CheeseGrid copygrid, SimulationNode parent) {
        List<ColumnShift> choices = getChoices(copygrid);
        if (choices.size() == 0)
            return;
        Collections.sort(choices, comparator);
        --rounds;
        
        for (ColumnShift choice : choices) {
            SimulationNode child;
            
            if (rounds > 0) {
                CheeseGrid nextBranchGrid = new CheeseGrid(copygrid);
                
                // no point in analyzing this one -- it's not the end result
                // --------------
                // if this is the first level or recursion, this child is
                // what will be used to create the chosen ColumnShift
                // -- hence, the x & dir values do matter
                child = new SimulationNode(choice.x(), choice.dir(), -1);
                
                nextBranchGrid.ctrl().orders().add(choice);
                nextBranchGrid.ctrl().executeAll();
                
                // opponent chooses a move
                opponent.grid(nextBranchGrid);
                IOrder opponentChoice = opponent.getOrder();
                if (opponentChoice == null)
                    continue;
                nextBranchGrid.ctrl().orders().add(opponentChoice);
                nextBranchGrid.ctrl().executeAll();
                
                branch(rounds, nextBranchGrid, child);
            } else {
                child = SimulationNode.analyzeShift(choice, new CheeseGrid(copygrid), team);
            }
            
            parent.linkToChild(child);
        }
    }
    
    @Override
    public ComputerPlayer copy() {
        ComputerPlayerMid copy = new ComputerPlayerMid();
        copy.team = this.team;
        copy.lookAhead = this.lookAhead;
        return copy;
    }
    
}