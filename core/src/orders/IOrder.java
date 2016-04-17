package orders;

import model.CheeseException;
import model.CheeseGrid;

public interface IOrder {
    void execute(CheeseGrid grid) throws CheeseException;
    
    boolean finished();
    
    OrderType type();
}
