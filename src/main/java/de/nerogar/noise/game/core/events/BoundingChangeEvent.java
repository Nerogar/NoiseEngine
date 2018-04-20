package de.nerogar.noise.game.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.game.core.components.BoundingComponent;

public class BoundingChangeEvent implements Event {

	private BoundingComponent boundingComponent;

	public BoundingChangeEvent(BoundingComponent boundingComponent) {
		this.boundingComponent = boundingComponent;
	}

	public BoundingComponent getBoundingComponent() {
		return boundingComponent;
	}

}
