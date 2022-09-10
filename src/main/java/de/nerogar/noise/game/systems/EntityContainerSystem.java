package de.nerogar.noise.game.systems;

import de.nerogar.noise.game.EntityContainer;
import de.nerogar.noise.game.events.DespawnEntityEvent;
import de.nerogar.noise.game.events.SpawnEntityEvent;
import de.nerogar.noiseInterface.game.*;

import java.util.*;

public class EntityContainerSystem implements IGameSystem {

	private final EntityContainer entityContainer;

	public EntityContainerSystem() {
		entityContainer = new EntityContainer();
	}

	public long initEntity(long entity, IComponent[] components, IEventProducer<SpawnEntityEvent> events) {
		entityContainer.initEntity(entity, components);
		events.addEvent(new SpawnEntityEvent(entity, List.of(components)));
		return entity;
	}

	public long addEntity(short typeId, IComponent[] components, IEventProducer<SpawnEntityEvent> events) {
		long entity = entityContainer.addEntity(typeId, components);
		events.addEvent(new SpawnEntityEvent(entity, List.of(components)));
		return entity;
	}

	public void clearEntities(IEventProducer<DespawnEntityEvent> events) {
		Collection<Long> entities = entityContainer.getEntities();
		for (Long entity : entities) {
			events.addEvent(new DespawnEntityEvent(entity, entityContainer.get(entity)));
		}
		entityContainer.clearEntities();
	}

	public void removeEntity(long entity, IEventProducer<DespawnEntityEvent> events) {
		Iterable<IComponent> removedEntity = entityContainer.removeEntity(entity);
		events.addEvent(new DespawnEntityEvent(entity, removedEntity));
	}

	public Collection<Long> getEntities() {
		return entityContainer.getEntities();
	}

	public <T extends IComponent> T get(long entity, Class<T> componentClass) {
		return entityContainer.get(entity, componentClass);
	}

	public Collection<IComponent> get(long entity) {
		return entityContainer.get(entity);
	}

	public <T extends IComponent> Collection<T> get(Class<T> componentClass) {
		return entityContainer.get(componentClass);
	}

}
