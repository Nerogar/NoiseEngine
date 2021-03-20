package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noise.oldGame.Component;
import de.nerogar.noise.oldGame.Entity;

import java.util.Map;

public class EntityDespawnEvent implements IEvent {

	private Entity                                     entity;
	private Map<Class<? extends Component>, Component> components;

	public EntityDespawnEvent(Entity entity, Map<Class<? extends Component>, Component> components) {
		this.entity = entity;
		this.components = components;
	}

	public Entity getEntity() {
		return entity;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> componentClass) {
		return (T) components.get(componentClass);
	}

}
