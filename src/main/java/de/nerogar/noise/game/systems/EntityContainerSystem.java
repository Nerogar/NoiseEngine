package de.nerogar.noise.game.systems;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.AbstractGameSystem;
import de.nerogar.noise.game.EntityContainer;
import de.nerogar.noise.game.events.SpawnEntityEvent;
import de.nerogar.noiseInterface.game.IComponent;

public class EntityContainerSystem extends AbstractGameSystem {

	private final EntityContainer entityContainer;
	private       EventManager    eventManager;

	public EntityContainerSystem(EventManager eventManager) {
		super(eventManager);
		this.eventManager = eventManager;

		entityContainer = new EntityContainer();

	}

	public EntityContainer getEntityContainer() {
		return entityContainer;
	}

	public void addEntity(IComponent[] components) {
		int entityId = entityContainer.addEntity(components);
		eventManager.trigger(new SpawnEntityEvent(entityId, components));
	}
}
