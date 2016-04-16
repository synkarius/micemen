package orders;

import model.CheeseGrid;

public interface IOrder {
	void execute(CheeseGrid grid);
	boolean finished();
	OrderType type();
}
