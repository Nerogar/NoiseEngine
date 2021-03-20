package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noise.oldGame.core.components.SynchronizedComponent;

public class ComponentUpdateEvent implements IEvent {

	private SynchronizedComponent component;

	public ComponentUpdateEvent(SynchronizedComponent component) {
		this.component = component;
	}

	public SynchronizedComponent getComponent() {
		return component;
	}

}
