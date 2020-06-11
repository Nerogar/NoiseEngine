package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.oldGame.core.components.BoundingComponent;

public class BoundingChangeEvent implements Event {

	private BoundingComponent boundingComponent;

	public BoundingChangeEvent(BoundingComponent boundingComponent) {
		this.boundingComponent = boundingComponent;
	}

	public BoundingComponent getBoundingComponent() {
		return boundingComponent;
	}

}
