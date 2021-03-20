package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noise.oldGame.core.components.BoundingComponent;

public class BoundingChangeEvent implements IEvent {

	private BoundingComponent boundingComponent;

	public BoundingChangeEvent(BoundingComponent boundingComponent) {
		this.boundingComponent = boundingComponent;
	}

	public BoundingComponent getBoundingComponent() {
		return boundingComponent;
	}

}
