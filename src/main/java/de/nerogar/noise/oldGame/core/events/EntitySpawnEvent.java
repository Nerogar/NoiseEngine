package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noise.oldGame.Entity;

public class EntitySpawnEvent implements IEvent {

	private Entity entity;

	public EntitySpawnEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

}
