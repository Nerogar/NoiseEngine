package de.nerogar.noise.event;

import de.nerogar.noise.util.Bounding;

public interface EventPositionConstraint<E extends Event & EventPositionConstraint> extends EventConstraint<PositionEventManager> {

	@Override
	public default PositionEventManager getNewConstraintEventManager() {
		return new PositionEventManager<E>();
	}

	public Bounding getBounding();

}
