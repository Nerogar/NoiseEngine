package de.nerogar.noise.game.systems;

import de.nerogar.noise.event.EventHub;
import de.nerogar.noise.game.AbstractGameSystem;
import de.nerogar.noise.game.EntityContainer;
import de.nerogar.noise.game.events.RemoveEntityEvent;
import de.nerogar.noise.game.events.SpawnEntityEvent;
import de.nerogar.noiseInterface.game.IComponent;
import de.nerogar.noiseInterface.game.ProducesEvent;

public class EntityContainerSystem extends AbstractGameSystem {

	private final EntityContainer entityContainer;

	public EntityContainerSystem(EventHub eventHub) {
		super(eventHub);

		entityContainer = new EntityContainer();
	}

	public EntityContainer getEntityContainer() {
		return entityContainer;
	}

	@ProducesEvent(SpawnEntityEvent.class)
	public int addEntity(IComponent[] components) {
		int entityId = entityContainer.addEntity(components);
		getEventHub().addEvent(new SpawnEntityEvent(entityId, components));
		return entityId;
	}

	@ProducesEvent(RemoveEntityEvent.class)
	public void removeEntity(int entityId) {
		IComponent[] removedEntity = entityContainer.removeEntity(entityId);
		getEventHub().addEvent(new RemoveEntityEvent(entityId, removedEntity));
	}

}
