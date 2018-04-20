package de.nerogar.noise.game.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.game.Component;
import de.nerogar.noise.game.Entity;

import java.util.Map;

public class EntityDespawnEvent implements Event {

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
