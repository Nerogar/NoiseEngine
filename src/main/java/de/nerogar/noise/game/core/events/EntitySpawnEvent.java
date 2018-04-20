package de.nerogar.noise.game.core.events;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.game.Entity;

public class EntitySpawnEvent implements Event {

	private Entity entity;

	public EntitySpawnEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

}
