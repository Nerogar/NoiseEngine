package de.nerogar.noise.oldGame.core.events;

import de.nerogar.noiseInterface.event.IEvent;

public class UpdateEvent implements IEvent {

	private float delta;

	public UpdateEvent(float delta) {
		this.delta = delta;
	}

	public float getDelta() {
		return delta;
	}

}
