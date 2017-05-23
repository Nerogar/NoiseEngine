package de.nerogar.noise.event;

public interface EventConstraint<M extends ConstraintEventManager> {

	default M getNewConstraintEventManager() {
		return null;
	}

}
