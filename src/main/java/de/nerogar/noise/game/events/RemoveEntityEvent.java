package de.nerogar.noise.game.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.game.IComponent;

public class RemoveEntityEvent implements IEvent {

	public final int          entityId;
	public final IComponent[] components;

	public RemoveEntityEvent(int entityId, IComponent[] components) {
		this.entityId = entityId;
		this.components = components;
	}

	public <T extends IComponent> T getComponent(Class<T> componentClass) {
		for (int i = 0; i < components.length; i++) {
			if (components[i].getClass().equals(componentClass)) {
				return (T) components[i];
			}
		}
		return null;
	}

}
