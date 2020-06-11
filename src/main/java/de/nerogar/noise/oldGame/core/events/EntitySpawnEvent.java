package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.oldGame.Entity;

public class EntitySpawnEvent implements Event {

	private Entity entity;

	public EntitySpawnEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

}
