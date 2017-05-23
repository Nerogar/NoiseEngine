package de.nerogar.noise.event;

public interface Event {

	public default Class<? extends EventConstraint> getSpecialConstraintClass() {
		return null;
	}

	public default ConstraintEventManager getNewEventManager() {
		return null;
	}

}
