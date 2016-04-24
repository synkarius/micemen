package orders;

import java.util.ArrayList;
import java.util.List;

import model.CheeseException;
import model.CheeseGrid;

/** FIFO */
public class Combo implements IOrder {
    
    private List<IOrder> orders = new ArrayList<>();
    
    /**
     * FIFO
     */
    @Override
    public void execute(CheeseGrid grid) throws CheeseException {
        while (orders.size() > 0) {
            IOrder order = orders.get(0);
            grid.ctrl().orders().add(order);
            orders.remove(order);
        }
    }
    
    public Combo add(IOrder order) {
        orders.add(order);
        return this;
    }
    
    @Override
    public boolean finished() {
        return orders.size() == 0;
    }
    
    @Override
    public OrderType type() {
        return OrderType.COMBO;
    }
    
}
