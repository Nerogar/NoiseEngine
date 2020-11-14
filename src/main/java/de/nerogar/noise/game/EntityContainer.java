package de.nerogar.noise.game;

import de.nerogar.noiseInterface.game.IComponent;
import de.nerogar.noiseInterface.game.IEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityContainer {

	private Map<Integer, IComponent[]> entities;
	private int                        maxId = 1;

	public EntityContainer() {
		this.entities = new HashMap<>();
	}

	public int addEntity(IComponent[] components) {
		int entityId = maxId++;
		for (int i = 0; i < components.length; i++) {
			components[i].setEntityId(entityId);
		}
		entities.put(entityId, components);
		return entityId;
	}

	public IComponent[] removeEntity(int entityId) {
		return entities.remove(entityId);
	}

	public IEntity getEntity(int entityId) {
		return new Entity(entityId, this);
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T getEntityComponent(int entityId, Class<T> componentClass) {
		IComponent[] allComponents = entities.get(entityId);
		for (IComponent component : allComponents) {
			if (component.getClass() == componentClass) {
				return (T) component;
			}
		}
		return null;
	}

	/**
	 * Adds all components of an entity matching the specified component class to the list including subclasses.
	 *
	 * @param entityId
	 * @param componentClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends IComponent> void getEntityComponents(int entityId, Class<T> componentClass, List<? super T> components) {
		IComponent[] allComponents = entities.get(entityId);

		for (IComponent component : allComponents) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				components.add((T) component);
			}
		}
	}

	public IComponent[] getComponents(int entityId) {
		return entities.get(entityId);
	}

	public <T extends IComponent> void getComponents(Class<T> componentClass, List<T> components) {
		for (IComponent[] allComponents : entities.values()) {
			for (IComponent component : allComponents) {
				if (component.getClass() == componentClass) {
					components.add((T) component);
				}
			}
		}
	}

}
