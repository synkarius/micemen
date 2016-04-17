package gridparts;

import java.util.ArrayList;
import java.util.List;

import control.Direction;
import entity.sim.Mouse.Team;
import model.CheeseGrid;

public class WholeGameRecording {
    /** recording of a single move */
    private static class Move {
        public int  ox;
        public int  oy;
        public int  dx;
        public int  dy;
        public Team team;
    }
    
    /**
     * recording of a whole turn, including board prior to choice, pole choice,
     * and all resulting moves
     */
    private static class Turn {
        public String     board;
        public Team       activeTeam;
        public Direction  chosenShiftDir;
        public int        chosenShiftX;
        public List<Move> moves = new ArrayList<>();
        
        public boolean isEmpty() {
            return moves.size() == 0 && board == null;
        }
    }
    
    private List<Turn> turns       = new ArrayList<>();
    private Turn       currentTurn = new Turn();
    private boolean    on;
    
    public void startRecording() {
        turns.add(currentTurn);
        on = true;
    }
    
    public void board(CheeseGrid grid) {
        if (on)
            currentTurn.board = grid.toString();
    }
    
    public void team(Team team) {
        if (on)
            currentTurn.activeTeam = team;
    }
    
    public void shift(Direction dir, int x) {
        if (on) {
            currentTurn.chosenShiftDir = dir;
            currentTurn.chosenShiftX = x;
        }
    }
    
    /**
     * last before pass
     */
    public void addMove(Team team, int ox, int oy, int dx, int dy) {
        if (on) {
            Move m = new Move();
            m.team = team;
            m.ox = ox;
            m.oy = oy;
            m.dx = dx;
            m.dy = dy;
            currentTurn.moves.add(m);
        }
    }
    
    public void pass() {
        if (on) {
            currentTurn = new Turn();
            turns.add(currentTurn);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder readable = new StringBuilder();
        
        if (on)
            for (Turn t : turns) {
                readable.append(t.board)
                        .append('\n')
                        .append(t.activeTeam.toString())
                        .append(" chooses ")
                        .append("" + t.chosenShiftX)
                        .append('-')
                        .append(t.chosenShiftDir.toString())
                        .append('\n')
                        .append("results in")
                        .append('\n');
                t.moves.stream()
                        .forEach(move -> readable.append(move.team.toString())
                                .append(" : (")
                                .append("" + move.ox)
                                .append(", ")
                                .append("" + move.oy)
                                .append(") -> (")
                                .append("" + move.dx)
                                .append(", ")
                                .append("" + move.dy)
                                .append(")")
                                .append('\n'));
                readable.append('\n');
            }
        
        return readable.toString();
    }
    
}
