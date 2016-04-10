package orders;

import java.util.ArrayList;
import java.util.List;

import entity.sim.Mouse;
import model.SimPoint;

public class MouseMove implements IOrder {

	private final Mouse simMouse;
	private final SimPoint origin;
	private final List<SimPoint> moves;

	public MouseMove(Mouse simMouse, SimPoint origin) {
		this.simMouse = simMouse;
		this.origin = origin;
		this.moves = new ArrayList<>();
	}

	public void add(int xDif, int yDif) {
		moves.add(new SimPoint(xDif, yDif));
	}

	public SimPoint consolidate() {
		return moves.stream().reduce(new SimPoint(0, 0), SimPoint::add);
	}
}
