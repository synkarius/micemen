package control;

import model.CheeseException;
import orders.IOrder;

public interface IController {
    IOrder getOrder() throws CheeseException;
}
