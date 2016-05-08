package orders;

import model.CheeseException;
import model.CheeseGrid;

public class Progress implements IOrder {
    
    private int    ready;
    /** total=0 indicates reset the menu state */
    private double total;
    
    /** draws a progress bar on the screen */
    public Progress(int ready, double total) {
        this.ready = ready;
        this.total = total;
    }
    
    @Override
    public void execute(CheeseGrid grid) throws CheeseException {
        grid.state().thinkingReady = ready;
        grid.state().thinkingTotal = total;
    }
    
    @Override
    public boolean finished() {
        return true;
    }
    
    @Override
    public OrderType type() {
        return OrderType.PROGRESS;
    }
    
}
