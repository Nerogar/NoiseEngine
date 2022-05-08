package de.nerogar.noise.game.systems;

import de.nerogar.noise.game.EntityContainer;
import de.nerogar.noise.game.events.DespawnEntityEvent;
import de.nerogar.noise.game.events.SpawnEntityEvent;
import de.nerogar.noiseInterface.game.*;

import java.util.List;

public class EntityContainerSystem implements IGameSystem {

	private final EntityContainer entityContainer;

	public EntityContainerSystem() {
		entityContainer = new EntityContainer();
	}

	public int addEntity(IComponent[] components, IEventProducer<SpawnEntityEvent> spawnEntityEvents) {
		int entityId = entityContainer.addEntity(components);
		spawnEntityEvents.addEvent(new SpawnEntityEvent(entityId, components));
		return entityId;
	}

	public void removeEntity(int entityId, IEventProducer<DespawnEntityEvent> removeEntityEvents) {
		IComponent[] removedEntity = entityContainer.removeEntity(entityId);
		removeEntityEvents.addEvent(new DespawnEntityEvent(entityId, removedEntity));
	}

	public IEntity getEntity(int entityId) {
		return entityContainer.getEntity(entityId);
	}

	public <T extends IComponent> T getEntityComponent(int entityId, Class<T> componentClass) {
		return entityContainer.getEntityComponent(entityId, componentClass);
	}

	public <T extends IComponent> List<? super T> getEntityComponents(int entityId, Class<T> componentClass, List<? super T> components) {
		return entityContainer.getEntityComponents(entityId, componentClass, components);
	}

	public <T extends IComponent> boolean hasEntityComponent(int entityId, Class<T> componentClass) {
		return entityContainer.getEntityComponent(entityId, componentClass) != null;
	}

	public IComponent[] getComponents(int entityId) {
		return entityContainer.getComponents(entityId);
	}

	public <T extends IComponent> T getEntityComponent(IComponent component, Class<T> componentClass) {
		return entityContainer.getEntityComponent(component.getEntityId(), componentClass);
	}

	public <T extends IComponent> List<? super T> getEntityComponents(IComponent component, Class<T> componentClass, List<? super T> components) {
		return entityContainer.getEntityComponents(component.getEntityId(), componentClass, components);
	}

	public <T extends IComponent> boolean hasEntityComponent(IComponent component, Class<T> componentClass) {
		return entityContainer.getEntityComponent(component.getEntityId(), componentClass) != null;
	}

	public IComponent[] getComponents(IComponent component) {
		return entityContainer.getComponents(component.getEntityId());
	}

	public <T extends IComponent> List<T> getComponents(Class<T> componentClass, List<T> components) {
		return entityContainer.getComponents(componentClass, components);
	}

}
