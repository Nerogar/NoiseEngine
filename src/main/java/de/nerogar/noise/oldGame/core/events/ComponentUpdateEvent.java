package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.oldGame.core.components.SynchronizedComponent;

public class ComponentUpdateEvent implements Event {

	private SynchronizedComponent component;

	public ComponentUpdateEvent(SynchronizedComponent component) {
		this.component = component;
	}

	public SynchronizedComponent getComponent() {
		return component;
	}

}
