package de.nerogar.noise.game;

import de.nerogar.noiseInterface.game.IComponent;

import java.util.*;

/**
 * Container for entities. An entity is a long in the format {@code EEEE-EEEE-TTTT-UUUU} where:
 * - E is the entityId
 * - T is the typeId
 * - U is currently unused
 */
public class EntityContainer {

	/**Current max id of existing entities*/
	private int maxEntityId = 0;

	private HashSet<Long>                                     entities;
	private Map<Long, List<IComponent>>                       componentsByEntity;
	private Map<Class<? extends IComponent>, Set<IComponent>> componentsByComponentType;

	public EntityContainer() {
		this.entities = new HashSet<>();
		this.componentsByEntity = new HashMap<>();
		this.componentsByComponentType = new HashMap<>();
	}

	public static long getEntity(int entityId, short typeId) {
		return ((long) entityId << 32) | ((long) typeId << 16);
	}

	public static int getEntityId(long entity) {
		return (int) (entity >> 32);
	}

	public static short getTypeId(long entity) {
		return (short) (entity >> 16);
	}

	public void initEntity(long entity, IComponent[] components) {
		maxEntityId = Math.max(maxEntityId, getEntityId(entity));

		for (int i = 0; i < components.length; i++) {
			components[i].setEntity(entity);
		}

		// add to entities
		entities.add(entity);

		// add to componentsByEntity
		componentsByEntity.put(entity, Arrays.asList(components));

		// add to componentsByComponentType
		for (int i = 0; i < components.length; i++) {
			IComponent component = components[i];
			Class<? extends IComponent> componentClass = component.getClass();

			Set<IComponent> c = componentsByComponentType.get(componentClass);
			if (c == null) {
				c = new HashSet<>();
				componentsByComponentType.put(componentClass, c);
			}
			c.add(component);
		}
	}

	public long addEntity(short typeId, IComponent[] components) {
		int entityId = ++maxEntityId;
		long entity = getEntity(entityId, typeId);

		initEntity(entity, components);

		return entity;
	}

	public Collection<IComponent> removeEntity(long entity) {
		List<IComponent> components = componentsByEntity.get(entity);

		entities.remove(entity);
		componentsByEntity.remove(entity);
		for (IComponent component : components) {
			Class<? extends IComponent> componentClass = component.getClass();
			componentsByComponentType.get(componentClass).remove(component);
		}

		return components;
	}

	public void clearEntities() {
		maxEntityId = 1;

		entities.clear();
		componentsByEntity.clear();
		componentsByComponentType.clear();
	}

	public Collection<Long> getEntities() {
		return entities;
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T get(long entity, Class<T> componentClass) {
		List<IComponent> allComponents = componentsByEntity.get(entity);
		for (int i = 0; i < allComponents.size(); i++) {
			IComponent component = allComponents.get(i);
			if (component.getClass() == componentClass) {
				return (T) component;
			}
		}
		return null;
	}

	public Collection<IComponent> get(long entity) {
		return componentsByEntity.get(entity);
	}

	public <T extends IComponent> Collection<T> get(Class<T> componentClass) {
		return (Collection<T>) componentsByComponentType.get(componentClass);
	}

}
