package de.nerogar.noise.game.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.game.core.components.SynchronizedComponent;

public class ComponentUpdateEvent implements Event {

	private SynchronizedComponent component;

	public ComponentUpdateEvent(SynchronizedComponent component) {
		this.component = component;
	}

	public SynchronizedComponent getComponent() {
		return component;
	}

}
