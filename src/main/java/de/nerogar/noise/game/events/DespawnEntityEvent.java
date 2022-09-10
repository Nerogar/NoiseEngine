package de.nerogar.noise.game.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.game.IComponent;

public record DespawnEntityEvent(long entity, Iterable<IComponent> components) implements IEvent {

	public <T extends IComponent> T getComponent(Class<T> componentClass) {
		for (IComponent component : components) {
			if (component.getClass().equals(componentClass)) {
				return (T) component;
			}
		}
		return null;
	}

}
