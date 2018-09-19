package de.nerogar.noise.game;

import de.nerogar.noise.game.core.events.EntityDespawnEvent;
import de.nerogar.noise.game.core.events.EntitySpawnEvent;

import java.lang.reflect.Modifier;
import java.util.*;

public class EntityList {

	private Map<Integer, Entity>                                    entities;
	private Map<Entity, Map<Class<? extends Component>, Component>> entityComponentMap;
	private Map<Class<? extends Component>, Set<Component>>         allComponentsMap;

	private CoreMap map;

	public EntityList(CoreMap map) {
		this.map = map;

		entities = new HashMap<>();
		entityComponentMap = new HashMap<>();
		allComponentsMap = new HashMap<>();
	}

	public void put(Entity entity, List<Component> components) {
		entities.put(entity.getID(), entity);
		entityComponentMap.put(entity, new HashMap<>());

		// add components to maps
		for (Component component : components) {
			addComponent(entity, component);
		}

		// trigger the spawn event
		map.getEventManager().trigger(new EntitySpawnEvent(entity));

		// initialize the systems with the new entity
		for (Component component : components) {
			component.initSystems();
		}

		// initialize components
		for (Component component : components) {
			component.init();
		}
	}

	public void remove(int id) {
		Entity entity = entities.remove(id);
		Map<Class<? extends Component>, Component> components = entityComponentMap.get(entity);
		for (Map.Entry<Class<? extends Component>, Component> entry : components.entrySet()) {
			allComponentsMap.get(entry.getKey()).remove(entry.getValue());
			entry.getValue().cleanup();
		}

		entityComponentMap.remove(entity);

		map.getEventManager().trigger(new EntityDespawnEvent(entity, components));
	}

	public boolean containsID(int id) {
		return entities.containsKey(id);
	}

	public Entity get(int id) {
		return entities.get(id);
	}

	public Collection<Entity> getEntities() {
		return entities.values();
	}

	public void addComponent(Entity entity, Component component) {

		Class<? extends Component> componentClass = component.getClass();
		while (!Modifier.isAbstract(componentClass.getModifiers())) {
			entityComponentMap.get(entity).put(componentClass, component);
			allComponentsMap.computeIfAbsent(componentClass, c -> new HashSet<>()).add(component);

			// cast is safe because Component is abstract
			componentClass = (Class<? extends Component>) componentClass.getSuperclass();
		}

		component.setEntity(entity);
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Entity entity, Class<T> componentClass) {
		Map<Class<? extends Component>, Component> entityComponents = entityComponentMap.get(entity);
		return entityComponents != null ? (T) entityComponents.get(componentClass) : null;
	}

	public <T extends Component> boolean hasComponent(Entity entity, Class<T> componentClass) {
		Map<Class<? extends Component>, Component> entityComponents = entityComponentMap.get(entity);
		return entityComponents != null && entityComponentMap.get(entity).containsKey(componentClass);
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> Set<T> getComponents(Class<T> componentClass) {
		return (Set<T>) allComponentsMap.computeIfAbsent(componentClass, c -> new HashSet<>());
	}

	public Collection<Component> getComponents(Entity entity) {
		return entityComponentMap.get(entity).values();
	}
}
