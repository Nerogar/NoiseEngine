package de.nerogar.noise.game.events;

import de.nerogar.noiseInterface.event.IEvent;

public class TransformationChangeEvent implements IEvent {
	public final int entityId;

	public TransformationChangeEvent(int entityId) {
		this.entityId = entityId;
	}
}
