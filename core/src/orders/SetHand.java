package orders;

import model.CheeseException;
import model.CheeseGrid;

public class SetHand implements IOrder {
    
    @Override
    public void execute(CheeseGrid grid) throws CheeseException {
        //TODO: this entire class
    }
    
    @Override
    public boolean finished() {
        return true;
    }
    
    @Override
    public OrderType type() {
        return OrderType.SET_HAND;
    }
    
}
