package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.SetHand;

public class ComputerPlayerMid extends ComputerPlayer {
    
    private ComputerPlayer          opponent  = new ComputerPlayerBasic();
    private int                     lookAhead = 2;
    private Comparator<ColumnShift> comparator;
    // private Comparator<ColumnShift> opponentComparator;
    
    @Override
    public ComputerPlayer team(Team team) {
        super.team(team);
        comparator = team == Team.RED ? RED_SORT : BLUE_SORT;
        // opponentComparator = team == Team.BLUE ? RED_SORT : BLUE_SORT;
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
    
    @Override
    public IOrder getOrder() throws CheeseException {
        
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
        // List<SimulationNode> bottomLevelChildren = new ArrayList<>();
        // getEndResults(allFather, bottomLevelChildren);
        // SimulationNode best = null;
        // for (SimulationNode node : bottomLevelChildren) {
        // if (best == null || best.value() < node.value())
        // best = node;
        // }
        // return best.getTopLevel();
    }
    
    private static void getEndResults(SimulationNode node, List<SimulationNode> bottomLevelChildren) {
        // analyze the tree and then pick something out of the first level
        if (!node.children.isEmpty()) {
            // this has children-- get them and then get back here
            for (SimulationNode child : node.children)
                getEndResults(child, bottomLevelChildren);
        } else {
            bottomLevelChildren.add(node);
        }
    }
    
    public void branch(int rounds, CheeseGrid copygrid, SimulationNode parent) {
        List<ColumnShift> choices = getChoices(copygrid);
        if (choices.size() == 0)
            return;
        Collections.sort(choices, comparator);
        --rounds;
        
        for (ColumnShift choice : choices) {
            SimulationNode result;
            
            if (rounds > 0) {
                CheeseGrid nextBranchGrid = new CheeseGrid(copygrid);
                
                // no point in analyzing this one -- it's not the end result
                result = new SimulationNode(choice.x(), choice.dir(), -1);
                // SimulationNode.analyzeShift(choice, new CheeseGrid(copygrid),
                // team);
                
                nextBranchGrid.ctrl().orders().add(choice);
                nextBranchGrid.ctrl().executeAll();
                
                // opponent chooses a move
                opponent.grid(nextBranchGrid);
                // List<ColumnShift> opponentChoices =
                // getChoices(nextBranchGrid);
                // Collections.sort(opponentChoices, opponentComparator);
                IOrder opponentChoice = opponent.getOrder();
                if (opponentChoice == null)
                    continue;
                nextBranchGrid.ctrl().orders().add(opponentChoice);
                nextBranchGrid.ctrl().executeAll();
                
                branch(rounds, nextBranchGrid, result);
            } else {
                result = SimulationNode.analyzeShift(choice, new CheeseGrid(copygrid), team);
            }
            
            parent.linkToChild(result);
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
