package de.nerogar.noise.event;

import de.nerogar.noise.util.Bounding;

public class EventListenerPositionConstraint implements EventListenerConstraint<EventPositionConstraint> {

	private Bounding bounding;

	public EventListenerPositionConstraint(Bounding bounding) {
		this.bounding = bounding;
	}

	public Bounding getBounding(){
		return bounding;
	}

	@Override
	public boolean isValidGeneric(EventPositionConstraint event) {
		return bounding.overlapsBounding(event.getBounding());
	}

	@Override
	public Class<EventPositionConstraint> getEventConstraintClass() {
		return EventPositionConstraint.class;
	}

}
