package de.nerogar.noise.game.systems;

import de.nerogar.noise.event.EventHub;
import de.nerogar.noise.game.EntityContainer;
import de.nerogar.noise.game.events.RemoveEntityEvent;
import de.nerogar.noise.game.events.SpawnEntityEvent;
import de.nerogar.noiseInterface.game.*;

import java.util.List;

public class EntityContainerSystem implements IGameSystem {

	private final EntityContainer entityContainer;
	private       EventHub        eventHub;

	public EntityContainerSystem() {
		entityContainer = new EntityContainer();
	}

	@InjectionMethod
	public void inject(EventHub eventHub) {
		this.eventHub = eventHub;
	}

	@ProducesEvent(SpawnEntityEvent.class)
	public int addEntity(IComponent[] components) {
		int entityId = entityContainer.addEntity(components);
		eventHub.addEvent(new SpawnEntityEvent(entityId, components));
		return entityId;
	}

	@ProducesEvent(RemoveEntityEvent.class)
	public void removeEntity(int entityId) {
		IComponent[] removedEntity = entityContainer.removeEntity(entityId);
		eventHub.addEvent(new RemoveEntityEvent(entityId, removedEntity));
	}

	public IEntity getEntity(int entityId) {
		return entityContainer.getEntity(entityId);
	}

	public <T extends IComponent> T getEntityComponent(int entityId, Class<T> componentClass) {
		return entityContainer.getEntityComponent(entityId, componentClass);
	}

	public <T extends IComponent> void getEntityComponents(int entityId, Class<T> componentClass, List<? super T> components) {
		entityContainer.getEntityComponents(entityId, componentClass, components);
	}

	public IComponent[] getComponents(int entityId) {
		return entityContainer.getComponents(entityId);
	}

	public <T extends IComponent> void getComponents(Class<T> componentClass, List<T> components) {
		entityContainer.getComponents(componentClass, components);
	}

}
