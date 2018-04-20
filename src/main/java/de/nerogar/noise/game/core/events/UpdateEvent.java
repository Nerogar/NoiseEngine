package de.nerogar.noise.game.core.events;

import de.nerogar.noise.event.Event;

public class UpdateEvent implements Event {

	private float delta;

	public UpdateEvent(float delta) {
		this.delta = delta;
	}

	public float getDelta() {
		return delta;
	}

}
